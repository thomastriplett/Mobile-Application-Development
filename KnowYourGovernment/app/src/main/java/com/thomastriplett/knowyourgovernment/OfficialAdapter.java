package com.thomastriplett.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Thomas on 4/2/2018.
 */

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder>{

    private static final String TAG = "OfficialAdapter";
    private List<Official> officialList;
    private MainActivity mainAct;

    public OfficialAdapter(List<Official> officialList, MainActivity ma) {
        this.officialList = officialList;
        mainAct = ma;
    }

    @Override
    public OfficialViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official_list_row, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new OfficialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OfficialViewHolder holder, int position) {
        Official official = officialList.get(position);
        holder.office.setText(official.getOffice());
        holder.name.setText(official.getName());
        holder.party.setText(official.getParty());
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
