package com.thomastriplett.quicknotes;

import android.os.Bundle;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;

import android.util.JsonReader;
import android.util.JsonWriter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private int count = 0;
    private TextView lastUpdate;
    private EditText userText;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "MainActivity");

        lastUpdate = (TextView) findViewById(R.id.textView2);
        userText = (EditText) findViewById(R.id.editText2);

        String time = new SimpleDateFormat("MM/dd/yy hh:mm:ss a").format(new Date());
        lastUpdate.setText((CharSequence)time);
        userText.setMovementMethod(new ScrollingMovementMethod());
        userText.setTextIsSelectable(true);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "Quick Notes Resuming");
        note = loadFile();  // Load the JSON containing the product data - if it exists
        if (note != null) { // null means no file was loaded
            lastUpdate.setText(note.getLastUpdate());
            Log.d(TAG, lastUpdate.getText().toString());
            userText.setText(note.getUserText());
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "Quick Notes Pausing");
        if (note == null) {
            note = new Note();
        }
        note.setLastUpdate(lastUpdate.getText().toString());
        note.setUserText(userText.getText().toString());
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Quick Notes Stopping");
        saveNote();
        super.onStop();
    }

    private Note loadFile() {

        Log.d(TAG, "loadFile: Loading JSON File");
        note = new Note();
        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.file_name));
            JsonReader reader = new JsonReader(new InputStreamReader(is, getString(R.string.encoding)));

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("lastUpdate")) {
                    note.setLastUpdate(reader.nextString());
                } else if (name.equals("userText")) {
                    note.setUserText(reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();

        } catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
            note = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return note;
    }

    private void saveNote() {

        Log.d(TAG, "saveNote: Saving JSON File");
        try {
            String time = new SimpleDateFormat("MM/dd/yy hh:mm:ss a").format(new Date());
            lastUpdate.setText((CharSequence) time);
            note.setLastUpdate(lastUpdate.getText().toString());
            Log.d(TAG, lastUpdate.getText().toString());

            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("lastUpdate").value(note.getLastUpdate());
            writer.name("userText").value(note.getUserText());
            writer.endObject();
            writer.close();

            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
