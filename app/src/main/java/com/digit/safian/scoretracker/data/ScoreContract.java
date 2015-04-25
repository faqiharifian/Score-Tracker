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

import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Defines table and column names for the weather database.
 */
public class ScoreContract {

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /*
        Inner class that defines the table contents of the location table
        Students: This is where you will add the strings.  (Similar to what has been
        done for WeatherEntry)
     */
    public static final class MahasiswaEntry implements BaseColumns {
        public static final String TABLE_NAME = "mahasiswa";

        public static final String COLUMN_NIM = "nim";
        public static final String COLUMN_NAMA_MHS = "nama";

    }

    /* Inner class that defines the table contents of the weather table */
    public static final class MakulEntry implements BaseColumns {

        public static final String TABLE_NAME = "mata_kuliah";

        public static final String COLUMN_NAMA_MAKUL = "nama";
        public static final String COLUMN_PENGAMPU_1 = "pengampu_1";
        public static final String COLUMN_PENGAMPU_2 = "pengampu_2";

    }

    public static final class MakulNilaiEntry implements BaseColumns {

        public static final String TABLE_NAME = "mata_kuliah_nilai";

        public static final String COLUMN_JUDUL = "judul";
        public static final String COLUMN_ID_MHS = "id_mahasiswa";
        public static final String COLUMN_ID_MAKUL = "id_makul";
        public static final String COLUMN_NILAI = "nilai";

    }
}
