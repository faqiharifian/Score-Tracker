package com.digit.safian.scoretracker;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.digit.safian.scoretracker.data.ScoreContract;

/**
 * Created by faqih_000 on 4/28/2015.
 */
public class NilaiMhsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int NILAI_LOADER = 1;
    private NilaiAdapter mNilaiAdapter;

    public NilaiMhsFragment() {
        Log.v("NilaiMhsFragment", "created");
    }


    private void updateNilaiMhs(){
        Log.v("update: ","called");
        FetchNilaiMhsTask nilaiTask = new FetchNilaiMhsTask(getActivity());
        nilaiTask.execute("8");
    }

    @Override
    public void onStart(){
        super.onStart();
        updateNilaiMhs();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nilai_mhs, container, false);
        Log.v("CreateView", "called");
        String sortOrder = ScoreContract.NilaiEntry.COLUMN_MAHASISWA +" ASC";
        Uri nilaiUri = ScoreContract.NilaiEntry.buildNilaiUri(8);

        Cursor cur = getActivity().getContentResolver().query(nilaiUri, null, null, null, sortOrder);

        mNilaiAdapter = new NilaiAdapter(getActivity(), null, 0);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_nilai_mhs);
        listView.setAdapter(mNilaiAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        Log.v("onActivityCreated", "called");
        getLoaderManager().initLoader(NILAI_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v("onCreateLoader ", "called");
        Uri nilaiUri = ScoreContract.NilaiEntry.buildNilaiUri(8);
        return new CursorLoader(
                getActivity(),
                nilaiUri,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.v("onLoadFinished ", "called");
        mNilaiAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.v("onLoaderReset ", "called");
        mNilaiAdapter.swapCursor(null);
    }

}
