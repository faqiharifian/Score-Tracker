package com.digit.safian.scoretracker.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by faqih_000 on 5/9/2015.
 */
public class ScoreSyncService extends Service{
    private static final Object sSyncAdapterLock = new Object();
    private static ScoreSyncAdapter sScoreSyncAdapter = null;
    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock){
            if(sScoreSyncAdapter == null){
                sScoreSyncAdapter = new ScoreSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sScoreSyncAdapter.getSyncAdapterBinder();
    }
}
