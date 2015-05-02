package com.digit.safian.scoretracker;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Date;


public class MainActivity extends ActionBarActivity {
    Boolean login = true;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    protected void onCreate(Bundle savedInstanceState) {
        if(login == true){
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
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        if(login == true){
            Intent intent = new Intent(MainActivity.this, MhsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
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
    }

    public static class PlaceholderFragment extends Fragment{
        public PlaceholderFragment(){}

    }

}
