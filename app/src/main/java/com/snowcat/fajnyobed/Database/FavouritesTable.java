package com.snowcat.fajnyobed.Database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Snowcat on 27-Sep-15.
 */
public class FavouritesTable {

    // Database table
    public static final String TABLE_FAVOURITES = "history";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_RESTAURANT_ID = "restaurant_id";
    public static final String COLUMN_RESTAURANT_NAME = "name";
    public static final String COLUMN_RESTAURANT_PLACE = "place";
    public static final String COLUMN_RESTAURANT_RATING = "rating";
    public static final String COLUMN_RESTAURANT_PHOTO_URL = "url";
    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_FAVOURITES
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_RESTAURANT_ID + " text not null, "
            + COLUMN_RESTAURANT_NAME + " text not null, "
            + COLUMN_RESTAURANT_PLACE + " text not null, "
            + COLUMN_RESTAURANT_RATING + " integer not null, "
            + COLUMN_RESTAURANT_PHOTO_URL + " text not null "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(FavouritesTable.class.getName(), "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
        onCreate(database);
    }
}
