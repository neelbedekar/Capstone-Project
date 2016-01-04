package com.neelbedekar.android.quickshare.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Milind Bedekar on 12/31/2015.
 */
public class QuickshareDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "quickshare.db";

    public QuickshareDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_CONTACTS_TABLE = "CREATE TABLE " + QuickshareContract.ContactEntry.TABLE_NAME + " (" +
                QuickshareContract.ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuickshareContract.ContactEntry.NAME + " TEXT, " +
                QuickshareContract.ContactEntry.PHONE + " TEXT, " +
                QuickshareContract.ContactEntry.EMAIL + " TEXT, " +
                QuickshareContract.ContactEntry.ADDRESS + " TEXT, " +
                QuickshareContract.ContactEntry.FACEBOOK + " TEXT, " +
                QuickshareContract.ContactEntry.LINKEDIN + " TEXT, " +
                QuickshareContract.ContactEntry.TWITTER + " TEXT, " +
                QuickshareContract.ContactEntry.SNAPCHAT + " TEXT, " +
                QuickshareContract.ContactEntry.LAT + " TEXT, " +
                QuickshareContract.ContactEntry.LONG + " TEXT, " +
                QuickshareContract.ContactEntry.IMAGE + " BLOB)";

        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + QuickshareContract.ContactEntry.TABLE_NAME);
        onCreate(db);
    }
}
