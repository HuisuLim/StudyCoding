package com.example.StudyCoding.Database.CodeDatabase;

import android.content.Context;
import android.database.Cursor;

public class CodeTask {
    private CodeRepository repository;

    // 생성자: CodeRepository 초기화
    public CodeTask(Context context) {
        this.repository = new CodeRepository(context);
    }

    // URL 데이터를 불러오는 메서드
    public CodeTask getCodeTaskByUrl(String url) {
        ensureRepositoryInitialized(); // repository 초기화 확인
        Cursor cursor = repository.getCodeSubmissionByUrl(url);
        if (cursor.moveToFirst()) {
            String codeInput = cursor.getString(cursor.getColumnIndexOrThrow(CodeDatabaseHelper.COLUMN_CODE_INPUT));
            int languageId = cursor.getInt(cursor.getColumnIndexOrThrow(CodeDatabaseHelper.COLUMN_LANGUAGE_ID));
            cursor.close();
            return new CodeTask(url, codeInput, languageId);
        }
        cursor.close();
        return null; // 데이터가 없을 경우
    }

    // 데이터 저장 (업서트)
    public void saveCodeTask(CodeTask codeTask) {
        ensureRepositoryInitialized(); // repository 초기화 확인
        repository.upsertCodeSubmission(codeTask.getUrl(), codeTask.getCodeInput(), codeTask.getLanguageId());
    }


    // 데이터베이스 닫기
    public void close() {
        if (repository != null) {
            repository.close();
        }
    }

    // 기존 CodeTask 필드
    private String url;
    private String codeInput;
    private int languageId;

    // 기존 생성자
    public CodeTask(String url, String codeInput, int languageId) {
        this.url = url;
        this.codeInput = codeInput;
        this.languageId = languageId;
    }

    // Getter 메서드
    public String getUrl() {
        return url;
    }

    public String getCodeInput() {
        return codeInput;
    }

    public int getLanguageId() {
        return languageId;
    }

    // toString 메서드 (디버깅용)
    @Override
    public String toString() {
        return "CodeTask{" +
                "url='" + url + '\'' +
                ", codeInput='" + codeInput + '\'' +
                ", languageId=" + languageId +
                '}';
    }

    // repository가 null인 경우 초기화
    private void ensureRepositoryInitialized() {
        if (repository == null) {
            throw new IllegalStateException("Repository is not initialized. Ensure to initialize CodeTask with a Context.");
        }
    }
}