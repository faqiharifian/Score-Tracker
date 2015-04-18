package com.digit.safian.scoretracker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.util.Arrays;
import java.util.List;

/**
 * Created by faqih_000 on 4/18/2015.
 */
public class MakulDosenFragment extends Fragment {

    private ArrayAdapter<String> mMakulAdapter;
    public MakulDosenFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_dosen,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar Item clicks here, the action bar will
        // automatically handle clicks on the Home/up button, so long
        // as you specify a parent activity in AndroidManifest.xml
        int id = item.getItemId();
        if (id == com.digit.safian.scoretracker.R.id.action_refresh){
            updateMakulDosen();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMakulDosen(){
        FetchMakulDosenTask makulTask = new FetchMakulDosenTask();
        makulTask.execute();
    }

    /*@Override
    public void onStart(){
        super.onStart();
        updateMakulDosen();
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dosen, container, false);


        String[] makulArray = {
                "Proyek Perangkat Lunak",
                "Analisis dan Perancangan Sistem Informasi",
                "Keamanan Jaringan",
                "Data Warehouse",
                "Pemrogramman Konkuren",
                "Metode Tangkas Perangkat Lunak",
                "Topik Khusus",
                "Sistem Temu Balik Informasi"
        };

        List<String> daftarMakul = new ArrayList<String>(
                Arrays.asList(makulArray)
        );
        mMakulAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_makul,
                R.id.list_item_makul_textview,
                daftarMakul
        );
        ListView listView = (ListView) rootView.findViewById(R.id.listview_makul_dosen);
        listView.setAdapter(mMakulAdapter);



        return rootView;
    }

    public class FetchMakulDosenTask extends AsyncTask<Void, Void, String[]> {

        private final String LOG_TAG = FetchMakulDosenTask.class.getSimpleName();

        private String formatName(String name){
            return name.replace("name: ","");
        }

        private String[] getMakulDataFromJson(String makulJsonStr)
                throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String OWM_RESPONSEDATA = "responseData";
            final String OWM_FEED = "feed";
            final String OWM_ENTRIES = "entries";
            final String OWM_CONTENT = "content";

            JSONObject makulJson = new JSONObject(makulJsonStr);
            JSONObject jsonResponse = makulJson.getJSONObject(OWM_RESPONSEDATA);
            JSONObject responseFeed = jsonResponse.getJSONObject(OWM_FEED);
            JSONArray makulArray = responseFeed.getJSONArray(OWM_ENTRIES);

            String[] resultStrs = new String[makulArray.length()];
            for(int i = 0; i < makulArray.length(); i++) {
                String content;

                // Get the JSON object representing the day
                JSONObject iMakul = makulArray.getJSONObject(i);
                content = iMakul.getString(OWM_CONTENT);

                resultStrs[i] = formatName(content);
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;

        }

        @Override
        protected String[] doInBackground(Void... params){
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
                URL url = new URL("http://ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=100&q=https://spreadsheets.google.com/feeds/list/1H9Y7zJJ8oUCfoZ9qC07TcnujunnU7OxL0yr9OMEPE64/6/public/basic");

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
                return getMakulDataFromJson(makulJsonStr);
            }catch(JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null){
                mMakulAdapter.clear();
                for (String dayForecastStr : result){
                    mMakulAdapter.add(dayForecastStr);
                }
            }
        }
    }
}