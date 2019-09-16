package com.thomastriplett.multinotes;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.JsonWriter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";

    private static final int ADD_REQUEST_CODE = 1;

    private ArrayList<Note> allNotes = new ArrayList<>();

    private RecyclerView recyclerView;

    private NoteAdapter nAdapter;

    //private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        nAdapter = new NoteAdapter(this, allNotes);

        recyclerView.setAdapter(nAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //textView = (TextView) findViewById(R.id.text_view);
        doAsync();
        Intent intent = getIntent();
        if (intent.hasExtra("newNote")) {
            Note newNote = (Note) intent.getParcelableExtra("newNote");
            allNotes.add(newNote);
        }
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_item:
                Log.d(TAG, "You want to add a note");
                Intent editIntent = new Intent(MainActivity.this, EditActivity.class);
                startActivityForResult(editIntent, ADD_REQUEST_CODE);
                return true;
            case R.id.info_item:
                Log.d(TAG, "You want app info");
                Intent infoIntent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(infoIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_REQUEST_CODE) {
            Log.d(TAG, "It's kind of working");
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "The data was successfully sent");
                Note newNote = (Note) data.getParcelableExtra("New Note");
                String newTitle = newNote.getTitle();
                boolean flag = false;
                for (int i = 0; i<allNotes.size(); i++) {
                    Note currentNote = allNotes.get(i);
                    if (currentNote.getTitle().equals(newTitle)) {
                        int loc = allNotes.indexOf(currentNote);
                        allNotes.set(loc, newNote);
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    allNotes.add(newNote);
                }
                nAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View v) {

        int pos = recyclerView.getChildLayoutPosition(v);
        Note n = allNotes.get(pos);
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("currentNote", n);
        setResult(RESULT_OK, intent);
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        final Note n = allNotes.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                allNotes.remove(n);
                nAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do Nothing
            }
        });

        builder.setMessage("Delete Note?");
        builder.setTitle("Note Deletion");
        AlertDialog dialog =  builder.create();
        dialog.show();
        return false;
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "Multi Notes Pausing");
        saveNote();
        super.onPause();
    }


    public void doAsync() {
        if (FileAsyncTask.running) {
            return;
        }

        FileAsyncTask.running = true;
        long delay = 0;


        new FileAsyncTask(this).execute(delay);
    }

    public void whenAsyncIsDone(ArrayList<Note> notes) {
        //textView.setText(textView.getText().toString() + "\n" + string.toString() + " END")
        if (notes.size() != 0) {
            //allNotes.addAll(notes);
            for(int i = 0; i<notes.size(); i++) {
                Note currentNote = notes.get(i);
                if (!allNotes.contains(currentNote)) {
                    allNotes.add(currentNote);
                }
            }
        }
        nAdapter.notifyDataSetChanged();
        Log.d(TAG, String.valueOf(allNotes.size()));
        Log.d(TAG, "AsyncTask Complete");
    }

    private void saveNote() {

        Log.d(TAG, "saveNote: Saving JSON File");
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
            writer.setIndent("  ");
            writer.beginObject();

            for (int i = 0; i<allNotes.size(); i++) {
                Note currentNote = allNotes.get(i);
                writer.name("title").value(currentNote.getTitle());
                writer.name("date").value(currentNote.getDate());
                writer.name("text").value(currentNote.getText());
            }
            writer.endObject();
            writer.close();

        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}