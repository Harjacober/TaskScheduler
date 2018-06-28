package com.example.welcome.taskscheduler;

import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.welcome.taskscheduler.adapters.TaskListViewAdapter;
import com.example.welcome.taskscheduler.data.TaskContract;
import com.example.welcome.taskscheduler.utils.NotificationUtils;
import com.example.welcome.taskscheduler.utils.TaskSchedulerUtils;

public class TaskDetails extends AppCompatActivity {
    private String mId;
    private Uri mTaskWithId;
    private String uniqueId;
    private boolean fromActivity;
    private TextView createdDayNum;
    private TextView createdDayWord;
    private TextView createdMonthYear;
    private TextView createdTime;
    private TextView taskDescription;
    private TextView categoryText;
    private ImageView taskImage;
    private ImageView categoryColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        final Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            mId = intent.getStringExtra(Intent.EXTRA_TEXT);
            mTaskWithId = TaskContract.TaskEntry.buildTaskUri(Long.parseLong(mId));
            fromActivity = true;
        } else if (intent.hasExtra(NotificationUtils.UNIQUE_ID_KEY)) {
            uniqueId = intent.getStringExtra(NotificationUtils.UNIQUE_ID_KEY);
            fromActivity = false;
        }

        displayTaskDetails();
        createdDayNum = findViewById(R.id.createdDayNum);
        createdDayWord = findViewById(R.id.createdDayWord);
        createdMonthYear = findViewById(R.id.createdMonthYear);
        createdTime = findViewById(R.id.createdTime);
        taskDescription = findViewById(R.id.taskDescription);
//        taskImage = findViewById(R.id.taskImage);
        categoryColor = findViewById(R.id.radioButton);
        categoryText = findViewById(R.id.category);

        android.support.v7.app.ActionBar actiobar = getSupportActionBar();
        if (actiobar != null) {
            actiobar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        } else if (id == R.id.delete_medication) {
//            CompletedTaskDeletingIntentService.startActionDeleteTask(this, uniqueId);
            if (mTaskWithId != null) {
                getContentResolver().delete(mTaskWithId,
                        null,
                        null);
                finish();
            } else {
                getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI,
                        TaskContract.TaskEntry.UNIQUE_ID + "=?",
                        new String[]{uniqueId});
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayTaskDetails() {
        String[] projection = {TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.CREATED_DAY_NUM,
                TaskContract.TaskEntry.CREATED_DAY_WORD,
                TaskContract.TaskEntry.CREATED_TIME,
                TaskContract.TaskEntry.CREATED_YEAR,
                TaskContract.TaskEntry.CATEGORY,
                TaskContract.TaskEntry.TASK_DESCRIPTION,
                TaskContract.TaskEntry.IMAGE_DATA,
                TaskContract.TaskEntry.CREATED_MONTH};
        new FetchTaskDetails().execute(projection);
    }

    class FetchTaskDetails extends AsyncTask<String[], Void, Cursor> {

        @Override
        protected Cursor doInBackground(String[]... strings) {
            String[] projection = strings[0];
            Cursor cursor;
            if (fromActivity) {
                cursor = getContentResolver().query(mTaskWithId,
                        projection,
                        null,
                        null,
                        null);
            } else {
                cursor = getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,
                        projection,
                        TaskContract.TaskEntry.UNIQUE_ID + "=?",
                        new String[]{uniqueId},
                        null);
            }
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            try{
                if (cursor != null){
                    cursor.moveToFirst();
                    String dayNum = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.CREATED_DAY_NUM));
                    String dayWord = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.CREATED_DAY_WORD));
                    String year = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.CREATED_YEAR));
                    String month = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.CREATED_MONTH));
                    String time = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.CREATED_TIME));
                    String desc = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.TASK_DESCRIPTION));
                    String categoryHere = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.CATEGORY));
                    TypedArray array = getApplicationContext().getResources().obtainTypedArray(R.array.categories);
                    String pCategory = array.getString(Integer.valueOf(categoryHere));

                    createdDayNum.setText(dayNum);
                    createdDayWord.setText(dayWord);
                    createdMonthYear.setText(month + " " + year);
                    createdTime.setText(TaskSchedulerUtils.getTimeInReadableFormat(Long.valueOf(time)-3600000));
                    taskDescription.setText(desc);
                    categoryText.setText(pCategory);
                    TaskListViewAdapter.categoryImage(Integer.valueOf(categoryHere), categoryColor);
//                    taskImage.setVisibility(View.GONE);
                }
            }finally {
                cursor.close();
            }
            super.onPostExecute(cursor);
        }
    }
}
