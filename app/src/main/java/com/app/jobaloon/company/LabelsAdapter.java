package com.app.jobaloon.company;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.jobaloon.main.R;

import java.util.List;

/**
 * Created by SICS-Dpc2 on 30-Jan-15.
 */
public class LabelsAdapter extends ArrayAdapter<String> {

    private LayoutInflater mInflater;

    // Constructors
    public LabelsAdapter(Context context, List<String> objects) {
        super(context, 0, objects);
        this.mInflater = LayoutInflater.from(context);
    }

    public LabelsAdapter(Context context, String[] objects) {
        super(context, 0, objects);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            View view = mInflater.inflate(R.layout.labels_list_adapter, parent, false);
            vh = ViewHolder.create((LinearLayout) view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        String item = getItem(position);
        vh.textView.setText(item);
        return vh.rootView;
    }

    private static class ViewHolder {
        public final LinearLayout rootView;
        public final TextView textView;

        private ViewHolder(LinearLayout rootView, TextView textView) {
            this.rootView = rootView;
            this.textView = textView;
        }

        public static ViewHolder create(LinearLayout rootView) {
            TextView textView = (TextView) rootView.findViewById(R.id.textView);
            return new ViewHolder(rootView, textView);
        }
    }
}
