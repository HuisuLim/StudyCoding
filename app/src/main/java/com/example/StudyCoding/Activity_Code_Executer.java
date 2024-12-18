package com.example.StudyCoding;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.List;

import de.markusressel.kodeeditor.library.view.CodeEditorLayout;
import de.markusressel.kodehighlighter.core.LanguageRuleBook;
import io.github.kbiakov.codeview.classifier.CodeProcessor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Code_Executer extends AppCompatActivity {
    private static final String TAG = "Judge0";
    private API_Judge0 api;

    private EditText input;
    private CodeEditorLayout codeEditorLayout;
    private TextView outputTextView;
    private Button confirmButton;
    private Button saveButtonMain;
    private ImageButton homeButton;
    private Spinner languageSpinner;
    private CodeTask codeTask;
    private List<SubmissionLanguage> submissionLanguages = new ArrayList<>();
    private CodeRepository repository;
    private int selectedLanguageId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CodeProcessor.init(this);
        setContentView(R.layout.activity_code_executor);

        repository = new CodeRepository(this);
        codeTask = new CodeTask(this);
        api = Client_Judge0.getInstance().create(API_Judge0.class);
        // URL 받기
        String url = getIntent().getStringExtra("url");
        // UI 요소 초기화
        input = findViewById(R.id.yourInput);
        outputTextView = findViewById(R.id.outputTextView);
        languageSpinner = findViewById(R.id.languageSpinner);
        confirmButton = findViewById(R.id.confirmButton);
        saveButtonMain = findViewById(R.id.saveButton);
        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료 (이전 액티비티로 돌아감)
            }
        });
        codeEditorLayout = findViewById(R.id.codeEditorView);
        LanguageRuleBook languageRuleBook2 = new CustomLanguageRuleBook(
                new TypeKeywordRule(),
                new ControlKeywordRule()
        );
        codeEditorLayout.setLanguageRuleBook(languageRuleBook2);
        initializeCodeInput(url);
        saveButtonMain.setOnClickListener(v -> {
            if (url != null) {
                // URL이 repository에 존재하면 바로 upsert
                String userCode = codeEditorLayout.getText().toString().trim();
                repository.upsertCodeSubmission(url, userCode, selectedLanguageId);
                Toast.makeText(Activity_Code_Executer.this, "Code updated successfully for URL: " + url, Toast.LENGTH_SHORT).show();
            } else {
                // 기존 로직 유지
                showCustomSaveDialog();
            }
        });

        /*
        saveButton.setOnClickListener(v -> {
            if (url != null) {
                // URL이 전달된 경우 바로 저장
                String userCode = codeEditorLayout.getText().toString().trim();
                if (TextUtils.isEmpty(userCode) || selectedLanguageId == -1) {
                    Toast.makeText(Activity_Code_Executer.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                // URL에 저장
                repository.upsertCodeSubmission(url, userCode, selectedLanguageId);
                Toast.makeText(Activity_Code_Executer.this, "Code saved successfully for URL: " + url, Toast.LENGTH_SHORT).show();
            } else {
                // 팝업창 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Code_Executer.this);
                builder.setTitle("Save Code");
                // 팝업창에 EditText 추가
                final EditText titleInput = new EditText(Activity_Code_Executer.this);
                titleInput.setHint("Enter title");
                builder.setView(titleInput);
                // AlertDialog 생성
                AlertDialog dialog = builder.create();
                // "Save" 버튼 설정
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save", (d, which) -> {
                });
                // "Cancel" 버튼 설정
                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (d, which) -> dialog.dismiss());
                dialog.show();
                // 커스텀 "Save" 버튼 동작 설정
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                    String title = titleInput.getText().toString().trim();
                    String userCode = codeEditorLayout.getText().toString().trim();
                    if (TextUtils.isEmpty(title)) {
                        Toast.makeText(Activity_Code_Executer.this, "Title is required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(userCode) || selectedLanguageId == -1) {
                        Toast.makeText(Activity_Code_Executer.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // "사용자정의/" + 입력 제목으로 매핑
                    String formattedTitle = "사용자정의/" + title;
                    // 중복 검사
                    if (repository.doesTitleExist(formattedTitle)) {
                        Toast.makeText(Activity_Code_Executer.this, "The title already exists. Please use a different title.", Toast.LENGTH_SHORT).show();
                    } else {
                        // 저장 로직
                        repository.upsertCodeSubmission(formattedTitle, userCode, selectedLanguageId);
                        Toast.makeText(Activity_Code_Executer.this, "Code saved successfully with title: " + formattedTitle, Toast.LENGTH_SHORT).show();
                        dialog.dismiss(); // 성공적으로 저장되면 창 닫기
                    }
                });
            }
        });

         */


        Log.d(TAG, "Fetching language list...");
        fetchLanguages(url);

        confirmButton.setOnClickListener(v -> {
            String userInput = input.getText().toString().trim();
            String userCode = codeEditorLayout.getText().toString().trim();
            Log.d(TAG, "Button Clicked. User Code: " + userCode);
            Log.d(TAG, "User Input: " + userInput);
            Log.d(TAG, "Selected Language ID: " + selectedLanguageId);
            createSubmission(userCode, selectedLanguageId, userInput, null);
        });
    }

    private void fetchLanguages(String url) {
        api.getLanguages().enqueue(new Callback<List<SubmissionLanguage>>() {
            @Override
            public void onResponse(Call<List<SubmissionLanguage>> call, Response<List<SubmissionLanguage>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    submissionLanguages = response.body();
                    Log.d(TAG, "Languages Fetched: " + submissionLanguages.size());
                    setupSpinner();
                    initializeSpinner(url);
                } else {
                    Log.e(TAG, "Failed to load languages. Error code: " + response.code());
                    Toast.makeText(Activity_Code_Executer.this, "Failed to load languages", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SubmissionLanguage>> call, Throwable t) {
                Log.e(TAG, "Error Fetching Languages: " + t.getMessage());
                Toast.makeText(Activity_Code_Executer.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                this,
                R.layout.spinner_selected_item,       // 커스텀 레이아웃 적용
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

    private void initializeCodeInput(String url) {
        if (url == null) {
            return;
        }

        Cursor cursor = repository.getCodeSubmissionByUrl(url);
        if (cursor.moveToFirst()) {
            String existingCode = cursor.getString(cursor.getColumnIndexOrThrow(CodeDatabaseHelper.COLUMN_CODE_INPUT));
            codeEditorLayout.setText(existingCode);
        }
        cursor.close();
    }

    private void initializeSpinner(String url) {
        if (url == null || submissionLanguages.isEmpty()) return;

        Cursor cursor = repository.getCodeSubmissionByUrl(url);
        if (cursor.moveToFirst()) {
            int existingLanguageId = cursor.getInt(cursor.getColumnIndexOrThrow(CodeDatabaseHelper.COLUMN_LANGUAGE_ID));

            for (int i = 0; i < submissionLanguages.size(); i++) {
                if (submissionLanguages.get(i).getId() == existingLanguageId) {
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
    private void showCustomSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.custom_dialog_save_code, null);
        builder.setView(customView);

        // UI 요소 참조
        EditText codeTitleInput = customView.findViewById(R.id.codeTitleInput);
        Button cancelButton = customView.findViewById(R.id.cancelButton);
        Button saveButton = customView.findViewById(R.id.saveButton);

        AlertDialog dialog = builder.create();
        dialog.show();

        // 버튼 클릭 리스너
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        saveButton.setOnClickListener(v -> {
            String title = codeTitleInput.getText().toString().trim();
            String userCode = codeEditorLayout.getText().toString().trim();
            if (TextUtils.isEmpty(title)) {
                Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(userCode) || selectedLanguageId == -1) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String formattedTitle = "사용자정의/" + title;
            if (repository.doesTitleExist(formattedTitle)) {
                Toast.makeText(this, "The title already exists.", Toast.LENGTH_SHORT).show();
            } else {
                repository.upsertCodeSubmission(formattedTitle, userCode, selectedLanguageId);
                Toast.makeText(this, "Code saved successfully!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

}
