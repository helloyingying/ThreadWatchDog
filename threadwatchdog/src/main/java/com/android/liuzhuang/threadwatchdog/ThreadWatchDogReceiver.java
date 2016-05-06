package com.android.liuzhuang.threadwatchdog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ThreadWatchDogReceiver extends BroadcastReceiver {
    public static final String CLOSE_ACTION = "THREAD_WATCH_DOG_CLOSE";

    public ThreadWatchDogReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (CLOSE_ACTION.equals(intent.getAction())) {
            Intent stopIntent = new Intent(context, ThreadWatchDogService.class);
            context.stopService(stopIntent);
        }
    }
}
