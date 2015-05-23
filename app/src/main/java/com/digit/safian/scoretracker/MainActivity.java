package com.digit.safian.scoretracker;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.digit.safian.scoretracker.sync.ScoreSyncAdapter;


public class MainActivity extends Activity {
    private String mSemester;
    Boolean login = true;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSemester = prefs.getString(getString(R.string.pref_semester_key), "");

        String mNotif = prefs.getString(getString(R.string.pref_notif_key), "");

        /*String lastNotif = this.getString(R.string.pref_notif_key);
        long lastSync = prefs.getLong(lastNotif, 0);
        Log.v("notify", String.valueOf(lastSync));

        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(lastNotif, System.currentTimeMillis());
        editor.commit();*/

        Log.v(mSemester, mNotif);
        if(!mSemester.equals("")){
            ScoreSyncAdapter.initializeSyncAdapter(this);
        }

        final Button masuk = (Button) findViewById(R.id.masuk);
        masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                String semester = prefs.getString(getString(R.string.pref_semester_key), "");
                Log.v("masuk", "klik");
                if(semester.equals("")){
                    Log.v("masuk klik", "Setting");
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }else{
                    Log.v("masuk klik", "Mhs");
                    Intent intent = new Intent(MainActivity.this, MhsActivity.class);
                    startActivity(intent);
                }
            }
        });

        final Button setting = (Button) findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        final Button about = (Button) findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        /*if(login == true){
            Intent intent = new Intent(MainActivity.this, MhsActivity.class);
            startActivity(intent);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                Date date = new Date();
                int month = date.getMonth();
                String semester;
                if(month >= 9 && month < 3){
                    semester = prefs.getString(getString(R.string.pref_semester_key), "");
                }else{
                    semester = prefs.getString(getString(R.string.pref_semester_key), "");
                }
                Log.v(LOG_TAG, semester);
                if(semester.equals("")){
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(MainActivity.this, MhsActivity.class);
                    startActivity(intent);
                }

            }
        });*/

    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String semester = prefs.getString(getString(R.string.pref_semester_key), "");

        if(!mSemester.equals(semester)){
            Log.v("onResume", "change");
            mSemester = semester;
            //ScoreSyncAdapter.initializeSyncAdapter(this);
        }
        /*if(login == true){
            Intent intent = new Intent(MainActivity.this, MhsActivity.class);
            startActivity(intent);
        }*/
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    public static class PlaceholderFragment extends Fragment{
        public PlaceholderFragment(){}

    }

}
