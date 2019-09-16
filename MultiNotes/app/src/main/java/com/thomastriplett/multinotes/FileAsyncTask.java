package com.thomastriplett.multinotes;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Thomas on 2/12/2018.
 */

public class FileAsyncTask extends AsyncTask<Long, Void, ArrayList<Note>> {

    private static final String TAG = "FileAsyncTask";
    private MainActivity mainActivity;
    public static boolean running = false;

    public FileAsyncTask(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected ArrayList<Note> doInBackground(Long... params) {
        Log.d(TAG, "doInBackground: Starting background execution");

        Log.d(TAG, "loadFile: Loading JSON File");
        ArrayList<Note> allNotes = new ArrayList<>();
        try {
            InputStream is = mainActivity.getApplicationContext().openFileInput("Note.json");
            JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
            Note currentNote = new Note();

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("title")) {
                    currentNote.setTitle(reader.nextString());
                } else if (name.equals("date")) {
                    currentNote.setDate(reader.nextString());
                } else if (name.equals("text")) {
                    currentNote.setText(reader.nextString());
                    allNotes.add(currentNote);
                    currentNote = new Note();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();

        } catch (FileNotFoundException e) {
            Log.d(TAG, "No File present!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "doInBackground: Done - returning arrayList");
        return allNotes;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        Log.d(TAG, "onPreExecute: ");
    }

    @Override
    protected void onPostExecute(ArrayList<Note> notes) {

        mainActivity.whenAsyncIsDone(notes);

        running = false;
        Log.d(TAG, "onPostExecute: AsyncTask terminating successfully");
    }
}
