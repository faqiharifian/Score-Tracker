package com.digit.safian.scoretracker;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class NilaiMhsActivity extends ActionBarActivity{
    private final String FragmentTag = "NMF";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        long makulId = getIntent().getExtras().getLong("makulId");
        String title = getIntent().getExtras().getString("title");
        setTitle(title);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nilai_mhs);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
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






    /**
     * A placeholder fragment containing a simple view.
     */



}
