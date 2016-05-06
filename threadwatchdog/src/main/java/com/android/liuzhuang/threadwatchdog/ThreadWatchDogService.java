package com.android.liuzhuang.threadwatchdog;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import java.util.List;

public class ThreadWatchDogService extends Service implements ThreadDumper.DumpCallback {
    private final String TAG = "ThreadWatchDog";

    private int notifyId = 364736432;

    private NotificationManager notificationManager;

    private RemoteViews remoteViews;

    public ThreadWatchDogService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        remoteViews = new RemoteViews(getPackageName(), R.layout.notification_remote_view);
        ThreadDumper.getInstance().registerDumpCallback(this).startLooper(1000);
        LogUtil.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ThreadDumper.getInstance().stopLooper();
        if (notificationManager != null) {
            notificationManager.cancel(notifyId);
        }
        LogUtil.d(TAG, "onDestroy");
    }

    public void showNotification(int threadCount) {
        if (remoteViews == null) {
            return;
        }
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        Intent jumpIntent = new Intent(this, ThreadWatchDogActivity.class);
        PendingIntent jumpPendingIntent = PendingIntent.getActivity(this, 0,
                jumpIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setTextViewText(R.id.noti_info, threadCount + " threads are running...");
        Intent closeIntent = new Intent(ThreadWatchDogReceiver.CLOSE_ACTION);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.noti_close, closePendingIntent);

        builder.setContent(remoteViews)
                .setTicker("Thread Watch Dog")
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setContentIntent(jumpPendingIntent)
                .setSmallIcon(R.drawable.small_icon)
                .setPriority(NotificationCompat.PRIORITY_MIN);

        notificationManager.notify(notifyId, builder.build());
    }

    @Override
    public void dumpFinish(List<Thread> threads) {
        if (threads != null && !threads.isEmpty()) {
            showNotification(threads.size());
        }
    }
}
