package com.digit.safian.scoretracker;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.ShareActionProvider;
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

import java.util.List;


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
    private Cursor c;

    private Uri mUri;


    public NilaiMhsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nilai_mhs, container, false);

        Bundle args = getArguments();
        if(args != null){
            mUri = args.getParcelable(NILAI_URI);
        }
        if(null != mUri) {
            c = getActivity().getContentResolver().query(
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


    @Override
    public void onStart(){
        super.onStart();
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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_share){
            createShareNilaiIntent();
        }

        return super.onOptionsItemSelected(item);
    }



    public void setHeader(Cursor c){
        final float scale = getActivity().getResources().getDisplayMetrics().density;

        Typeface face = Typeface.create("sans-serif-condensed", Typeface.NORMAL);

        View view = getView();
        TableRow tableRow = (TableRow) view.findViewById(R.id.header);

        TextView namaView = new TextView(getActivity());
        namaView.setText("NAMA");
        namaView.setGravity(Gravity.CENTER);
        namaView.setWidth((int) (150 * scale));
        namaView.setTextColor(Color.WHITE);
        namaView.setTypeface(face);
        tableRow.addView(namaView);

        c.moveToFirst();
        while(!c.isAfterLast()){
            TextView textView = new TextView(getActivity());
            textView.setText(c.getString(c.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_JUDUL)).toUpperCase());
            textView.setGravity(Gravity.CENTER);
            textView.setWidth((int) (75 * scale));
            textView.setTextColor(Color.WHITE);
            textView.setTypeface(face);
            tableRow.addView(textView);

            c.moveToNext();
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        if(null != c) {
            setHeader(c);
        }
        getLoaderManager().initLoader(10, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<List<Cursor>> onCreateLoader(int i, Bundle bundle) {

        if(null != c) {
            NilaiLoader nilaiLoader = new NilaiLoader(getActivity(), c);
            return nilaiLoader;
        }else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Cursor>> cursorLoader, List<Cursor> cursor) {
        mNilaiAdapter.setData(cursor);

    }

    @Override
    public void onLoaderReset(Loader<List<Cursor>> cursorLoader) {
        mNilaiAdapter.setData(null);
    }


    private Intent createShareNilaiIntent(){
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
        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareNilaiIntent());
        }
        return sharingIntent;
    }

}
