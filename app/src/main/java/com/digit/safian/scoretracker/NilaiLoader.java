package com.digit.safian.scoretracker;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

/**
 * Created by faqih_000 on 5/1/2015.
 */
public class NilaiLoader  extends AsyncTaskLoader<List<Cursor>>{
    private static final String TAG = "Nilai Loader";

    final PackageManager mPm;

    private List<Cursor> mNilai;

    public NilaiLoader(Context context){
        super(context);
        mPm = getContext().getPackageManager();
    }
    @Override
    public List<Cursor> loadInBackground() {
        return null;
    }
}
