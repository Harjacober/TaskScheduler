package com.example.welcome.taskscheduler.reminder;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.example.welcome.taskscheduler.utils.ReminderTask;
import com.example.welcome.taskscheduler.utils.TaskSchedulerUtils;

import java.util.concurrent.TimeUnit;

public class SetPeriodicTImeTableReminderJob extends Job {
    public static final String PERIODIC_TAG = "schedule-timetable-periodic-job";
    @NonNull
    @Override
    protected Result onRunJob(Params params) {

        PersistableBundleCompat bundleCompat = params.getExtras();
        ReminderTask.executeTask(getContext(), ReminderTask.ACTION_TASK_REMINDER,
                bundleCompat.getString("unique-id", ""),
                bundleCompat.getString("task-name", ""));
        return Result.SUCCESS;
    }

    public static void schedulePeriodic(long interval, PersistableBundleCompat bundle) {
        new JobRequest.Builder(PERIODIC_TAG)
                .setPeriodic(TimeUnit.HOURS.toMillis(1), TimeUnit.MINUTES.toMillis(5))
                .setUpdateCurrent(false)
                .setPersisted(true)
                .setExtras(bundle)
                .build()
                .schedule();
    }
}
