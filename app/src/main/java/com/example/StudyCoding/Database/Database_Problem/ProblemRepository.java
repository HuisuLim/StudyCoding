package com.example.StudyCoding.Database.Database_Problem;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.database.Cursor;
import android.text.TextUtils;
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

    public List<Problem> getAllProblems() {
        List<Problem> problemList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(ProblemDatabaseHelper.TABLE_NAME,
                null, // 모든 컬럼
                null,
                null,
                null,
                null,
                null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int problemId = cursor.getInt(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_PROBLEM_ID));
                String titleKo = cursor.getString(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_TITLE_KO));
                int level = cursor.getInt(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_LEVEL));
                String tag1 = cursor.getString(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_TAG_1));
                String tag2 = cursor.getString(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_TAG_2));
                String tag3 = cursor.getString(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_TAG_3));
                String tag4 = cursor.getString(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_TAG_4));
                String tag5 = cursor.getString(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_TAG_5));

                Problem problem = new Problem(problemId, titleKo, level, tag1, tag2, tag3, tag4, tag5);
                problemList.add(problem);
            }
            cursor.close();
        }

        db.close();
        return problemList;
    }
    public List<String> getAllDistinctTags(Context context) {
        List<String> tags = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // tag_1 ~ tag_5 컬럼에서 DISTINCT 값을 얻으려면 UNION 등을 사용할 수 있음
        // 예: SELECT DISTINCT tag_1 AS tag FROM problem_table UNION SELECT DISTINCT tag_2 AS tag FROM problem_table ...
        // 여기서는 예시로 tag_1만 사용 (실제 구현 시 모든 태그 칼럼을 합쳐서 UNION)
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + ProblemDatabaseHelper.COLUMN_TAG_1 + " FROM " + ProblemDatabaseHelper.TABLE_NAME, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String tag = cursor.getString(0);
                if (tag != null && !tag.trim().isEmpty()) {
                    tags.add(tag);
                }
            }
            cursor.close();
        }
        db.close();

        // 필요하다면 중복 제거나 정렬 수행
        // Set 사용 등
        Set<String> tagSet = new HashSet<>(tags);
        tags = new ArrayList<>(tagSet);
        Collections.sort(tags);

        return tags;
    }
    public List<Problem> searchProblems(String number, String title, String levelName, String tag) {
        List<Problem> results = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 조건을 저장할 리스트
        List<String> selectionArgs = new ArrayList<>();
        List<String> whereClauses = new ArrayList<>();

        // number가 비어있지 않다면, 정확 일치나 LIKE 조건 설정
        if (!number.isEmpty()) {
            whereClauses.add(ProblemDatabaseHelper.COLUMN_PROBLEM_ID + " = ?");
            selectionArgs.add(number);
        }

        // title이 비어있지 않다면 title LIKE 검색
        if (!title.isEmpty()) {
            whereClauses.add(ProblemDatabaseHelper.COLUMN_TITLE_KO + " LIKE ?");
            selectionArgs.add("%" + title + "%");
        }

        // levelName이 null이 아니라면 levelName에 해당하는 level 수치를 reverse mapping 해야 함
        // 여기서는 레벨명 -> 레벨ID 변환 함수가 필요
        if (levelName != null) {
            int levelId = getLevelIdFromName(levelName);
            whereClauses.add(ProblemDatabaseHelper.COLUMN_LEVEL + " = ?");
            selectionArgs.add(String.valueOf(levelId));
        }

        // tag가 null이 아니라면 tag 칼럼들 중 하나라도 tag가 있는지 확인
        // 예: (tag_1 = ? OR tag_2 = ? OR ...)
        // 단순히 tag_1만 검색한다면 다음과 같이 가능.
        // 모든 tag 칼럼을 검색하려면 (tag_1 = ? OR tag_2 = ? ...) 식으로 쿼리 구성 필요
        if (tag != null) {
            whereClauses.add("(" +
                    ProblemDatabaseHelper.COLUMN_TAG_1 + " = ? OR " +
                    ProblemDatabaseHelper.COLUMN_TAG_2 + " = ? OR " +
                    ProblemDatabaseHelper.COLUMN_TAG_3 + " = ? OR " +
                    ProblemDatabaseHelper.COLUMN_TAG_4 + " = ? OR " +
                    ProblemDatabaseHelper.COLUMN_TAG_5 + " = ?)");
            // 한 태그값으로 5개 칼럼 모두 비교
            for (int i = 0; i < 5; i++) {
                selectionArgs.add(tag);
            }
        }

        String selection = null;
        if (!whereClauses.isEmpty()) {
            selection = TextUtils.join(" AND ", whereClauses);
        }

        Cursor cursor = db.query(
                ProblemDatabaseHelper.TABLE_NAME,
                null,
                selection,
                selectionArgs.toArray(new String[0]),
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int problemId = cursor.getInt(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_PROBLEM_ID));
                String titleKo = cursor.getString(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_TITLE_KO));
                int level = cursor.getInt(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_LEVEL));
                String tag1 = cursor.getString(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_TAG_1));
                String tag2 = cursor.getString(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_TAG_2));
                String tag3 = cursor.getString(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_TAG_3));
                String tag4 = cursor.getString(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_TAG_4));
                String tag5 = cursor.getString(cursor.getColumnIndexOrThrow(ProblemDatabaseHelper.COLUMN_TAG_5));

                Problem p = new Problem(problemId, titleKo, level, tag1, tag2, tag3, tag4, tag5);
                results.add(p);
            }
            cursor.close();
        }
        db.close();
        return results;
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
    private int getLevelIdFromName(String levelName) {
        switch (levelName) {
            case "Unrated / Not Ratable": return 0;
            case "Bronze V": return 1;
            case "Bronze IV": return 2;
            case "Bronze III": return 3;
            case "Bronze II": return 4;
            case "Bronze I": return 5;
            case "Silver V": return 6;
            case "Silver IV": return 7;
            case "Silver III": return 8;
            case "Silver II": return 9;
            case "Silver I": return 10;
            case "Gold V": return 11;
            case "Gold IV": return 12;
            case "Gold III": return 13;
            case "Gold II": return 14;
            case "Gold I": return 15;
            case "Platinum V": return 16;
            case "Platinum IV": return 17;
            case "Platinum III": return 18;
            case "Platinum II": return 19;
            case "Platinum I": return 20;
            case "Diamond V": return 21;
            case "Diamond IV": return 22;
            case "Diamond III": return 23;
            case "Diamond II": return 24;
            case "Diamond I": return 25;
            case "Ruby V": return 26;
            case "Ruby IV": return 27;
            case "Ruby III": return 28;
            case "Ruby II": return 29;
            case "Ruby I": return 30;
        }
        return -1; // 매칭되지 않는 경우
    }

}
