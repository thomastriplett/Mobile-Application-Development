package com.thomastriplett.stockwatch;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
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

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by Thomas on 3/5/2018.
 */

// This Async Task searches for the user's input and sends the corresponding symbol and company name to MainActivity

public class AsyncTask1 extends AsyncTask<String, Void, ArrayList<HashMap<String,String>>> {

    private static final String TAG = "AsyncTask1";
    private MainActivity mainActivity;
    public static boolean running = false;

    public AsyncTask1(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected ArrayList<HashMap<String,String>> doInBackground(String... strings) {

        HttpURLConnection connection = null;
        String response = "";
        HashMap<String,String> params = new HashMap<String,String>();
        try {
            URL url = new URL("http://d.yimg.com/aq/autoc?region=US&lang=en-US&query="+strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(15001);
            connection.setConnectTimeout(15001);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            //connection.setRequestProperty("Stocks","Mozilla/5.0");
                OutputStream ostream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(ostream, "UTF-8"));
                StringBuilder requestresult = new StringBuilder();
                boolean first = true;
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (first)
                        first = false;
                    else
                        requestresult.append("&");
                    requestresult.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    requestresult.append("=");
                    requestresult.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                }
                writer.write(requestresult.toString());
                writer.flush();
                writer.close();
            ostream.close();
            int reqresponseCode = connection.getResponseCode();
            if (reqresponseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "The response is: "+response);
        ArrayList<HashMap<String,String>> stocks = ParseJSON(response);
        return stocks;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String,String>> stocks) {

        if (stocks == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
            builder.setTitle("No Stock Found");
            builder.setMessage("There Was No Stock Found With That Symbol");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            mainActivity.whenAsync1IsDone(stocks);
        }

        running = false;
        Log.d(TAG, "onPostExecute: AsyncTask1 terminating successfully");
    }

    private ArrayList<HashMap<String, String>> ParseJSON(String json) {
        if (json != null) {
            try {
        // Hashmap for ListView
                ArrayList<HashMap<String, String>> stockList = new ArrayList<HashMap<String, String>>();
                JSONObject jsonObj = new JSONObject(json);
        // Getting JSON Array node
                JSONObject resultSet = jsonObj.getJSONObject("ResultSet");
                JSONArray stocks = resultSet.getJSONArray("Result");
        // looping through All Stocks
                for (int i = 0; i < stocks.length(); i++) {
                    JSONObject c = stocks.getJSONObject(i);
                    String symbol = c.getString("symbol");
                    String company = c.getString("name");
                    String type = c.getString("type");
        // tmp hashmap for single stock
                    HashMap<String, String> stock = new HashMap<String, String>();
        // adding every child node to HashMap key => value
                    if (!symbol.contains(".") && type.equals("S")) {
                        stock.put("symbol", symbol);
                        stock.put("name", company);
                        // adding stock to stocks list
                        stockList.add(stock);
                    }
                }
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
