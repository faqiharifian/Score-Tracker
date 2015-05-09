package com.digit.safian.scoretracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.digit.safian.scoretracker.data.ScoreContract;
import com.digit.safian.scoretracker.sync.ScoreSyncAdapter;

//import android.content.Loader;
//import android.support.v4.content.Loader;

public class MakulMhsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int MAKUL_LOADER = 0;
    private MakulAdapter mMakulAdapter;
    private String mSemester;
    private static Menu optionsMenu;

    public MakulMhsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSemester = prefs.getString(getString(R.string.pref_semester_key), "");
        // Add this line in order for this fragment to handle menu events
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        this.optionsMenu = menu;
        inflater.inflate(R.menu.menu_mhs,menu);
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
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }else if(id == R.id.action_about){
            startActivity(new Intent(getActivity(), AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void updateMakulMhs(){
        setRefreshState(true);
        ScoreSyncAdapter.syncImmediately(getActivity());
        /*Intent intent = new Intent(getActivity(), MakulService.class);
        int semesterInt = Integer.parseInt(mSemester);
        intent.putExtra(MakulService.SEMESTER_QUERY_EXTRA, mSemester);
        getActivity().startService(intent);
        if(semesterInt % 2 == 0 && semesterInt >= 6){
            Intent newIntent = new Intent(getActivity(), MakulService.class);
            newIntent.putExtra(MakulService.SEMESTER_QUERY_EXTRA, "8");
            getActivity().startService(newIntent);
        }else if(semesterInt % 2 == 1 && semesterInt >= 5){
            Intent newIntent = new Intent(getActivity(), MakulService.class);
            newIntent.putExtra(MakulService.SEMESTER_QUERY_EXTRA, "7");
            getActivity().startService(newIntent);
        }*/
        /*FetchMakulMhsTask makulTask = new FetchMakulMhsTask(getActivity());
        int semesterInt = Integer.parseInt(mSemester);
        makulTask.execute(mSemester);
        if(semesterInt % 2 == 0 && semesterInt >= 6){
            FetchMakulMhsTask newMakulTask = new FetchMakulMhsTask(getActivity());
            newMakulTask.execute("8");
        }else if(semesterInt % 2 == 1 && semesterInt >= 5){
            FetchMakulMhsTask newMakulTask = new FetchMakulMhsTask(getActivity());
            newMakulTask.execute("7");
        }*/
    }

    void onLocationChanged( ) {
        updateMakulMhs();
        getLoaderManager().restartLoader(MAKUL_LOADER, null, this);
    }

    @Override
    public void onStart(){
        super.onStart();
        //updateMakulMhs();
    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String semester = prefs.getString(getString(R.string.pref_semester_key), "");
        if (semester != null && !semester.equals(mSemester)) {
            mSemester = semester;
            onLocationChanged();
        }
        setRefreshState(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mhs, container, false);
        String sortOrder = ScoreContract.MakulEntry.COLUMN_ID_MAKUL + " ASC";
        Uri makulUri = ScoreContract.MakulEntry.CONTENT_URI;

        Cursor cur = getActivity().getContentResolver().query(makulUri, null, null, null, sortOrder);

        mMakulAdapter = new MakulAdapter(getActivity(), null, 0);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_makul_mhs);
        listView.setAdapter(mMakulAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                long makul = view.getId();
                TextView titleView = (TextView) view;
                String title = titleView.getText().toString();
                //long makul = mMakulAdapter.getItemId(position);
                Log.v("makul id clicked", String.valueOf(makul));
                Intent intent = new Intent(getActivity(), NilaiMhsActivity.class)
                        .putExtra("makulId", makul)
                        .putExtra("title", title);
                //Intent intent = new Intent(getActivity(), NilaiMhsActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(MAKUL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri makulUri = ScoreContract.MakulEntry.buildMakulWithSemesterUri(mSemester);
        return new CursorLoader(
                getActivity(),
                makulUri,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mMakulAdapter.swapCursor(cursor);
        setRefreshState(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMakulAdapter.swapCursor(null);
    }
}