package com.digit.safian.scoretracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;

import com.digit.safian.scoretracker.sync.ScoreSyncAdapter;

/**
 * Created by faqih_000 on 5/12/2015.
 */
public class splashscreen extends Activity {

    private static int splashInterval = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String semester = prefs.getString(getString(R.string.pref_semester_key), "");

        if(!semester.equals("")){
            ScoreSyncAdapter.initializeSyncAdapter(this);
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splashscreen);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent i = new Intent(splashscreen.this, MainActivity.class);
                startActivity(i);
                this.finish();
            }
            private void finish() {
                // TODO Auto-generated method stub
            }
        }, splashInterval);
    };
}

