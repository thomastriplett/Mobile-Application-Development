package com.thomastriplett.multinotes;

import android.content.DialogInterface;
import android.content.Intent;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private EditText title;
    private EditText text;

    private String origTitle;
    private String origText;

    private static final int ADD_REQUEST_CODE = 1;

    private static final String TAG = "EditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        title = (EditText) findViewById(R.id.edit_title);

        text = (EditText) findViewById(R.id.edit_text);

        Intent intent = getIntent();
        if (intent.hasExtra("currentNote")) {
            Note currentNote = intent.getParcelableExtra("currentNote");
            title.setText(currentNote.getTitle());
            Log.d(TAG, title.getText().toString());
            origTitle = currentNote.getTitle();
            text.setText(currentNote.getText());
            origText = currentNote.getText();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save_item:
                Log.d(TAG, "You want to save a note");
                if (!title.getText().toString().equals("") && (!title.getText().toString().equals(origTitle) && !text.getText().toString().equals(origText))) {
                    SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yy hh:mm a");
                    Date newDate = new Date();
                    String date = sd.format(newDate);
                    Note newNote = new Note(title.getText().toString(), date, text.getText().toString());
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    intent.putExtra("newNote", newNote);
                    setResult(RESULT_OK, intent);
                    startActivityForResult(intent, ADD_REQUEST_CODE);
                } else if (title.getText().toString().equals(origTitle) && text.getText().toString().equals(origText)) {
                    Log.d(TAG, "The Note was not changed");
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    setResult(RESULT_CANCELED, intent);
                    startActivityForResult(intent, ADD_REQUEST_CODE);
                } else if (title.getText().toString().equals(origTitle)) {
                    SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yy hh:mm a");
                    Date newDate = new Date();
                    String date = sd.format(newDate);
                    Note newNote = new Note(title.getText().toString(), date, text.getText().toString());
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    intent.putExtra("newNote", newNote);
                    setResult(RESULT_OK, intent);
                    startActivityForResult(intent, ADD_REQUEST_CODE);
                } else {
                    Log.d(TAG, "The Note had no title");
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    setResult(RESULT_CANCELED, intent);
                    Toast.makeText(this, "Untitled Note Not Saved", Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent, ADD_REQUEST_CODE);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (title.getText().toString().equals(origTitle) && text.getText().toString().equals(origText)) {
            Intent intent = new Intent(EditActivity.this, MainActivity.class);
            setResult(RESULT_CANCELED, intent);
            startActivityForResult(intent, ADD_REQUEST_CODE);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yy hh:mm a");
                    Date newDate = new Date();
                    String date = sd.format(newDate);
                    Note newNote = new Note(title.getText().toString(), date, text.getText().toString());
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    intent.putExtra("newNote", newNote);
                    setResult(RESULT_OK, intent);
                    startActivityForResult(intent, ADD_REQUEST_CODE);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    setResult(RESULT_CANCELED, intent);
                    startActivityForResult(intent, ADD_REQUEST_CODE);
                }
            });

            builder.setMessage("Save Note?");
            builder.setTitle("Exit Activity");
            AlertDialog dialog =  builder.create();
            dialog.show();
        }
    }
}
