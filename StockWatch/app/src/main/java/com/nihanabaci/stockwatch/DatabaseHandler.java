package com.nihanabaci.stockwatch;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import android.util.Log;


public class DatabaseHandler extends SQLiteOpenHelper{

    private static final String TAG = "DatabaseHandler";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    // DB Name
    private static final String DATABASE_NAME = "StockAppDB";

    // DB Table Name
    private static final String TABLE_NAME = "StockTable";

    ///DB Columns
    private static final String SYMBOL = "symbol";
    private static final String NAME = "name";
    private static final String ID = "id";


    // DB Table Create Code
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    NAME + " TEXT not null, " +
                    ID + " TEXT not null)";

    private SQLiteDatabase database;


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase(); // Inherited from SQLiteOpenHelper

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // onCreate is only called is the DB does not exist

        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public ArrayList<stock> loadStocks() {

        // Load countries - return ArrayList of loaded countries
        Log.d(TAG, "loadStocks: START");
        ArrayList<stock> stocks = new ArrayList<>();

        Cursor cursor = database.query(
                TABLE_NAME,  // The table to query
                new String[]{SYMBOL, NAME, ID}, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null); // The sort order

        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String name = cursor.getString(1);
                String id = cursor.getString(2);

                stock c = new stock(symbol, name, id);
                stocks.add(c);
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "loadStocks: DONE");

        return stocks;
    }

    public void addStock(stock stock) {
        ContentValues values = new ContentValues();

        values.put(SYMBOL, stock.getSymbol());
        values.put(NAME, stock.getName());
        values.put(ID, stock.getId());

        //deleteCountry(country.getName());

        long key = database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addStock: " + key);
    }

    public void updateStock(stock stock) {
        ContentValues values = new ContentValues();

        values.put(SYMBOL, stock.getSymbol());
        values.put(NAME, stock.getName());
        values.put(ID, stock.getId());


        long key = database.update(TABLE_NAME, values, SYMBOL + " = ?", new String[]{stock.getName()});

        Log.d(TAG, "updateStock: " + key);
    }

    public void deleteStock(String name) {
        Log.d(TAG, "deleteStock: " + name);

        int cnt = database.delete(TABLE_NAME, SYMBOL + " = ?", new String[]{name});

        Log.d(TAG, "deleteStock: " + cnt);
    }

    public void dumpDbToLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String name = cursor.getString(1);
                String id = cursor.getString(2);

                Log.d(TAG, "dumpDbToLog: " +
                        String.format("%s %-18s", SYMBOL + ":", symbol) +
                        String.format("%s %-18s", NAME + ":", name) +
                        String.format("%s %-18s", ID + ":", id));
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
