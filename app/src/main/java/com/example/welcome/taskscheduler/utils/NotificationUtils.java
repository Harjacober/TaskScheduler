package com.example.welcome.taskscheduler.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.welcome.taskscheduler.R;
import com.example.welcome.taskscheduler.TaskDetails;
import com.example.welcome.taskscheduler.services.TaskReminderIntentService;

public class NotificationUtils {
    private static final int TASK_REMINDER_PENDING_INTENT_ID = 64;
    private static final int TASK_REMINDER_NOTIFICATION_ID = 644;
    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 355;
    private static final int ACTION_TASK_PENDING_INTENT_ID = 254;
    public static final String UNIQUE_ID_KEY = "uniqueId-key";
    public static final String TASK_TEXT = "task-text-key";

    public static void clearAllNotifications(Context context){
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void remindUserToCompleteTask(Context context, String uniqueId,
                                                  String taskText){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_done_white_24dp)
                .setLargeIcon(largeIcon(context))
                .setContentTitle("Complete your task")
                .setContentText(taskText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        taskText
                )).setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context, uniqueId))
                .addAction(taskCompleted(context, uniqueId))
                .addAction(ignoreTask(context))
                .setAutoCancel(true);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
        }

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(TASK_REMINDER_NOTIFICATION_ID,
                notificationBuilder.build());


    }

    private static PendingIntent contentIntent(Context context, String uniqueId){
        Intent startActivityIntent = new Intent(context, TaskDetails.class);
        startActivityIntent.putExtra(UNIQUE_ID_KEY, uniqueId);


        return PendingIntent.getActivity(context,
                TASK_REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context){
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_search_white_24dp);
        return largeIcon;
    }

    private static NotificationCompat.Action ignoreTask(Context context){
        Intent ignoreReminderIntent = new Intent(context, TaskReminderIntentService.class);
        ignoreReminderIntent.setAction(ReminderTask.ACTION_DISMISS_NOTIFICATION);
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action ignoreReminderAction = new NotificationCompat.Action(R.drawable.ic_done_white_24dp,
                "No thanks",
                ignoreReminderPendingIntent);
        return ignoreReminderAction;
    }

    private static NotificationCompat.Action taskCompleted(Context context, String uniqueId){
        Intent taskCompletedIntent = new Intent(context, TaskReminderIntentService.class);
        taskCompletedIntent.putExtra(UNIQUE_ID_KEY, uniqueId);
        taskCompletedIntent.setAction(ReminderTask.ACTION_TASK_COMPLETED);
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(context,
                ACTION_TASK_PENDING_INTENT_ID,
                taskCompletedIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Action ignoreReminderAction = new NotificationCompat.Action(R.drawable.ic_done_white_24dp,
                "Completed",
                ignoreReminderPendingIntent);
        return ignoreReminderAction;
    }
}

