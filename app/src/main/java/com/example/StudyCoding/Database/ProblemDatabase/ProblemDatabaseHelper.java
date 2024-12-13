package com.example.StudyCoding.Database.ProblemDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProblemDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "problems.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "problem_table";
    public static final String COLUMN_PROBLEM_ID = "problemId";
    public static final String COLUMN_TITLE_KO = "titleKo";
    public static final String COLUMN_LEVEL = "level";
    public static final String COLUMN_TAG_1 = "tag_1";
    public static final String COLUMN_TAG_2 = "tag_2";
    public static final String COLUMN_TAG_3 = "tag_3";
    public static final String COLUMN_TAG_4 = "tag_4";
    public static final String COLUMN_TAG_5 = "tag_5";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_PROBLEM_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_TITLE_KO + " TEXT, " +
                    COLUMN_LEVEL + " INTEGER, " +
                    COLUMN_TAG_1 + " TEXT, " +
                    COLUMN_TAG_2 + " TEXT, " +
                    COLUMN_TAG_3 + " TEXT, " +
                    COLUMN_TAG_4 + " TEXT, " +
                    COLUMN_TAG_5 + " TEXT" +
                    ");";

    public ProblemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

