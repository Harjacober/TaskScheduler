package com.example.welcome.taskscheduler.reminder;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.example.welcome.taskscheduler.utils.ReminderTask;
import com.example.welcome.taskscheduler.utils.showNotificationJob;

import java.util.concurrent.TimeUnit;

public class SetTimeTableReminderJob extends Job {
    public static final String TIME_TABLE_TAG = "show_notification_job_time_table_tag";
    @NonNull
    @Override
    protected Result onRunJob(Params params) {

        PersistableBundleCompat bundleCompat = params.getExtras();
        long interval = bundleCompat.getLong("interval", 0);
        SetPeriodicTImeTableReminderJob.schedulePeriodic(interval, bundleCompat);
        return Result.SUCCESS;
    }

    public static void scheduleExactJob(long startTime, PersistableBundleCompat bundle){
        new JobRequest.Builder(SetTimeTableReminderJob.TIME_TABLE_TAG)
                .setExact(900000)
                .setUpdateCurrent(false)
                .setPersisted(true)
                .setExtras(bundle)
                .build()
                .schedule();
    }

}
