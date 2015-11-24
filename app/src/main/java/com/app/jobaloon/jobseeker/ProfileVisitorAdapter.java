package com.app.jobaloon.jobseeker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.jobaloon.gson.ProfileVistorGSON;
import com.app.jobaloon.main.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProfileVisitorAdapter extends ArrayAdapter<ProfileVistorGSON.UserDetails> {
    private LayoutInflater mInflater;
    Context context;
    String newDate;

    // Constructors
    public ProfileVisitorAdapter(Context context, List<ProfileVistorGSON.UserDetails> object) {
        super(context, 0, object);
        this.context = context;
        this.mInflater = LayoutInflater.from(context);

    }

    public ProfileVisitorAdapter(Context context, ProfileVistorGSON.UserDetails[] objects) {
        super(context, 0, objects);

        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    private static class ViewHolder {

        public final RelativeLayout rootView;
        private TextView visitornameText;
        private TextView date_tv;
        private TextView time_tv;
        private TextView number_tv;

        private ViewHolder(RelativeLayout rootView, TextView visitornameText,
                           TextView date_tv, TextView time_tv, TextView number_tv) {

            this.rootView = rootView;
            this.visitornameText = visitornameText;
            this.date_tv = date_tv;
            this.time_tv = time_tv;
            this.number_tv = number_tv;
        }

        public static ViewHolder create(RelativeLayout rootView) {

            TextView visitornameText = (TextView) rootView
                    .findViewById(R.id.visitor_name);
            TextView date_tv = (TextView) rootView
                    .findViewById(R.id.visited_date);
            TextView time_tv = (TextView) rootView
                    .findViewById(R.id.visited_time);
            TextView number_tv = (TextView) rootView
                    .findViewById(R.id.number_of_views);

            return new ViewHolder(rootView, visitornameText, date_tv, time_tv,
                    number_tv);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            View view = mInflater.inflate(R.layout.visited_users_row, parent,
                    false);
            vh = ViewHolder.create((RelativeLayout) view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        ProfileVistorGSON.UserDetails item = getItem(position);

        vh.visitornameText.setText(item.username);
        String date_with_time = item.date;
        String date = date_with_time.substring(0, 11);
        String time = date_with_time.substring(11,
                date_with_time.lastIndexOf(":"));
        String fin_time=date_with_time.substring(date_with_time.lastIndexOf(" "),date_with_time.length());
        String time_to_display=time;
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-mm-dd");
            SimpleDateFormat output = new SimpleDateFormat("dd/mm/yyyy");
            Date setDate = input.parse(date);
            newDate = output.format(setDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        vh.date_tv.setText(newDate);
        vh.time_tv.setText(time_to_display);
        return vh.rootView;
    }

}
