package com.example.welcome.taskscheduler;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.welcome.taskscheduler.fragments.TaskCalendarView;
import com.example.welcome.taskscheduler.fragments.TaskListView;
import com.example.welcome.taskscheduler.fragments.TaskMoreItemView;

public class MainActivity extends AppCompatActivity
        implements TaskListView.OnFragmentInteractionListener
        , TaskCalendarView.OnFragmentInteractionListener
        , TaskMoreItemView.OnFragmentInteractionListener, AdapterView.OnItemSelectedListener {

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private static final int PAGES_NUM=3;
    public static final String PREFS_CATEGORY = "prefs-category";
    public static final String PREF_KEY = "prefs-key";
    public static final String PREF_TYPE = "prefs-type";
    public static final String PREF_BOOLEAN = "prefs-boolean";
    private boolean fromSpinner = true;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setElevation(0);
        PagerTabStrip mPagerTabStrip= findViewById(R.id.pager_tab_strip);
        mPagerTabStrip.setTabIndicatorColor(Color.parseColor("#ffffff"));
        mViewPager= findViewById(R.id.pager);
        mPagerAdapter=new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        sharedPreferences = getSharedPreferences(PREFS_CATEGORY, 0);
        editor = sharedPreferences.edit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        //Set up Spinner
        MenuItem item = menu.findItem(R.id.spinner);
        final Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ((TextView) spinner.getSelectedView()).setTextColor(Color.WHITE);
            }
        });
        spinner.setOnItemSelectedListener(this);

        //Search View
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)
                menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search){
            //Start search dialog
            super.onSearchRequested();
        }else if(id == R.id.settings){

        }else if (id == R.id.sortBy){
            sortByDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sortByDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this,
                        R.style.DialogTheme));
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dia_sort_by_category, null);
        final RadioButton uncategorized = view.findViewById(R.id.uncategorized);
        final RadioButton work = view.findViewById(R.id.work);
        final RadioButton personal = view.findViewById(R.id.personal);
        final RadioButton familyAffair = view.findViewById(R.id.familyAffair);
        final RadioButton study = view.findViewById(R.id.study);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        builder.setView(view).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        final AlertDialog dialog = builder.create();
//            final TypedArray array = getResources().obtainTypedArray(R.array.categories);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (uncategorized.isChecked()){
                    editor.putString(PREF_KEY, "0");
                    editor.commit();
                }else if (work.isChecked()){
                    editor.putString(PREF_KEY, "1");
                    editor.commit();
                }else if (personal.isChecked()){
                    editor.putString(PREF_KEY, "2");
                    editor.commit();
                }else if (familyAffair.isChecked()){
                    editor.putString(PREF_KEY, "3");
                    editor.commit();
                }else if (study.isChecked()){
                    editor.putString(PREF_KEY, "4");
                    editor.commit();
                }
                String category = sharedPreferences.getString(PREFS_CATEGORY, null);
                mPagerAdapter.notifyDataSetChanged();
                fromSpinner = false;
                editor.putBoolean(PREF_BOOLEAN, fromSpinner);
                dialog.dismiss();
            }
        });
        String category = sharedPreferences.getString(PREF_KEY, "");
        if (category.equals("0")){
            uncategorized.setChecked(true);
        }else if (category.equals("1")){
            work.setChecked(true);
        }else if (category.equals("2")){
            personal.setChecked(true);
        }else if (category.equals("3")){
            familyAffair.setChecked(true);
        } else{
            study.setChecked(true);
        }
        dialog.show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Intent intent = new Intent(this, AddNewTask.class);
        startActivity(intent);

    }

    @Override
    public void onListItemCLicked(long id) {
        String uId=String.valueOf(id);
        //Uri currentWorkerUri= ContentUris.withAppendedId(MedicationContract.MedicationEntry.CONTENT_URI, id);
        Intent intent=new Intent(this, TaskDetails.class);
        //intent.setData(currentWorkerUri);
        intent.putExtra(Intent.EXTRA_TEXT, uId);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        fromSpinner = true;
        String typeSelected = String.valueOf(adapterView.getItemAtPosition(position));
        editor.putString(PREF_TYPE, typeSelected);
        editor.putBoolean(PREF_BOOLEAN, fromSpinner);
        editor.commit();
        mPagerAdapter.notifyDataSetChanged();
        /*TaskListView fragment = (TaskListView) getSupportFragmentManager().getFragments().get(2);
        getSupportFragmentManager().beginTransaction()
                .detach(fragment)
                .attach(fragment)
                .commit();*/

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter{

        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0: return TaskListView.newInstance(null, null);
                case 1: return TaskCalendarView.newInstance(null, null);
                case 2: return TaskMoreItemView.newInstance(null, null);
                default: return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: return Html.fromHtml("<font color=#ffffff>HOME</font>.");
                case 1: return Html.fromHtml("<font color=#ffffff>CALENDAR VIEW</font>.");
                case 2: return Html.fromHtml("<font color=#ffffff>MORE</font>.");
                default:return null;
            }
        }

        @Override
        public int getCount() {
            return PAGES_NUM;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
