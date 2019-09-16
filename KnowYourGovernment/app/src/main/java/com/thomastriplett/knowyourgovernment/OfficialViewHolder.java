package com.thomastriplett.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Thomas on 4/2/2018.
 */

public class OfficialViewHolder extends RecyclerView.ViewHolder{

    public TextView office;
    public TextView name;
    public TextView party;

    public OfficialViewHolder(View view) {
        super(view);
        office = (TextView) view.findViewById(R.id.office_view);
        name = (TextView) view.findViewById(R.id.name_view);
        party = (TextView) view.findViewById(R.id.party_view);
    }
}
