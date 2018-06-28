package com.example.welcome.taskscheduler.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.welcome.taskscheduler.R;
import com.example.welcome.taskscheduler.data.SearchResult;
import com.example.welcome.taskscheduler.data.TaskContract;
import com.example.welcome.taskscheduler.utils.TaskSchedulerUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.welcome.taskscheduler.adapters.TaskListViewAdapter.categoryImage;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.DataObjectHolder> {

    public interface ListItemCLickListener{
        void onItemCLickListener(String clickedItemUniqueId);
    }
    private ListItemCLickListener mlistener;
    private ArrayList<JSONObject> data;
    private Context mContext;

    public SearchResultAdapter(ListItemCLickListener mlistener, ArrayList<JSONObject> data, Context mContext) {
        this.mlistener = mlistener;
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public SearchResultAdapter.DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_task_recycler_view, parent, false);
        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchResultAdapter.DataObjectHolder holder, int position) {
        try {
            holder.bind(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView createdDayNum;
        TextView createdDayWord;
        TextView createdMonthYear;
        TextView createdTime;
        TextView taskDescription;
        ImageView taskImage;
        TextView categoryText;
        TextView textView;
        ImageView categoryColor;
        ImageView checkBox;
        public DataObjectHolder(View itemView) {
            super(itemView);
            createdDayNum = itemView.findViewById(R.id.createdDayNum);
            createdDayWord = itemView.findViewById(R.id.createdDayWord);
            createdMonthYear = itemView.findViewById(R.id.createdMonthYear);
            createdTime = itemView.findViewById(R.id.createdTime);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            taskImage = itemView.findViewById(R.id.taskImage);
            textView = itemView.findViewById(R.id.textView);
            categoryText = itemView.findViewById(R.id.category);
            categoryColor = itemView.findViewById(R.id.radioButton);
            checkBox = itemView.findViewById(R.id.checkbox);
        }

        public void bind(int position) throws JSONException{
            String dayNum = data.get(position).getString(SearchResult.CREATED_DAY_NUM);
            String dayWord = data.get(position).getString(SearchResult.CREATED_DAY_WORD);
            String year = data.get(position).getString(SearchResult.CREATED_YEAR);
            String month =data.get(position).getString(SearchResult.CREATED_MONTH);
            String time = data.get(position).getString(SearchResult.CREATED_TIME);
            String desc = data.get(position).getString(SearchResult.TASK_DESCRIPTION);
            String categoryHere = data.get(position).getString(SearchResult.CATEGORY);
            String type = data.get(position).getString(SearchResult.TYPE);
            final String uniqueId =data.get(position).getString(SearchResult.UNIQUE_ID);
            TypedArray array = mContext.getResources().obtainTypedArray(R.array.categories);
            String pCategory = array.getString(Integer.valueOf(categoryHere));
            createdDayNum.setText(dayNum);
            createdDayWord.setText(dayWord);
            createdMonthYear.setText(month + " " + year);
            createdTime.setText(TaskSchedulerUtils.getTimeInReadableFormat(Long.valueOf(time)-3600000));
            taskDescription.setText(desc);
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
                    mContext.getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI,
                            TaskContract.TaskEntry.UNIQUE_ID+"=?",
                            new String[]{uniqueId});
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void update(ArrayList<JSONObject> mdata) {
        data = mdata;
        notifyDataSetChanged();
    }
}
