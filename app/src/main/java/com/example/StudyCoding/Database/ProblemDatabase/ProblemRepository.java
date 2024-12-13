package com.example.StudyCoding.Database.ProblemDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import android.database.Cursor;
import android.util.Log;


public class ProblemRepository {
    private final ProblemDatabaseHelper dbHelper;

    public ProblemRepository(Context context) {
        dbHelper = new ProblemDatabaseHelper(context);
    }

    // 문제와 태그 저장
    public void insertProblem(int problemId, String titleKo, int level, List<String> tags) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ProblemDatabaseHelper.COLUMN_PROBLEM_ID, problemId);
        values.put(ProblemDatabaseHelper.COLUMN_TITLE_KO, titleKo);
        values.put(ProblemDatabaseHelper.COLUMN_LEVEL, level);

        // 최대 5개의 태그 저장
        for (int i = 0; i < tags.size() && i < 5; i++) {
            values.put("tag_" + (i + 1), tags.get(i));
        }

        db.insertWithOnConflict(
                ProblemDatabaseHelper.TABLE_NAME,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
        db.close();
    }
    public void UpsertProblem(int problemId, String titleKo, int level, List<String> tags) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ProblemDatabaseHelper.COLUMN_PROBLEM_ID, problemId);
        values.put(ProblemDatabaseHelper.COLUMN_TITLE_KO, titleKo);
        values.put(ProblemDatabaseHelper.COLUMN_LEVEL, level);

        // 최대 5개의 태그 저장
        for (int i = 0; i < tags.size() && i < 5; i++) {
            values.put("tag_" + (i + 1), tags.get(i));
        }

        // 업데이트 또는 삽입
        int rowsUpdated = db.update(
                ProblemDatabaseHelper.TABLE_NAME,
                values,
                ProblemDatabaseHelper.COLUMN_PROBLEM_ID + " = ?", // WHERE 조건
                new String[]{String.valueOf(problemId)} // WHERE 인자
        );

        if (rowsUpdated == 0) {
            // 업데이트된 행이 없으면 삽입
            db.insertWithOnConflict(
                    ProblemDatabaseHelper.TABLE_NAME,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
            );
        }

        db.close();
    }


    // 데이터 조회 및 Logcat 출력
    public void printAllProblems() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                ProblemDatabaseHelper.TABLE_NAME, // 테이블 이름
                null, // 모든 열 선택
                null, // 조건 없음
                null, // 조건 값 없음
                null, // 그룹화 없음
                null, // 정렬 없음
                null  // 정렬 조건 없음
        );

        if (cursor.moveToFirst()) {
            do {
                int problemId = cursor.getInt(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_PROBLEM_ID));
                String titleKo = cursor.getString(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_TITLE_KO));
                int level = cursor.getInt(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_LEVEL));
                String tag1 = cursor.getString(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_TAG_1));
                String tag2 = cursor.getString(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_TAG_2));
                String tag3 = cursor.getString(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_TAG_3));
                String tag4 = cursor.getString(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_TAG_4));
                String tag5 = cursor.getString(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_TAG_5));

                // Logcat에 출력
                Log.d("ProblemData", "ProblemId: " + problemId +
                        ", Title: " + titleKo +
                        ", Level: " + level +
                        ", Tags: [" + tag1 + ", " + tag2 + ", " + tag3 + ", " + tag4 + ", " + tag5 + "]");
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }
}
