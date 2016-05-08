package com.android.liuzhuang.threadwatchdog.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.android.liuzhuang.threadwatchdog.R;

public class ThreadDetailActivity extends Activity {

    public static final String EXTRA_KEY_TITLE = "thread_title";
    public static final String EXTRA_KEY_STACK = "thread_stack";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_detail);
        TextView title = (TextView) findViewById(R.id.title);
        TextView stack = (TextView) findViewById(R.id.stack);
        Intent intent = getIntent();
        String titleStr = intent.getStringExtra(EXTRA_KEY_TITLE);
        String stackStr = intent.getStringExtra(EXTRA_KEY_STACK);
        title.setText(titleStr);
        stack.setText(stackStr);
    }
}
