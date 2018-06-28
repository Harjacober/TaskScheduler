package com.example.welcome.taskscheduler.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.welcome.taskscheduler.utils.ReminderTask;

import static com.example.welcome.taskscheduler.utils.NotificationUtils.TASK_TEXT;
import static com.example.welcome.taskscheduler.utils.NotificationUtils.UNIQUE_ID_KEY;

public class TaskReminderIntentService extends IntentService {
    public TaskReminderIntentService() {
        super("TaskReminderIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String uniqueId = null;
        if (intent.hasExtra("unique-id")) {
            uniqueId = intent.getStringExtra(UNIQUE_ID_KEY);
        }
        ReminderTask.executeTask(this, intent.getAction(),
                null,
                uniqueId);
    }
}
