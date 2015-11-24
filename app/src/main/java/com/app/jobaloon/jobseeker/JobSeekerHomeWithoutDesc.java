package com.app.jobaloon.jobseeker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.jobaloon.chat.ChatList;
import com.app.jobaloon.gson.LabelsGSON;
import com.app.jobaloon.main.R;
import com.app.jobaloon.main.WelcomePage;
import com.app.jobaloon.utils.AppPreferences;
import com.app.jobaloon.utils.AsycHttpCall;
import com.app.jobaloon.utils.FragmentActions;
import com.app.jobaloon.utils.MyCrouton;
import com.app.jobaloon.utils.Response;
import com.app.jobaloon.utils.SaveSecurePreference;
import com.app.jobaloon.utils.UncaughtExceptionReporter;
import com.app.jobaloon.utils.Validation;
import com.app.jobaloon.utils.VolleyForAll;
import com.app.jobaloon.utils.VolleyOnResponseListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by SICS-Dpc2 on 03-Apr-15.
 */
public class JobSeekerHomeWithoutDesc extends FragmentActions implements VolleyOnResponseListener, Response {
    private ImageView chatIcon, settingsIcon;
    private View actionBarView;

    private VolleyForAll volleyForAll;
    private static final int LABELS = 1;
    private ArrayList<String> labelsList;
    private ArrayList<HashMap<String, String>> labelList;
    private MyCrouton myCrouton;
    private AppPreferences preferences;
    private Validation validation;

    @InjectView(R.id.labelListView)
    ListView jobSeekerLabelList;
    Context con;
    ArrayAdapter<String> myAdapter;
    FragmentActivity act;
    ArrayAdapter<String> adapter;

    SaveSecurePreference preference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_seeker_home_withoutdesc, container, false);
        view.setClickable(true);
        ButterKnife.inject(this, view);
        volleyForAll = new VolleyForAll(getActivity(), this);
        labelsList = new ArrayList<String>();
        labelList = new ArrayList<HashMap<String, String>>();
        jobSeekerLabelList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        myCrouton = new MyCrouton();
        validation = new Validation();
        actionBarView = getActivity().getActionBar().getCustomView();
        con = getActivity();
        act = getActivity();
        preferences = new AppPreferences(con, "JobBoxData");

        Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionReporter(
                con));

        settingsIcon = (ImageView) actionBarView.findViewById(R.id.leftActionImage);
        chatIcon = (ImageView) actionBarView.findViewById(R.id.rightActionImage);
        settingsIcon.setImageDrawable(con.getResources().getDrawable(R.drawable.settings));
        TextView countVisitorsText = (TextView) actionBarView.findViewById(R.id.countVisitorsText);
        //making the visitor count gone here for making sure it is not showing for company profile.
        countVisitorsText.setVisibility(View.GONE);
        getActivity().getActionBar().show();
        chatIcon.setImageDrawable(con.getResources().getDrawable(R.drawable.msg));
        chatIcon.setVisibility(View.VISIBLE);
        settingsIcon.setVisibility(View.VISIBLE);

        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragmentNew(new ChatList(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "ChatList", act);
            }
        });

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragmentNew(new EditJobSeekerProfile(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "EditJobSeekerProfile", act);
            }
        });

        //getting labels from the server
//        String labelsURL = getResources().getString(R.string.url_domain);
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("type", "Label");
//        volleyForAll.volleyToNetwork(labelsURL, VolleyForAll.HttpRequestType.HTTP_POST, params, LABELS);
        preference = new SaveSecurePreference(getActivity());
        String labelsURL = getActivity().getResources().getString(R.string.url_new_domain) + "labels.php/labels";
        RequestParams params = new RequestParams();
        params.put("userid", preference.getPrefValue(WelcomePage.USER_KEY));
        params.put("oauthToken", preference.getPrefValue(WelcomePage.AUTH_KEY));
        System.out.println("====== params : " + params);
        AsycHttpCall.getInstance().CallApiPost(labelsURL, params, this, LABELS);


        return view;
    }

    @Override
    public void onVolleyResponse(JSONObject result, int code) {
        switch (code) {
            case LABELS:
//                parseLabels(result);
                break;
            default:
                break;
        }
    }

    @Override
    public void onVolleyError(String result, int code) {

    }

//    private void parseLabels(JSONObject result) {
//        if (result.optBoolean("Result")) {
//            JSONArray jsonArray = result.optJSONArray("Label");
//            int size = jsonArray.length();
//            for (int i = 0; i < size; i++) {
//                labelsList.add(jsonArray.optString(i));
//                HashMap<String, String> hMap = new HashMap<String, String>();
//                hMap.put("labelName", jsonArray.optString(i).toString());
//                hMap.put("selectedStatus", "false");
//                labelList.add(hMap);
//            }
//
//        }
//        try {
//            Collections.sort(labelsList, new Comparator<String>() {
//                @Override
//                public int compare(String s1, String s2) {
//                    return s1.compareToIgnoreCase(s2);
//                }
//            });
//            for (int i = 0; i < labelList.size(); i++) {
//                if (!preferences.getData("Labels").equals("") && preferences.getData("Labels") != null) {
//                    if (URLDecoder.decode(preferences.getData("Labels")).equals(labelList.get(i).get("labelName"))) {
//                        System.out.println("labelList : " + labelList.get(i).get("labelName") + "    " + URLDecoder.decode(preferences.getData("Labels")));
//                        HashMap<String, String> hmap = new HashMap<String, String>();
//                        hmap.put("labelName", labelList.get(i).get("labelName"));
//                        hmap.put("selectedStatus", "true");
//                        labelList.set(i, hmap);
//                    }
//                }
//            }
//            System.out.println("labelList : " + labelList);
//            LazyAdapter adapter = new LazyAdapter(getActivity(), labelList);
//            adapter.notifyDataSetChanged();
//            jobSeekerLabelList.setAdapter(adapter);
//
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onSuccess(String response, int code) {

        switch (code) {
            case LABELS:
                parseLabel(response);
                break;
        }
    }

    @Override
    public void onFail(String response, int code) {

    }

    public void parseLabel(String response) {
        if (response != null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();
            LabelsGSON labelGSON = gson.fromJson(response,
                    LabelsGSON.class);
            if (labelGSON != null) {
                String labels = labelGSON.Label.toString();
                String[] separated = labels.split(",");
                for (int i = 0; i < separated.length; i++) {
                    if (i == 0)
                        separated[i] = separated[i].replace("[", "");

                    if (i == separated.length - 1)
                        separated[i] = separated[i].replace("]", "");
                    labelsList.add(separated[i]);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("labelName", separated[i]);
                    hashMap.put("selectedStatus", "false");
                    labelList.add(hashMap);
                }
            }
            try {
                Collections.sort(labelsList, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareToIgnoreCase(s2);
                    }
                });
                for (int i = 0; i < labelList.size(); i++) {
                    if (!preferences.getData("Labels").equals("") && preferences.getData("Labels") != null) {
                        if (URLDecoder.decode(preferences.getData("Labels")).equals(labelList.get(i).get("labelName"))) {
                            HashMap<String, String> hmap = new HashMap<String, String>();
                            hmap.put("labelName", labelList.get(i).get("labelName"));
                            hmap.put("selectedStatus", "true");
                            labelList.set(i, hmap);
                        }
                    }
                }
                LazyAdapter adapter = new LazyAdapter(getActivity(), labelList);
                adapter.notifyDataSetChanged();
                jobSeekerLabelList.setAdapter(adapter);

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public class LazyAdapter extends BaseAdapter implements Filterable {
        private ArrayList<HashMap<String, String>> adapterList;
        Context con;

        public LazyAdapter(Context con, ArrayList<HashMap<String, String>> adapterList) {
            this.con = con;
            this.adapterList = adapterList;
        }

        @Override
        public int getCount() {
            return adapterList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            View rowview = convertView;
            ViewHolder holder = null;
            if (rowview == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowview = inflater.inflate(R.layout.label_row, null);
                holder = new ViewHolder();
                holder.name = (TextView) rowview
                        .findViewById(R.id.label_row_text);
                holder.layout = (LinearLayout) rowview.findViewById(R.id.label_row_layout);
                rowview.setTag(holder);
            } else {
                holder = (ViewHolder) rowview.getTag();
            }


            holder.name.setText(adapterList.get(position).get("labelName"));
//            if (!preferences.getData("Labels").equals("")) {
//                if (URLDecoder.decode(preferences.getData("Labels")).equals(adapterList.get(position))) {
//                    holder.layout.setBackgroundColor(getResources().getColor(R.color.blue));
//                    jobSeekerLabelList.setSelection(adapterList.indexOf(URLDecoder.decode(preferences.getData("Labels"))));
//                }
//
//            }
            if (adapterList.get(position).get("selectedStatus").equals("true")) {
                holder.layout.setBackgroundColor(getResources().getColor(R.color.blue));
//                jobSeekerLabelList.setSelection(adapterList.indexOf(URLDecoder.decode(preferences.getData("Labels"))));
            } else
                holder.layout.setBackgroundColor(getResources().getColor(R.color.transparent));

            rowview.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String label = labelList.get(position).get("labelName");
                    preferences.saveData("Labels", URLEncoder.encode(label));
//                    preferences.saveData("");
                    addFragment(new JobListNew(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "JobListNew");
                }
            });

            return rowview;
        }

        @Override
        public Filter getFilter() {
            return null;
        }
    }

    public static class ViewHolder {
        LinearLayout layout;
        TextView name;
    }
}
