package com.digit.safian.scoretracker;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
import java.util.Vector;

/**
 * Created by faqih_000 on 4/28/2015.
 */
public class FetchMakulMhsTask extends AsyncTask<Void, Void, Void> {

    private final Context mContext;
    public FetchMakulMhsTask(Context context){
        mContext = context;
    }
    private final String LOG_TAG = FetchMakulMhsTask.class.getSimpleName();

    private String formatName(String name){
        return name.replace("name: ","");
    }

    private void getMakulDataFromJson(String makulJsonStr)
            throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_FEED = "feed";
        final String OWM_ENTRIES = "entry";
        final String OWM_ID = "gsx$id";
        final String OWM_NAME = "gsx$name";
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

            cVVector.add(makulValues);
        }
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);

            mContext.getContentResolver().bulkInsert(ScoreContract.MakulEntry.CONTENT_URI, cvArray);
            Log.v(LOG_TAG, "complete");
        }


        //return resultStrs;

    }

    @Override
    protected Void doInBackground(Void... params){
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String makulJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL("https://spreadsheets.google.com/feeds/list/1H9Y7zJJ8oUCfoZ9qC07TcnujunnU7OxL0yr9OMEPE64/6/public/values?alt=json");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
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
                return null;
            }
            makulJsonStr = buffer.toString();
            //Log.v(LOG_TAG, makulJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
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
            getMakulDataFromJson(makulJsonStr);
            //return getMakulDataFromJson(makulJsonStr);
        }catch(JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    long addMakul(String id_makul, String name){
        long makulId;
        ContentValues makulValues = new ContentValues();
        makulValues.put(ScoreContract.MakulEntry.COLUMN_ID_MAKUL, id_makul);
        makulValues.put(ScoreContract.MakulEntry.COLUMN_NAMA_MAKUL, name);

        Cursor makulCursor = mContext.getContentResolver().query(
                ScoreContract.MakulEntry.CONTENT_URI,
                new String[]{ScoreContract.MakulEntry._ID},
                ScoreContract.MakulEntry.COLUMN_ID_MAKUL + " = ?",
                new String[]{id_makul},
                null
        );

        if(makulCursor.moveToFirst()){
            int makulIdIndex = makulCursor.getColumnIndex(ScoreContract.MakulEntry._ID);
            makulId = makulCursor.getLong(makulIdIndex);
            mContext.getContentResolver().update(
                    ScoreContract.MakulEntry.CONTENT_URI,
                    makulValues,
                    ScoreContract.MakulEntry._ID + " = ? ",
                    new String[]{String.valueOf(makulId)}
            );

        }else{
            Uri insertedId = mContext.getContentResolver().insert(
                    ScoreContract.MakulEntry.CONTENT_URI,
                    makulValues
            );
            makulId = ContentUris.parseId(insertedId);
        }
        makulCursor.close();
        return makulId;
    }
        /*@Override
        protected void onPostExecute(String[] result) {
            if (result != null){
                mMakulAdapter.clear();
                for (String dayForecastStr : result){
                    mMakulAdapter.add(dayForecastStr);
                }
            }
        }*/
}