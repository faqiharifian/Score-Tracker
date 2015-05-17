package com.digit.safian.scoretracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;


public class MhsActivity extends ActionBarActivity {
    private final String NILAIFRAGMENT_TAG = "NFTAG";

    private boolean mTwoPane;
    private String mSemester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSemester = prefs.getString(getString(R.string.pref_semester_key), "");

        if(findViewById(R.id.nilai_mhs_container) != null){
            mTwoPane = true;

            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nilai_mhs_container, new NilaiMhsFragment(), NILAIFRAGMENT_TAG)
                        .commit();
            }
        }else{
            mTwoPane = false;
        }

            /*setContentView(R.layout.activity_mhs);
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new MakulMhsFragment())
                        .commit();
            }*/


    }

    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String semester = prefs.getString(getString(R.string.pref_semester_key), "");

        if(semester != null){
            MakulMhsFragment makul = (MakulMhsFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_makul);
            if(null != makul && !semester.equals(mSemester)){
                makul.onSemesterChanged();
            }
        }
    }

    /*public void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String semester = prefs.getString(getString(R.string.pref_semester_key), "");
        if(semester.equals("")){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }else{
            ScoreSyncAdapter.initializeSyncAdapter(this);
            setContentView(R.layout.activity_mhs);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new MakulMhsFragment())
                        .commit();

        }
    }*/


}
