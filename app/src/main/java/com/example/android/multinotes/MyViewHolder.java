package com.example.android.multinotes;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView des;
    public TextView time;


    public MyViewHolder(View view)
    {
        super(view);
        title = view.findViewById(R.id.title);
        time = view.findViewById(R.id.time);
        des = view.findViewById(R.id.des);

    }
}
