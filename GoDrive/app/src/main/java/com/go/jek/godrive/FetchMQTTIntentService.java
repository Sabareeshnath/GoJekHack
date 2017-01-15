package com.go.jek.godrive;

import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;

/**
 * Created by kumardev on 1/15/2017.
 */

public class FetchMQTTIntentService extends IntentService {

    protected ResultReceiver mReceiver;

    public FetchMQTTIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
