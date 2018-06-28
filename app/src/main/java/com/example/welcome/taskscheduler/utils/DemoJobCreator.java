package com.example.welcome.taskscheduler.utils;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.example.welcome.taskscheduler.reminder.SetPeriodicTImeTableReminderJob;
import com.example.welcome.taskscheduler.reminder.SetTimeTableReminderJob;

public class DemoJobCreator implements JobCreator {
    @Override
    public Job create(String tag) {
        switch (tag) {
            case showNotificationJob.SCHEDULE_TAG:
                return new showNotificationJob();
            case SetTimeTableReminderJob.TIME_TABLE_TAG:
                return new SetTimeTableReminderJob();
            case SetPeriodicTImeTableReminderJob.PERIODIC_TAG:
                return new SetPeriodicTImeTableReminderJob();
            default:
                return null;
        }
    }
}
