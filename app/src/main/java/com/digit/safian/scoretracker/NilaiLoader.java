package com.digit.safian.scoretracker;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.digit.safian.scoretracker.data.ScoreContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by faqih_000 on 5/1/2015.
 */
public class NilaiLoader  extends AsyncTaskLoader<List<Cursor>>{
    private static final String TAG = "Nilai Loader";
    private Cursor cursor;
    final PackageManager mPm;

    private List<Cursor> mNilai;

    public NilaiLoader(Context context, Cursor c){
        super(context);
        this.cursor = c;
        Log.v("NilaiLoader", "created");
        /*c.moveToFirst();
        int k = 0;
        while(!this.cursor.isAfterLast()){
            for(int j=0; j<this.cursor.getColumnCount(); j++){
                Log.v("record["+k+"], column[" + this.cursor.getColumnName(j) + "]", this.cursor.getString(j));
            }
            this.cursor.moveToNext();
            ++k;
        }*/
        mPm = getContext().getPackageManager();
    }
    @Override
    public List<Cursor> loadInBackground() {
        Log.v("nilai loader", "load background called");
        List<Cursor> entries = new ArrayList<Cursor>(this.cursor.getCount());

        this.cursor.moveToFirst();
        int k = 0;

        while(!this.cursor.isAfterLast()){
            String makulId = this.cursor.getString(this.cursor.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_ID_MAKUL));
            /*for(int j=0; j<this.cursor.getColumnCount(); j++){
                Log.v("record["+k+"], column[" + j + "]", this.cursor.getString(j));
            }*/
            Uri uri = ScoreContract.NilaiEntry.buildNilaiMakulUri(makulId, this.cursor.getString(this.cursor.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_JUDUL)));
            String[] projection = new String[]{ScoreContract.NilaiEntry.COLUMN_ID_MAKUL, ScoreContract.NilaiEntry.COLUMN_JUDUL};
            Cursor c = getContext().getContentResolver().query(uri, null, null, null, null);
            entries.add(c);
            this.cursor.moveToNext();
            ++k;
        }
        /*while(!this.cursor.isAfterLast()){
            Uri uri = ScoreContract.NilaiEntry.buildNilaiMakulUri(makulId, this.cursor.getString(this.cursor.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_JUDUL)));
            String[] projection = new String[]{ScoreContract.NilaiEntry.COLUMN_ID_MAKUL, ScoreContract.NilaiEntry.COLUMN_JUDUL};
            Cursor c = getContext().getContentResolver().query(uri, projection, null, null, null);
            entries.add(c);
        }*/
        return entries;
    }

    @Override
    public void deliverResult(List<Cursor> nilai){
        if(isReset()){
            if(nilai != null){
                releaseResources(nilai);
                return;
            }
        }

        List<Cursor> oldNilai = mNilai;
        mNilai = nilai;

        if(isStarted()){
            super.deliverResult(nilai);
        }

        if(oldNilai != null && oldNilai != nilai){
            releaseResources(oldNilai);
        }
    }

    @Override
    protected void onStartLoading(){
        if(mNilai != null){
            deliverResult(mNilai);
        }

        /*if (mAppsObserver == null) {
      mAppsObserver = new InstalledAppsObserver(this);
    }

    if (mLocaleObserver == null) {
      mLocaleObserver = new SystemLocaleObserver(this);
    }*/
        if(takeContentChanged()){
            forceLoad();
        }else if(mNilai == null){
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading(){
        cancelLoad();
    }

    @Override
    protected void onReset(){
        onStopLoading();
        if(mNilai != null){
            releaseResources(mNilai);
            mNilai = null;
        }

        /*if (mAppsObserver != null) {
      getContext().unregisterReceiver(mAppsObserver);
      mAppsObserver = null;
    }

    if (mLocaleObserver != null) {
      getContext().unregisterReceiver(mLocaleObserver);
      mLocaleObserver = null;
    }*/
    }

    @Override
    public void onCanceled(List<Cursor> nilai){
        super.onCanceled(nilai);

        releaseResources(nilai);
    }

    @Override
    public void forceLoad(){
        super.forceLoad();
    }

    private void releaseResources(List<Cursor> nilai){
        for(int i=0;i<nilai.size();i++){
            nilai.get(i).close();
        }
    }
}
