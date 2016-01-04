package com.neelbedekar.android.quickshare.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Milind Bedekar on 12/31/2015.
 */
public class QuickshareContract {
    public static final String CONTENT_AUTHORITY = "com.neelbedekar.android.quickshare";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CONTACT = "contact";

    public static final class ContactEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONTACT).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_CONTACT;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_CONTACT;

        public static final String TABLE_NAME = "contacts";

        public static final String NAME = "name";

        public static final String PHONE = "phone";

        public static final String EMAIL = "email";

        public static final String ADDRESS = "address";

        public static final String FACEBOOK = "facebook";

        public static final String LINKEDIN = "linkedin";

        public static final String TWITTER = "twitter";

        public static final String SNAPCHAT = "snapchat";

        public static final String IMAGE = "image";

        public static final String LAT = "latitude";

        public static final String LONG = "longitude";

        public static Uri buildContactUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
