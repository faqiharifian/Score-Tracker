package com.digit.safian.scoretracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;


public class NilaiMhsActivity extends ActionBarActivity{
    private final String FragmentTag = "NMF";
    private static Menu optionsMenu;

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

    public static void setRefreshState(final boolean refreshing){
        if(optionsMenu != null){
            final MenuItem refreshItem = optionsMenu.findItem(R.id.action_refresh);
            if(refreshItem != null){
                if(refreshing){
                    refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                }else{
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nilai_mhs, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        ShareActionProvider mShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(mShareAction != null){
            mShareAction.setShareIntent(createShareNilaiIntent());
        }
        return true;
    }

    private Intent createShareNilaiIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        //shareIntent.putExtra(Intent.EXTRA_TEXT);
        return shareIntent;
    }


    /**
     * A placeholder fragment containing a simple view.
     */



}
