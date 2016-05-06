package com.android.liuzhuang.threadwatchdog;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * dump all threads information.
 * Created by liuzhuang on 16/5/5.
 */
public class ThreadDumper implements Handler.Callback {
    private HandlerThreadWrapper handlerThreadWrapper;
    private Set<DumpCallback> dumpCallbackSet;
    private boolean running = false;
    private Handler mainHandler;
    private int intervalMillis;

    private Runnable execute = new Runnable() {
        @Override
        public void run() {
            List<Thread> threads = dump();
            Message message = new Message();
            message.obj = threads;
            mainHandler.sendMessage(message);
            if (running) {
                handlerThreadWrapper.getHandler().postDelayed(execute, intervalMillis);
            }
        }
    };

    public static ThreadDumper getInstance() {
        return InstanceHolder.instance;
    }

    private ThreadDumper() {
        dumpCallbackSet = new HashSet<DumpCallback>();
        handlerThreadWrapper = new HandlerThreadWrapper("ThreadWatchDog");
        mainHandler = new Handler(Looper.getMainLooper(), this);
    }

    public ThreadDumper registerDumpCallback(DumpCallback callback) {
        if (callback == null) {
            return getInstance();
        }
        if (dumpCallbackSet == null) {
            dumpCallbackSet = new HashSet<DumpCallback>();
        }
        dumpCallbackSet.add(callback);
        return getInstance();
    }

    public ThreadDumper unregisterDumpCallback(DumpCallback callback) {
        if (callback == null || dumpCallbackSet == null) {
            return getInstance();
        }
        dumpCallbackSet.remove(callback);
        return getInstance();
    }

    public void startLooper(int intervalMillis) {
        this.intervalMillis = intervalMillis;
        if (!running) {
            running = true;
            handlerThreadWrapper.getHandler().post(execute);
        }
    }

    public void stopLooper() {
        if (running) {
            running = false;
            handlerThreadWrapper.getHandler().removeCallbacks(execute);
        }
    }

    /**
     * dump all thread currently.
     * @return
     */
    private List<Thread> dump() {
        ThreadGroup rootGroup = Thread.currentThread( ).getThreadGroup( );
        ThreadGroup parentGroup;
        while ( ( parentGroup = rootGroup.getParent() ) != null ) {
            rootGroup = parentGroup;
        }

        Thread[] threads = new Thread[ rootGroup.activeCount() ];
        while ( rootGroup.enumerate( threads, true ) == threads.length ) {
            threads = new Thread[ threads.length * 2 ];
        }

        List<Thread> threadList = new ArrayList<Thread>();
        for (int i = 0; i < threads.length; i++) {
            if (threads[i] != null) {
                threadList.add(threads[i]);
                if (LogUtil.isShow()) {
                    LogUtil.e("thread " + threads[i].getName(), "isAlive = " + threads[i].isAlive() +
                            " isDaemon = "+threads[i].isDaemon() + " isInterrupted = "+threads[i].isInterrupted() +
                            " Priority = "+threads[i].getPriority() + " state = " + threads[i].getState());
                }
            }
        }

        return threadList;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (dumpCallbackSet != null && !dumpCallbackSet.isEmpty() &&
                msg != null && msg.obj != null && msg.obj instanceof List) {
            for (DumpCallback aDumpCallbackSet : dumpCallbackSet) {
                aDumpCallbackSet.dumpFinish((List<Thread>) msg.obj);
            }
        }
        return true;
    }

    private static class InstanceHolder {
        private static ThreadDumper instance = new ThreadDumper();
    }

    public interface DumpCallback {
        void dumpFinish(List<Thread> threads);
    }

}
