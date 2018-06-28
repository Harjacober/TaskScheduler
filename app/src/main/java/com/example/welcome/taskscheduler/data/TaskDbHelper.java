package com.example.welcome.taskscheduler.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="TaskScheduler.db";
    private static final int DATABASE_VERSION=1;

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CFI_TABLE="CREATE TABLE "+ TaskContract.TaskEntry.TABLE_NAME+"("
                + TaskContract.TaskEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TaskContract.TaskEntry.CREATED_DAY_NUM+" TEXT NOT NULL, "
                + TaskContract.TaskEntry.CREATED_DAY_WORD+" TEXT NOT NULL, "
                + TaskContract.TaskEntry.CREATED_MONTH+" TEXT NOT NULL, "
                + TaskContract.TaskEntry.CREATED_YEAR+" TEXT NOT NULL, "
                + TaskContract.TaskEntry.CREATED_TIME+" TEXT NOT NULL, "
                + TaskContract.TaskEntry.TASK_DESCRIPTION+" TEXT NOT NULL, "
                + TaskContract.TaskEntry.CATEGORY+" TEXT NOT NULL, "
                + TaskContract.TaskEntry.UNIQUE_ID+" TEXT NOT NULL, "
                + TaskContract.TaskEntry.TYPE+" TEXT NOT NULL, "
                + TaskContract.TaskEntry.TASK_TIME_WITH_DATE+" TEXT NOT NULL, "
                + TaskContract.TaskEntry.TASK_TIME_WITHOUT_DATE+" TEXT NOT NULL, "
                + TaskContract.TaskEntry.IMAGE_DATA+" TEXT NOT NULL);";
        sqLiteDatabase.execSQL(CREATE_CFI_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String DROP_TABLE="DROP IF EXIST "+ TaskContract.TaskEntry.TABLE_NAME+";";
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);
    }
}
