package com.digit.safian.scoretracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.digit.safian.scoretracker.sync.ScoreSyncAdapter;


public class MhsActivity extends ActionBarActivity implements MakulMhsFragment.Callback{
    private ProgressDialog progressDialog;
    private final String NILAIFRAGMENT_TAG = "NFTAG";

    private boolean mTwoPane;
    private String mSemester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);;
        mSemester = Utility.getPreferredSemester(this);

        setContentView(R.layout.activity_mhs);
        if(findViewById(R.id.nilai_mhs_container) != null){
            mTwoPane = true;

            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nilai_mhs_container, new NilaiMhsFragment(), NILAIFRAGMENT_TAG)
                        .commit();
            }
        }else{
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        Log.v("onCreateView", "instant progress");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("retrieving...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_mhs,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar Item clicks here, the action bar will
        // automatically handle clicks on the Home/up button, so long
        // as you specify a parent activity in AndroidManifest.xml
        int id = item.getItemId();
        if (id == com.digit.safian.scoretracker.R.id.action_refresh){
            updateMakulMhs();
            return true;
        }else if(id == R.id.action_settings){
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        String semester = Utility.getPreferredSemester(this);

        if(semester != null && !semester.equals(mSemester)){
            MakulMhsFragment makul = (MakulMhsFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_makul);
            if(null != makul){
                makul.onSemesterChanged();
            }
            NilaiMhsFragment nilai = (NilaiMhsFragment)getSupportFragmentManager().findFragmentByTag(NILAIFRAGMENT_TAG);
            if(null != nilai){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nilai_mhs_container, new NilaiMhsFragment(), NILAIFRAGMENT_TAG)
                        .commit();
            }
            mSemester = semester;
        }
    }

    @Override
    public void onItemSelected(Uri contentUri, String title){
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(NilaiMhsFragment.NILAI_URI, contentUri);

            NilaiMhsFragment fragment = new NilaiMhsFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nilai_mhs_container, fragment, NILAIFRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this, NilaiMhsActivity.class)
                    .setData(contentUri)
                    .putExtra("title", title);
            startActivity(intent);
        }
    }


    private void updateMakulMhs(){
        ScoreSyncAdapter.syncImmediately(this);

    }
}
