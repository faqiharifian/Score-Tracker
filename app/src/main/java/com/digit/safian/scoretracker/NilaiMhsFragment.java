package com.digit.safian.scoretracker;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TableLayout;

import com.digit.safian.scoretracker.data.ScoreContract;

import java.util.List;


/**
 * Created by faqih_000 on 4/28/2015.
 */
public class NilaiMhsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Cursor>>{
    private static final int NILAI_LOADER = 1;
    private NilaiAdapter mNilaiAdapter;
    long makulId = -1;
    private static Menu optionsMenu;
    private String mPath;
    private ShareActionProvider mShareActionProvider;


    public NilaiMhsFragment() {
        //Log.v("NilaiMhsFragment", "created");
    }

    private void updateNilaiMhs(){
        //Log.v("update: ","called");
        setRefreshState(true);
        FetchNilaiMhsTask nilaiTask = new FetchNilaiMhsTask(getActivity());
        nilaiTask.execute(String.valueOf(makulId));
    }

    @Override
    public void onStart(){

        super.onStart();
        updateNilaiMhs();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        optionsMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_nilai_mhs, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(mPath != null){
            mShareActionProvider.setShareIntent(createShareNilaiIntent());
        }
    }

    private Intent createShareNilaiIntent(){
        Uri uri = Uri.parse(mPath);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharingIntent.setType("image/png");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(sharingIntent,
                "Share image using"));
        //shareIntent.putExtra(Intent.EXTRA_TEXT);
        return sharingIntent;
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

        mNilaiAdapter = new NilaiAdapter(getActivity());

        ListView listView = (ListView) rootView.findViewById(R.id.listview_nilai_mhs);
        listView.setAdapter(mNilaiAdapter);

        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        //Log.v("onActivityCreated", "called");
        getLoaderManager().initLoader(10, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<List<Cursor>> onCreateLoader(int i, Bundle bundle) {
        //Log.v("onCreateLoader ", "called");
        Uri nilaiUri = ScoreContract.NilaiEntry.buildNilaiJudulUri(String.valueOf(makulId));
        Cursor c = getActivity().getContentResolver().query(
                nilaiUri,
                new String[]{ScoreContract.NilaiEntry._ID,ScoreContract.NilaiEntry.COLUMN_ID_MAKUL, ScoreContract.NilaiEntry.COLUMN_JUDUL},
                null,
                null,
                ScoreContract.NilaiEntry._ID
                );
        /*c.moveToFirst();
        int k = 0;
        while(!c.isAfterLast()){
            for(int j=0; j<c.getColumnCount(); j++){
                Log.v("record["+k+"], column[" + j + "]", c.getString(j));
            }
            c.moveToNext();
            ++k;
        }*/
        return new NilaiLoader(getActivity(), c);
        //return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Cursor>> cursorLoader, List<Cursor> cursor) {
        //Log.v("onLoadFinished ", "called");
        mNilaiAdapter.setData(cursor);

        setRefreshState(false);
        TableLayout tableMessage = (TableLayout) getActivity().findViewById(R.id.tabel_nilai);
        Bitmap cs = null;
        tableMessage.setDrawingCacheEnabled(true);
        tableMessage.buildDrawingCache(true);
        cs = Bitmap.createBitmap(tableMessage.getDrawingCache());
        Canvas canvas = new Canvas(cs);
        tableMessage.draw(canvas);
        canvas.save();
        tableMessage.setDrawingCacheEnabled(false);
        mPath = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), cs,
                "MyTableOutput", null);

        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareNilaiIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Cursor>> cursorLoader) {
        //Log.v("onLoaderReset ", "called");
        mNilaiAdapter.setData(null);
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

}
