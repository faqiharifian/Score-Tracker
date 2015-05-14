package com.digit.safian.scoretracker.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.support.v4.app.TaskStackBuilder;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.digit.safian.scoretracker.MainActivity;
import com.digit.safian.scoretracker.R;
import com.digit.safian.scoretracker.data.ScoreContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by faqih_000 on 5/9/2015.
 */
public class ScoreSyncAdapter extends AbstractThreadedSyncAdapter{
    public final String LOG_TAG = ScoreSyncAdapter.class.getSimpleName();

    /*public static final int SYNC_INTERVAL = 60 * 60 * 6;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/6;*/
    public static final int SYNC_INTERVAL = 60 * 30;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    //private static final long DAY_IN_MILLIS = 1000 * 60;
    final int SCORE_NOTIFICATION_ID = 3153;

    public ScoreSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String prefSemester = prefs.getString(getContext().getString(R.string.pref_semester_key), "");

        int semesterInt = Integer.parseInt(prefSemester);
        List<String> arraySemester = new ArrayList<>();

        List<String> makulIds = new ArrayList<>();

        arraySemester.add(prefSemester);
        if(semesterInt % 2 == 0 && semesterInt >= 6){
            arraySemester.add("8");
        }else if(semesterInt % 2 == 1 && semesterInt >= 5){
            arraySemester.add("7");
        }
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        for(String semester : arraySemester) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.



            // Will contain the raw JSON response as a string.
            String makulJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("https://spreadsheets.google.com/feeds/list/1H9Y7zJJ8oUCfoZ9qC07TcnujunnU7OxL0yr9OMEPE64/" + semester + "/public/values?alt=json");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return;
                }
                makulJsonStr = buffer.toString();
                //Log.v(LOG_TAG, makulJsonStr);

            } catch (IOException e) {
                Log.e("Fetch", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Fetch", "Error closing stream", e);
                    }
                }
            }
            try {
                if(makulJsonStr != null) {
                    makulIds.addAll(getMakulDataFromJson(makulJsonStr, semester));
                }
                //return getMakulDataFromJson(makulJsonStr);
            } catch (JSONException e) {
                Log.e("Fetch", e.getMessage(), e);
                e.printStackTrace();
            }
        }

        if(!makulIds.isEmpty()){
            for(String makulId : makulIds){

                // These two need to be declared outside the try/catch
                // so that they can be closed in the finally block.
                urlConnection = null;
                reader = null;

                // Will contain the raw JSON response as a string.
                String nilaiJsonStr = null;

                try {
                    // Construct the URL for the OpenWeatherMap query
                    // Possible parameters are avaiable at OWM's forecast API page, at
                    // http://openweathermap.org/API#forecast
                    URL url = new URL("https://spreadsheets.google.com/feeds/list/1-wtjHhLl39syH6dDTFzDX-glDGaA3izzI_JBLa0nddQ/"+makulId+"/public/values?alt=json");

                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return;
                    }
                    nilaiJsonStr = buffer.toString();
                    Log.v(LOG_TAG, nilaiJsonStr);

                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attemping
                    // to parse it.
                    return;
                } finally{
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
                try{
                    if(nilaiJsonStr != null) {
                        getNilaiDataFromJson(nilaiJsonStr, makulId);
                    }
                    //return getMakulDataFromJson(nilaiJsonStr);
                }catch(JSONException e){
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }


            }
        }

        //MakulMhsFragment.setRefreshState(false);
        return;

    }

    public static void syncImmediately(Context context){
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context){
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if(null == accountManager.getPassword(newAccount)){
            if(!accountManager.addAccountExplicitly(newAccount, "", null)){
                return null;
            }

            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime){
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Log.v("config", "kitkat");
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        }else{
            Log.v("config", "else");
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context){
        ScoreSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context){
        getSyncAccount(context);
    }

    private List<String> getMakulDataFromJson(String makulJsonStr, String semester)
            throws JSONException {
        List<String> makulIds = new ArrayList<>();

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_FEED = "feed";
        final String OWM_ENTRIES = "entry";
        final String OWM_ID = "gsx$id";
        final String OWM_NAME = "gsx$nama";
        final String OWM_CONTENT = "$t";


        JSONObject makulJson = new JSONObject(makulJsonStr);
        JSONObject responseFeed = makulJson.getJSONObject(OWM_FEED);
        JSONArray makulArray = responseFeed.getJSONArray(OWM_ENTRIES);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(makulArray.length());
        String[] resultStrs = new String[makulArray.length()];
        for(int i = 0; i < makulArray.length(); i++) {
            String id_makul;
            String name;

            // Get the JSON object representing the day
            JSONObject iMakul = makulArray.getJSONObject(i);
            JSONObject idMakul = iMakul.getJSONObject(OWM_ID);
            JSONObject nameMakul = iMakul.getJSONObject(OWM_NAME);
            id_makul = idMakul.getString(OWM_CONTENT);
            name = nameMakul.getString(OWM_CONTENT);

            ContentValues makulValues = new ContentValues();
            makulValues.put(ScoreContract.MakulEntry.COLUMN_ID_MAKUL, id_makul);
            makulValues.put(ScoreContract.MakulEntry.COLUMN_NAMA_MAKUL, name);
            makulValues.put(ScoreContract.MakulEntry.COLUMN_SEMESTER, semester);

            cVVector.add(makulValues);

            makulIds.add(id_makul);
        }
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);

            getContext().getContentResolver().bulkInsert(ScoreContract.MakulEntry.CONTENT_URI, cvArray);
            Log.v("makul sync", "complete "+semester);
        }

        return makulIds;
    }

    private void getNilaiDataFromJson(String nilaiJsonStr, String makulId)
            throws JSONException {
        // These are the names of the JSON objects that need to be extracted.

        int countJudul = 0;

        final String OWM_FEED = "feed";
        final String OWM_ENTRIES = "entry";
        final String OWM_ID = "gsx$id";
        final String OWM_NAME = "gsx$name";
        final String OWM_CONTENT = "$t";

        JSONObject nilaiJson = new JSONObject(nilaiJsonStr);
        JSONObject responseFeed = nilaiJson.getJSONObject(OWM_FEED);
        JSONArray makulArray = responseFeed.getJSONArray(OWM_ENTRIES);

        Vector<ContentValues> cVector = new Vector<ContentValues>(makulArray.length());
        String[] resultStrs = new String[makulArray.length()];
        for(int i = 0; i < makulArray.length(); i++) {

            // Get the JSON object representing the day
            JSONObject iMakul = makulArray.getJSONObject(i);
            Iterator<String> keys = iMakul.keys();
            ContentValues nilaiValues = null;
            Map<String, String> values = new HashMap<>();


            while (keys.hasNext()) {
                String keyJson = keys.next();
                String keyValue;

                if(makulId.equals("45")){
                    Log.v("key JSON", keyJson);
                }

                if (keyJson.split("\\$")[0].equals("gsx")) {
                    keyValue = keyJson.split("\\$")[1];
                    JSONObject contentJson = iMakul.getJSONObject(keyJson);
                    String content = contentJson.getString(OWM_CONTENT);
                    if(makulId.equals("45")){
                        Log.v(keyValue, content);
                    }
                    values.put(keyValue, content);
                }
            }
            String mahasiswa = values.get("nama");
            values.remove("nama");
            countJudul = values.size();
            for(String key : values.keySet()){
                nilaiValues = new ContentValues();
                /*Log.v(LOG_TAG, "start");
                Log.v(LOG_TAG, ScoreContract.NilaiEntry.COLUMN_ID_MAKUL +" = "+ makulId);
                Log.v(LOG_TAG, ScoreContract.NilaiEntry.COLUMN_MAHASISWA +" = "+ mahasiswa);
                Log.v(LOG_TAG, ScoreContract.NilaiEntry.COLUMN_JUDUL +" = "+ key);
                Log.v(LOG_TAG, ScoreContract.NilaiEntry.COLUMN_NILAI +" = "+ values.get(key));
                Log.v(LOG_TAG, "================NEXT==============");*/

                nilaiValues.put(ScoreContract.NilaiEntry.COLUMN_ID_MAKUL, makulId);
                nilaiValues.put(ScoreContract.NilaiEntry.COLUMN_MAHASISWA, mahasiswa);
                nilaiValues.put(ScoreContract.NilaiEntry.COLUMN_JUDUL, key);
                nilaiValues.put(ScoreContract.NilaiEntry.COLUMN_NILAI, values.get(key));

                cVector.add(nilaiValues);
            }


        }
        notifyScore(makulId, countJudul);
        Log.v(makulId, String.valueOf(countJudul));
        if (cVector.size() > 0) {
            /*for(ContentValues i : cVector){
                for(String key : i.keySet()){
                    Log.v(LOG_TAG, key +" = "+ i.getAsString(key));
                }
                Log.v(LOG_TAG, "================NEXT==============");
            }*/
            ContentValues[] cvArray = new ContentValues[cVector.size()];
            cVector.toArray(cvArray);

            getContext().getContentResolver().bulkInsert(ScoreContract.NilaiEntry.CONTENT_URI, cvArray);
            Log.v("fetch nilai ", "complete "+makulId);
        }

    }

    private void notifyScore(String makulId, int countJudul){
        Log.v("notify", makulId+": "+countJudul);
        Context context = getContext();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String mNotif = prefs.getString(context.getString(R.string.pref_notif_key), "");



        if(mNotif.equals("on")) {

            String lastNotif = context.getString(R.string.pref_last_notify);
            long lastSync = prefs.getLong(lastNotif, 0);
            Log.v("notify", String.valueOf(lastSync));
            Log.v("current", String.valueOf(System.currentTimeMillis()));
            Log.v("diff", String.valueOf(System.currentTimeMillis() - lastSync));

            //if(System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS ) {
                Log.v("inside", "if");

            /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String lastNotifKey = context.getString(R.string.pref_last_notif);
            long lastSync = prefs.getLong(lastNotifKey, 0);*/

                Uri makulUri = ScoreContract.MakulEntry.buildMakulByIdUri("0", makulId);

                Cursor cursor = context.getContentResolver().query(
                        makulUri,
                        null,
                        null,
                        null,
                        null
                );
                Log.v("cursor size", String.valueOf(cursor.getCount()));

                if (cursor.moveToFirst()) {
                    int count_judul = cursor.getInt(cursor.getColumnIndex(ScoreContract.MakulEntry.COLUMN_COUNT_JUDUL));

                    if (countJudul != count_judul) {
                        String makul = cursor.getString(cursor.getColumnIndex(ScoreContract.MakulEntry.COLUMN_NAMA_MAKUL));

                        String title = context.getString(R.string.app_name);

                        String contentText = String.format(context.getString(R.string.format_notification),
                                makul);
                        Log.v("notify", contentText);

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext())
                                .setSmallIcon(R.drawable.ic_stat)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle(title)
                                .setContentText(contentText);

                        Intent resultIntent = new Intent(context, MainActivity.class);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent =
                                stackBuilder.getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );
                        mBuilder.setContentIntent(resultPendingIntent);

                        NotificationManager mNotificationManager =
                                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

                        mNotificationManager.notify(SCORE_NOTIFICATION_ID, mBuilder.build());

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putLong(lastNotif, System.currentTimeMillis());
                        editor.commit();
                    }
                }

                cursor.close();

                makulUri = ScoreContract.MakulEntry.CONTENT_URI;

                ContentValues values = new ContentValues();

                values.put(ScoreContract.MakulEntry.COLUMN_COUNT_JUDUL, countJudul);

                String selection = ScoreContract.MakulEntry.TABLE_NAME +
                        "." + ScoreContract.MakulEntry.COLUMN_ID_MAKUL + " = ?";

                context.getContentResolver().update(
                        makulUri,
                        values,
                        selection,
                        new String[]{makulId}
                );

            //}
        }
    }
}
