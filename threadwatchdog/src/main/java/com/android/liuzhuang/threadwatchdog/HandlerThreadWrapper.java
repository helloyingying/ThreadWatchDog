package com.android.liuzhuang.threadwatchdog;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Wrapper of HandlerThread
 * Created by liuzhuang on 16/5/5.
 */
public class HandlerThreadWrapper {
    private Handler handler = null;

    public HandlerThreadWrapper(String name) {
        HandlerThread handlerThread = new HandlerThread(name);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    public Handler getHandler() {
        return handler;
    }
}
