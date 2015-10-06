package com.snowcat.fajnyobed.Database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by Snowcat on 27-Sep-15.
 */
public class FajnyObedContentProvider extends ContentProvider {

    // database
    private FajnyObedDatabaseHelper database;
    // used for the UriMacher
    private static final int FAVOURITES = 1;
    private static final int FAVOURITE_ID = 2;
    private static final String AUTHORITY = "com.snowcat.fajnyobed.android.contentprovider";
    private static final String BASE_PATH_FAVOURITE = "favourites";
    public static final Uri CONTENT_URI_FAVOURITE = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH_FAVOURITE);;
    public static final String CONTENT_TYPE_FAVOURITE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/favourites";
    public static final String CONTENT_ITEM_TYPE_FAVOURITE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/favourite";
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_FAVOURITE, FAVOURITES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_FAVOURITE + "/#", FAVOURITE_ID);
    }
    @Override
    public boolean onCreate() {
        database = new FajnyObedDatabaseHelper(getContext());
        return false;
    }
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case FAVOURITES:
                queryBuilder.setTables(FavouritesTable.TABLE_FAVOURITES);
                break;
            case FAVOURITE_ID:
                queryBuilder.setTables(FavouritesTable.TABLE_FAVOURITES);
                queryBuilder.appendWhere(FavouritesTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsDeleted = 0;
        long id = 0;
        switch (uriType) {
            case FAVOURITES:
                id = db.insert(FavouritesTable.TABLE_FAVOURITES, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH_FAVOURITE + "/" + id);
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case FAVOURITES:
                rowsDeleted = db.delete(FavouritesTable.TABLE_FAVOURITES, selection, selectionArgs);
                break;
            case FAVOURITE_ID:
                String contactId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(FavouritesTable.TABLE_FAVOURITES,
                            FavouritesTable.COLUMN_ID + "=" + contactId, null);
                } else {
                    rowsDeleted = db.delete(FavouritesTable.TABLE_FAVOURITES,
                            FavouritesTable.COLUMN_ID + "=" + contactId + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case FAVOURITES:
                rowsUpdated = db.update(FavouritesTable.TABLE_FAVOURITES, values, selection, selectionArgs);
                break;
            case FAVOURITE_ID:
                String contactId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(FavouritesTable.TABLE_FAVOURITES, values,
                            FavouritesTable.COLUMN_ID + "=" + contactId, null);
                    break;
                } else {
                    rowsUpdated = db.update(FavouritesTable.TABLE_FAVOURITES, values,
                            FavouritesTable.COLUMN_ID + "=" + contactId + " and " + selection, selectionArgs);
                    break;
                }
            default:
                throw new IllegalArgumentException("Unknown URI:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
