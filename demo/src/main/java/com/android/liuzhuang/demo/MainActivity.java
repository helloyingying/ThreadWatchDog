package com.android.liuzhuang.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.liuzhuang.threadwatchdog.ThreadWatchDog;
import com.android.liuzhuang.threadwatchdog.ThreadWatchDogReceiver;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_thread:
                Random random = new Random();
                final int x = random.nextInt(9) + 1;
                if (x % 2 == 0) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                Log.d("test" + x, "running...");
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    thread.setPriority(x);
                    thread.start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                                Log.d("test" + x, "running...");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }, "test" + x).start();
                }
                break;
            case R.id.start_check:
                ThreadWatchDog.getInstance().start(getApplicationContext());
                break;
            case R.id.stop_check:
                Intent intent = new Intent(ThreadWatchDogReceiver.CLOSE_ACTION);
                sendBroadcast(intent);
                break;
            default:
                break;
        }
    }
}
