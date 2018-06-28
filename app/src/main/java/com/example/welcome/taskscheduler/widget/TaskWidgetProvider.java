package com.example.welcome.taskscheduler.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.welcome.taskscheduler.R;
import com.example.welcome.taskscheduler.TaskDetails;
import com.example.welcome.taskscheduler.services.CompletedTaskDeletingIntentService;
import com.example.welcome.taskscheduler.services.GridViewWidgetService;
import com.example.welcome.taskscheduler.utils.NotificationUtils;

/**
 * Implementation of App Widget functionality.
 */
public class TaskWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                String uniqueId, String desc, String date, int appWidgetId) {

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        RemoteViews remoteViews;
        if (width < 200){
            remoteViews = getSingleTaskRemoteView(context, uniqueId, desc, date);
        }else {
            remoteViews = getMultipleGridRemoteview(context);
        }
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    public static void updateTaskWidget(Context context, AppWidgetManager appWidgetManager,
                                        String uniqueId, String desc, String date, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, uniqueId, desc, date, appWidgetId);
        }
    }

    private static RemoteViews getMultipleGridRemoteview(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);
        // Set the GridWidgetService intent to act as the adapter for the GridView
        Intent intent = new Intent(context, GridViewWidgetService.class);
        views.setRemoteAdapter(R.id.widget_grid_view, intent);
        // Set the PlantDetailActivity intent to launch when clicked
        Intent appIntent = new Intent(context, TaskDetails.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_grid_view, appPendingIntent);
        // Handle empty gardens
        views.setEmptyView(R.id.widget_grid_view, R.id.empty_view);
        return views;
    }

    private static RemoteViews getSingleTaskRemoteView(Context context, String uniqueId
                                                        ,String desc , String date) {
        Intent intent;
        Log.d(TaskWidgetProvider.class.getSimpleName(), "UniqueId=" + uniqueId);
        intent = new Intent(context, TaskDetails.class);
        intent.putExtra(NotificationUtils.UNIQUE_ID_KEY, uniqueId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.task_widget_provider);
        views.setTextViewText(R.id.widget_task_desc, desc);
        views.setTextViewText(R.id.widget_task_date, date);
        views.setOnClickPendingIntent(R.id.linearLayout, pendingIntent);
        // Add the completedService click handler
        Intent deleteIntent = new Intent(context, CompletedTaskDeletingIntentService.class);
        deleteIntent.setAction(CompletedTaskDeletingIntentService.ACTION_DELETE_COMPLETED_TASK);
        // Add the task ID as extra to delete only that task when clicked
        deleteIntent.putExtra(CompletedTaskDeletingIntentService.UNIQUE_ID, uniqueId);
        PendingIntent wateringPendingIntent = PendingIntent.getService(context, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_completed_button, wateringPendingIntent);
        return views;
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        CompletedTaskDeletingIntentService.startActionUpdateTaskWidget(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
            CompletedTaskDeletingIntentService.startActionUpdateTaskWidget(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

