package com.android.liuzhuang.threadwatchdog.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.android.liuzhuang.threadwatchdog.utils.HandlerThreadWrapper;
import com.android.liuzhuang.threadwatchdog.utils.LogUtil;

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
    private Set<DumpCallback> onceDumpCallbackSet;
    private Set<DumpCallback> looperDumpCallbackSet;
    private boolean running = false;
    private Handler mainHandler;
    private int intervalMillis;
    private final int MSG_LOOPER = 0;
    private final int MSG_ONCE = 1;

    private Runnable looperDump = new Runnable() {
        @Override
        public void run() {
            List<Thread> threads = dump(null);
            Message message = new Message();
            message.what = MSG_LOOPER;
            message.obj = threads;
            mainHandler.sendMessage(message);
            if (running) {
                handlerThreadWrapper.getHandler().postDelayed(looperDump, intervalMillis);
            }
        }
    };

    public static ThreadDumper getInstance() {
        return InstanceHolder.instance;
    }

    private ThreadDumper() {
        onceDumpCallbackSet = new HashSet<DumpCallback>();
        looperDumpCallbackSet = new HashSet<DumpCallback>();
        handlerThreadWrapper = new HandlerThreadWrapper("ThreadWatchDog");
        mainHandler = new Handler(Looper.getMainLooper(), this);
    }

    public ThreadDumper registerLooperDumpCallback(DumpCallback callback) {
        if (callback == null) {
            return getInstance();
        }
        looperDumpCallbackSet.add(callback);
        return getInstance();
    }

    public ThreadDumper registerOnceDumpCallback(DumpCallback callback) {
        if (callback == null) {
            return getInstance();
        }
        onceDumpCallbackSet.add(callback);
        return getInstance();
    }

    public ThreadDumper unregisterDumpCallback(DumpCallback callback) {
        if (callback == null) {
            return getInstance();
        }
        if (onceDumpCallbackSet != null) {
            onceDumpCallbackSet.remove(callback);
        }
        if (looperDumpCallbackSet != null) {
            looperDumpCallbackSet.remove(callback);
        }
        return getInstance();
    }

    public void startLooper(int intervalMillis) {
        this.intervalMillis = intervalMillis;
        running = true;
        handlerThreadWrapper.getHandler().removeCallbacks(looperDump);
        handlerThreadWrapper.getHandler().post(looperDump);
    }

    public void dumpOnce(final ThreadFilter filter) {

        handlerThreadWrapper.getHandler().post(new Runnable() {
            @Override
            public void run() {
                List<Thread> threads = dump(filter);
                Message message = new Message();
                message.obj = threads;
                message.what = MSG_ONCE;
                mainHandler.sendMessage(message);
            }
        });
    }

    public void stopLooper() {
        if (running) {
            running = false;
            handlerThreadWrapper.getHandler().removeCallbacks(looperDump);
        }
    }

    /**
     * dump all thread currently.
     * @return
     */
    private List<Thread> dump(ThreadFilter filter) {
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
            if (threads[i] != null && !threadList.contains(threads[i])) {
                if (filter != null) {
                    if (filter.noDaemonThreads && !threads[i].isDaemon()) {
                        threadList.add(threads[i]);
                    } else if (filter.state != null && threads[i].getState() == filter.state) {
                        threadList.add(threads[i]);
                    }
                } else {
                    threadList.add(threads[i]);
                }
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
        if (msg != null && msg.obj != null && msg.obj instanceof List) {
            if (msg.what == MSG_LOOPER) {
                for (DumpCallback aDumpCallbackSet : looperDumpCallbackSet) {
                    aDumpCallbackSet.onDumpFinish((List<Thread>) msg.obj);
                }
            } else if (msg.what == MSG_ONCE) {
                for (DumpCallback aDumpCallbackSet : onceDumpCallbackSet) {
                    aDumpCallbackSet.onDumpFinish((List<Thread>) msg.obj);
                }
            }
        }
        return true;
    }

    private static class InstanceHolder {
        private static ThreadDumper instance = new ThreadDumper();
    }

    public interface DumpCallback {
        void onDumpFinish(List<Thread> threads);
    }

}
