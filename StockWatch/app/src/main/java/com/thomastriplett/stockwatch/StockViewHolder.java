package com.thomastriplett.stockwatch;

import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Thomas on 3/5/2018.
 */

public class StockViewHolder extends RecyclerView.ViewHolder {

    public TextView symbol;
    public TextView name;
    public TextView price;
    public TextView price_change;
    public TextView change_percentage;

    public StockViewHolder(View view) {
        super(view);
        symbol = (TextView) view.findViewById(R.id.symbol);
        name = (TextView) view.findViewById(R.id.name);
        price = (TextView) view.findViewById(R.id.price);
        price_change = (TextView) view.findViewById(R.id.price_change);
        change_percentage = (TextView) view.findViewById(R.id.change_percentage);
    }
}
