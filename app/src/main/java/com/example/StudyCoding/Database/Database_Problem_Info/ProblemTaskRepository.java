package com.example.StudyCoding.Database.Database_Problem_Info;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.StudyCoding.API.APIModels.BrowseAIModels.RobotRetrievedResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProblemTaskRepository {
    private final SQLiteDatabase db;

    public ProblemTaskRepository(Context context) {
        ProblemTaskDatabaseHelper dbHelper = new ProblemTaskDatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void insertTask(RobotRetrievedResponse response) {
        String originUrl = response.getResult().getInputParameters().getOriginUrl();

        if (isUrlExists(originUrl)) {
            return;
        }
        ContentValues values = new ContentValues();

        values.put(ProblemTaskDatabaseHelper.COLUMN_TASK_ID, response.getResult().getId());
        values.put(ProblemTaskDatabaseHelper.COLUMN_ORIGIN_URL, response.getResult().getInputParameters().getOriginUrl());

        Map<String, String> capturedTexts = response.getResult().getCapturedTexts();
        values.put(ProblemTaskDatabaseHelper.COLUMN_CONSTRAINT_TEXT, capturedTexts.get("constraint"));
        values.put(ProblemTaskDatabaseHelper.COLUMN_TITLE, capturedTexts.get("title"));
        values.put(ProblemTaskDatabaseHelper.COLUMN_PROBLEM_TEXT, capturedTexts.get("problem_text"));
        values.put(ProblemTaskDatabaseHelper.COLUMN_INPUT, capturedTexts.get("input"));
        values.put(ProblemTaskDatabaseHelper.COLUMN_OUTPUT, capturedTexts.get("output"));

        values.put(ProblemTaskDatabaseHelper.COLUMN_SCREENSHOT_URL, response.getResult().getCapturedScreenshots().getProblem_capture().getSrc());

        db.insert(ProblemTaskDatabaseHelper.TABLE_NAME, null, values);
    }

    // 테이블의 총 행 수 반환
    public int getRowCount() {
        String query = "SELECT COUNT(*) FROM " + ProblemTaskDatabaseHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        int rowCount = 0;
        if (cursor.moveToFirst()) {
            rowCount = cursor.getInt(0); // COUNT(*) 결과 가져오기
        }
        cursor.close();
        return rowCount;
    }


    // 특정 URL을 가진 행 삭제
    public int deleteRowByUrl(String originUrl) {
        return db.delete(ProblemTaskDatabaseHelper.TABLE_NAME,
                ProblemTaskDatabaseHelper.COLUMN_ORIGIN_URL + " = ?",
                new String[]{originUrl});
    }



    public void close() {
        db.close();
    }

    // 모든 데이터 조회
    public List<String> getAllTasks() {
        List<String> tasks = new ArrayList<>();
        String query = "SELECT * FROM " + ProblemTaskDatabaseHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    String taskId = getSafeString(cursor, ProblemTaskDatabaseHelper.COLUMN_TASK_ID);
                    String originUrl = getSafeString(cursor, ProblemTaskDatabaseHelper.COLUMN_ORIGIN_URL);
                    String constraint = getSafeString(cursor, ProblemTaskDatabaseHelper.COLUMN_CONSTRAINT_TEXT);
                    String title = getSafeString(cursor, ProblemTaskDatabaseHelper.COLUMN_TITLE);
                    String problemText = getSafeString(cursor, ProblemTaskDatabaseHelper.COLUMN_PROBLEM_TEXT);
                    String input = getSafeString(cursor, ProblemTaskDatabaseHelper.COLUMN_INPUT);
                    String output = getSafeString(cursor, ProblemTaskDatabaseHelper.COLUMN_OUTPUT);
                    String screenshotUrl = getSafeString(cursor, ProblemTaskDatabaseHelper.COLUMN_SCREENSHOT_URL);

                    String task = "Task ID: " + taskId +
                            ", Origin URL: " + originUrl +
                            ", Constraint: " + constraint +
                            ", Title: " + title +
                            ", Problem Text: " + problemText +
                            ", Input: " + input +
                            ", Output: " + output +
                            ", Screenshot URL: " + screenshotUrl;

                    tasks.add(task);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close(); // 항상 Cursor를 닫습니다.
        }

        return tasks;
    }

    // 특정 URL로 데이터를 반환하는 메서드
    public RobotRetrievedResponse getTaskByUrl(String originUrl) {
        String query = "SELECT * FROM " + ProblemTaskDatabaseHelper.TABLE_NAME +
                " WHERE " + ProblemTaskDatabaseHelper.COLUMN_ORIGIN_URL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{originUrl});

        RobotRetrievedResponse response = null;

        if (cursor.moveToFirst()) {
            RobotRetrievedResponse.Result result = new RobotRetrievedResponse.Result();

            // 데이터베이스에서 값 읽기
            result.setId(getSafeString(cursor, ProblemTaskDatabaseHelper.COLUMN_TASK_ID));

            // InputParameters 생성 및 설정
            RobotRetrievedResponse.Result.InputParameters inputParameters =
                    new RobotRetrievedResponse.Result.InputParameters();
            inputParameters.setOriginUrl(getSafeString(cursor, ProblemTaskDatabaseHelper.COLUMN_ORIGIN_URL));
            result.setInputParameters(inputParameters);

            // CapturedTexts 설정
            Map<String, String> capturedTexts = new HashMap<>();
            capturedTexts.put("constraint", getSafeString(cursor, ProblemTaskDatabaseHelper.COLUMN_CONSTRAINT_TEXT));
            capturedTexts.put("title", getSafeString(cursor, ProblemTaskDatabaseHelper.COLUMN_TITLE));
            capturedTexts.put("problem_text", getSafeString(cursor, ProblemTaskDatabaseHelper.COLUMN_PROBLEM_TEXT));
            capturedTexts.put("input", getSafeString(cursor, ProblemTaskDatabaseHelper.COLUMN_INPUT));
            capturedTexts.put("output", getSafeString(cursor, ProblemTaskDatabaseHelper.COLUMN_OUTPUT));
            result.setCapturedTexts(capturedTexts);

            // CapturedScreenshots 설정
            RobotRetrievedResponse.Result.CapturedScreenshots capturedScreenshots =
                    new RobotRetrievedResponse.Result.CapturedScreenshots();
            RobotRetrievedResponse.Result.CapturedScreenshots.ProblemCapture problemCapture =
                    new RobotRetrievedResponse.Result.CapturedScreenshots.ProblemCapture();
            problemCapture.setSrc(getSafeString(cursor, ProblemTaskDatabaseHelper.COLUMN_SCREENSHOT_URL));
            capturedScreenshots.setProblem_capture(problemCapture);
            result.setCapturedScreenshots(capturedScreenshots);

            // 결과 객체 생성
            response = new RobotRetrievedResponse();
            response.setResult(result);
        }
        cursor.close();
        return response;
    }


    // 안전하게 컬럼 값을 가져오는 메서드
    private String getSafeString(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return index != -1 ? cursor.getString(index) : "N/A";
    }
    // 특정 URL 존재 여부 확인
    public boolean isUrlExists(String originUrl) {
        String query = "SELECT COUNT(*) FROM " + ProblemTaskDatabaseHelper.TABLE_NAME +
                " WHERE " + ProblemTaskDatabaseHelper.COLUMN_ORIGIN_URL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{originUrl});

        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0; // COUNT(*) 결과가 0보다 크면 존재
        }
        cursor.close();
        return exists;
    }

}
