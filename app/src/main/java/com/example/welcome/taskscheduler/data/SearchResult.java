package com.example.welcome.taskscheduler.data;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welcome.taskscheduler.R;
import com.example.welcome.taskscheduler.TaskDetails;
import com.example.welcome.taskscheduler.adapters.SearchResultAdapter;
import com.example.welcome.taskscheduler.utils.NotificationUtils;
import com.example.welcome.taskscheduler.utils.TaskSchedulerUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchResult extends AppCompatActivity implements SearchResultAdapter.ListItemCLickListener{

    private RecyclerView mRecyclerView;
    private SearchResultAdapter searchResultAdapter;
    ArrayList<JSONObject> data;
    private TextView mEmptyView;
    public static final String CREATED_DAY_NUM = "created-day-num";
    public static final String CREATED_DAY_WORD = "created-day-word";
    public static final String CREATED_YEAR = "created-year";
    public static final String CREATED_MONTH = "created-month";
    public static final String CREATED_TIME = "created-time";
    public static final String TASK_DESCRIPTION = "task-description";
    public static final String CATEGORY = "category";
    public static final String TYPE = "type";
    public static final String UNIQUE_ID = "unique-id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        mEmptyView = findViewById(R.id.empty_view);
        data = new ArrayList<>();
        searchResultAdapter = new SearchResultAdapter(this, data, this);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(searchResultAdapter);

        android.support.v7.app.ActionBar actionbar=getSupportActionBar();
        if (actionbar!=null){
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
        //search
        handleIntent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY).trim();
            //Do some stuffs
            new queryFromBackGround().execute(query);
            Toast.makeText(this, "Something just hapened", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemCLickListener(String clickedItemUniqueId) {
        Intent intent=new Intent(SearchResult.this, TaskDetails.class);
        intent.putExtra(NotificationUtils.UNIQUE_ID_KEY, clickedItemUniqueId);
        startActivity(intent);
    }

    class queryFromBackGround extends AsyncTask<String, Void, Cursor> {

        @Override
        protected Cursor doInBackground(String... strings) {
            String query = strings[0];
            Cursor cursor = TaskSchedulerUtils.search(query, getApplicationContext());
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor == null) {
                mEmptyView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
            } else {
                cursor.moveToNext();
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < cursor.getCount(); i++) {
                    try {
                        jsonObject.put(CREATED_DAY_NUM,
                                cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.CREATED_DAY_NUM)));
                        jsonObject.put(CREATED_DAY_WORD,
                                cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.CREATED_DAY_WORD)));
                        jsonObject.put(CREATED_YEAR,
                                cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.CREATED_YEAR)));
                        jsonObject.put(CREATED_MONTH,
                                cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.CREATED_MONTH)));
                        jsonObject.put(CREATED_TIME,
                                cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.CREATED_TIME)));
                        jsonObject.put(TASK_DESCRIPTION,
                                cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.TASK_DESCRIPTION)));
                        jsonObject.put(CATEGORY,
                                cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.CATEGORY)));
                        jsonObject.put(TYPE,
                                cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.TYPE)));
                        jsonObject.put(UNIQUE_ID,
                                cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.UNIQUE_ID)));


                        data.add(jsonObject);
                        cursor.moveToNext();
                    } catch (JSONException e) {
                    }
                }
                mEmptyView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                searchResultAdapter.update(data);
            }
        }
    }
}
