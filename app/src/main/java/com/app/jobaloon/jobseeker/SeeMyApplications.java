package com.app.jobaloon.jobseeker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.jobaloon.main.R;
import com.app.jobaloon.utils.AppPreferences;
import com.app.jobaloon.utils.FragmentActions;
import com.app.jobaloon.utils.MyCrouton;
import com.app.jobaloon.utils.MyProgressDialog;
import com.app.jobaloon.utils.UncaughtExceptionReporter;
import com.app.jobaloon.utils.VolleyForAll;
import com.app.jobaloon.utils.VolleyOnResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by SICS-Dpc2 on 13-Apr-15.
 */
public class SeeMyApplications extends FragmentActions implements VolleyOnResponseListener {

    private static final int APPLICATIONS = 5000, DELETE_APP = 2;
    private VolleyForAll volleyForAll;
    private MyCrouton myCrouton;
    private MyProgressDialog myProgressDialog;
    private AppPreferences preferences;
    ArrayList<HashMap<String, String>> jobArrayList = new ArrayList<HashMap<String, String>>();

    @InjectView(R.id.backToScreenImage)
    ImageView backToScreenImage;
    @InjectView(R.id.seemyApplicationsListView)
    ListView seemyApplicationsListView;
    LazyAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_applications, container, false);
        preferences = new AppPreferences(getActivity(), "JobBoxData");
        volleyForAll = new VolleyForAll(getActivity(), this);
        myCrouton = new MyCrouton();
        myProgressDialog = new MyProgressDialog(getActivity());
        Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionReporter(
                getActivity()));
        myProgressDialog.setProgress();
        String jobListURL = getResources().getString(R.string.url_domain);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "userLikeOffers");
        params.put("userid", preferences.getData("user_id"));
        volleyForAll.volleyToNetwork(jobListURL, VolleyForAll.HttpRequestType.HTTP_POST, params, APPLICATIONS);

        view.setClickable(true);
        ButterKnife.inject(this, view);
        return view;
    }

    @OnClick(R.id.backToScreenImage)
    public void Back() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void onVolleyResponse(JSONObject result, int code) {
        myProgressDialog.dismissProgress();
        if (code == APPLICATIONS) {
            if (result.optBoolean("Result")) {
                JSONArray jarray;
                jarray = result.optJSONArray("Details");
                if (jarray != null) {
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject jobj = jarray.optJSONObject(i);
                        HashMap<String, String> hMaps = new HashMap<String, String>();
                        hMaps.put("job_id", jobj.optString("id"));
                        hMaps.put("Role", jobj.optString("Role"));
                        hMaps.put("start_date", jobj.optString("start_date"));
                        hMaps.put("end_date", jobj.optString("end_date"));
                        hMaps.put("working_days", jobj.optString("working_days"));
                        hMaps.put("location", jobj.optString("location"));
                        hMaps.put("distance", jobj.optString("distance"));
                        hMaps.put("jobDescription", jobj.optString("jobDescription"));
                        hMaps.put("compony_id", jobj.optString("compony_id"));
                        hMaps.put("user_name", jobj.optString("user_name"));
                        jobArrayList.add(hMaps);
                    }
                }
            }
            adapter = new LazyAdapter(jobArrayList);
            seemyApplicationsListView.setAdapter(adapter);
        } else {
            if (result.optBoolean("Result")) {
                myCrouton.showCrouton(getActivity(), "Eliminado la aplicación", 2);
                System.out.println("code   " + code);
                jobArrayList.remove(code);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onVolleyError(String result, int code) {

    }

    public class LazyAdapter extends BaseAdapter implements Filterable {
        private ArrayList<HashMap<String, String>> adapterList;

        public LazyAdapter(ArrayList<HashMap<String, String>> adapterList) {
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


        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            View rowview = convertView;
            ViewHolder holder = null;
            if (rowview == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowview = inflater.inflate(R.layout.myapplications_row, null);
                holder = new ViewHolder();
                holder.jobName = (TextView) rowview
                        .findViewById(R.id.applicationCompanyName);
                holder.applicationDescription = (TextView) rowview
                        .findViewById(R.id.applicationDescription);
                holder.applicationHours = (TextView) rowview
                        .findViewById(R.id.applicationHours);

                holder.applicationDays = (TextView) rowview
                        .findViewById(R.id.applicationDays);
                holder.applicationLocation = (TextView) rowview
                        .findViewById(R.id.applicationLocation);
                holder.deleteButton = (ImageView) rowview
                        .findViewById(R.id.eraseapplicationButton);
                rowview.setTag(holder);
            } else {
                holder = (ViewHolder) rowview.getTag();
            }

            holder.jobName.setText(adapterList.get(position).get("user_name"));
            holder.applicationDescription.setText(adapterList.get(position).get("jobDescription"));
            holder.applicationHours.setText(adapterList.get(position).get("start_date") + " - " + adapterList.get(position).get("end_date"));
            holder.applicationDays.setText(adapterList.get(position).get("working_days"));
            holder.applicationLocation.setText(adapterList.get(position).get("location"));

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Delete entry")
                            .setMessage("¿Deseas eliminar tu candidatura?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String labelsURL = getActivity().getResources().getString(R.string.url_domain);
                                    HashMap<String, String> params = new HashMap<String, String>();
                                    params.put("type", "LikeOffer");
                                    params.put("userid", preferences.getData("user_id"));
                                    params.put("id", adapterList.get(position).get("job_id"));
                                    params.put("status", "delete");
                                    volleyForAll.volleyToNetwork(labelsURL, VolleyForAll.HttpRequestType.HTTP_POST, params, position);
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


            return rowview;
        }

        @Override
        public Filter getFilter() {
            return null;
        }
    }

    public static class ViewHolder {
        TextView jobName, applicationDescription, applicationHours, applicationDays, applicationLocation;
        ImageView deleteButton;
    }
}
