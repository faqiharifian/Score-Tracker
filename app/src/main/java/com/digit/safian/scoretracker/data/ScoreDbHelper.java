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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.digit.safian.scoretracker.data.ScoreContract.MakulEntry;
import com.digit.safian.scoretracker.data.ScoreContract.NilaiEntry;

/**
 * Manages a local database for weather data.
 */
public class ScoreDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "score.db";

    public ScoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String SQL_CREATE_MAKUL_TABLE = "CREATE TABLE " + MakulEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                MakulEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                MakulEntry.COLUMN_ID_MAKUL + " TEXT NOT NULL," +
                MakulEntry.COLUMN_NAMA_MAKUL + " TEXT NOT NULL, "+
                MakulEntry.COLUMN_SEMESTER + " TEXT NOT NULL, "+
                MakulEntry.COLUMN_COUNT_JUDUL + " INTEGER, "+
                " UNIQUE (" + MakulEntry.COLUMN_ID_MAKUL + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_NILAI_TABLE = "CREATE TABLE " + NilaiEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                NilaiEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                // the ID of the location entry associated with this weather data

                NilaiEntry.COLUMN_ID_MAKUL + " TEXT NOT NULL, " +
                NilaiEntry.COLUMN_MAHASISWA + " TEXT NOT NULL, " +
                NilaiEntry.COLUMN_JUDUL + " TEXT NOT NULL, " +
                NilaiEntry.COLUMN_NILAI + " TEXT NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + NilaiEntry.COLUMN_ID_MAKUL + ") REFERENCES " +
                MakulEntry.TABLE_NAME + " (" + MakulEntry.COLUMN_ID_MAKUL + "), " +
                " UNIQUE (" + NilaiEntry.COLUMN_ID_MAKUL + ", " +
                NilaiEntry.COLUMN_JUDUL + ", " +
                NilaiEntry.COLUMN_MAHASISWA + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_MAKUL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_NILAI_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MakulEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NilaiEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
