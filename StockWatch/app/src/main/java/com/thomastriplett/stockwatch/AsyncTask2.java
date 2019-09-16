package com.thomastriplett.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Thomas on 3/5/2018.
 */

// This Async Task downloads the data for a given symbol and parses through it to create a new stock object

public class AsyncTask2 extends AsyncTask<String, Void, ArrayList<HashMap<String,String>>> {

    private static final String TAG = "AsyncTask2";
    private MainActivity mainActivity;
    public static boolean running = false;

    public AsyncTask2(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected ArrayList<HashMap<String,String>> doInBackground(String... strings) {

        String stockURL = "https://api.iextrading.com/1.0/stock";
        Uri.Builder buildURL = Uri.parse(stockURL).buildUpon();
        buildURL.appendPath(strings[0]);
        buildURL.appendPath("/quote");
        String urlToUse = buildURL.build().toString();
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        ArrayList<HashMap<String,String>> stocks = ParseJSON(sb.toString());
        return stocks;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String,String>> stock) {

        if (stock == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
            builder.setTitle("No Stock Found");
            builder.setMessage("There Was No Stock Found With That Symbol");
            AlertDialog dialog =  builder.create();
            dialog.show();
        }
        else {
            mainActivity.whenAsync2IsDone(stock);
        }

        running = false;
        Log.d(TAG, "onPostExecute: AsyncTask2 terminating successfully");
    }

    private ArrayList<HashMap<String, String>> ParseJSON(String json) {
        if (json != null) {
            try {
                // Hashmap for ListView
                ArrayList<HashMap<String, String>> stockList = new ArrayList<HashMap<String, String>>();
                JSONObject jsonObj = new JSONObject(json);
                // Getting JSON Array node
                //JSONObject resultSet = jsonObj.getJSONObject("ResultSet");
                // looping through All Stocks
                //for (int i = 0; i < stocks.length(); i++) {
                    //JSONObject c = stocks.getJSONObject(i);
                    String symbol = jsonObj.getString("symbol");
                    String company = jsonObj.getString("companyName");
                    double price = jsonObj.getDouble("latestPrice");
                    double price_change = jsonObj.getDouble("change");
                    double change_percentage = jsonObj.getDouble("changePercent");
                    // tmp hashmap for single stock
                    HashMap<String, String> stock = new HashMap<String, String>();
                    // adding every child node to HashMap key => value
                    stock.put("symbol", symbol);
                    stock.put("name", company);
                    stock.put("price", Double.toString(price));
                    stock.put("price_change", Double.toString(price_change));
                    stock.put("change_percentage", Double.toString(change_percentage));
                    // adding stock to stocks list
                    stockList.add(stock);
                //}
                return stockList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }
}
