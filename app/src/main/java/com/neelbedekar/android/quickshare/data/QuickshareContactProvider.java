package com.neelbedekar.android.quickshare.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Milind Bedekar on 12/31/2015.
 */
public class QuickshareContactProvider extends ContentProvider {

    private static final int CONTACT = 100;
    private static final int CONTACT_ID = 101;
    private QuickshareDBHelper dbHelper;
    private static final UriMatcher uriMatcher = buildUriMatcher();


    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = QuickshareContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, QuickshareContract.PATH_CONTACT, CONTACT);
        matcher.addURI(authority, QuickshareContract.PATH_CONTACT+"/#", CONTACT_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new QuickshareDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case CONTACT:
                cursor = dbHelper.getReadableDatabase().query(
                        QuickshareContract.ContactEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CONTACT_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        QuickshareContract.ContactEntry.TABLE_NAME,
                        projection,
                        QuickshareContract.ContactEntry._ID + " = ?",
                        new String[]{Long.toString(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                cursor.moveToFirst();
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case CONTACT:
                return QuickshareContract.ContactEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case CONTACT:
                long _id = db.insert(QuickshareContract.ContactEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = QuickshareContract.ContactEntry.buildContactUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case CONTACT:
                rowsDeleted = db.delete(QuickshareContract.ContactEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CONTACT_ID:
                rowsDeleted = db.delete(
                        QuickshareContract.ContactEntry.TABLE_NAME,
                        QuickshareContract.ContactEntry._ID + " = " + "'" + ContentUris.parseId(uri) + "'",
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case CONTACT:
                rowsUpdated = db.update(QuickshareContract.ContactEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
