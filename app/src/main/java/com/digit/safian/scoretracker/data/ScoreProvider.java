/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.digit.safian.scoretracker.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class ScoreProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ScoreDbHelper mOpenHelper;

    static final int MAKUL = 100;
    static final int NILAI_BY_MAKUL = 101;
    static final int NILAI = 200;

    private static final SQLiteQueryBuilder sNilaiByMakulSettingQueryBuilder;

    static{
        sNilaiByMakulSettingQueryBuilder = new SQLiteQueryBuilder();
        
        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sNilaiByMakulSettingQueryBuilder.setTables(
                ScoreContract.NilaiEntry.TABLE_NAME + " INNER JOIN " +
                        ScoreContract.MakulEntry.TABLE_NAME +
                        " ON " + ScoreContract.NilaiEntry.TABLE_NAME +
                        "." + ScoreContract.NilaiEntry.COLUMN_ID_MAKUL +
                        " = " + ScoreContract.MakulEntry.TABLE_NAME +
                        "." + ScoreContract.MakulEntry._ID);
    }


    private static final String sMakul =
            ScoreContract.MakulEntry.TABLE_NAME;

    //location.location_setting = ? AND date = ?
    private static final String sNilaiAndMakul =
            ScoreContract.NilaiEntry.TABLE_NAME +
                    "." + ScoreContract.NilaiEntry.COLUMN_ID_MAKUL + " = ?";


    private Cursor getMakul(Uri uri, String[] projection, String sortOrder){
        String[] selectionArgs;
        String selection;

        return sNilaiByMakulSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMakul,
                null,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getNilaiByMakul(Uri uri, String[] projection, String sortOrder){
        String id_makul = ScoreContract.MakulEntry.getIdMakul(uri);

        return sNilaiByMakulSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sNilaiAndMakul,
                new String[]{id_makul},
                null,
                null,
                sortOrder
        );
    }


    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ScoreContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ScoreContract.PATH_MAKUL, MAKUL);
        matcher.addURI(authority, ScoreContract.PATH_NILAI + "/*", NILAI_BY_MAKUL);
        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new ScoreDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
//            case WEATHER_WITH_LOCATION_AND_DATE:
//            case WEATHER_WITH_LOCATION:
            case MAKUL:
                return ScoreContract.MakulEntry.CONTENT_TYPE;
            case NILAI_BY_MAKUL:
                return ScoreContract.NilaiEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"
            case MAKUL:
            {
                retCursor = getMakul(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case NILAI_BY_MAKUL: {
                retCursor = getNilaiByMakul(uri, projection, sortOrder);
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MAKUL: {
                long _id = db.insert(ScoreContract.MakulEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ScoreContract.MakulEntry.buildMakulUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case NILAI:{
                long _id = db.insert(ScoreContract.NilaiEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ScoreContract.NilaiEntry.buildNilaiUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database

        // Student: Use the uriMatcher to match the WEATHER and LOCATION URI's we are going to
        // handle.  If it doesn't match these, throw an UnsupportedOperationException.

        // Student: A null value deletes all rows.  In my implementation of this, I only notified
        // the uri listeners (using the content resolver) if the rowsDeleted != 0 or the selection
        // is null.
        // Oh, and you should notify the listeners here.

        // Student: return the actual rows deleted
        return 0;
    }



    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MAKUL: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ScoreContract.MakulEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case NILAI:{
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ScoreContract.NilaiEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}