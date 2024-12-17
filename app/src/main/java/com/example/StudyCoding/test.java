package com.example.StudyCoding;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.StudyCoding.Database.Database_Code.CodeDatabaseHelper;
import com.example.StudyCoding.Database.Database_Code.CodeRepository;

public class test extends AppCompatActivity {

    private static final String TAG = "TestActivity"; // 로그 태그 정의
    private CodeRepository codeRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_problem_list);
        // Context를 사용하여 CodeRepository 초기화
        codeRepository = new CodeRepository(this);

        // 전체 데이터를 출력하는 메서드 호출
        printAllCodeSubmissions();
        //codeRepository.upsertCodeSubmission("https://www.acmicpc.net/problem/1006","hihi", 54);
        //printAllCodeSubmissions();
    }

    public void printAllCodeSubmissions() {
        // CodeRepository를 통해 getAllCodeSubmissions 호출
        Cursor cursor = codeRepository.getAllCodeSubmissions();
        if (cursor.moveToFirst()) {
            do {
                String url = cursor.getString(cursor.getColumnIndexOrThrow(CodeDatabaseHelper.COLUMN_URL));
                String codeInput = cursor.getString(cursor.getColumnIndexOrThrow(CodeDatabaseHelper.COLUMN_CODE_INPUT));
                int languageId = cursor.getInt(cursor.getColumnIndexOrThrow(CodeDatabaseHelper.COLUMN_LANGUAGE_ID));

                // 데이터를 Log로 출력
                Log.d(TAG, "URL: " + url);
                Log.d(TAG, "Code Input: " + codeInput);
                Log.d(TAG, "Language ID: " + languageId);
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, "No data found in the database.");
        }
        cursor.close();
    }

}
