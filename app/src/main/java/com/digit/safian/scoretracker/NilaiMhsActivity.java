package com.digit.safian.scoretracker;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class NilaiMhsActivity extends ActionBarActivity{
    private final String FragmentTag = "NMF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long makulId = getIntent().getExtras().getLong("makulId");
        Log.v("mhs nilai act", String.valueOf(makulId));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nilai_mhs);
        if (savedInstanceState == null) {

            Bundle args = new Bundle();
            args.putLong("makulId", makulId);
            NilaiMhsFragment fragment = new NilaiMhsFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment, FragmentTag)
                    .commit();
        }
        //setContentView(new TableMainLayout(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nilai_mhs, menu);
        return true;
    }



    /**
     * A placeholder fragment containing a simple view.
     */



}
