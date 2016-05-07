package com.android.liuzhuang.threadwatchdog.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.liuzhuang.threadwatchdog.R;
import com.android.liuzhuang.threadwatchdog.adapter.ThreadListAdapter;
import com.android.liuzhuang.threadwatchdog.core.ThreadDumper;
import com.android.liuzhuang.threadwatchdog.core.ThreadFilter;

import java.util.List;

public class ThreadWatchDogActivity extends Activity implements ThreadDumper.DumpCallback {
    private ThreadListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_watch_dog);
        init();
    }

    private void init() {
        ListView listView = (ListView) findViewById(R.id.thread_list);
        adapter = new ThreadListAdapter(this);
        listView.setAdapter(adapter);
        ThreadDumper.getInstance().registerOnceDumpCallback(this).dumpOnce(null);
    }


    @Override
    public void onDumpFinish(List<Thread> threads) {
        adapter.setData(threads);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_thread_activity, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        ThreadFilter filter = new ThreadFilter();
        if (id == R.id.all_thread) {
            filter = null;
        } else if (id == R.id.no_daemon) {
            filter.noDaemonThreads = true;
        } else if (id == R.id.new_thread) {
            filter.state = Thread.State.NEW;
        } else if (id == R.id.runnable) {
            filter.state = Thread.State.RUNNABLE;
        } else if (id == R.id.blocked) {
            filter.state = Thread.State.BLOCKED;
        } else if (id == R.id.waiting) {
            filter.state = Thread.State.WAITING;
        } else if (id == R.id.timed_waiting) {
            filter.state = Thread.State.TIMED_WAITING;
        } else if (id == R.id.terminated) {
            filter.state = Thread.State.TERMINATED;
        }
        ThreadDumper.getInstance().registerOnceDumpCallback(this).dumpOnce(filter);

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThreadDumper.getInstance().unregisterDumpCallback(this);
    }
}
