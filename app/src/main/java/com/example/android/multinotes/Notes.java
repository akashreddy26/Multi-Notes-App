package com.example.android.multinotes;

import java.io.Serializable;
import java.util.Date;

public class Notes implements Comparable<Notes>, Serializable {

    private String title;
    private String description;
    private Date lastUpdatedTime;

    Notes()
    {}
    

    public Notes(String title, String description, Date lastUpdatedTime)
    {
        this.title = title;
        this.description = description;
        this.lastUpdatedTime = lastUpdatedTime;

    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }

    public void setDescription(String description)
    {
       this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }


    public String toString()
    {
        return "Title=" + title + ", Description=" + description + ", lastUpdatedTime=" + lastUpdatedTime;
    }

    @Override
    public int compareTo(Notes notes) {
        if (notes == null || getLastUpdatedTime() == null || notes.getLastUpdatedTime() == null) {
            return 0;
        }
        return notes.getLastUpdatedTime().compareTo(this.lastUpdatedTime);
    }

}
