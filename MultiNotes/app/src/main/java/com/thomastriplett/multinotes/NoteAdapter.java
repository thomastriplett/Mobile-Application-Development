package com.thomastriplett.multinotes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Thomas on 2/11/2018.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private ArrayList<Note> allNotes;
    private MainActivity mainActivity;

    public NoteAdapter(MainActivity ma, ArrayList<Note> notes) {
        mainActivity = ma;
        this.allNotes = notes;
    }
    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notelist_row, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note = allNotes.get(position);
        holder.title.setText(note.getTitle());
        holder.date.setText(note.getDate());
        if (note.getText().length() <= 80) {
            holder.text.setText(note.getText());
        }
        else {
            String s = note.getText();
            String newText = s.substring(0, 80)+"...";
            holder.text.setText(newText);
        }
    }

    @Override
    public int getItemCount() {
        return allNotes.size();
    }
}
