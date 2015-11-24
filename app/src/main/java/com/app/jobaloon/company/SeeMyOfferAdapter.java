package com.app.jobaloon.company;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.jobaloon.gson.MyOfferListGSON;
import com.app.jobaloon.main.R;
import com.app.jobaloon.utils.AppPreferences;
import com.app.jobaloon.utils.VolleyForAll;
import com.app.jobaloon.utils.VolleyOnResponseListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by SICS-Dpc2 on 31-Jan-15.
 */
public class SeeMyOfferAdapter extends ArrayAdapter<MyOfferListGSON.Details> implements VolleyOnResponseListener {

    private LayoutInflater mInflater;
    private FragmentActivity _context;
    private AppPreferences preferences;
    private VolleyForAll volleyForAll;
    List<MyOfferListGSON.Details> objectsDetails;
    boolean[] itemChecked;

    // Constructors
    public SeeMyOfferAdapter(Context context, List<MyOfferListGSON.Details> objects) {
        super(context, 0, objects);
        objectsDetails = objects;
        volleyForAll = new VolleyForAll(context, this);
        this.mInflater = LayoutInflater.from(context);
        this._context = (FragmentActivity) context;
        preferences = new AppPreferences(_context, "JobBoxData");
        itemChecked = new boolean[objects.size()];
        itemChecked[0]=true;
    }

    public SeeMyOfferAdapter(Context context, MyOfferListGSON.Details[] objects) {
        super(context, 0, objects);
        this.mInflater = LayoutInflater.from(context);
        this._context = (FragmentActivity) context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            View view = mInflater.inflate(R.layout.myoffer_list_row, parent, false);
            vh = ViewHolder.create((LinearLayout) view);

            view.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final MyOfferListGSON.Details item = getItem(position);
        preferences.saveData("SelectedJob",item.Role);
        vh.RoleText.setText(item.Role);
        vh.TimeText.setText(item.start_date.substring(0,item.start_date.length()-3) + "  -  " + item.end_date.substring(0,item.start_date.length()-3));
        vh.DaysText.setText(item.working_days);
        vh.DiscText.setText(item.jobDescription);

        if(preferences.getData("Selected_offer_id").equals(item.id)){
            itemChecked[position]=true;
        }else{
            itemChecked[position]=false;
        }


        if (itemChecked[position]) {
            vh.CheckBoxButton.setImageResource(R.drawable.selected_offer);
        } else {
            vh.CheckBoxButton.setImageResource(R.drawable.unselected_offer);
        }
        vh.CheckBoxButton.setTag(position);
        vh.CheckBoxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.saveData("Selected_offer_id",item.id);
                boolean set = true;
                for (int i = 0; i < itemChecked.length; i++) {
                    if (i == Integer.parseInt(vh.CheckBoxButton.getTag().toString())) {
                        System.out.println("Entered loop : " + i);
                        itemChecked[i] = set;
                        preferences.saveData("SelectedJob",item.Role);
                        preferences.saveData("prevLabel",item.Role);
                        preferences.saveData("Distance",item.distance);
                        preferences.saveData("FromAddress",item.location);
                        preferences.saveData("jobName",item.jobDescription);
                    } else {
                        itemChecked[i] = (!set);
                    }
                }
                notifyDataSetChanged();
            }
        });


        vh.DeleteButton.setTag(position);
        vh.DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete entry")
                        .setMessage("¿Deseas eliminar esta oferta?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String labelsURL = getContext().getResources().getString(R.string.url_domain);
                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("type", "deletePost");
                                params.put("id", item.id);
                                volleyForAll.volleyToNetwork(labelsURL, VolleyForAll.HttpRequestType.HTTP_POST, params, Integer.parseInt(vh.DeleteButton.getTag().toString()));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
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

    @Override
    public void onVolleyResponse(JSONObject result, int code) {
        if (result.optBoolean("Result")) {
            try {
                objectsDetails.remove(code);
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }
            if (objectsDetails.size() == 0) {
                preferences.saveBooleanData("afterPost", false);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public void onVolleyError(String result, int code) {

    }

    /**
     * ViewHolder class for layout.<br />
     * <br />
     * Auto-created on 2015-01-31 17:36:58 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private static class ViewHolder {
        public final TextView RoleText;
        public final TextView TimeText;
        public final TextView DaysText;
        public final TextView DiscText;
        public final ImageView DeleteButton;
        public final LinearLayout rootView;
        public final ImageView CheckBoxButton;

        private ViewHolder(LinearLayout rootView, TextView RoleText, TextView TimeText, TextView DaysText, TextView DiscText, ImageView DeleteButton, ImageView CheckBoxButton) {
            this.rootView = rootView;
            this.RoleText = RoleText;
            this.TimeText = TimeText;
            this.DaysText = DaysText;
            this.DiscText = DiscText;
            this.DeleteButton = DeleteButton;
            this.CheckBoxButton = CheckBoxButton;
        }

        public static ViewHolder create(LinearLayout rootView) {
            TextView RoleText = (TextView) rootView.findViewById(R.id.RoleText);
            TextView TimeText = (TextView) rootView.findViewById(R.id.TimeText);
            TextView DaysText = (TextView) rootView.findViewById(R.id.DaysText);
            TextView DiscText = (TextView) rootView.findViewById(R.id.DiscText);
            ImageView DeleteButton = (ImageView) rootView.findViewById(R.id.DeleteButton);
            ImageView CheckBoxButton = (ImageView) rootView.findViewById(R.id.CheckBoxSelect);

            return new ViewHolder(rootView, RoleText, TimeText, DaysText, DiscText, DeleteButton, CheckBoxButton);
        }
    }
}
