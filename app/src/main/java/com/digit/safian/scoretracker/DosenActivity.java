package com.digit.safian.scoretracker;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class DosenActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dosen);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MakulDosenFragment())
                    .commit();
        }
    }




    /**
     * A placeholder fragment containing a simple view.
     */

}
