package com.example.welcome.taskscheduler.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;

import com.example.welcome.taskscheduler.R;
import com.example.welcome.taskscheduler.adapters.TaskListViewAdapter;
import com.example.welcome.taskscheduler.data.TaskContract;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TaskCalendarView extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{
    private static final int PRODUCT_LOADER = 0;
    private TaskListViewAdapter mAdapter;
    private ListView mListView;

    private OnFragmentInteractionListener mListener;
    private CalendarView calendarView;
    private String mUtcDateWithOutTime = null;

    public TaskCalendarView() {
        // Required empty public constructor
    }

    public static TaskCalendarView newInstance() {
        TaskCalendarView fragment = new TaskCalendarView();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_calendar_view, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed(null);
            }
        });
        mAdapter = new TaskListViewAdapter(getContext(), null);
        mListView = view.findViewById(R.id.listview);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(view.findViewById(R.id.empty_view));
        calendarView = view.findViewById(R.id.calendarView);
        getLoaderManager().initLoader(PRODUCT_LOADER,null,this);
        setCalendarViewListener();
        return view;

    }

    private void setCalendarViewListener(){
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                Calendar calendar = new GregorianCalendar(year, month, day);
                mUtcDateWithOutTime = String.valueOf(calendar.getTimeInMillis());
                restartLoader();
            }
        });
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection={TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.CREATED_DAY_NUM,
                TaskContract.TaskEntry.CREATED_DAY_WORD,
                TaskContract.TaskEntry.CREATED_TIME,
                TaskContract.TaskEntry.CREATED_YEAR,
                TaskContract.TaskEntry.CATEGORY,
                TaskContract.TaskEntry.TASK_DESCRIPTION,
                TaskContract.TaskEntry.IMAGE_DATA,
                TaskContract.TaskEntry.UNIQUE_ID,
                TaskContract.TaskEntry.TYPE,
                TaskContract.TaskEntry.TASK_TIME_WITH_DATE,
                TaskContract.TaskEntry.TASK_TIME_WITHOUT_DATE,
                TaskContract.TaskEntry.CREATED_MONTH };
        String selection;
        String[] selectionArgs;
        if (mUtcDateWithOutTime != null) {
            selection = TaskContract.TaskEntry.TASK_TIME_WITHOUT_DATE + "=?";
            selectionArgs = new String[]{mUtcDateWithOutTime};
        }else {
            selection = null;
            selectionArgs = null;
        }
        return new CursorLoader(getContext(),
                TaskContract.TaskEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void restartLoader() {
        getLoaderManager().restartLoader(PRODUCT_LOADER,null,this);
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
