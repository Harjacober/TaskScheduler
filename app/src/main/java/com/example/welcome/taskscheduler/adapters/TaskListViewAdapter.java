package com.example.welcome.taskscheduler.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.welcome.taskscheduler.R;
import com.example.welcome.taskscheduler.data.TaskContract;
import com.example.welcome.taskscheduler.utils.TaskSchedulerUtils;

public class TaskListViewAdapter extends CursorAdapter {


    public TaskListViewAdapter(Context mContext, Cursor mCursor) {
        super(mContext, mCursor,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.item_task_recycler_view, viewGroup, false);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView createdDayNum;
        TextView createdDayWord;
        TextView createdMonthYear;
        TextView createdTime;
        TextView taskDescription;
        ImageView taskImage;
        TextView categoryText;
        TextView textView;
        ImageView categoryColor = view.findViewById(R.id.radioButton);
        ImageView checkBox = view.findViewById(R.id.checkbox);

        createdDayNum = view.findViewById(R.id.createdDayNum);
        createdDayWord = view.findViewById(R.id.createdDayWord);
        createdMonthYear = view.findViewById(R.id.createdMonthYear);
        createdTime = view.findViewById(R.id.createdTime);
        taskDescription = view.findViewById(R.id.taskDescription);
        taskImage = view.findViewById(R.id.taskImage);
        textView = view.findViewById(R.id.textView);
        categoryText = view.findViewById(R.id.category);

        if (cursor == null) return;
        String dayNum = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.CREATED_DAY_NUM));
        String dayWord = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.CREATED_DAY_WORD));
        String year = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.CREATED_YEAR));
        String month = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.CREATED_MONTH));
        String time = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.CREATED_TIME));
        String desc = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.TASK_DESCRIPTION));
        String categoryHere = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.CATEGORY));
        String type = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.TYPE));
        final String uniqueId = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.UNIQUE_ID));
        TypedArray array = context.getResources().obtainTypedArray(R.array.categories);
        String pCategory = array.getString(Integer.valueOf(categoryHere));
        createdDayNum.setText(dayNum);
        createdDayWord.setText(dayWord);
        createdMonthYear.setText(month + " " + year);
        createdTime.setText(TaskSchedulerUtils.getTimeInReadableFormat(Long.valueOf(time)-3600000));
        taskDescription.setText(getScaledDesc(desc));
        categoryText.setText(pCategory);
        categoryImage(Integer.valueOf(categoryHere), categoryColor);
        taskImage.setVisibility(View.GONE);
        if (type.equals("Records")){
            checkBox.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI,
                        TaskContract.TaskEntry.UNIQUE_ID+"=?",
                        new String[]{uniqueId});
                notifyDataSetChanged();
            }
        });

    }
    public static void categoryImage(int index, ImageView imageView){
        switch (index){
            case 0:
                imageView.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
                break;
            case 1:
                imageView.setImageResource(R.drawable.ic_radio_button_checked_red_24dp);
                break;
            case 2:
                imageView.setImageResource(R.drawable.ic_radio_button_checked_yellow_24dp);
                break;
            case 3:
                imageView.setImageResource(R.drawable.ic_radio_button_checked_blue_24dp);
                break;
            case 4:
                imageView.setImageResource(R.drawable.ic_radio_button_checked_green_24dp);
                break;
        }
    }
    public String getScaledDesc(String mCaskDescription) {
        StringBuilder desc = new StringBuilder();
        if (mCaskDescription.length() > 100){
            for (int i = 0; i < 100; i++){
                desc.append(mCaskDescription.charAt(i));
            }
            return desc.toString()+"...";
        }else{
            return mCaskDescription;
        }
    }
}
