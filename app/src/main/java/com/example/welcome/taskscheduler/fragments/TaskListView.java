package com.example.welcome.taskscheduler.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.welcome.taskscheduler.MainActivity;
import com.example.welcome.taskscheduler.R;
import com.example.welcome.taskscheduler.adapters.TaskListViewAdapter;
import com.example.welcome.taskscheduler.data.TaskContract;

import static com.example.welcome.taskscheduler.MainActivity.PREF_BOOLEAN;
import static com.example.welcome.taskscheduler.MainActivity.PREF_KEY;
import static com.example.welcome.taskscheduler.MainActivity.PREF_TYPE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TaskListView.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TaskListView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskListView extends Fragment
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TaskListViewAdapter mAdapter;
    private ListView mListView;
    public static final int PRODUCT_LOADER = 0;

    private OnFragmentInteractionListener mListener;

    public TaskListView() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaskListView.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskListView newInstance(String param1, String param2) {
        TaskListView fragment = new TaskListView();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_list_view, container, false);
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
        //This method will be called in the main Activity later
        setListViewListener();
        getLoaderManager().initLoader(PRODUCT_LOADER,null,this);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    // TODO: Rename method, update argument and hook method into UI event
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
                TaskContract.TaskEntry.CREATED_MONTH };

        //Where and where args specified by the spinner at the action bar
        String selection;
        String[] selectionArgs;
        SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.PREFS_CATEGORY, 0);
        boolean fromSpinner = preferences.getBoolean(PREF_BOOLEAN, false);

        if (fromSpinner) {
            String selectedType = preferences.getString(PREF_TYPE, "");
            if (selectedType.equals("All") || selectedType.equals("")) {
                selection = null;
                selectionArgs = null;
            } else {
                selection = TaskContract.TaskEntry.TYPE + "=?";
                selectionArgs = new String[]{selectedType};
            }
        }else{
            String selectedMonth = preferences.getString(PREF_KEY, "");

            if (selectedMonth.equals("")) {
                selection = null;
                selectionArgs = null;
            } else {
                selection = TaskContract.TaskEntry.CATEGORY + "=?";
                selectionArgs = new String[]{selectedMonth};
            }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void onListItemCLicked(long id);

    }

    public void setListViewListener(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i("bbbbbbb", "list view clicked");
                mListener.onListItemCLicked(id);
            }
        });
    }
}
