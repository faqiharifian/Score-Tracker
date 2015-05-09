package com.digit.safian.scoretracker.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by faqih_000 on 5/9/2015.
 */
public class ScoreAuthenticatorService extends Service{
    private ScoreAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new ScoreAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
