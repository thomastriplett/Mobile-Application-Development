package com.thomastriplett.multinotes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Thomas on 2/10/2018.
 */

public class Note implements Parcelable {
    private String title;
    private String date;
    private String text;

    public Note() {
        title = "title";
        date = "today";
        text = "this is text";
    }

    public Note(String t, String d, String txt) {
        title = t;
        date = d;
        text = txt;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(date);
        parcel.writeString(text);
    }

    private Note(Parcel in){
        this.title = in.readString();
        this.date = in.readString();
        this.text = in.readString();
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int i) {
            return new Note[i];
        }

    };
}
