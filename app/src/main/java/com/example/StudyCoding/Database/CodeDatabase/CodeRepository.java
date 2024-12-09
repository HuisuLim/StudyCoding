package com.example.StudyCoding.Database.CodeDatabase;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CodeRepository {
    private final SQLiteDatabase db;

    public CodeRepository(Context context) {
        CodeDatabaseHelper dbHelper = new CodeDatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }


    public void insertCodeSubmission(String url, String codeInput, int languageId) {
        ContentValues values = new ContentValues();
        values.put(CodeDatabaseHelper.COLUMN_URL, url);
        values.put(CodeDatabaseHelper.COLUMN_CODE_INPUT, codeInput);
        values.put(CodeDatabaseHelper.COLUMN_LANGUAGE_ID, languageId);

        db.insertWithOnConflict(CodeDatabaseHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // 특정 URL의 데이터 삭제
    public int deleteCodeSubmissionByUrl(String url) {
        return db.delete(
                CodeDatabaseHelper.TABLE_NAME,
                CodeDatabaseHelper.COLUMN_URL + " = ?",
                new String[]{url}
        );
    }
    public Cursor getCodeSubmissionByUrl(String url) {
        return db.query(
                CodeDatabaseHelper.TABLE_NAME,
                null,
                CodeDatabaseHelper.COLUMN_URL + " = ?",
                new String[]{url},
                null,
                null,
                null
        );
    }
    // 기존 URL이 존재하면 업데이트, 존재하지 않으면 삽입
    public void upsertCodeSubmission(String url, String codeInput, int languageId) {
        Cursor cursor = db.query(
                CodeDatabaseHelper.TABLE_NAME,
                null,
                CodeDatabaseHelper.COLUMN_URL + " = ?",
                new String[]{url},
                null,
                null,
                null
        );

        ContentValues values = new ContentValues();
        values.put(CodeDatabaseHelper.COLUMN_URL, url);
        values.put(CodeDatabaseHelper.COLUMN_CODE_INPUT, codeInput);
        values.put(CodeDatabaseHelper.COLUMN_LANGUAGE_ID, languageId);

        if (cursor.moveToFirst()) {
            // URL이 존재하면 업데이트
            db.update(
                    CodeDatabaseHelper.TABLE_NAME,
                    values,
                    CodeDatabaseHelper.COLUMN_URL + " = ?",
                    new String[]{url}
            );
        } else {
            // URL이 존재하지 않으면 삽입
            db.insert(
                    CodeDatabaseHelper.TABLE_NAME,
                    null,
                    values
            );
        }
        cursor.close(); // Cursor 닫기
    }

    public void close() {
        db.close();
    }
    public Cursor getAllCodeSubmissions() {
        return db.query(
                CodeDatabaseHelper.TABLE_NAME,
                null, // 모든 열 선택
                null, // 조건 없음
                null, // 조건 값 없음
                null, // 그룹화 없음
                null, // 필터링 없음
                null  // 정렬 없음
        );
    }
}
