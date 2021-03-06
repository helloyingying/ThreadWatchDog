package com.android.liuzhuang.threadwatchdog.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.liuzhuang.threadwatchdog.R;
import com.android.liuzhuang.threadwatchdog.ui.ThreadDetailActivity;
import com.android.liuzhuang.threadwatchdog.utils.StackUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * adapter of thread list.
 * Created by liuzhuang on 16/5/6.
 */
public class ThreadListAdapter extends BaseAdapter {
    private Context context;
    private List<Thread> data;
    private LayoutInflater inflater;

    public ThreadListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        data = new ArrayList<Thread>();
    }

    public void setData(List<Thread> data) {
        if (data == null || data.isEmpty()) {
            this.data.clear();
            Toast.makeText(context, "no threads", Toast.LENGTH_SHORT).show();
            return;
        }
        this.data.clear();
        this.data.addAll(data);
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Thread getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ThreadViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.thread_card, parent, false);
            holder = new ThreadViewHolder(convertView, context);
            convertView.setTag(holder);
        } else {
            holder = (ThreadViewHolder) convertView.getTag();
        }
        holder.refresh(getItem(position));
        return convertView;
    }

    public static class ThreadViewHolder {
        private TextView title;
        private TextView detail;
        private View itemView;
        private Context context;
        public ThreadViewHolder(View itemView,Context context) {
            this.context = context;
            this.itemView = itemView;
            title = (TextView) itemView.findViewById(R.id.card_title);
            detail = (TextView) itemView.findViewById(R.id.card_detail);
        }

        public void refresh(final Thread thread) {
            if (thread != null) {
                title.setText(thread.getName());
                final String stackStr = StackUtil.getStackTrace(thread);
                detail.setText(stackStr);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ThreadDetailActivity.class);
                        intent.putExtra(ThreadDetailActivity.EXTRA_KEY_TITLE, thread.getName());
                        intent.putExtra(ThreadDetailActivity.EXTRA_KEY_STACK, stackStr);
                        context.startActivity(intent);
                    }
                });
            }
        }
    }
}
