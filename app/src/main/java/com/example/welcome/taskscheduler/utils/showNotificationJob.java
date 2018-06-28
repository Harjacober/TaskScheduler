package com.example.welcome.taskscheduler.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

public class showNotificationJob extends Job {
    static final String SCHEDULE_TAG = "show_notification_job_schedule_tag";
    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        PersistableBundleCompat bundleCompat = params.getExtras();
        ReminderTask.executeTask(getContext(), ReminderTask.ACTION_TASK_REMINDER,
                bundleCompat.getString("unique-id", ""),
                bundleCompat.getString("task-name", ""));
        return Result.SUCCESS;
    }

    public static void scheduleExactJob(long startTime, PersistableBundleCompat bundle){
        new JobRequest.Builder(showNotificationJob.SCHEDULE_TAG)
                .setExact(900000)
                .setUpdateCurrent(false)
                .setPersisted(true)
                .setExtras(bundle)
                .build()
                .schedule();
        Log.i("ssssssssss", "job scheduled");
    }
}
