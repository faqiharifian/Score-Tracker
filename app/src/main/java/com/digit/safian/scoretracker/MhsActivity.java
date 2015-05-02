package com.digit.safian.scoretracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;


public class MhsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String semester = prefs.getString(getString(R.string.pref_semester_key), "");
        if(semester.equals("")){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }else{
            setContentView(R.layout.activity_mhs);
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new MakulMhsFragment())
                        .commit();
            }
        }

    }


}
