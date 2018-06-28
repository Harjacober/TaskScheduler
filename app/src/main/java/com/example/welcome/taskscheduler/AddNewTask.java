package com.example.welcome.taskscheduler;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.example.welcome.taskscheduler.adapters.GridViewAdapter;
import com.example.welcome.taskscheduler.data.TaskContract;
import com.example.welcome.taskscheduler.reminder.SetTimeTableReminderJob;
import com.example.welcome.taskscheduler.utils.DemoJobCreator;
import com.example.welcome.taskscheduler.utils.TaskSchedulerUtils;
import com.example.welcome.taskscheduler.utils.showNotificationJob;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddNewTask extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView createdDayNum;
    private TextView createdDayWord;
    private TextView createdMonth;
    private TextView createdYear;
    private TextView createdTime;
    private EditText taskDescription;
    private ImageView taskImage;
    private String mCreatedDayNum;
    private String mCreatedDayWord;
    private String mCreatedMonth;
    private String mCreatedYear;
    private String mCreatedHour;
    private String mCreatedMinute;
    private String mCreatedTime;
    private String mCaskDescription;
    private String mCtaskImage;
    private String mCategory;
    private long mTimeFromPickerInMillis = 0;
    private int year, month, day;
    private long mUtcDate;
    private long mUtcDateWthoutTime;
    private long interval;
    private static final int DIALOG_DATE = 999;
    private static final String SCHEDULE = "Schedules";
    private static final String RECORD = "Records";
    private static final String TIMETABLE = "TimeTable";
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GALLERY_PHOTO = 6;
    GridViewAdapter gridViewAdapter;
    GridView gridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);


        JobManager.create(this).addJobCreator(new DemoJobCreator());
        setUpSpinner();
        createdDayNum = findViewById(R.id.createdDayNum);
        createdDayWord = findViewById(R.id.createdDayWord);
        createdMonth = findViewById(R.id.createdMonth);
        createdYear = findViewById(R.id.createdYear);
        createdTime = findViewById(R.id.createdTime);
        taskDescription = findViewById(R.id.taskDescription);
        taskImage = findViewById(R.id.taskImage);
        setDefaultDataInVIews();
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setUpSpinner() {
        TypedArray array = getResources().obtainTypedArray(R.array.categories);
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner.setAdapter(arrayAdapter);
        spinner.setPrompt(array.getString(0));
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_task, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addPictures){
            showImageDialogOption();

        }else if (id == R.id.voice){

        }else if (id == R.id.chooseDate){
            showDialog(DIALOG_DATE);

        }else if (id == R.id.chooseTime){
            showTimePickerDialog();
        }else if (id == R.id.discard){
            finish();
        }else if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }else if (id == R.id.save){
            dialogSchedulerOrRecord();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showImageDialogOption() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_gallery_camera, null);
        RelativeLayout camera = view.findViewById(R.id.camera);
        RelativeLayout gallery = view.findViewById(R.id.gallery);
        gridView = view.findViewById(R.id.picturesGridView);
        gridViewAdapter = new GridViewAdapter(mCurrentPhotoPath, this);
        camera.setOnClickListener(onCameraClickedListener);
        gallery.setOnClickListener(onGalleryClickedListener);
        builder.setView(view).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Launch Gallery
    private View.OnClickListener onGalleryClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, REQUEST_GALLERY_PHOTO);
        }
    };

    //Launch Camera App
    private View.OnClickListener onCameraClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dispatchTakePictureIntent();
        }
    };



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.welcome.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    ArrayList<String> mCurrentPhotoPath = new ArrayList<>();

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath.add("file:" + image.getAbsolutePath());
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Log.i("aaaaaa", String.valueOf(mCurrentPhotoPath));
            gridViewAdapter.update(mCurrentPhotoPath);
            gridView.setAdapter(gridViewAdapter);
        }else if (requestCode == REQUEST_GALLERY_PHOTO && resultCode == RESULT_OK){
            Uri uri = data.getData();
            mCurrentPhotoPath.add(uri.getPath());
            gridView.setAdapter(gridViewAdapter);
        }
    }




    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        mCategory = String.valueOf(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void saveTaskDataToDatabase(String type){
        mCaskDescription = taskDescription.getText().toString();
        mCtaskImage = "image file location";
        String uniqueId = TaskSchedulerUtils.generateUniqueId();
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.CREATED_DAY_NUM, mCreatedDayNum);
        values.put(TaskContract.TaskEntry.CREATED_DAY_WORD, mCreatedDayWord);
        values.put(TaskContract.TaskEntry.CREATED_MONTH, mCreatedMonth);
        values.put(TaskContract.TaskEntry.CREATED_YEAR, mCreatedYear);
        values.put(TaskContract.TaskEntry.CREATED_TIME, mTimeFromPickerInMillis);
        values.put(TaskContract.TaskEntry.TASK_DESCRIPTION, mCaskDescription);
        values.put(TaskContract.TaskEntry.IMAGE_DATA, mCtaskImage);
        values.put(TaskContract.TaskEntry.CATEGORY, mCategory);
        values.put(TaskContract.TaskEntry.UNIQUE_ID, uniqueId);
        values.put(TaskContract.TaskEntry.TASK_TIME_WITH_DATE, mUtcDate);
        values.put(TaskContract.TaskEntry.TASK_TIME_WITHOUT_DATE, mUtcDateWthoutTime);
        if (type.equals(SCHEDULE)) {
            //schedule reminder if it is a task
            values.put(TaskContract.TaskEntry.TYPE, SCHEDULE);
            getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, values);
            long duration = (mUtcDate) - System.currentTimeMillis();
            PersistableBundleCompat bundleCompat = new PersistableBundleCompat();
            bundleCompat.putString("unique-id", uniqueId);
            bundleCompat.putString("task-name", getScaledDesc(mCaskDescription));
            showNotificationJob.scheduleExactJob(duration, bundleCompat);
        }else if (type.equals(RECORD)){
            //just save without scheduling otherwise
            values.put(TaskContract.TaskEntry.TYPE, RECORD);
            getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, values);
            Log.i("eeeerecordclciked", String.valueOf(mUtcDate));
            Log.i("currenttime", String.valueOf(System.currentTimeMillis()));
            long duration = (mUtcDate) - System.currentTimeMillis();
            Log.i("diff", String.valueOf(duration));
        }else if (type.equals(TIMETABLE)){
            values.put(TaskContract.TaskEntry.TYPE, TIMETABLE);
            getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, values);
            long duration = (mUtcDate) - System.currentTimeMillis();
            Log.i("eeeetimetableclciked", String.valueOf(mUtcDate));
            PersistableBundleCompat bundleCompat = new PersistableBundleCompat();
            bundleCompat.putString("unique-id", uniqueId);
            bundleCompat.putString("task-name", getScaledDesc(mCaskDescription));
            bundleCompat.putLong("interval", interval);
            SetTimeTableReminderJob.scheduleExactJob(duration, bundleCompat);
        }
        finish();

    }

    public String getScaledDesc(String mCaskDescription) {
        StringBuilder desc = new StringBuilder();
        if (mCaskDescription.length() > 20){
            for (int i = 0; i < 20; i++){
                desc.append(mCaskDescription.charAt(i));
            }
        }
        return desc.toString();
    }

    public void OnDateViewClicked(View view) {
        showDialog(DIALOG_DATE);
    }

    private DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            //Process the date set by the user in order to add to database
            Calendar calendar = new GregorianCalendar(year, month, day);
            mUtcDateWthoutTime = calendar.getTimeInMillis();
            mUtcDate = calendar.getTimeInMillis() + mTimeFromPickerInMillis;
            Log.i("eeeedateset", String.valueOf(mUtcDate));
            //update all views
            displayDateInViews(year, month+1, day);
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE){
            return new DatePickerDialog(this,
                    R.style.DialogTheme,
                    startDateListener, year, month, day);
        }
        return super.onCreateDialog(id);
    }

    private void displayDateInViews(int year, int month, int day) {
        mCreatedDayWord = TaskSchedulerUtils.getDayName(this, mUtcDate);
        mCreatedDayNum = String.valueOf(day);
        mCreatedMonth = TaskSchedulerUtils.getMonthName(month);
        mCreatedYear = String.valueOf(year);
        createdDayNum.setText(mCreatedDayNum);
        createdMonth.setText(mCreatedMonth);
        createdYear.setText(mCreatedYear);
        createdDayWord.setText(mCreatedDayWord);
    }

    public void OnTimeViewClicked(View view) {
        showTimePickerDialog();
    }

    private void showTimePickerDialog() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog timepicker = new TimePickerDialog(new ContextThemeWrapper(
                this, R.style.DialogTheme),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourHere, int minuteHere) {
                        mCreatedTime = TaskSchedulerUtils.getTimeInReadableFormat(mTimeFromPickerInMillis-3600000);
                        createdTime.setText(mCreatedTime);
                        long date = mUtcDate - mTimeFromPickerInMillis;
                        mTimeFromPickerInMillis = (((hourHere * 60) + minuteHere) * 60) * 1000;
                        mUtcDate = date + mTimeFromPickerInMillis;
                        Log.i("eeeetimeset", String.valueOf(mUtcDate));
                    }
                }, hour, minute, true);
        timepicker.setTitle("Schedule Task Time");
        timepicker.show();
    }

    public void setDefaultDataInVIews(){
        Calendar currentTime = Calendar.getInstance();
        int year = currentTime.get(Calendar.YEAR);
        int month = currentTime.get(Calendar.MONTH);
        int dayNum = currentTime.get(Calendar.DAY_OF_MONTH);
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        Calendar calendar = new GregorianCalendar(year, month, dayNum);
        mUtcDate = calendar.getTimeInMillis() + mTimeFromPickerInMillis;
        mUtcDateWthoutTime = calendar.getTimeInMillis();
        Log.i("eeeedefault", String.valueOf(mUtcDate));
        mTimeFromPickerInMillis = (((hour * 60) + minute) * 60) * 1000;
        mCreatedDayNum = String.valueOf(dayNum);
        mCreatedDayWord = TaskSchedulerUtils.getDayName(this, mUtcDate);
        mCreatedMonth = TaskSchedulerUtils.getMonthName(month + 1);
        mCreatedYear = String.valueOf(year);
        mCreatedHour = String.valueOf(hour);
        mCreatedMinute = String.valueOf(minute);
        mCreatedTime = TaskSchedulerUtils.getTimeInReadableFormat(mTimeFromPickerInMillis-3600000);
        mCategory = "0";

        createdYear.setText(mCreatedYear);
        createdMonth.setText(mCreatedMonth);
        createdDayNum.setText(mCreatedDayNum);
        createdDayWord.setText(mCreatedDayWord);
        createdTime.setText(mCreatedTime);
    }

    public void dialogSchedulerOrRecord(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Is this a schedule(you get reminded when task is due) or a record?")
                .setPositiveButton("SCHEDULE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveTaskDataToDatabase(SCHEDULE);
                    }
                }).setNegativeButton("RECORD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveTaskDataToDatabase(RECORD);
            }
        }).setNeutralButton("TIME TABLE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                createIntervalDialog();
//                dialog.dismiss();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createIntervalDialog() {
       AlertDialog.Builder builder = new AlertDialog.Builder(this);
       View view = getLayoutInflater().inflate(R.layout.dialog_interval, null);
       builder.setView(view);
       final EditText editText = view.findViewById(R.id.editText);
       RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
       final RadioButton oneDay = view.findViewById(R.id.oneDay);
       final RadioButton threeDays = view.findViewById(R.id.threeDays);
       final RadioButton oneWeek = view.findViewById(R.id.oneWeek);
       final RadioButton twoWeeks = view.findViewById(R.id.twoWeeks);
       final RadioButton threeWeeks = view.findViewById(R.id.threeWeeks);
       final RadioButton fourWeeks = view.findViewById(R.id.fourWeeks);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (oneDay.isChecked()){
                    editText.setText("24");
                }else if (threeDays.isChecked()){
                    editText.setText("72");
                }else if (oneWeek.isChecked()){
                    editText.setText("168");
                }else if (twoWeeks.isChecked()){
                    editText.setText("336");
                }else if (threeWeeks.isChecked()){
                    editText.setText("504");
                }else if (fourWeeks.isChecked()){
                    editText.setText("672");
                }
            }
        });

       builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               interval = Long.valueOf(editText.getText().toString());
               saveTaskDataToDatabase(TIMETABLE);
           }
       }).create().show();
    }
}
