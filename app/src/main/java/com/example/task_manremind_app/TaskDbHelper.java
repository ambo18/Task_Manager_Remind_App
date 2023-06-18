package com.example.task_manremind_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "task.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TASKS_TABLE =
            "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " (" +
                    TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TaskContract.TaskEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                    TaskContract.TaskEntry.COLUMN_DESCRIPTION + " TEXT, " +
                    TaskContract.TaskEntry.COLUMN_DUE_DATE + " TEXT, " +
                    TaskContract.TaskEntry.COLUMN_PRIORITY + " INTEGER)";

    private static final String SQL_DELETE_TASKS_TABLE =
            "DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME;

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TASKS_TABLE);
        onCreate(db);
    }
}
