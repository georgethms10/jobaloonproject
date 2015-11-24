package com.app.jobaloon.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.app.jobaloon.gson.ChatListGSON;
import com.app.jobaloon.main.R;
import com.app.jobaloon.utils.AppPreferences;

import java.util.List;

/**
 * Created by SICS-Dpc2 on 31-Jan-15.
 */
public class MyChatListAdapter extends ArrayAdapter<ChatListGSON.Details> {

    private LayoutInflater mInflater;
    private FragmentActivity _context;
    private AppPreferences preferences;

    // Constructors
    public MyChatListAdapter(Context context, List<ChatListGSON.Details> objects) {
        super(context, 0, objects);
        this.mInflater = LayoutInflater.from(context);
        this._context = (FragmentActivity) context;
        preferences = new AppPreferences(_context, "JobBoxData");
    }

    public MyChatListAdapter(Context context, ChatListGSON.Details[] objects) {
        super(context, 0, objects);
        this.mInflater = LayoutInflater.from(context);
        this._context = (FragmentActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            View view = mInflater.inflate(R.layout.chat_list_row, parent, false);
            vh = ViewHolder.create((RelativeLayout) view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final ChatListGSON.Details item = getItem(position);

        vh.nameText.setText(item.user_name);
        int count = item.unReadcount;
        if (count != 0) {
            vh.countText.setVisibility(View.VISIBLE);
            if (count > 9)
                vh.countText.setText("9+");
            else
                vh.countText.setText(count + "");
        } else
            vh.countText.setVisibility(View.GONE);
        if (preferences.getData("user_type").equals("Company")) {
            vh.profileIcon.setVisibility(View.VISIBLE);
            if (!item.image.equals(""))
                Glide.with(_context).load(_context.getString(R.string.url_image) + item.image).into(vh.profileIcon);
            else
                vh.profileIcon.setImageResource(R.drawable.avatar);
        } else
            vh.profileIcon.setVisibility(View.INVISIBLE);
        vh.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatPage chatPage = new ChatPage();
                Bundle bundle = new Bundle();
                bundle.putString("selectedUser", item.user_id);
                bundle.putString("autoMessage", "");//no message (only from like Button)
                chatPage.setArguments(bundle);
                item.unReadcount = 0; //removing badge
                if (preferences.getData("user_type").equalsIgnoreCase("Company"))
                    addFragment(chatPage, R.id.frame, true, FragmentTransaction.TRANSIT_NONE, "chatPage");
                else
                    replaceFragment(chatPage, R.id.frame, true, FragmentTransaction.TRANSIT_NONE, "chatPage");
                notifyDataSetChanged();
            }
        });
        return vh.rootView;
    }

    public void addFragment(Fragment fragment, int frameId,
                            boolean addToBackStack, int transition, String name) {
        FragmentTransaction ft = _context.getSupportFragmentManager().beginTransaction();
        ft.add(frameId, fragment);
        ft.setTransition(transition);
        if (addToBackStack)
            ft.addToBackStack(name);
        ft.commit();
    }

    public void replaceFragment(Fragment fragment, int frameId,
                                boolean addToBackStack, int transition, String name) {
        FragmentTransaction ft = _context.getSupportFragmentManager().beginTransaction();
        ft.replace(frameId, fragment);
        ft.setTransition(transition);
        if (addToBackStack)
            ft.addToBackStack(name);
        ft.commit();
    }

    /**
     * ViewHolder class for layout.<br />
     * <br />
     * Auto-created on 2015-01-31 17:36:58 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private static class ViewHolder {
        public final RelativeLayout rootView;
        public final com.meg7.widget.CircleImageView profileIcon;
        public final TextView nameText;
        public final TextView countText;

        private ViewHolder(RelativeLayout rootView, com.meg7.widget.CircleImageView profileIcon, TextView nameText, TextView countText) {
            this.rootView = rootView;
            this.profileIcon = profileIcon;
            this.nameText = nameText;
            this.countText = countText;
        }

        public static ViewHolder create(RelativeLayout rootView) {
            com.meg7.widget.CircleImageView profileIcon = (com.meg7.widget.CircleImageView) rootView.findViewById(R.id.profileIcon);
            TextView nameText = (TextView) rootView.findViewById(R.id.nameText);
            TextView countText = (TextView) rootView.findViewById(R.id.countText);
            FrameLayout frameLayout = (FrameLayout) rootView.findViewById(R.id.frameLayout);
            return new ViewHolder(rootView, profileIcon, nameText, countText);
        }
    }
}
