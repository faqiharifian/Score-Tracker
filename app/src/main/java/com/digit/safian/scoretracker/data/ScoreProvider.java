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
    private final String LOG_TAG = ScoreProvider.class.getSimpleName();
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ScoreDbHelper mOpenHelper;

    static final int MAKUL = 100;
    static final int MAKUL_BY_SEMESTER = 101;
    static final int MAKUL_BY_ID = 102;
    static final int NILAI_BY_MAKUL = 202;
    static final int NILAI_JUDUL = 201;
    static final int NILAI = 200;

    private static final SQLiteQueryBuilder sNilaiByMakulSettingQueryBuilder;

    static{
        sNilaiByMakulSettingQueryBuilder = new SQLiteQueryBuilder();
        
        //This is an inner join which looks like
        //nilai INNER JOIN makul ON nilai.id_makul = makul._id
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

    private static final String sNilaiAndMakul =
            ScoreContract.NilaiEntry.TABLE_NAME +
                    "." + ScoreContract.NilaiEntry.COLUMN_ID_MAKUL + " = ?" + " AND " +
                    ScoreContract.NilaiEntry.TABLE_NAME +
            "." + ScoreContract.NilaiEntry.COLUMN_JUDUL + " = ?";

    private static final String sNilaiAndJudul =
            ScoreContract.NilaiEntry.TABLE_NAME +
                    "." + ScoreContract.NilaiEntry.COLUMN_ID_MAKUL + " = ?";

    private static final String sMakulBySemester =
            ScoreContract.MakulEntry.TABLE_NAME +
                    "." + ScoreContract.MakulEntry.COLUMN_SEMESTER + " = ?";

    private static final String sMakulById =
            ScoreContract.MakulEntry.TABLE_NAME +
                    "." + ScoreContract.MakulEntry.COLUMN_ID_MAKUL + " = ?";

    private static final String sMakulBySemesterAkhir =
            ScoreContract.MakulEntry.TABLE_NAME +
                    "." + ScoreContract.MakulEntry.COLUMN_SEMESTER + " = ?" + " OR " +
                    ScoreContract.MakulEntry.TABLE_NAME + "." + ScoreContract.MakulEntry.COLUMN_SEMESTER + " = ?";

    private Cursor getMakul(Uri uri, String[] projection, String sortOrder){
        return mOpenHelper.getReadableDatabase().query(
                ScoreContract.MakulEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getMakulBySemester(Uri uri, String[] projection, String sortOrder){
        String semester = ScoreContract.MakulEntry.getSemester(uri);
        if(semester.equals("5")){
            return mOpenHelper.getReadableDatabase().query(
                    ScoreContract.MakulEntry.TABLE_NAME,
                    projection,
                    sMakulBySemesterAkhir,
                    new String[]{semester, "7"},
                    null,
                    null,
                    sortOrder
            );
        }else if(semester.equals("6")){
            return mOpenHelper.getReadableDatabase().query(
                    ScoreContract.MakulEntry.TABLE_NAME,
                    projection,
                    sMakulBySemesterAkhir,
                    new String[]{semester, "8"},
                    null,
                    null,
                    sortOrder
            );
        }else{
            return mOpenHelper.getReadableDatabase().query(
                    ScoreContract.MakulEntry.TABLE_NAME,
                    projection,
                    sMakulBySemester,
                    new String[]{semester},
                    null,
                    null,
                    sortOrder
            );
        }

    }

    private Cursor getMakulById(Uri uri, String[] projection, String sortOrder){
        String semester = ScoreContract.MakulEntry.getSemester(uri);
        String id = ScoreContract.MakulEntry.getId(uri);
        return mOpenHelper.getReadableDatabase().query(
                ScoreContract.MakulEntry.TABLE_NAME,
                null,
                sMakulById,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getNilaiByMakul(Uri uri, String[] projection, String sortOrder){
        String id_makul = ScoreContract.NilaiEntry.getIdMakul(uri);
        String judul_nilai = ScoreContract.NilaiEntry.getJudul(uri);
        return mOpenHelper.getReadableDatabase().query(
                ScoreContract.NilaiEntry.TABLE_NAME,
                null,
                sNilaiAndMakul,
                new String[]{id_makul, judul_nilai},
                null,
                null,
                sortOrder

        );
    }

    private Cursor getNilaiJudul(Uri uri, String[] projection, String sortOrder){
        String id_makul = ScoreContract.NilaiEntry.getIdMakul(uri);
        return mOpenHelper.getReadableDatabase().query(
                ScoreContract.NilaiEntry.TABLE_NAME,
                projection,
                sNilaiAndJudul,
                new String[]{id_makul},
                ScoreContract.NilaiEntry.COLUMN_JUDUL,
                null,
                sortOrder
        );
    }

    private Boolean isIdExist(Integer table, String id){
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        if(MAKUL == table){
            final String selection = ScoreContract.MakulEntry.TABLE_NAME + "." + ScoreContract.MakulEntry.COLUMN_ID_MAKUL + " = ?";
            Cursor cursor = db.query(ScoreContract.MakulEntry.TABLE_NAME, new String[]{"id_makul"}, selection, new String[]{id}, null, null, null);
            if(cursor.getCount() == 0){
                return false;
            }
        }
        return true;
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ScoreContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ScoreContract.PATH_MAKUL, MAKUL);
        matcher.addURI(authority, ScoreContract.PATH_MAKUL + "/*", MAKUL_BY_SEMESTER);
        matcher.addURI(authority, ScoreContract.PATH_MAKUL + "/*/*", MAKUL_BY_ID);

        matcher.addURI(authority, ScoreContract.PATH_NILAI, NILAI);
        matcher.addURI(authority, ScoreContract.PATH_NILAI + "/*/*", NILAI_BY_MAKUL);
        matcher.addURI(authority, ScoreContract.PATH_NILAI + "/*", NILAI_JUDUL);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new ScoreDbHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MAKUL:
                return ScoreContract.MakulEntry.CONTENT_TYPE;
            case NILAI_BY_MAKUL:
                return ScoreContract.NilaiEntry.CONTENT_TYPE;
            case NILAI:
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
            case MAKUL:
            {
                retCursor = getMakul(uri, projection, sortOrder);
                break;
            }
            case MAKUL_BY_SEMESTER:
            {
                retCursor = getMakulBySemester(uri, projection, sortOrder);
                break;
            }
            case MAKUL_BY_ID:
            {
                retCursor = getMakulById(uri, projection, sortOrder);
                break;
            }
            case NILAI_BY_MAKUL: {
                retCursor = getNilaiByMakul(uri, projection, sortOrder);
                break;
            }
            case NILAI_JUDUL: {
                retCursor = getNilaiJudul(uri, projection, sortOrder);
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
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MAKUL:
                rowsDeleted = db.delete(
                        ScoreContract.MakulEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case NILAI:
                rowsDeleted = db.delete(
                        ScoreContract.NilaiEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }



    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MAKUL:
                rowsUpdated = db.update(ScoreContract.MakulEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            case NILAI:
                rowsUpdated = db.update(ScoreContract.NilaiEntry.TABLE_NAME, values, selection,
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

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        //Log.v(LOG_TAG, String.valueOf(existing.moveToFirst()));
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MAKUL: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    long _id = -1;
                    for (ContentValues value : values) {
                        long makulId = value.getAsLong(ScoreContract.MakulEntry.COLUMN_ID_MAKUL);
                        _id = db.update(
                                ScoreContract.MakulEntry.TABLE_NAME,
                                value,
                                ScoreContract.MakulEntry.COLUMN_ID_MAKUL + " = ?",
                                new String[]{String.valueOf(makulId)});
                        if(_id <= 0){
                            _id = db.insert(ScoreContract.MakulEntry.TABLE_NAME, null, value);
                        }
                        if (_id > 0) {
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
                    long _id = -1;
                    for (ContentValues value : values) {
                        String id_makul = value.getAsString(ScoreContract.NilaiEntry.COLUMN_ID_MAKUL);
                        String mhs = value.getAsString(ScoreContract.NilaiEntry.COLUMN_MAHASISWA);
                        String judul = value.getAsString(ScoreContract.NilaiEntry.COLUMN_JUDUL);
                        String selection =
                                ScoreContract.NilaiEntry.COLUMN_ID_MAKUL + " = ? AND " +
                                        ScoreContract.NilaiEntry.COLUMN_MAHASISWA + " = ? AND " +
                                        ScoreContract.NilaiEntry.COLUMN_JUDUL + " = ?";
                        _id = db.update(
                                ScoreContract.NilaiEntry.TABLE_NAME,
                                value,
                                selection,
                                new String[]{id_makul, mhs, judul});
                        if(_id <= 0){
                            _id = db.insert(ScoreContract.NilaiEntry.TABLE_NAME, null, value);
                        }
                        if (_id > 0) {
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