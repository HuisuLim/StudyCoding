package com.example.StudyCoding.Database.Database_Code;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CodeDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "codeDatabase.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "codeSubmissions";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_CODE_INPUT = "codeInput";
    public static final String COLUMN_LANGUAGE_ID = "languageId";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_URL + " TEXT PRIMARY KEY, " +
                    COLUMN_CODE_INPUT + " TEXT, " +
                    COLUMN_LANGUAGE_ID + " INTEGER" +
                    ");";

    public CodeDatabaseHelper(Context context) {
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
