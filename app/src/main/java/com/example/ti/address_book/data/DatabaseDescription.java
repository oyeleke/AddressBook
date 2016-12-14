package com.example.ti.address_book.data;
// class that describes the table and column name for this apps database
//and other information required by the content provider

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ti on 30/07/2016.
 */
public class DatabaseDescription {

    public static final String AUTHORITY = "com.examples.ti.address_book.data";
    // the content provider name is typically the package name

    // base uri used to interact with content provider

    private static final Uri BASE_CONTENT_URI =
            Uri.parse("content://" + AUTHORITY);

    // a nested class which defines contents of the contacts table

    public static final class Contact implements BaseColumns{

        public static final String TABLE_NAME= "contacts";

        // uri for contacts table

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_STREET = "street";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_ZIP = "zip";

        //to create a uri for a specific contact

        public static Uri buildContactUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
