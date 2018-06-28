package com.example.welcome.taskscheduler.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.welcome.taskscheduler.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {
    private ArrayList<String> data;
    private Context context;

    public GridViewAdapter(ArrayList<String> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View convertView;
        if (view == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_image_gridview, viewGroup, false);
        }else{
            convertView = view;
        }
        ImageView imageView = convertView.findViewById(R.id.imageView);
        Picasso.with(context).load(data.get(position)).into(imageView);
        return view;
    }

    public void update(ArrayList<String> dataHere){
        data = dataHere;
        notifyDataSetChanged();
    }
}
