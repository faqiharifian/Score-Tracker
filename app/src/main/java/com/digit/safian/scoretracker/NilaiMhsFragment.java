package com.digit.safian.scoretracker;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.digit.safian.scoretracker.data.ScoreContract;
import com.digit.safian.scoretracker.service.NilaiService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by faqih_000 on 4/28/2015.
 */
public class NilaiMhsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Cursor>>{
    static final String NILAI_URI = "URI";
    private static final int NILAI_LOADER = 1;
    private NilaiAdapter mNilaiAdapter;
    long makulId = -1;
    private static Menu optionsMenu;
    private String mPath;
    private ShareActionProvider mShareActionProvider;
    private Set<String> header;
    private Cursor c;

    private Uri mUri;


    public NilaiMhsFragment() {
        //Log.v("NilaiMhsFragment", "created");
        header = new HashSet<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nilai_mhs, container, false);
        //Log.v("CreateView", "called");

        Bundle args = getArguments();
        if(args != null){
            mUri = args.getParcelable(NILAI_URI);
        }
        //makulId = getActivity().getIntent().getExtras().getLong("makulId");
        /*Bundle bundle = getArguments();
        if(bundle != null) {
            makulId = bundle.getLong("makulId");
        }*/
        //Log.v("makulId", String.valueOf(makulId));

        //Uri nilaiUri = ScoreContract.NilaiEntry.buildNilaiUri(makulId);

        //Cursor cur = getActivity().getContentResolver().query(nilaiUri, null, null, null, sortOrder);

        //Uri nilaiUri = ScoreContract.NilaiEntry.buildNilaiJudulUri(String.valueOf(makulId));
        if(null != mUri) {
            c = getActivity().getContentResolver().query(
                    //nilaiUri,
                    mUri,
                    new String[]{ScoreContract.NilaiEntry.COLUMN_ID_MAKUL, ScoreContract.NilaiEntry.COLUMN_JUDUL},
                    null,
                    null,
                    ScoreContract.NilaiEntry._ID
            );
        }

        mNilaiAdapter = new NilaiAdapter(getActivity());


        ListView listView = (ListView) rootView.findViewById(R.id.listview_nilai_mhs);
        listView.setAdapter(mNilaiAdapter);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events
        setHasOptionsMenu(true);

    }

    private void updateNilaiMhs(){
        //Log.v("update: ","called");

        Intent intent = new Intent(getActivity(), NilaiService.class);
        intent.putExtra(NilaiService.MAKUL_EXTRA, String.valueOf(makulId));
        getActivity().startService(intent);
    }

    @Override
    public void onStart(){
        super.onStart();
        //updateNilaiMhs();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        optionsMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_nilai_mhs, menu);
        /*MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(mPath != null){
            mShareActionProvider.setShareIntent(createShareNilaiIntent());
        }*/
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
        }else if(id == R.id.action_share){
            createShareNilaiIntent();
        }

        return super.onOptionsItemSelected(item);
    }



    public void setHeader(Cursor c){
        final float scale = getActivity().getResources().getDisplayMetrics().density;

        View view = getView();
        TableRow tableRow = (TableRow) view.findViewById(R.id.header);

        TextView namaView = new TextView(getActivity());
        namaView.setText("NAMA");
        namaView.setGravity(Gravity.CENTER);
        namaView.setWidth((int) (150*scale));
        namaView.setTextColor(Color.WHITE);
        tableRow.addView(namaView);

        c.moveToFirst();
        while(!c.isAfterLast()){
            Log.v("cursor judul", c.getString(c.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_JUDUL)));
            //header.add(c.getString(c.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_JUDUL)));
            TextView textView = new TextView(getActivity());
            textView.setText(c.getString(c.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_JUDUL)).toUpperCase());
            textView.setGravity(Gravity.CENTER);
            textView.setWidth((int) (75*scale));
            textView.setTextColor(Color.WHITE);
            tableRow.addView(textView);
            c.moveToNext();
        }

        /*for(String head : header){

        }*/
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        //Log.v("onActivityCreated", "called");
        if(null != c) {
            setHeader(c);
        }
        getLoaderManager().initLoader(10, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<List<Cursor>> onCreateLoader(int i, Bundle bundle) {
        //Log.v("onCreateLoader ", "called");

        /*c.moveToFirst();
        int k = 0;
        while(!c.isAfterLast()){
            for(int j=0; j<c.getColumnCount(); j++){
                Log.v("record["+k+"], column[" + j + "]", c.getString(j));
            }
            c.moveToNext();
            ++k;
        }*/
        if(null != c) {
            NilaiLoader nilaiLoader = new NilaiLoader(getActivity(), c);
            //nilaiLoader.forceLoad();
            return nilaiLoader;
        }else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Cursor>> cursorLoader, List<Cursor> cursor) {
        //Log.v("onLoadFinished ", "called");
        mNilaiAdapter.setData(cursor);

    }

    @Override
    public void onLoaderReset(Loader<List<Cursor>> cursorLoader) {
        //Log.v("onLoaderReset ", "called");
        mNilaiAdapter.setData(null);
    }


    private Intent createShareNilaiIntent(){

        /*TableLayout tableMessage = (TableLayout) getActivity().findViewById(R.id.tabel_nilai);
        Bitmap cs = null;
        tableMessage.setDrawingCacheEnabled(true);
        tableMessage.buildDrawingCache(true);
        cs = Bitmap.createBitmap(tableMessage.getDrawingCache());
        Canvas canvas = new Canvas(cs);
        tableMessage.draw(canvas);*/
        View theView = getView();
        Bitmap b = null;
        theView.setDrawingCacheEnabled(true);
        theView.buildDrawingCache(true);
        b = Bitmap.createBitmap(theView.getDrawingCache());
        Canvas c = new Canvas(b);
        theView.draw(c);
        c.save();
        theView.setDrawingCacheEnabled(false);
        mPath = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), b,
                "MyTableOutput", null);


        Uri uri = Uri.parse(mPath);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharingIntent.setType("image/png");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(sharingIntent,
                "Share image using"));
        //shareIntent.putExtra(Intent.EXTRA_TEXT);
        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareNilaiIntent());
        }
        return sharingIntent;
    }

}
