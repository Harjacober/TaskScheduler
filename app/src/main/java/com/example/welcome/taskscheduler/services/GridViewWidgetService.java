package com.example.welcome.taskscheduler.services;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.welcome.taskscheduler.R;
import com.example.welcome.taskscheduler.data.TaskContract;
import com.example.welcome.taskscheduler.utils.NotificationUtils;
import com.example.welcome.taskscheduler.utils.TaskSchedulerUtils;

public class GridViewWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
    class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{

        Context mContext;
        Cursor mCursor;
        public GridRemoteViewsFactory(Context context){
            mContext = context;
        }
        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            if (mCursor != null) mCursor.close();
            mCursor = mContext.getContentResolver().query(
                    TaskContract.TaskEntry.CONTENT_URI,
                    null,
                    TaskContract.TaskEntry.TYPE+"=?",
                    new String[] {"Schedules"},
                    null
            );
        }

        @Override
        public void onDestroy() {
            if (mCursor!=null) {
                mCursor.close();
            }
        }

        @Override
        public int getCount() {
            if (mCursor == null) return 0;
            return mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (mCursor == null || mCursor.getCount() == 0) return null;
            mCursor.moveToPosition(position);

            String uniqueId = mCursor.getString(mCursor.getColumnIndex(TaskContract.TaskEntry.UNIQUE_ID));
            String desc = mCursor.getString(mCursor.getColumnIndex(TaskContract.TaskEntry.TASK_DESCRIPTION));
            String timeInMillis = mCursor.getString(mCursor.getColumnIndex(TaskContract.TaskEntry.TASK_TIME_WITH_DATE));
            String date = TaskSchedulerUtils.getReadableDateString(mContext, Long.valueOf(timeInMillis));
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.task_widget_provider);
            views.setTextViewText(R.id.widget_task_desc, desc);
            views.setTextViewText(R.id.widget_task_date, date);
            // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
            Bundle extras = new Bundle();
            extras.putString(NotificationUtils.UNIQUE_ID_KEY, uniqueId);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            views.setOnClickFillInIntent(R.id.widget_task_desc, fillInIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
