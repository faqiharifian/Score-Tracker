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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Defines table and column names for the weather database.
 */
public class ScoreContract {

    public static final String CONTENT_AUTHORITY = "com.digit.safian.scoretracker";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MAKUL = "makul";
    public static final String PATH_NILAI = "nilai";
    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /* Inner class that defines the table contents of the weather table */
    public static final class MakulEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MAKUL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MAKUL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MAKUL;

        public static final String TABLE_NAME = "mata_kuliah";

        public static final String COLUMN_NAMA_MAKUL = "nama";

        public static Uri buildMakulUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        
        public static String getIdMakul(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static final class NilaiEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NILAI).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NILAI;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NILAI;

        public static final String TABLE_NAME = "nilai";

        public static final String COLUMN_ID_MAKUL = "id_makul";
        public static final String COLUMN_MAHASISWA = "mahasiswa";
        public static final String COLUMN_JUDUL = "judul";
        public static final String COLUMN_NILAI = "nilai";


        public static Uri buildNilaiUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildNilaiMakulUri(String makulId){
            return CONTENT_URI.buildUpon().appendPath(makulId).build();
        }

        public static String getMakulIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }


    }


}
