package com.snowcat.fajnyobed.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.snowcat.fajnyobed.Logic.Restaurant;

/**
 * Created by Snowcat on 27-Sep-15.
 */
public class FajnyObedDatabaseHelper extends SQLiteOpenHelper {

    private ContentResolver contentResolver;
    private static final String DATABASE_NAME = "antikphone.db";
    private static final int DATABASE_VERSION = 1;
    
    public FajnyObedDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        contentResolver = context.getContentResolver();
    }
    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        FavouritesTable.onCreate(db);
    }
    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        FavouritesTable.onUpgrade(db, oldVersion, newVersion);
    }
    public int addFavourite(Restaurant restaurant) {
        ContentValues values = new ContentValues();
        values.put(FavouritesTable.COLUMN_RESTAURANT_ID, restaurant.id);
        values.put(FavouritesTable.COLUMN_RESTAURANT_NAME, restaurant.name);
        values.put(FavouritesTable.COLUMN_RESTAURANT_PLACE, restaurant.street);
        values.put(FavouritesTable.COLUMN_RESTAURANT_RATING, restaurant.rating);
        values.put(FavouritesTable.COLUMN_RESTAURANT_PHOTO_URL, restaurant.promoPhotos.get(0));
        Uri uri = contentResolver.insert(FajnyObedContentProvider.CONTENT_URI_FAVOURITE, values);
        int rowsInserted = Integer.parseInt(uri.getLastPathSegment());
        return rowsInserted;
    }

    public boolean deleteFavourite(Uri uri) {
        boolean result = false;
        int rowsDeleted = contentResolver.delete(uri, null, null);
        if (rowsDeleted > 0) {
            result = true;
        }
        return result;
    }
}
