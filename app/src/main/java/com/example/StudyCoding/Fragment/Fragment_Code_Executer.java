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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.StudyCoding.API.API_Judge0;
import com.example.StudyCoding.Database.Database_Code.CodeDatabaseHelper;
import com.example.StudyCoding.Database.Database_Code.CodeRepository;
import com.example.StudyCoding.Database.Database_Code.CodeTask;
import com.example.StudyCoding.LanguageRuleBook.ControlKeywordRule;
import com.example.StudyCoding.LanguageRuleBook.CustomLanguageRuleBook;
import com.example.StudyCoding.LanguageRuleBook.TypeKeywordRule;
import com.example.StudyCoding.Models.Judge0Models.SubmissionLanguage;
import com.example.StudyCoding.Models.Judge0Models.SubmissionRequest;
import com.example.StudyCoding.Models.Judge0Models.SubmissionResponse;
import com.example.StudyCoding.API.Client_Judge0;
import com.example.StudyCoding.R;

import java.util.ArrayList;
import java.util.List;

import de.markusressel.kodeeditor.library.view.CodeEditorLayout;
import de.markusressel.kodehighlighter.core.LanguageRuleBook;
import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.classifier.CodeProcessor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_Code_Executer extends Fragment {
    private static final String TAG = "Judge0";
    private API_Judge0 api;

    private CodeView codeView;
    private EditText input;
    private TextView outputTextView;
    private Button confirmButton;
    private Button saveButton;
    private ImageButton homeButton;
    private Spinner languageSpinner;
    private CodeTask codeTask;
    private List<SubmissionLanguage> submissionLanguages = new ArrayList<>();
    private CodeRepository repository;
    private int selectedLanguageId = -1;
    private CodeEditorLayout codeEditorLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CodeProcessor.init(requireContext());
        View rootView = inflater.inflate(R.layout.activity_code_executor, container, false);
        String url = getArguments().getString("url");
        repository = new CodeRepository(requireContext());
        codeTask = new CodeTask(requireContext());
        api = Client_Judge0.getInstance().create(API_Judge0.class);

        // UI 요소 초기화
        input = rootView.findViewById(R.id.yourInput);
        outputTextView = rootView.findViewById(R.id.outputTextView);
        languageSpinner = rootView.findViewById(R.id.languageSpinner);
        homeButton = rootView.findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().finish(); // 현재 액티비티 종료
                }
            }
        });
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
        api.getLanguages().enqueue(new Callback<List<SubmissionLanguage>>() {
            @Override
            public void onResponse(Call<List<SubmissionLanguage>> call, Response<List<SubmissionLanguage>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    submissionLanguages = response.body();
                    Log.d(TAG, "Languages Fetched: " + submissionLanguages.size());
                    setupSpinner();
                    // URL 데이터를 기반으로 Code와 Spinner 초기화
                    initializeSpinner(url);

                } else {
                    Log.e(TAG, "Failed to load languages. Error code: " + response.code());
                    Toast.makeText(requireContext(), "Failed to load languages", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SubmissionLanguage>> call, Throwable t) {
                Log.e(TAG, "Error Fetching Languages: " + t.getMessage());
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinner() {
        List<String> languageNames = new ArrayList<>();
        for (SubmissionLanguage submissionLanguage : submissionLanguages) {
            languageNames.add(submissionLanguage.getName());
        }
        Log.d(TAG, "Setting up spinner with languages: " + languageNames);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_selected_item,
                languageNames
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item); // 드롭다운용 커스텀 레이아웃 적용
        languageSpinner.setAdapter(adapter);


        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLanguageId = submissionLanguages.get(position).getId();
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
        if (url == null || submissionLanguages.isEmpty()) return;

        Cursor cursor = repository.getCodeSubmissionByUrl(url);
        if (cursor.moveToFirst()) {
            int existingLanguageId = cursor.getInt(cursor.getColumnIndexOrThrow(CodeDatabaseHelper.COLUMN_LANGUAGE_ID));

            // Spinner 초기화 (해당 언어 ID를 찾아 선택)
            for (int i = 0; i < submissionLanguages.size(); i++) {
                if (submissionLanguages.get(i).getId() == existingLanguageId) {
                    languageSpinner.setSelection(i);
                    break;
                }
            }
            Toast.makeText(requireContext(), "Spinner set for URL.", Toast.LENGTH_SHORT).show();
        } else {
        }
        cursor.close();
    }





}
