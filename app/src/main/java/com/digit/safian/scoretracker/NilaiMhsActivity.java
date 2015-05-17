package com.digit.safian.scoretracker;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class NilaiMhsActivity extends ActionBarActivity{
    //private final String FragmentTag = "NMF";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //long makulId = getIntent().getExtras().getLong("makulId");
        /*String title = getIntent().getExtras().getString("title");
        setTitle(title);*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nilai_mhs);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {

            Bundle args = new Bundle();
            args.putParcelable(NilaiMhsFragment.NILAI_URI, getIntent().getData());

            NilaiMhsFragment fragment = new NilaiMhsFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.nilai_mhs_container, fragment)
                    .commit();
        }
        //setContentView(new TableMainLayout(this));
    }






    /**
     * A placeholder fragment containing a simple view.
     */



}
