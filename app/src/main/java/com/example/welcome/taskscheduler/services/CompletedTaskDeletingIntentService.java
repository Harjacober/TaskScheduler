package com.example.welcome.taskscheduler.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.welcome.taskscheduler.R;
import com.example.welcome.taskscheduler.data.TaskContract;
import com.example.welcome.taskscheduler.utils.TaskSchedulerUtils;
import com.example.welcome.taskscheduler.widget.TaskWidgetProvider;

public class CompletedTaskDeletingIntentService extends IntentService {
    public static final String ACTION_DELETE_COMPLETED_TASK = "com.example.welcome.taskscheduler.action_delete_completed_task";
    public static final String ACTION_UPDATE_WIDGET_TASK = "com.example.welcome.taskscheduler.action_update_completed_task";
    public static final String UNIQUE_ID = "uniqueId-key";

    public CompletedTaskDeletingIntentService() {
        super("CompletedTaskDeletingIntentService");
    }

    public static void startActionDeleteTask(Context context, String uniqueId){
        Intent intent = new Intent(context, CompletedTaskDeletingIntentService.class);
        intent.setAction(ACTION_DELETE_COMPLETED_TASK);
        intent.putExtra(UNIQUE_ID, uniqueId);
        context.startService(intent);
    }
    public static void startActionUpdateTaskWidget(Context context){
        Intent intent = new Intent(context, CompletedTaskDeletingIntentService.class);
        intent.setAction(ACTION_UPDATE_WIDGET_TASK);
        context.startService(intent);
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null){
            String action = intent.getAction();
            if (ACTION_DELETE_COMPLETED_TASK.equals(action)){
                String uniqueId = intent.getStringExtra(UNIQUE_ID);
                handleActionDeleteTask(uniqueId);
            }else if(ACTION_UPDATE_WIDGET_TASK.equals(action)){
                handleActionUpdatePlantWidget();
            }
        }
    }

    private void handleActionUpdatePlantWidget() {
        Cursor cursor = getContentResolver().query(
                TaskContract.TaskEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        String uniqueId = null;
        String desc = "No Task Available";
        String date = "19/06/18";
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i=1; i <= cursor.getCount(); i++) {
                uniqueId = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.UNIQUE_ID));
                desc = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.TASK_DESCRIPTION));
                String timeInMillis = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.TASK_TIME_WITH_DATE));
                date = TaskSchedulerUtils.getReadableDateString(this, Long.valueOf(timeInMillis));

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, TaskWidgetProvider.class));
                //Trigger data update to handle the GridView widgets and force a data refresh
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);
                //Now update all widgets
                TaskWidgetProvider.updateTaskWidget(this, appWidgetManager, uniqueId,desc ,date,appWidgetIds);
                cursor.moveToNext();
            }
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, TaskWidgetProvider.class));
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);
        //Now update all widgets
        TaskWidgetProvider.updateTaskWidget(this, appWidgetManager, uniqueId,desc ,date,appWidgetIds);

    }

    private void handleActionDeleteTask(String uniqueId) {
        getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI,
                TaskContract.TaskEntry.UNIQUE_ID+"=?",
                new String[] {uniqueId});
        startActionUpdateTaskWidget(this);
    }
}

