package com.thomastriplett.multinotes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Thomas on 2/11/2018.
 */

public class NoteViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView date;
    public TextView text;

    public NoteViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title_view);
        date = (TextView) itemView.findViewById(R.id.date_view);
        text = (TextView) itemView.findViewById(R.id.text_view);
    }
}
