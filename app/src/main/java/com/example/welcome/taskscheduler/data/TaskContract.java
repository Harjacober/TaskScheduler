package com.example.welcome.taskscheduler.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class TaskContract {
    public static final String CONTENT_AUTHORITY="com.example.welcome.taskscheduler";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    private TaskContract(){}

    public static final class TaskEntry implements BaseColumns {
        public final static String _ID=BaseColumns._ID;
        public static final String TABLE_NAME="Task_collection";
        public static final String CREATED_DAY_NUM="created_day_num";
        public static final String CREATED_DAY_WORD="created_day_word";
        public static final String CREATED_MONTH="created_month";
        public static final String CREATED_YEAR="created_year";
        public static final String CREATED_TIME="created_time";
        public static final String TASK_DESCRIPTION="task_description";
        public static final String IMAGE_DATA="image_data";
        public static final String CATEGORY="category";
        public static final String UNIQUE_ID="unique_id";
        public static final String TYPE="type";
        public static final String TASK_TIME_WITH_DATE="task_time_with_date";
        public static final String TASK_TIME_WITHOUT_DATE="task_time_without_date";
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();


        public static Uri buildTaskUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

    }
}
