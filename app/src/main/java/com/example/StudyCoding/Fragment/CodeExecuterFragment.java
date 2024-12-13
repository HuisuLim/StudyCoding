package com.example.StudyCoding.Fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.StudyCoding.API.Judge0_API;
import com.example.StudyCoding.Database.CodeDatabase.CodeDatabaseHelper;
import com.example.StudyCoding.Database.CodeDatabase.CodeRepository;
import com.example.StudyCoding.Database.CodeDatabase.CodeTask;
import com.example.StudyCoding.LanguageRuleBook.ControlKeywordRule;
import com.example.StudyCoding.LanguageRuleBook.CustomLanguageRuleBook;
import com.example.StudyCoding.LanguageRuleBook.TypeKeywordRule;
import com.example.StudyCoding.Models.Judge0Models.Language;
import com.example.StudyCoding.Models.Judge0Models.SubmissionRequest;
import com.example.StudyCoding.Models.Judge0Models.SubmissionResponse;
import com.example.StudyCoding.API.RetrofitClient;
import com.example.StudyCoding.R;

import java.util.ArrayList;
import java.util.List;

import de.markusressel.kodeeditor.library.view.CodeEditorLayout;
import de.markusressel.kodehighlighter.core.LanguageRuleBook;
import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.classifier.CodeProcessor;
import io.github.kbiakov.codeview.highlight.ColorTheme;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CodeExecuterFragment extends Fragment {
    private static final String TAG = "Judge0";
    private Judge0_API api;

    private CodeView codeView;
    private EditText input;
    private TextView outputTextView;
    private Button confirmButton;
    private Button saveButton;
    private Spinner languageSpinner;
    private CodeTask codeTask;
    private List<Language> languages = new ArrayList<>();
    private CodeRepository repository;
    private int selectedLanguageId = -1;
    private CodeEditorLayout codeEditorLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CodeProcessor.init(requireContext());
        View rootView = inflater.inflate(R.layout.code_executer, container, false);
        String url = getArguments().getString("url");
        repository = new CodeRepository(requireContext());
        codeTask = new CodeTask(requireContext());
        api = RetrofitClient.getInstance().create(Judge0_API.class);

        // UI 요소 초기화
        input = rootView.findViewById(R.id.yourInput);
        outputTextView = rootView.findViewById(R.id.outputTextView);
        languageSpinner = rootView.findViewById(R.id.languageSpinner);
        confirmButton = rootView.findViewById(R.id.confirmButton);
        saveButton = rootView.findViewById(R.id.saveButton);
        codeEditorLayout = rootView.findViewById(R.id.codeEditorView);
        LanguageRuleBook languageRuleBook2 = new CustomLanguageRuleBook(
                new TypeKeywordRule(),
                new ControlKeywordRule()
        );
        codeEditorLayout.setLanguageRuleBook(languageRuleBook2);
        // URL 데이터를 기반으로 Code와 Spinner 초기화
        initializeCodeInput(url);
        saveButton.setOnClickListener(v -> {
            String userCode = codeEditorLayout.getText().toString().trim();
            if (url != null && !userCode.isEmpty() && selectedLanguageId != -1) {
                repository.upsertCodeSubmission(url, userCode, selectedLanguageId);
                Toast.makeText(requireContext(), "Code saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
        // 언어 목록 불러오기
        Log.d(TAG, "Fetching language list...");
        fetchLanguages(url);

        // 버튼 클릭 이벤트 설정
        confirmButton.setOnClickListener(v -> {
            String userInput = input.getText().toString().trim();
            String userCode = codeEditorLayout.getText().toString().trim();
            Log.d(TAG, "Button Clicked. User Code: " + userCode);
            Log.d(TAG, "User Input: " + userInput);
            Log.d(TAG, "Selected Language ID: " + selectedLanguageId);
            createSubmission(userCode, selectedLanguageId, userInput, "100");
        });

        return rootView;
    }

    private void fetchLanguages(String url) {
        api.getLanguages().enqueue(new Callback<List<Language>>() {
            @Override
            public void onResponse(Call<List<Language>> call, Response<List<Language>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    languages = response.body();
                    Log.d(TAG, "Languages Fetched: " + languages.size());
                    setupSpinner();
                    // URL 데이터를 기반으로 Code와 Spinner 초기화
                    initializeSpinner(url);

                } else {
                    Log.e(TAG, "Failed to load languages. Error code: " + response.code());
                    Toast.makeText(requireContext(), "Failed to load languages", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Language>> call, Throwable t) {
                Log.e(TAG, "Error Fetching Languages: " + t.getMessage());
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinner() {
        List<String> languageNames = new ArrayList<>();
        for (Language language : languages) {
            languageNames.add(language.getName());
        }
        Log.d(TAG, "Setting up spinner with languages: " + languageNames);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, languageNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLanguageId = languages.get(position).getId();
                Log.d(TAG, "Selected Language ID: " + selectedLanguageId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedLanguageId = -1;
                Log.d(TAG, "No language selected.");
            }
        });
    }

    private void createSubmission(String formattedText, int languageId, String stdIn, String stdOut) {
        SubmissionRequest request = new SubmissionRequest(formattedText, languageId, stdIn, stdOut);
        Log.d(TAG, "Creating Submission with data: " + request);

        api.createSubmission(false, true, "*", request).enqueue(new Callback<SubmissionResponse>() {
            @Override
            public void onResponse(Call<SubmissionResponse> call, Response<SubmissionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    Log.d(TAG, "Submission Token: " + token);
                    getSubmission(token);
                } else {
                    Log.e(TAG, "Submission failed. Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SubmissionResponse> call, Throwable t) {
                Log.e(TAG, "Submission Request failed: " + t.getMessage());
            }
        });
    }

    private void getSubmission(String token) {
        Log.d(TAG, "Fetching submission result for token: " + token);
        api.getSubmission(token, false, "stdout,stderr,status_id,language_id").enqueue(new Callback<SubmissionResponse>() {
            @Override
            public void onResponse(Call<SubmissionResponse> call, Response<SubmissionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SubmissionResponse result = response.body();
                    Log.d(TAG, "Submission Result: " + result);
                    if (result.getStdout() != null) {
                        outputTextView.setText(result.getStdout());
                    } else {
                        outputTextView.setText(result.getStderr());
                    }
                } else {
                    Log.e(TAG, "Failed to fetch submission result. Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SubmissionResponse> call, Throwable t) {
                Log.e(TAG, "Fetching Submission failed: " + t.getMessage());
            }
        });
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String escapedCode = TextUtils.htmlEncode(s.toString());
            codeView.setCode(escapedCode);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


    private void initializeCodeInput(String url) {
        if (url == null) {
            return;
        }

        Cursor cursor = repository.getCodeSubmissionByUrl(url);
        if (cursor.moveToFirst()) {
            String existingCode = cursor.getString(cursor.getColumnIndexOrThrow(CodeDatabaseHelper.COLUMN_CODE_INPUT));
            // Code Input 초기화
            codeEditorLayout.setText(existingCode);
            // CodeView 초기화 (post로 타이밍 보장)
        }
        cursor.close();
    }


    private void initializeSpinner(String url) {
        if (url == null || languages.isEmpty()) return;

        Cursor cursor = repository.getCodeSubmissionByUrl(url);
        if (cursor.moveToFirst()) {
            int existingLanguageId = cursor.getInt(cursor.getColumnIndexOrThrow(CodeDatabaseHelper.COLUMN_LANGUAGE_ID));

            // Spinner 초기화 (해당 언어 ID를 찾아 선택)
            for (int i = 0; i < languages.size(); i++) {
                if (languages.get(i).getId() == existingLanguageId) {
                    languageSpinner.setSelection(i);
                    break;
                }
            }
            Toast.makeText(requireContext(), "Spinner set for URL.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "No saved language for URL.", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }





}
