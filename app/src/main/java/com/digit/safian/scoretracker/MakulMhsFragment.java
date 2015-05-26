package com.digit.safian.scoretracker;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.digit.safian.scoretracker.data.ScoreContract;
import com.digit.safian.scoretracker.sync.ScoreSyncAdapter;


public class MakulMhsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int MAKUL_LOADER = 0;
    private MakulAdapter mMakulAdapter;

    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";

    private static Menu optionsMenu;


    public interface Callback{

        public void onItemSelected(Uri nilaiUri, String title);
    }

    public MakulMhsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Add this line in order for this fragment to handle menu events
        setHasOptionsMenu(true);

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
    }

    void onSemesterChanged() {
        updateMakulMhs();
        getLoaderManager().restartLoader(MAKUL_LOADER, null, this);
    }

    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mhs, container, false);


        mMakulAdapter = new MakulAdapter(getActivity(), null, 0);

        mListView = (ListView) rootView.findViewById(R.id.listview_makul_mhs);
        mListView.setAdapter(mMakulAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                TextView titleView = (TextView) view;
                String title = titleView.getText().toString();
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String makulId = cursor.getString(cursor.getColumnIndex(ScoreContract.MakulEntry.COLUMN_ID_MAKUL));
                    ((Callback) getActivity())
                            .onItemSelected(ScoreContract.NilaiEntry.buildNilaiJudulUri(makulId), title);
                }

                mPosition = position;

            }
        });

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        setRefreshState(true);
        getLoaderManager().initLoader(MAKUL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){

        if(mPosition != ListView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String semester = Utility.getPreferredSemester(getActivity());
        Uri makulUri = ScoreContract.MakulEntry.buildMakulWithSemesterUri(semester);
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
        if(mPosition != ListView.INVALID_POSITION){
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMakulAdapter.swapCursor(null);
    }
}