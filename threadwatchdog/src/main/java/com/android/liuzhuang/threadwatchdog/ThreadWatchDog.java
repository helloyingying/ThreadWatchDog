package com.android.liuzhuang.threadwatchdog;

import android.content.Context;
import android.content.Intent;

/**
 * Face of ThreadWatchDog
 * Created by liuzhuang on 16/5/5.
 */
public final class ThreadWatchDog {

    private ThreadWatchDog() {
    }

    public static ThreadWatchDog getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        private static ThreadWatchDog instance = new ThreadWatchDog();
    }

    public void start(Context context) {
        Context applicationContext = context.getApplicationContext();
        Intent startIntent = new Intent(applicationContext, ThreadWatchDogService.class);
        applicationContext.startService(startIntent);
    }
}
