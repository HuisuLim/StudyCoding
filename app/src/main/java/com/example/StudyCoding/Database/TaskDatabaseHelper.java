package com.example.StudyCoding.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "taskDatabase.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "tasks";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TASK_ID = "taskId";
    public static final String COLUMN_ORIGIN_URL = "originUrl";
    public static final String COLUMN_CONSTRAINT_TEXT = "constraintText"; // 이름 변경
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_PROBLEM_TEXT = "problemText";
    public static final String COLUMN_INPUT = "input";
    public static final String COLUMN_OUTPUT = "output";
    public static final String COLUMN_SCREENSHOT_URL = "screenshotUrl";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TASK_ID + " TEXT, " +
                    COLUMN_ORIGIN_URL + " TEXT, " +
                    COLUMN_CONSTRAINT_TEXT + " TEXT, " + // 이름 변경
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_PROBLEM_TEXT + " TEXT, " +
                    COLUMN_INPUT + " TEXT, " +
                    COLUMN_OUTPUT + " TEXT, " +
                    COLUMN_SCREENSHOT_URL + " TEXT" +
                    ");";

    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
