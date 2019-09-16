package com.thomastriplett.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Thomas on 3/5/2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    // DB Name
    private static final String DATABASE_NAME = "StockAppDB";

    // DB Table Name
    private static final String TABLE_NAME = "StockTable";

    ///DB Columns
    private static final String SYMBOL = "Symbol";
    private static final String STOCK = "StockName";

    // DB Table Create Code
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    STOCK + " TEXT not null)";

    private SQLiteDatabase database;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase(); // Inherited from SQLiteOpenHelper
        Log.d(TAG, "DatabaseHandler: C'tor DONE");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // onCreate is only called is the DB does not exist
        Log.d(TAG, "onCreate: Making New DB");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public ArrayList<String[]> loadStocks() {

        // Load stocks - return ArrayList of loaded stocks
        Log.d(TAG, "loadStocks: START");
        ArrayList<String[]> stocks = new ArrayList<>();

        Cursor cursor = database.query(
                TABLE_NAME,  // The table to query
                new String[]{SYMBOL, STOCK}, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null); // The sort order

        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String stock = cursor.getString(1);
                stocks.add(new String[]{symbol, stock});
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "loadStocks: DONE");

        return stocks;
    }

    public void addStock(Stock stock) {
        ContentValues values = new ContentValues();

        values.put(SYMBOL, stock.getSymbol());
        values.put(STOCK, stock.getName());

        long key = database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addStock: " + key);
    }

    public void updateStock(Stock stock) {
        ContentValues values = new ContentValues();

        values.put(SYMBOL, stock.getSymbol());
        values.put(STOCK, stock.getName());

        long key = database.update(TABLE_NAME, values, STOCK + " = ?", new String[]{stock.getName()});

        Log.d(TAG, "updateStock: " + key);
    }

    public void deleteStock(String name) {
        Log.d(TAG, "deleteStock: " + name);

        int cnt = database.delete(TABLE_NAME, STOCK + " = ?", new String[]{name});

        Log.d(TAG, "deleteStock: " + cnt);
    }

    public void dumpDbToLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            Log.d(TAG, "dumpDbToLog: vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String stock = cursor.getString(1);
                Log.d(TAG, "dumpDbToLog: " +
                        String.format("%s %-18s", SYMBOL + ":", symbol) +
                        String.format("%s %-18s", STOCK + ":", stock));
                cursor.moveToNext();
            }
            cursor.close();
        }

        Log.d(TAG, "dumpDbToLog: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }

    public void shutDown() {
        database.close();
    }
}
