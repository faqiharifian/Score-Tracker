package com.digit.safian.scoretracker;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

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
        mPm = getContext().getPackageManager();
    }
    @Override
    public List<Cursor> loadInBackground() {
        List<Cursor> entries = new ArrayList<Cursor>(this.cursor.getCount());

        this.cursor.moveToFirst();

        while(!this.cursor.isAfterLast()){
            String makulId = this.cursor.getString(this.cursor.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_ID_MAKUL));

            Uri uri = ScoreContract.NilaiEntry.buildNilaiMakulUri(makulId, this.cursor.getString(this.cursor.getColumnIndex(ScoreContract.NilaiEntry.COLUMN_JUDUL)));

            Cursor c = getContext().getContentResolver().query(uri, null, null, null, null);
            entries.add(c);
            this.cursor.moveToNext();
        }

        return entries;
    }

    @Override
    public void deliverResult(List<Cursor> nilai){
        if(isReset()){
            if(nilai.isEmpty()){
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
        }else{
            forceLoad();
        }/*else if(mNilai.isEmpty()){
            forceLoad();
        }*/
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

    //private List<Cursor> setCursor(){}
}
