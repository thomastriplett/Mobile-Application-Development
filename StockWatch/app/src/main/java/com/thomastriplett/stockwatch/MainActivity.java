package com.thomastriplett.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private static final String TAG = "MainActivity";
    private List<Stock> stockList = new ArrayList<>();  // Main content is here

    private RecyclerView recyclerView; // Layout's recyclerview
    private SwipeRefreshLayout swiper; // The SwipeRefreshLayout

    private StockAdapter mAdapter; // Data to recyclerview adapter

    private DatabaseHandler databaseHandler;

    private String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        mAdapter = new StockAdapter(stockList, this);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHandler = new DatabaseHandler(this);

        swiper = (SwipeRefreshLayout) findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        boolean connection = doNetCheck();
        if (!connection) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Stock Watch Cannot Work Without A Network Connection");
            AlertDialog dialog =  builder.create();
            dialog.show();
        }
        else {
            // Get all Stocks from DB, store in a Temporary List
            databaseHandler.dumpDbToLog();
            List<String[]> tempList = databaseHandler.loadStocks();
            for (int i = 0; i<tempList.size(); i++) {
                // Download stock data from internet
                String current_symbol = tempList.get(i)[0];
                doAsync2(current_symbol);
            }
        }
    }


    @Override
    protected void onDestroy() {
        databaseHandler.shutDown();
        super.onDestroy();
    }

    private boolean doNetCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();


        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            Log.d(TAG,"You ARE Connected to the Internet!");
            return true;
        } else {
            Log.d(TAG,"You are NOT Connected to the Internet!");
            return false;
        }
    }

    private void doRefresh() {
        boolean connection = doNetCheck();
        if (!connection) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Stocks Cannot Be Updated Without A Network Connection");
            AlertDialog dialog =  builder.create();
            dialog.show();
        }
        else {
            List<Stock> tempStock = new ArrayList<Stock>();
            for (int i=0; i<stockList.size(); i++) {
                Stock currentStock = stockList.get(i);
                tempStock.add(currentStock);
            }

            stockList.clear();
            for (int j=0; j<tempStock.size(); j++) {
                String current_symbol = tempStock.get(j).getSymbol();
                doAsync2(current_symbol);
            }

        }
        swiper.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean connection = doNetCheck();
        if (!connection) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Stocks Cannot Be Added Without A Network Connection");
            AlertDialog dialog =  builder.create();
            dialog.show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Stock Selection");
            builder.setMessage("Please enter a Stock Symbol");

            // Set up the input
            final EditText input = new EditText(this);

            input.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    Log.d(TAG, "The imput is: "+m_Text);
                    if (!m_Text.equals("")) {
                        doAsync1(m_Text);
                        // use the async tasks to find the symbol
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();

            m_Text = "";

        }
        return true;
    }

    @Override
    public void onClick(View v) {

        int pos = recyclerView.getChildLayoutPosition(v);
        Stock m = stockList.get(pos);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.marketwatch.com/investing/stock/"+m.getSymbol()));
        startActivity(browserIntent);
    }

    @Override
    public boolean onLongClick(View v) {
        final int pos = recyclerView.getChildLayoutPosition(v);
        final Stock m = stockList.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseHandler.deleteStock(stockList.get(pos).getName());
                stockList.remove(m);
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do Nothing
            }
        });
        builder.setMessage("Delete Stock Symbol "+m.getSymbol()+"?");
        builder.setTitle("Delete Stock");
        AlertDialog dialog =  builder.create();
        dialog.show();
        return false;
    }

    public void doAsync1(String symbol) {
        if (AsyncTask1.running) {
            return;
        }

        AsyncTask1.running = true;

        new AsyncTask1(this).execute(symbol);
    }

    public void doAsync2(String symbol) {
        AsyncTask2.running = true;

        new AsyncTask2(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,symbol);
    }

    public void whenAsync1IsDone(ArrayList<HashMap<String,String>> stocks) {

        if(stocks.size() == 1){
            HashMap<String,String> stock = stocks.get(0);
            String symbol = stock.get("symbol");
            doAsync2(symbol);

        }
        else {
            final ArrayList<HashMap<String,String>> permaStocks = new ArrayList<HashMap<String, String>>();
            CharSequence s[] = new CharSequence[stocks.size()];
            for (int i=0; i<stocks.size(); i++) {
                HashMap<String,String> stock = stocks.get(i);
                String symbol = stock.get("symbol");
                String name = stock.get("name");
                String item = symbol+" - "+name;
                s[i] = item;
                permaStocks.add(stock);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Make a selection");
            builder.setItems(s, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on s[which]
                    HashMap<String,String> selected = permaStocks.get(which);
                    doAsync2(selected.get("symbol"));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Do Nothing
                }
            });
            builder.show();
        }
    }

    public void whenAsync2IsDone(ArrayList<HashMap<String,String>> stock) {

        HashMap<String,String> s = stock.get(0);
        String symbol = s.get("symbol");
        String company = s.get("name");
        double price = Double.parseDouble(s.get("price"));
        double price_change = Double.parseDouble(s.get("price_change"));
        double change_percentage = Double.parseDouble((s.get("change_percentage")));
        Stock newStock = new Stock(symbol, company, price, price_change, change_percentage);

        for (int i=0; i<stockList.size(); i++) {
            if (stockList.get(i).getSymbol().equals(newStock.getSymbol())) {
                return;
            }
        }
        stockList.add(newStock);
        Collections.sort(stockList);
        databaseHandler.addStock(newStock);
        mAdapter.notifyDataSetChanged();

    }
}
