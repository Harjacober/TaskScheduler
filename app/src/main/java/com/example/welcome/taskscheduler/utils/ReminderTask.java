package com.example.welcome.taskscheduler.utils;

import android.content.Context;

import com.example.welcome.taskscheduler.data.TaskContract;

public class ReminderTask {
    public static final String ACTION_TASK_COMPLETED = "task-completed-notification";
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
    public static final String ACTION_TASK_REMINDER = "task-reminder";

    public static void executeTask(Context context, String action, String medName, String uniqueId){
        if (ACTION_TASK_COMPLETED.equals(action)){
            taskCompleted(context, uniqueId);
        }else if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            NotificationUtils.clearAllNotifications(context);
        }else if (ACTION_TASK_REMINDER.equals(action)){
            remindUserAboutTask(context, medName, uniqueId);
        }
    }

    private static void remindUserAboutTask(Context context, String taskText, String uniqueId) {
        NotificationUtils.remindUserToCompleteTask(context, taskText, uniqueId);
    }

    private static void taskCompleted(Context context, String uniqueId) {
        //handle when user checked that he has completed his task
        context.getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI,
                TaskContract.TaskEntry.UNIQUE_ID+"=?",
                new String[] {uniqueId});
        NotificationUtils.clearAllNotifications(context);
    }

}
