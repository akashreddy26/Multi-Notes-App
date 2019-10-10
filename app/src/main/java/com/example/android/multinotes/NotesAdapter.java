package com.example.android.multinotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotesAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private List<Notes> noteslist;
    private MainActivity mainActivity;

    public NotesAdapter(List<Notes> nList, MainActivity ma)
    {
        this.noteslist = nList;
        mainActivity = ma;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notes, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {

       Notes notes = noteslist.get(position);

        holder.title.setText(notes.getTitle());
        holder.time.setText(dateToString(notes.getLastUpdatedTime()));


        String str = notes.getDescription();
        if(str!=null)

            if(str.length() > 80) {
            String str1 = str.substring(0, 80);
            String res = str1 + "...";
            holder.des.setText(res);
        }

        else
            {
                holder.des.setText(notes.getDescription());
            }
        else
        {
            holder.des.setText(" ");
        }

    }

    private String dateToString(Date lastUpdatedTime) {
        try {

            if (lastUpdatedTime != null) {
                SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getDateTimeInstance();
                formatter.applyPattern("EEE MMM dd, hh:mm aaa");
                return formatter.format(lastUpdatedTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public int getItemCount() {
        return noteslist.size();
    }
}
