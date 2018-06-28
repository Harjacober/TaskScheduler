package com.example.welcome.taskscheduler.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class TaskProvider extends ContentProvider{
    TaskDbHelper taskDbHelper;
    private static final int TASK =100;
    private static final int TASK_WITH_ID =101;
    private static final UriMatcher surimatcher=buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher=new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(TaskContract.CONTENT_AUTHORITY, TaskContract.TaskEntry.TABLE_NAME, TASK);
        matcher.addURI(TaskContract.CONTENT_AUTHORITY, TaskContract.TaskEntry.TABLE_NAME+ "/#", TASK_WITH_ID);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        taskDbHelper =new TaskDbHelper(getContext());

        TaskContract.BASE_CONTENT_URI.buildUpon().appendPath(TaskContract.TaskEntry.TABLE_NAME).build();

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortorder) {
        SQLiteDatabase sqLiteDatabase = taskDbHelper.getReadableDatabase();
        int match = surimatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case TASK:
                cursor= sqLiteDatabase.query(TaskContract.TaskEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortorder);
                break;
            case TASK_WITH_ID:
                selection= TaskContract.TaskEntry._ID+"=?";
                selectionArgs=new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor= sqLiteDatabase.query(TaskContract.TaskEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortorder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase sqLiteDatabase= taskDbHelper.getWritableDatabase();
        Uri returnUri;
        int match=surimatcher.match(uri);
        switch (match){
            case TASK: {
                long id = sqLiteDatabase.insert(TaskContract.TaskEntry.TABLE_NAME,

                        null,
                        contentValues);
                if (id > 0) {
                    returnUri = TaskContract.TaskEntry.buildTaskUri(id);
                } else {
                    throw new SQLException("failed to insert row into: " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown Uri " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase= taskDbHelper.getWritableDatabase();
        int numdeleted;
        int match=surimatcher.match(uri);
        switch (match){
            case TASK:
                numdeleted=sqLiteDatabase.delete(TaskContract.TaskEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '"
                        + TaskContract.TaskEntry.TABLE_NAME+"'");
                break;
            case TASK_WITH_ID:
                numdeleted=sqLiteDatabase.delete(TaskContract.TaskEntry.TABLE_NAME,
                        TaskContract.TaskEntry._ID+"=?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '"
                        + TaskContract.TaskEntry.TABLE_NAME+"'");
                break;
            default:
                throw new UnsupportedOperationException("Unlnown uri "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return numdeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase= taskDbHelper.getWritableDatabase();
        int numupdated=0;
        if (contentValues==null){
            throw new IllegalArgumentException("cannot have null content values");
        }
        int match=surimatcher.match(uri);
        switch (match){
            case TASK:
                numupdated=sqLiteDatabase.update(TaskContract.TaskEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case TASK_WITH_ID:
                numupdated=sqLiteDatabase.update(TaskContract.TaskEntry.TABLE_NAME,
                        contentValues,
                        TaskContract.TaskEntry._ID+"=?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri "+uri);
        }
        if (numupdated>0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return numupdated;
    }
}
