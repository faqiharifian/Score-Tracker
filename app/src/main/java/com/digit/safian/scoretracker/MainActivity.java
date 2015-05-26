package com.digit.safian.scoretracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.digit.safian.scoretracker.sync.ScoreSyncAdapter;


public class MainActivity extends Activity {
    private String mSemester;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSemester = Utility.getPreferredSemester(this);




        final Button start = (Button) findViewById(R.id.masuk);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, MhsActivity.class);
                startActivity(intent);
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

    }

    @Override
    public void onResume(){
        super.onResume();
        String semester = Utility.getPreferredSemester(this);

        if(!mSemester.equals(semester)){
            ScoreSyncAdapter.syncImmediately(this);
            mSemester = semester;
        }
    }

}
