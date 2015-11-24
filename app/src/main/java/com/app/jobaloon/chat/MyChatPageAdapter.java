package com.app.jobaloon.chat;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.jobaloon.gson.ChatPageGSON;
import com.app.jobaloon.main.R;
import com.app.jobaloon.utils.HideKeyBoard;

import java.util.List;

/**
 * Created by SICS-Dpc2 on 02-Feb-15.
 */
public class MyChatPageAdapter extends ArrayAdapter<ChatPageGSON.MessageData> {
    private String user_id;
    private LayoutInflater mInflater;
    private FragmentActivity context;

    // Constructors
    public MyChatPageAdapter(Context context, List<ChatPageGSON.MessageData> objects, String userId) {
        super(context, 0, objects);
        this.mInflater = LayoutInflater.from(context);
        this.user_id = userId;
        this.context=(FragmentActivity)context;
    }

    public MyChatPageAdapter(Context context, ChatPageGSON.MessageData[] objects, String userId) {
        super(context, 0, objects);
        this.mInflater = LayoutInflater.from(context);
        this.user_id = userId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            View view = mInflater.inflate(R.layout.chat_page_row, parent, false);
            vh = ViewHolder.create((LinearLayout) view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        ChatPageGSON.MessageData item = getItem(position);
        if (item.fromId.equals(user_id)) {
            vh.sendMessageTextView.setVisibility(View.VISIBLE);
            vh.receiveMessageTextView.setVisibility(View.GONE);
            vh.sendMessageTextView.setText(item.Message);
        } else {
            vh.receiveMessageTextView.setVisibility(View.VISIBLE);
            vh.sendMessageTextView.setVisibility(View.GONE);
            vh.receiveMessageTextView.setText(item.Message);
        }
        vh.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HideKeyBoard(context);
            }
        });
        return vh.rootView;
    }

    private static class ViewHolder {
        public final LinearLayout rootView;
        public final TextView receiveMessageTextView;
        public final TextView sendMessageTextView;

        private ViewHolder(LinearLayout rootView, TextView receiveMessageTextView, TextView sendMessageTextView) {
            this.rootView = rootView;
            this.receiveMessageTextView = receiveMessageTextView;
            this.sendMessageTextView = sendMessageTextView;
        }

        public static ViewHolder create(LinearLayout rootView) {
            TextView receiveMessageTextView = (TextView) rootView.findViewById(R.id.receiveMessageTextView);
            TextView sendMessageTextView = (TextView) rootView.findViewById(R.id.sendMessageTextView);
            return new ViewHolder(rootView, receiveMessageTextView, sendMessageTextView);
        }
    }
}
