package com.thomastriplett.stockwatch;

import android.graphics.drawable.Icon;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Thomas on 3/5/2018.
 */

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private static final String TAG = "StockAdapter";
    private List<Stock> stockList;
    private MainActivity mainAct;

    public StockAdapter(List<Stock> stockList, MainActivity ma) {
        this.stockList = stockList;
        mainAct = ma;
    }

    @Override
    public StockViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_list_row, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        holder.symbol.setText(stock.getSymbol());
        holder.name.setText(stock.getName());
        DecimalFormat df = new DecimalFormat("0.00");
        String priceString = df.format(stock.getPrice());
        holder.price.setText(priceString);
        String priceChangeString = df.format(stock.getPrice_change());
        holder.price_change.setText(priceChangeString);
        String percentString = df.format(stock.getChange_percentage()*100);
        holder.change_percentage.setText("("+percentString+"%)");
        if (stock.getPrice_change() >= 0) {
            holder.symbol.setTextColor(-6697984);
            holder.name.setTextColor(-6697984);
            holder.price.setTextColor(-6697984);
            holder.price_change.setTextColor(-6697984);
            holder.change_percentage.setTextColor(-6697984);
            holder.price_change.setText("▲"+priceChangeString);
        }
        else {
            holder.symbol.setTextColor(-3407872);
            holder.name.setTextColor(-3407872);
            holder.price.setTextColor(-3407872);
            holder.price_change.setTextColor(-3407872);
            holder.change_percentage.setTextColor(-3407872);
            holder.price_change.setText("▼"+priceChangeString);
        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
