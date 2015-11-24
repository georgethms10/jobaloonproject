package com.app.jobaloon.company;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by SICS-Dpc2 on 04-Apr-15.
 */

public class SeeMyOffer extends FragmentActions implements VolleyOnResponseListener {
    private static final int CHAT_LIST = 1, UPDATE_ID = 2;
    @InjectView(R.id.SeeMyOfferListView)
    ListView SeeMyOfferListView;
    private MyCrouton myCrouton;
    private AppPreferences preferences;
    private VolleyForAll volleyForAll;
    private MyProgressDialog myProgressDialog;
    private TextView titleText, upload_video_left_text, upload_video_center_text;
    private FragmentActivity _activity;
    ImageView settinsButton, chatButton;
    FrameLayout frameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.seemyoffer_list, container, false);
        view.setClickable(true);
        ButterKnife.inject(this, view);
        _activity = getActivity();

        Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionReporter(
                _activity));

        preferences = new AppPreferences(getActivity(), "JobBoxData");
        myProgressDialog = new MyProgressDialog(getActivity());
//        myProgressDialog.setProgress();
        myCrouton = new MyCrouton();
        volleyForAll = new VolleyForAll(getActivity(), this);

        if (SearchPageNew.chatListGSON.Details != null) {
            SeeMyOfferAdapter myChatListAdapter = new SeeMyOfferAdapter(_activity, SearchPageNew.chatListGSON.Details);
            SeeMyOfferListView.setAdapter(myChatListAdapter);
        } else
            myCrouton.showCrouton(getActivity(), getResources().getString(R.string.Aún_no_tienes_ninguna_oferta), 1);
        return view;
    }


    private void makeActionbarSuitable() {
        View actionBarView = getActivity().getActionBar().getCustomView();
        titleText = (TextView) actionBarView.findViewById(R.id.titleText);
        upload_video_left_text = (TextView) actionBarView.findViewById(R.id.upload_video_left_text);
        upload_video_left_text.setVisibility(View.VISIBLE);

        settinsButton = (ImageView) actionBarView.findViewById(R.id.leftActionImage);
        chatButton = (ImageView) actionBarView.findViewById(R.id.rightActionImage);
        frameLayout = (FrameLayout) actionBarView.findViewById(R.id.frameLayout);
        upload_video_center_text = (TextView) actionBarView.findViewById(R.id.upload_video_center_text);
        frameLayout.setVisibility(View.GONE);
        upload_video_center_text.setVisibility(View.GONE);
        settinsButton.setVisibility(View.GONE);
        titleText.setVisibility(View.VISIBLE);
        titleText.setTypeface(null, Typeface.NORMAL);
        chatButton.setVisibility(View.GONE);
        titleText.setText(getResources().getString(R.string.Ver_mis_ofertas));
        upload_video_left_text.setText(getResources().getString(R.string.app_name));
    }


    @Override
    public void onResume() {
        super.onResume();
        makeActionbarSuitable();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myProgressDialog.dismissProgress();
        settinsButton.setVisibility(View.VISIBLE);
        titleText.setVisibility(View.VISIBLE);
        chatButton.setVisibility(View.VISIBLE);
        upload_video_left_text.setVisibility(View.GONE);
        titleText.setVisibility(View.GONE);
    }

    @Override
    public void onVolleyResponse(JSONObject result, int code) {
        myProgressDialog.dismissProgress();
        if (code == UPDATE_ID) {
            if (result.optString("Result").equals("Success")) {
                replaceFragment(new SearchPageNew(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "UPDATE_ID");
            }
        } else
            myCrouton.showCrouton(getActivity(), getResources().getString(R.string.Aún_no_tienes_ninguna_oferta), 1);
    }

    @Override
    public void onVolleyError(String result, int code) {
        myProgressDialog.dismissProgress();
        myCrouton.showCrouton(getActivity(), result, 1);
    }

    @OnClick(R.id.backToScreenImage)
    public void backToScreenImage() {
        if(SeeMyOfferListView.getCount()!=0) {
            myProgressDialog.setProgress();
            String url = getResources().getString(R.string.url_domain);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("type", "updateJobId");
            params.put("compony_id", preferences.getData("user_id"));
            params.put("id", preferences.getData("Selected_offer_id"));
            volleyForAll.volleyToNetwork(url, VolleyForAll.HttpRequestType.HTTP_POST, params, UPDATE_ID);
        }else{
            preferences.saveData("jobName","");
            Bundle b=new Bundle();
            b.putString("offers","none");
            SearchPageNew SearchPage=new SearchPageNew();
            SearchPage.setArguments(b);
            replaceFragment(SearchPage, R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "SearchPageNew");
        }
    }
}
