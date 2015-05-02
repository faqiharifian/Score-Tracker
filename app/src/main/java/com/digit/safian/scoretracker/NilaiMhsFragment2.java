package com.digit.safian.scoretracker;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.digit.safian.scoretracker.data.ScoreContract;


/**
 * Created by faqih_000 on 4/28/2015.
 */
public class NilaiMhsFragment2 extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int NILAI_LOADER = 1;
    private NilaiAdapter mNilaiAdapter;
    long makulId = -1;



    public NilaiMhsFragment2() {
        //Log.v("NilaiMhsFragment", "created");
    }

    private void updateNilaiMhs(){
        //Log.v("update: ","called");
        NilaiMhsActivity.setRefreshState(true);
        FetchNilaiMhsTask nilaiTask = new FetchNilaiMhsTask(getActivity());
        nilaiTask.execute(String.valueOf(makulId));
    }

    @Override
    public void onStart(){

        super.onStart();
        updateNilaiMhs();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateNilaiMhs();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nilai_mhs, container, false);
        //Log.v("CreateView", "called");


        Bundle bundle = getArguments();
        if(bundle != null) {
            makulId = bundle.getLong("makulId");
        }
        //Log.v("makulId", String.valueOf(makulId));

        //Uri nilaiUri = ScoreContract.NilaiEntry.buildNilaiUri(makulId);

        //Cursor cur = getActivity().getContentResolver().query(nilaiUri, null, null, null, sortOrder);

        mNilaiAdapter = new NilaiAdapter(getActivity(), null, 0);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_nilai_mhs);
        listView.setAdapter(mNilaiAdapter);

        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        //Log.v("onActivityCreated", "called");
        getLoaderManager().initLoader(10, null, this);
        getLoaderManager().initLoader(11, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Log.v("onCreateLoader ", "called");
        CursorLoader cL;
        Uri nilaiUri = ScoreContract.NilaiEntry.buildNilaiMakulUri(String.valueOf(makulId));
        if(i == 10){
            cL = new CursorLoader(
                    getActivity(),
                    nilaiUri,
                    null,
                    ScoreContract.NilaiEntry.COLUMN_JUDUL + " = ?",
                    new String[]{"uts"},
                    ScoreContract.NilaiEntry.COLUMN_MAHASISWA + " ASC"
            );
        }else{
            cL = new CursorLoader(
                    getActivity(),
                    nilaiUri,
                    null,
                    ScoreContract.NilaiEntry.COLUMN_JUDUL + " = ?",
                    new String[]{"uas"},
                    ScoreContract.NilaiEntry.COLUMN_MAHASISWA + " ASC"
            );
        }




        return cL;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        //Log.v("onLoadFinished ", "called");
        mNilaiAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        //Log.v("onLoaderReset ", "called");
        mNilaiAdapter.swapCursor(null);
    }

}
