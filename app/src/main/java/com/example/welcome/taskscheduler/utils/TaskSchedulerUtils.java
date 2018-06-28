package com.example.welcome.taskscheduler.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.format.DateUtils;
import android.widget.ImageView;

import com.example.welcome.taskscheduler.data.TaskContract;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

import static android.text.format.DateUtils.DAY_IN_MILLIS;

public class TaskSchedulerUtils {
    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static String getTimeInReadableFormat(long timeInMillis){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        return sdf.format(timeInMillis);
    }

    public static String getMonthName(int month){
        switch (month){
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "Aug";
            case 9:
                return "Sept";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return "Invalid Month";
        }

    }
    public static long getDayNumber(long date) {
        TimeZone tz = TimeZone.getDefault();
        long gmtOffset = tz.getOffset(date);
        return (date + gmtOffset) / DAY_IN_MILLIS;
    }
    public static String getDayName(Context context, long dateInMillis) {
        /*
         * If the date is today, return the localized version of "Today" instead of the actual
         * day name.
         */
        long dayNumber = getDayNumber(dateInMillis);
        long currentDayNumber = getDayNumber(System.currentTimeMillis());
        if (dayNumber == currentDayNumber) {
            // return context.getString(R.string.today);
            return "Today";
        } else if (dayNumber == currentDayNumber + 1) {
            //return context.getString(R.string.tomorrow);
            return "Tomorrow";
        } else {
            /*
             * Otherwise, if the day is not today, the format is just the day of the week
             * (e.g "Wednesday")
             */
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }

    public static String getReadableDateString(Context context, long timeInMillis) {
        int flags = DateUtils.FORMAT_SHOW_DATE
                | DateUtils.FORMAT_NO_YEAR
                | DateUtils.FORMAT_SHOW_WEEKDAY;

        return DateUtils.formatDateTime(context, timeInMillis, flags);
    }
    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public static Cursor search(String query, Context context){
        query = "%" + query + "%";
        String selection = TaskContract.TaskEntry.TASK_DESCRIPTION + " LIKE ?";
        String[] selectionArgs = new String[] {query};
        Cursor cursor;
        cursor = context.getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        return cursor;
    }

    public static Bitmap scaledBitmap( String mCurrentPhotoPath) {
        // Get the dimensions of the View

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/120, photoH/100);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        return bitmap;
    }


}
