package com.app.jobaloon.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.app.jobaloon.company.EditCompanyProfile;
import com.app.jobaloon.company.SearchPageNew;
import com.app.jobaloon.gson.ChatListGSON;
import com.app.jobaloon.jobseeker.EditJobSeekerProfile;
import com.app.jobaloon.jobseeker.JobListNew;
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
 * Created by SICS-Dpc2 on 31-Jan-15.
 */
public class ChatList extends FragmentActions implements VolleyOnResponseListener {
    private static final int CHAT_LIST = 1;
    @InjectView(R.id.chatListView)
    ListView chatListView;
    @InjectView(R.id.topTextView)
    TextView topTextView;
    private MyCrouton myCrouton;
    private AppPreferences preferences;
    private VolleyForAll volleyForAll;
    private MyProgressDialog myProgressDialog;
    private ImageView leftActionImage, rightActionImage;
    private FragmentActivity _activity;
//    private FrameLayout ChatcountframeLayout;

//    @InjectView(R.id.ChatcountframeLayout)
//    FrameLayout ChatcountframeLayout;
//
//    @InjectView(R.id.ChatcountText)
//    TextView ChatcountText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_list, container, false);
        view.setClickable(true);
        ButterKnife.inject(this, view);
        _activity = getActivity();

        Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionReporter(
                _activity));

        preferences = new AppPreferences(getActivity(), "JobBoxData");
        if (preferences.getData("user_type").equalsIgnoreCase("Company"))
            topTextView.setText(getResources().getString(R.string.selected_candidates));
        else topTextView.setText(getResources().getString(R.string.interested_companies));
        myProgressDialog = new MyProgressDialog(getActivity());
        myProgressDialog.setProgress();
        myCrouton = new MyCrouton();
        volleyForAll = new VolleyForAll(getActivity(), this);
        String chatListURL = getResources().getString(R.string.url_domain);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "chatuser");
        params.put("userid", preferences.getData("user_id"));
        volleyForAll.volleyToNetwork(chatListURL, VolleyForAll.HttpRequestType.HTTP_POST, params, CHAT_LIST);
        return view;
    }

    @OnClick(R.id.backToScreenImage)
    public void goBackToSearch() {
        if (preferences.getData("user_type").equalsIgnoreCase("Company")) {
//            getActivity().finish();
//            getFragmentManager().popBackStack();
            replaceFragment(new SearchPageNew(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "SearchPage");
        } else {
//            getActivity().finish();
//            Intent intent = new Intent(getActivity(), BaseFragmentActivity.class);
//            startActivity(intent);
//            new ClearBackStack(getActivity());
            replaceFragment(new JobListNew(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "JobListNew");
        }
    }

    private void makeActionbarSuitable() {
        View actionBarView = getActivity().getActionBar().getCustomView();
        leftActionImage = (ImageView) actionBarView.findViewById(R.id.leftActionImage);
        rightActionImage = (ImageView) actionBarView.findViewById(R.id.rightActionImage);
//        ChatcountframeLayout=(FrameLayout) actionBarView.findViewById(R.id.frameLayout);
        leftActionImage.setVisibility(View.VISIBLE);
//        rightActionImage.setVisibility(View.GONE);
//        ChatcountframeLayout.setVisibility(View.GONE);
//        ChatcountText.setVisibility(View.GONE);
//        ChatcountframeLayout.setVisibility(View.GONE);
//        if (preferences.getData("user_type").equalsIgnoreCase("Company"))
            leftActionImage.setImageResource(R.drawable.settings);
//        else leftActionImage.setImageResource(R.drawable.glass);
//        leftActionImage.setVisibility(View.INVISIBLE);
//        rightActionImage.setVisibility(View.INVISIBLE);
        leftActionImage.setClickable(true);
        rightActionImage.setClickable(true);
        leftActionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                if (preferences.getData("user_type").equalsIgnoreCase("Company"))
                    addFragmentNew(new EditCompanyProfile(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "EditCompanyProfile",_activity);
                else
                    replaceFragmentNew(new EditJobSeekerProfile(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "EditJobSeekerProfile",_activity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        rightActionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    replaceFragmentNew(new ChatList(),R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "ChatPage",_activity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        leftActionImage.setClickable(true);
        rightActionImage.setClickable(true);
        // calling broadcast
        Intent intent = new Intent("backFromChatList");
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        makeActionbarSuitable();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myProgressDialog.dismissProgress();
    }

    @Override
    public void onVolleyResponse(JSONObject result, int code) {
        myProgressDialog.dismissProgress();
        if (code == CHAT_LIST && result != null) {
            if (result.optBoolean("Result")) {
                ChatListGSON chatListGSON = (new Gson()).fromJson(
                        result.toString(), ChatListGSON.class);
                if (chatListGSON != null) {
                    MyChatListAdapter myChatListAdapter = new MyChatListAdapter(_activity, chatListGSON.Details);
                    chatListView.setAdapter(myChatListAdapter);
                }
            } else
                myCrouton.showCrouton(_activity, "Aún no ha seleccionado a ningún candidato", 1);
        } else
            myCrouton.showCrouton(_activity, _activity.getResources().getString(R.string.failed_to_get_data_from_server), 1);
    }

    @Override
    public void onVolleyError(String result, int code) {
        myProgressDialog.dismissProgress();
//        myCrouton.showCrouton(getActivity(), result, 1);
    }
}
