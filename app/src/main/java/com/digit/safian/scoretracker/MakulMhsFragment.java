package com.digit.safian.scoretracker;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.digit.safian.scoretracker.data.ScoreContract;

//import android.content.Loader;
//import android.support.v4.content.Loader;

public class MakulMhsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int MAKUL_LOADER = 0;
    private MakulAdapter mMakulAdapter;
    public MakulMhsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMakulMhs(){
        FetchMakulMhsTask makulTask = new FetchMakulMhsTask(getActivity());
        makulTask.execute();
    }

    @Override
    public void onStart(){
        super.onStart();
        updateMakulMhs();
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
                String makul = (String) mMakulAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), NilaiMhsActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, makul);
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
        Uri makulUri = ScoreContract.MakulEntry.CONTENT_URI;
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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMakulAdapter.swapCursor(null);
    }
}