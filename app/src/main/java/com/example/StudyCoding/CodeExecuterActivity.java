package com.example.StudyCoding;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.StudyCoding.API.Judge0_API;
import com.example.StudyCoding.Database.CodeDatabase.CodeDatabaseHelper;
import com.example.StudyCoding.Database.CodeDatabase.CodeRepository;
import com.example.StudyCoding.Database.CodeDatabase.CodeTask;
import com.example.StudyCoding.Models.Judge0Models.Language;
import com.example.StudyCoding.Models.Judge0Models.SubmissionRequest;
import com.example.StudyCoding.Models.Judge0Models.SubmissionResponse;
import com.example.StudyCoding.API.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.classifier.CodeProcessor;
import io.github.kbiakov.codeview.highlight.ColorTheme;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CodeExecuterActivity extends AppCompatActivity {
    private static final String TAG = "Judge0";
    private Judge0_API api;

    private EditText codeInput;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CodeProcessor.init(this);
        setContentView(R.layout.code_executer);

        repository = new CodeRepository(this);
        codeTask = new CodeTask(this);
        api = RetrofitClient.getInstance().create(Judge0_API.class);
        // URL 받기
        String url = getIntent().getStringExtra("url");
        // UI 요소 초기화
        codeInput = findViewById(R.id.codeInput);
        codeView = findViewById(R.id.codeView);
        input = findViewById(R.id.yourInput);
        outputTextView = findViewById(R.id.outputTextView);
        languageSpinner = findViewById(R.id.languageSpinner);
        confirmButton = findViewById(R.id.confirmButton);
        saveButton = findViewById(R.id.saveButton);
        initializeCodeInput(url);
        saveButton.setOnClickListener(v -> {
            // 팝업창 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(CodeExecuterActivity.this);
            builder.setTitle("Save Code");

            // 팝업창에 EditText 추가
            final EditText titleInput = new EditText(CodeExecuterActivity.this);
            titleInput.setHint("Enter title");
            builder.setView(titleInput);

            // AlertDialog 생성
            AlertDialog dialog = builder.create();

            // "Save" 버튼 설정
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save", (d, which) -> {
                // 이 부분은 커스텀 버튼 동작으로 대체됩니다.
            });

            // "Cancel" 버튼 설정
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (d, which) -> dialog.dismiss());

            // 다이얼로그 표시 후 버튼 동작 커스터마이징
            dialog.show();

            // 커스텀 "Save" 버튼 동작 설정
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                String title = titleInput.getText().toString().trim();
                String userCode = codeInput.getText().toString().trim();

                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(CodeExecuterActivity.this, "Title is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(userCode) || selectedLanguageId == -1) {
                    Toast.makeText(CodeExecuterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // "사용자정의/" + 입력 제목으로 매핑
                String formattedTitle = "사용자정의/" + title;

                // 중복 검사
                if (repository.doesTitleExist(formattedTitle)) {
                    Toast.makeText(CodeExecuterActivity.this, "The title already exists. Please use a different title.", Toast.LENGTH_SHORT).show();
                } else {
                    // 저장 로직
                    repository.upsertCodeSubmission(formattedTitle, userCode, selectedLanguageId);
                    Toast.makeText(CodeExecuterActivity.this, "Code saved successfully with title: " + formattedTitle, Toast.LENGTH_SHORT).show();
                    dialog.dismiss(); // 성공적으로 저장되면 창 닫기
                }
            });
        });



        codeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String escapedCode = TextUtils.htmlEncode(s.toString());
                Log.d(TAG, "User Typed Code: " + escapedCode);
                codeView.setCode(escapedCode);
                codeView.updateOptions(new Function1<Options, Unit>() {
                    @Override
                    public Unit invoke(Options options) {
                        options.withCode(escapedCode).withLanguage("java").withTheme(ColorTheme.MONOKAI);
                        return null;
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Log.d(TAG, "Fetching language list...");
        fetchLanguages(url);

        confirmButton.setOnClickListener(v -> {
            String userInput = input.getText().toString().trim();
            String userCode = codeInput.getText().toString().trim();
            Log.d(TAG, "Button Clicked. User Code: " + userCode);
            Log.d(TAG, "User Input: " + userInput);
            Log.d(TAG, "Selected Language ID: " + selectedLanguageId);
            createSubmission(userCode, selectedLanguageId, userInput, "100");
        });
    }

    private void fetchLanguages(String url) {
        api.getLanguages().enqueue(new Callback<List<Language>>() {
            @Override
            public void onResponse(Call<List<Language>> call, Response<List<Language>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    languages = response.body();
                    Log.d(TAG, "Languages Fetched: " + languages.size());
                    setupSpinner();
                    initializeSpinner(url);
                } else {
                    Log.e(TAG, "Failed to load languages. Error code: " + response.code());
                    Toast.makeText(CodeExecuterActivity.this, "Failed to load languages", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Language>> call, Throwable t) {
                Log.e(TAG, "Error Fetching Languages: " + t.getMessage());
                Toast.makeText(CodeExecuterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinner() {
        List<String> languageNames = new ArrayList<>();
        for (Language language : languages) {
            languageNames.add(language.getName());
        }
        Log.d(TAG, "Setting up spinner with languages: " + languageNames);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languageNames);
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

    private void initializeCodeInput(String url) {
        codeView.setOptions(Options.Default.get(this)
                .withLanguage("java")
                .withTheme(ColorTheme.MONOKAI));
        if (url == null) {
            codeView.setOptions(Options.Default.get(this)
                    .withLanguage("java")
                    .withTheme(ColorTheme.MONOKAI));
            return;
        }

        Cursor cursor = repository.getCodeSubmissionByUrl(url);
        if (cursor.moveToFirst()) {
            String existingCode = cursor.getString(cursor.getColumnIndexOrThrow(CodeDatabaseHelper.COLUMN_CODE_INPUT));
            codeInput.setText(existingCode);
            codeView.post(() -> {
                codeView.setCode(existingCode);
                codeView.setOptions(Options.Default.get(this)
                        .withCode(existingCode)
                        .withLanguage("java")
                        .withTheme(ColorTheme.MONOKAI));
            });
        }
        cursor.close();
    }

    private void initializeSpinner(String url) {
        if (url == null || languages.isEmpty()) return;

        Cursor cursor = repository.getCodeSubmissionByUrl(url);
        if (cursor.moveToFirst()) {
            int existingLanguageId = cursor.getInt(cursor.getColumnIndexOrThrow(CodeDatabaseHelper.COLUMN_LANGUAGE_ID));

            for (int i = 0; i < languages.size(); i++) {
                if (languages.get(i).getId() == existingLanguageId) {
                    languageSpinner.setSelection(i);
                    break;
                }
            }
            Toast.makeText(this, "Spinner set for URL.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No saved language for URL.", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

}
