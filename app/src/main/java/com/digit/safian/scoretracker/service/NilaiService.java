package com.digit.safian.scoretracker.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Created by faqih_000 on 5/9/2015.
 */
public class NilaiService extends IntentService {
    public final static String MAKUL_EXTRA = "makul";
    public NilaiService() {
        super("NilaiService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String makulIdQuery = intent.getStringExtra(MAKUL_EXTRA);
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String nilaiJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL("https://spreadsheets.google.com/feeds/list/1-wtjHhLl39syH6dDTFzDX-glDGaA3izzI_JBLa0nddQ/"+makulIdQuery+"/public/values?alt=json");

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
            Log.v("NilaiService", nilaiJsonStr);

        } catch (IOException e) {
            Log.e("Nilai Service", "Error ", e);
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
                    Log.e("Nilai Service", "Error closing stream", e);
                }
            }
        }
        try{
            getMakulDataFromJson(nilaiJsonStr, makulIdQuery);
            //return getMakulDataFromJson(nilaiJsonStr);
        }catch(JSONException e){
            Log.e("JSON get", e.getMessage(), e);
            e.printStackTrace();
        }
        return;
    }

    private void getMakulDataFromJson(String nilaiJsonStr, String makulId)
            throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
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

                if (keyJson.split("\\$")[0].equals("gsx")) {
                    keyValue = keyJson.split("\\$")[1];
                    JSONObject contentJson = iMakul.getJSONObject(keyJson);
                    String content = contentJson.getString(OWM_CONTENT);
                    values.put(keyValue, content);
                }
            }
            String mahasiswa = values.get("nama");
            values.remove("nama");
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
        if (cVector.size() > 0) {
            /*for(ContentValues i : cVector){
                for(String key : i.keySet()){
                    Log.v(LOG_TAG, key +" = "+ i.getAsString(key));
                }
                Log.v(LOG_TAG, "================NEXT==============");
            }*/
            ContentValues[] cvArray = new ContentValues[cVector.size()];
            cVector.toArray(cvArray);

            this.getContentResolver().bulkInsert(ScoreContract.NilaiEntry.CONTENT_URI, cvArray);
            Log.v("get from json", "complete");
        }
       // NilaiMhsFragment.setRefreshState(false);

    }
}
