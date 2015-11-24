package com.app.jobaloon.company;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.app.jobaloon.gson.JobListGSON;
import com.app.jobaloon.gson.JobSeekersListGSON;
import com.app.jobaloon.main.WelcomePage;
import com.app.jobaloon.utils.AsycHttpCall;
import com.app.jobaloon.utils.Response;
import com.app.jobaloon.utils.SaveSecurePreference;
import com.google.gson.Gson;
import com.app.jobaloon.chat.ChatList;
import com.app.jobaloon.chat.ChatPage;
import com.app.jobaloon.gson.MyOfferListGSON;
import com.app.jobaloon.gson.SearchGSON;
import com.app.jobaloon.main.R;
import com.app.jobaloon.utils.AppPreferences;
import com.app.jobaloon.utils.FragmentActions;
import com.app.jobaloon.utils.MyCrouton;
import com.app.jobaloon.utils.MyProgressDialog;
import com.app.jobaloon.utils.UncaughtExceptionReporter;
import com.app.jobaloon.utils.VolleyForAll;
import com.app.jobaloon.utils.VolleyOnResponseListener;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by SICS-Dpc2 on 02-Apr-15.
 */
public class SearchPageNew extends FragmentActions implements VolleyOnResponseListener, Response {

    private ImageView chatIcon, settingsIcon;
    private View actionBarView;
    public boolean pageNavigated = false;
    @InjectView(R.id.searchBar)
    EditText searchBar;
    String searchId = "";

    public static MyOfferListGSON chatListGSON;

    @InjectView(R.id.seeMyOffers)
    Button seeMyOffers;

    @InjectView(R.id.companyEditProfileButton)
    Button editProfileButton;

    @InjectView(R.id.selectedCandidates)
    Button selectedCandidates;

    @InjectView(R.id.after_posting_header)
    TextView after_posting_header;
    @InjectView(R.id.after_posting_searchImageView)
    ImageView after_posting_searchImageView;
    @InjectView(R.id.after_posting_refreshButton)
    ImageView after_posting_refreshButton;


    @InjectView(R.id.searchImageView)
    ImageView searchImageView;

    @InjectView(R.id.searchDesc)
    TextView searchDesc;

    @InjectView(R.id.searchfeedvideoView)
    VideoView searchfeedVideoView;

    @InjectView(R.id.like_info_dislike_button_set)
    RelativeLayout like_info_dislike_button_set;

    @InjectView(R.id.profileLikeButton)
    ImageView profileLikeButton;

    @InjectView(R.id.infoButton)
    ImageView infoButton;

    @InjectView(R.id.profileDisLikeButton)
    ImageView profileDisLikeButton;


    @InjectView(R.id.image_and_video_layout)
    RelativeLayout image_and_video_layout;

    FrameLayout ChatcountframeLayout;

    @InjectView(R.id.ChatcountText)
    TextView ChatcountText;
    int i = 1;
    private SearchGSON gson;
    AppPreferences apfs;
    private VolleyForAll volleyForAll;
    private MyCrouton myCrouton;
    private MyProgressDialog myProgressDialog;
    ArrayList<HashMap<String, String>> jobProfileArrayList = new ArrayList<HashMap<String, String>>();
    private static final int PROFLIST = 1, LIKE_PROF = 2, UPDATE_VISIT_COUNT = 3, DISLIKE_PROF = 4, CHAT_COUNT = 5, CHAT_LIST = 6;
    int position = 0;
    private String autoMessage;
    private FragmentActivity act;
    private FrameLayout frameLayout;
    private TextView countText;
    private CountDownTimer countDownTimer;
    private Context con;
    UncaughtExceptionReporter exeptionReporter;
    private ImageView redCircle;
    private TextView upload_video_center_text;
    FragmentActivity ContextActivity;
    public static boolean navigated = false;

    SaveSecurePreference preference;
    String userId, authToken;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_page_new, container, false);
        view.setClickable(true);
        ButterKnife.inject(this, view);
        con = getActivity();
        preference = new SaveSecurePreference(con);
        userId = preference.getPrefValue(WelcomePage.USER_KEY);
        authToken = preference.getPrefValue(WelcomePage.AUTH_KEY);

        Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionReporter(
                con));

        apfs = new AppPreferences(con, "JobBoxData");
        volleyForAll = new VolleyForAll(con, this);

        myCrouton = new MyCrouton();
        myProgressDialog = new MyProgressDialog(getActivity());
        act = getActivity();
        ContextActivity = getActivity();
        Bundle b = getArguments();
//        String offerList=b.getString("offers");
//        if(offerList!=null&&offerList.equals("none")) {
        Search();
//        }
//---------------------------chat count for a candidate-----------------------------------------//

        String chatCountLink = con.getResources().getString(R.string.url_domain);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "ChatCount");
        params.put("userid", apfs.getData("user_id"));
        volleyForAll.volleyToNetwork(chatCountLink, VolleyForAll.HttpRequestType.HTTP_POST, params, CHAT_COUNT);

//---------------------------chat count for a candidate-----------------------------------------//

        searchBar.setHint(con.getResources().getString(R.string.Publicar_nueva_oferta));
        searchDesc.setVisibility(View.VISIBLE);
        searchDesc.setText(con.getResources().getString(R.string.Para_encontrar_un_trabajador_se_necesita_publicar_una_oferta));
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(width, width - 20);
        rp.setMargins(10, 10, 10, 10);
        rp.addRule(RelativeLayout.ABOVE, R.id.nameText);

        actionBarView = ContextActivity.getActionBar().getCustomView();

        settingsIcon = (ImageView) actionBarView.findViewById(R.id.leftActionImage);
        chatIcon = (ImageView) actionBarView.findViewById(R.id.rightActionImage);
        settingsIcon.setImageDrawable(getResources().getDrawable(R.drawable.settings));
        ChatcountframeLayout = (FrameLayout) actionBarView.findViewById(R.id.frameLayout);
        upload_video_center_text = (TextView) actionBarView.findViewById(R.id.upload_video_center_text);

        TextView countVisitorsText = (TextView) actionBarView.findViewById(R.id.countVisitorsText);
        TextView upload_video_left_text = (TextView) actionBarView.findViewById(R.id.upload_video_left_text);
        upload_video_left_text.setVisibility(View.GONE);
        upload_video_center_text.setVisibility(View.GONE);
        TextView titleText = (TextView) actionBarView.findViewById(R.id.titleText);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Hero Light.otf");
        titleText.setTypeface(font);
        //making the visitor count gone here for making sure it is not showing for company profile.
        countVisitorsText.setVisibility(View.GONE);
        titleText.setVisibility(View.VISIBLE);
        titleText.setText(con.getResources().getString(R.string.app_name));
        getActivity().getActionBar().show();
        frameLayout = (FrameLayout) actionBarView.findViewById(R.id.frameLayout);
        countText = (TextView) actionBarView.findViewById(R.id.countText);
        chatIcon.setImageDrawable(con.getResources().getDrawable(R.drawable.msg));
        chatIcon.setVisibility(View.VISIBLE);
        settingsIcon.setVisibility(View.VISIBLE);

        chatIcon.setClickable(true);
        settingsIcon.setClickable(true);
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContextActivity = getActivity();
//                exeptionReporter.sendMail();
                pageNavigated = true;
                if (searchfeedVideoView.isPlaying())
                    searchfeedVideoView.pause();
                searchfeedVideoView.setVisibility(View.GONE);
                addFragmentNew(new EditCompanyProfile(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "EditCompanyProfile", ContextActivity);
            }
        });
        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContextActivity = getActivity();
                exeptionReporter = new UncaughtExceptionReporter(con);
                ChatcountframeLayout.setVisibility(View.GONE);
                countDownTimer.cancel();
                pageNavigated = true;
                if (searchfeedVideoView.isPlaying())
                    searchfeedVideoView.pause();
                searchfeedVideoView.setVisibility(View.GONE);
                addFragmentNew(new ChatList(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "chat", ContextActivity);
            }
        });
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                backFromChatList, new IntentFilter("backFromChatList"));

        //.....................niv
        searchfeedVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                new CountDownTimer(700, 100) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        if (!navigated) {
                            image_and_video_layout.setEnabled(true);
                            searchfeedVideoView.start();
                        }
                    }

                }.start();
            }
        });
        searchfeedVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                image_and_video_layout.setEnabled(true);
                myCrouton.showCrouton(getActivity(), con.getResources().getString(R.string.can_not_play_the_video), 1);
                return true;
            }
        });
        image_and_video_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchfeedVideoView.isPlaying()) {
                    searchfeedVideoView.pause();
                } else {
                    searchfeedVideoView.start();
                }
            }
        });
        searchfeedVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });


        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                String chatCountLink = con.getResources().getString(R.string.url_domain);
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("type", "ChatCount");
                params.put("userid", apfs.getData("user_id"));
                volleyForAll.volleyToNetwork(chatCountLink, VolleyForAll.HttpRequestType.HTTP_POST, params, CHAT_COUNT);
                countDownTimer.start();
            }
        }.start();
        return view;
    }

    @OnClick(R.id.seeMyOffers)
    public void SeeMyOffers() {
        myProgressDialog = new MyProgressDialog(getActivity());
        myProgressDialog.setProgress();
        String myofferURL = getResources().getString(R.string.url_domain);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "viewJobs");
        params.put("compony_id", apfs.getData("user_id"));
        params.put("index", "0");
        volleyForAll.volleyToNetwork(myofferURL, VolleyForAll.HttpRequestType.HTTP_POST, params, CHAT_LIST);
//        replaceFragment(new SeeMyOffer(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "SeeOffers");
    }

    @OnClick(R.id.after_posting_refreshButton)
    public void refreshPage() {
        Search();
    }

    @OnClick(R.id.companyEditProfileButton)
    public void companyEditProfileButton() {
        replaceFragment(new EditCompanyProfile(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "editCompanyProfile");
    }

    @OnClick(R.id.selectedCandidates)
    public void selectedCandidates() {
        replaceFragmentNew(new ChatList(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "chat", ContextActivity);
    }

    @OnClick(R.id.searchBar)
    public void setSearchBar() {
        pageNavigated = true;
        myProgressDialog.dismissProgress();
        if (searchfeedVideoView != null) {
            searchfeedVideoView.pause();
            searchfeedVideoView.setVisibility(View.GONE);
            searchfeedVideoView = null;
        }
        SearchWindow.searchGSON = null;
        addFragmentFromUp(new SearchWindow(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "SearchWindow");
    }

    @OnClick(R.id.infoButton)
    public void infoButton() {
        if (position < jobProfileArrayList.size()) {
            String Header = jobProfileArrayList.get(position).get("search_user_name");
            String desc = jobProfileArrayList.get(position).get("search_Notes");
            apfs.saveData("jobDesc", desc);
            apfs.saveData("userName", Header);
            addFragment(new SeeInfo(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "SeeInfo");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        countDownTimer.cancel();
    }

    @Override
    public void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    @OnClick(R.id.profileLikeButton)
    public void profileLike() {
        if (position < jobProfileArrayList.size()) {

//            if (searchfeedVideoView.isPlaying())
            searchfeedVideoView.stopPlayback();

            myProgressDialog.setProgress();
            String jobProfListURL = con.getResources().getString(R.string.url_domain);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("type", "Like");
            params.put("userid1", jobProfileArrayList.get(position).get("search_user_id"));
            params.put("userid", apfs.getData("user_id"));
            params.put("searchid", searchId);
            params.put("status", "Like");
            params.put("offerid", apfs.getData("Selected_offer_id"));
//            System.out.println("offer like url : " + params);
            volleyForAll.volleyToNetwork(jobProfListURL + "type=Like&userid=" + apfs.getData("user_id") + "&userid1=" + jobProfileArrayList.get(position).get("search_user_id") + "&searchid=" + searchId + "&status=Like" + "&offerid=" + apfs.getData("Selected_offer_id"), VolleyForAll.HttpRequestType.HTTP_GET, null, LIKE_PROF);
            String visitorURL = "";
            try {
                visitorURL = con.getResources().getString(R.string.url_domain);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!visitorURL.equals("")) {
                HashMap<String, String> req_params = new HashMap<String, String>();
                req_params.put("type", "profilevisit");
                req_params.put("userid", apfs.getData("user_id"));
                req_params.put("userid1", jobProfileArrayList.get(position).get("search_user_id"));
                volleyForAll.volleyToNetwork(visitorURL, VolleyForAll.HttpRequestType.HTTP_POST, req_params, UPDATE_VISIT_COUNT);
            }

        } else {
            setVisibility();
        }
    }

    @OnClick(R.id.profileDisLikeButton)
    public void profileDisLike() {
        if (position < jobProfileArrayList.size()) {
            if (searchfeedVideoView.isPlaying())
                searchfeedVideoView.stopPlayback();
            myProgressDialog.setProgress();
            String jobProfListURL = con.getResources().getString(R.string.url_domain);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("type", "Like");
            params.put("userid1", jobProfileArrayList.get(position).get("search_user_id"));
            params.put("userid", apfs.getData("user_id"));
            params.put("searchid", searchId);
            params.put("status", "Disike");
            params.put("offerid", apfs.getData("Selected_offer_id"));
            volleyForAll.volleyToNetwork(jobProfListURL + "type=Like&userid=" + apfs.getData("user_id") + "&userid1=" + jobProfileArrayList.get(position).get("search_user_id") + "&searchid=" + searchId + "&offerid=" + apfs.getData("Selected_offer_id"), VolleyForAll.HttpRequestType.HTTP_GET, null, DISLIKE_PROF);

            String visitorURL = "";
            try {
                visitorURL = con.getResources().getString(R.string.url_domain);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!visitorURL.equals("")) {
                HashMap<String, String> req_params = new HashMap<String, String>();
                req_params.put("type", "profilevisit");
                req_params.put("userid", apfs.getData("user_id"));
                req_params.put("userid1", jobProfileArrayList.get(position).get("search_user_id"));
                volleyForAll.volleyToNetwork(visitorURL, VolleyForAll.HttpRequestType.HTTP_POST, req_params, UPDATE_VISIT_COUNT);
            }

        } else {
            setVisibility();
        }
    }

    private BroadcastReceiver backFromChatList = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            settingsIcon = (ImageView) actionBarView.findViewById(R.id.leftActionImage);
//            chatIcon = (ImageView) actionBarView.findViewById(R.id.rightActionImage);
//            settingsIcon.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    pageNavigated = true;
//                    if (searchfeedVideoView.isPlaying())
//                        searchfeedVideoView.pause();
//                    addFragmentNew(new EditCompanyProfile(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "EditCompanyProfile", act);
//                }
//            });
//            chatIcon.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    pageNavigated = true;
//                    if (searchfeedVideoView.isPlaying())
//                        searchfeedVideoView.pause();
////                    addFragment(new ChatList(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "chat");
//                    replaceFragment(new ChatList(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "chat");
//                }
//            });
        }
    };

    @Override
    public void onVolleyResponse(JSONObject result, int code) {
        myProgressDialog.dismissProgress();
        if (code == PROFLIST) {
            if (result.optBoolean("Result")) {
                searchId = String.valueOf(result.optInt("searchid"));
                JSONArray jarray = result.optJSONArray("Details");
                if (jarray != null) {
                    jobProfileArrayList.clear();
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject json = jarray.optJSONObject(i);
                        HashMap<String, String> hMaps = new HashMap<String, String>();
                        hMaps.put("search_user_id", json.optString("user_id"));
                        hMaps.put("search_user_name", json.optString("user_name"));
                        hMaps.put("search_user_email", json.optString("user_email"));
                        hMaps.put("search_user_address", json.optString("user_address"));
                        hMaps.put("search_Labels", json.optString("Labels"));
                        hMaps.put("search_video", json.optString("video"));
                        hMaps.put("search_image", json.optString("image"));
                        hMaps.put("search_Notes", json.optString("Notes"));
                        jobProfileArrayList.add(hMaps);
                    }
                } else {
                    jobProfileArrayList.clear();
                }
            }
            if (!jobProfileArrayList.isEmpty() && jobProfileArrayList != null && jobProfileArrayList.size() != 0) {
                searchfeedVideoView.setVisibility(View.VISIBLE);
                like_info_dislike_button_set.setVisibility(View.VISIBLE);
                after_posting_searchImageView.setVisibility(View.GONE);
                searchDesc.setVisibility(View.GONE);
                searchImageView.setVisibility(View.GONE);
                after_posting_refreshButton.setVisibility(View.GONE);
                Uri uri = Uri.parse(con.getResources().getString(R.string.url_video) + jobProfileArrayList.get(0).get("search_video"));
                searchfeedVideoView.setVideoURI(uri);

                String visitorURL = "";
                try {
                    visitorURL = con.getResources().getString(R.string.url_domain);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                if (!visitorURL.equals("")) {
//                    if (position < jobProfileArrayList.size()) {
//                        HashMap<String, String> params = new HashMap<String, String>();
//                        params.put("type", "profilevisit");
//                        params.put("userid", apfs.getData("user_id"));
//                        params.put("userid1", jobProfileArrayList.get(position).get("search_user_id"));
//                        System.out.println("visitor count : "+params);
//                        volleyForAll.volleyToNetwork(visitorURL, VolleyForAll.HttpRequestType.HTTP_POST, params, UPDATE_VISIT_COUNT);
//                    }
//                }

            } else {
                after_posting_header.setVisibility(View.VISIBLE);
                searchfeedVideoView.setVisibility(View.GONE);
                after_posting_header.setText("Oferta : " + apfs.getData("jobName"));
                after_posting_searchImageView.setVisibility(View.VISIBLE);
                after_posting_refreshButton.setVisibility(View.VISIBLE);
                searchImageView.setVisibility(View.GONE);
                like_info_dislike_button_set.setVisibility(View.GONE);
                searchDesc.setText(con.getResources().getString(R.string.Por_ahora_no_hay_más_candidatos_para_esta_oferta_En_breve_recibirás_nuevos_vídeoCVs));
            }
        } else if (code == LIKE_PROF) {
            if (result.optBoolean("Result")) {
                myCrouton.showCrouton(getActivity(), con.getResources().getString(R.string.success), 2);
                autoMessage = result.optString("Details");
                Bundle bundle = new Bundle();
                bundle.putString("selectedUser", jobProfileArrayList.get(position).get("search_user_id"));
                bundle.putString("autoMessage", autoMessage);
                ChatPage chatPage = new ChatPage();
                chatPage.setArguments(bundle);
                if (searchfeedVideoView.isPlaying()) {
                    searchfeedVideoView.pause();
//                    searchfeedVideoView = null;
                }
                addFragment(chatPage, R.id.frame, true, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "chatPage");
                navigated = true;
                position = position + 1;
                if (position < jobProfileArrayList.size()) {
//                    searchfeedVideoView.pause();
                    searchfeedVideoView.setVisibility(View.VISIBLE);
                    like_info_dislike_button_set.setVisibility(View.VISIBLE);
                    after_posting_searchImageView.setVisibility(View.GONE);
                    searchImageView.setVisibility(View.GONE);
                    Uri uri = Uri.parse(con.getResources().getString(R.string.url_video) + jobProfileArrayList.get(position).get("search_video"));
                    searchfeedVideoView.setVideoURI(uri);
                    searchfeedVideoView.pause();
                } else {
                    setVisibility();
                }
            } else {
                myCrouton.showCrouton(getActivity(), con.getResources().getString(R.string.failed), 1);
            }

        } else if (code == UPDATE_VISIT_COUNT) {
            Log.e("profile_visit", "success");
        } else if (code == DISLIKE_PROF) {
            if (result.optBoolean("Result")) {
                position++;
                if (position < jobProfileArrayList.size()) {
                    autoMessage = "";
                    searchfeedVideoView.setVisibility(View.VISIBLE);
                    like_info_dislike_button_set.setVisibility(View.VISIBLE);
                    after_posting_searchImageView.setVisibility(View.GONE);
                    searchImageView.setVisibility(View.GONE);
                    Uri uri = Uri.parse(con.getResources().getString(R.string.url_video) + jobProfileArrayList.get(position).get("search_video"));
                    searchfeedVideoView.setVideoURI(uri);
                    searchfeedVideoView.start();
                } else {
                    setVisibility();
                    myCrouton.showCrouton(getActivity(), con.getResources().getString(R.string.Por_ahora_no_hay_más_candidatos_para_esta_oferta_En_breve_recibirás_nuevos_vídeoCVs), 1);
                }
            }
        } else if (code == CHAT_COUNT) {
            try {
                if (result.optBoolean("Result")) {
                    int count = result.optInt("Count");
                    if (count != 0) {
                        frameLayout.setVisibility(View.VISIBLE);
                        ChatcountframeLayout.setVisibility(View.VISIBLE);
                        if (count > 9) {
                            countText.setText("9+");
                            ChatcountText.setText("9+");
                        } else {
                            countText.setText("" + count);
                            ChatcountText.setText("" + count);
                        }
                    } else {
                        frameLayout.setVisibility(View.GONE);
                        ChatcountframeLayout.setVisibility(View.GONE);
                    }
                }
                result = result.optJSONObject("Details");
                apfs.saveData("user_id", result.optString("user_id"));
                apfs.saveData("user_name", result.optString("user_name"));
                apfs.saveData("user_email", result.optString("user_email"));
                apfs.saveData("user_address", result.optString("user_address"));
                apfs.saveData("chat_notification", result.optString("chat_notification"));
                apfs.saveData("profile_visitor", result.optString("profile_visitor"));
                apfs.saveData("user_type", result.optString("user_type"));
                apfs.saveData("password", result.optString("password"));
                apfs.saveData("video", result.optString("video"));
                apfs.saveData("image", result.optString("image"));
                apfs.saveData("profile_notification", result.optString("profile_visitor"));
                apfs.saveData("description", result.optString("Notes"));
                apfs.saveBooleanData("loggedIn", true);
                apfs.saveData("video", result.optString("video"));
                apfs.saveData("profile_notification", result.optString("profile_visitor"));
                apfs.saveData("description", result.optString("Notes"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (code == CHAT_LIST) {
            if (result != null) {
                if (result.optBoolean("Result")) {
                    chatListGSON = (new Gson()).fromJson(
                            result.toString(), MyOfferListGSON.class);
                    replaceFragment(new SeeMyOffer(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "SeeOffers");
                } else
                    myCrouton.showCrouton(getActivity(), getResources().getString(R.string.Aún_no_tienes_ninguna_oferta), 1);
            }
        } else
            myCrouton.showCrouton(getActivity(), "Error", 1);
    }

    @Override
    public void onVolleyError(String result, int code) {
        myProgressDialog.dismissProgress();
    }

    public void prepare() {
        searchfeedVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                searchfeedVideoView.setMediaController(new MediaController(getActivity()));
                searchfeedVideoView.requestFocus();
                if (!navigated)
                    searchfeedVideoView.start();
            }
        });
    }

    public void Search() {
        position = 0;
        if (apfs.getBooleantData("Jobpost") || apfs.getBooleantData("afterPost")) {
            if (apfs.getData("prevLabel") != null && !apfs.getData("prevLabel").equals("")) {
                myProgressDialog.setProgress();
                String jobProfListURL = con.getResources().getString(R.string.url_new_domain) + "jobseekers.php/jobseekers/";
                RequestParams params = new RequestParams();
                params.put("userid", userId);
                params.put("location", apfs.getData("FromAddress"));
                params.put("jobtype", apfs.getData("prevLabel"));
                params.put("distance", apfs.getData("Distance"));
                params.put("oauthToken", authToken);
                AsycHttpCall.getInstance().CallApiPost(jobProfListURL, params, this, PROFLIST);
//                params.put("distanseLimit", apfs.getData("Distance"));
//                params.put("fromaddress", apfs.getData("FromAddress"));
//                params.put("urgentmode", "");
//                params.put("searchtype", "label");
//                params.put("userid", apfs.getData("user_id"));
//                params.put("id", apfs.getData("Selected_offer_id"));
                System.out.println("search params : " + params);
//                volleyForAll.volleyToNetwork(jobProfListURL, VolleyForAll.HttpRequestType.HTTP_POST, params, PROFLIST);
//                String jobProfListURL = con.getResources().getString(R.string.url_domain);
//                HashMap<String, String> params = new HashMap<String, String>();
//                params.put("type", "search");
//                params.put("label", apfs.getData("prevLabel"));
//                params.put("schedule", "");
//                params.put("weekdays", "");
//                params.put("hourlySalary", "");
//                params.put("distanseLimit", apfs.getData("Distance"));
//                params.put("fromaddress", apfs.getData("FromAddress"));
//                params.put("urgentmode", "");
//                params.put("searchtype", "label");
//                params.put("userid", apfs.getData("user_id"));
//                params.put("id", apfs.getData("Selected_offer_id"));
//                System.out.println("search params : " + params);
//                volleyForAll.volleyToNetwork(jobProfListURL, VolleyForAll.HttpRequestType.HTTP_POST, params, PROFLIST);
            } else {
                after_posting_header.setVisibility(View.VISIBLE);
                after_posting_header.setText("Oferta : " + apfs.getData("jobName"));
                after_posting_searchImageView.setVisibility(View.VISIBLE);
                after_posting_refreshButton.setVisibility(View.VISIBLE);
                searchImageView.setVisibility(View.GONE);
                searchfeedVideoView.setVisibility(View.GONE);
                searchDesc.setText(con.getResources().getString(R.string.Por_ahora_no_hay_más_candidatos_para_esta_oferta_En_breve_recibirás_nuevos_vídeoCVs));
                searchDesc.setVisibility(View.VISIBLE);
            }
        } else {
            searchImageView.setVisibility(View.VISIBLE);
        }
    }

    public void setVisibility() {
        searchDesc.setVisibility(View.VISIBLE);
        searchDesc.setText(con.getResources().getString(R.string.Por_ahora_no_hay_más_candidatos_para_esta_oferta_En_breve_recibirás_nuevos_vídeoCVs));
        searchfeedVideoView.setVisibility(View.GONE);
        after_posting_refreshButton.setVisibility(View.VISIBLE);
        profileLikeButton.setVisibility(View.GONE);
        profileDisLikeButton.setVisibility(View.GONE);
        infoButton.setVisibility(View.GONE);
        after_posting_searchImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess(String response, int code) {
        myProgressDialog.dismissProgress();
        switch (code) {
            case PROFLIST:
                parseJobSeekers(response);
                break;
        }
    }

    public void parseJobSeekers(String response) {
        if (response != null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();

            JobSeekersListGSON seekersListGSON = gson.fromJson(response,
                    JobSeekersListGSON.class);
            if (seekersListGSON.result.equals("success")) {
                if (seekersListGSON.Seekers != null) {
                    jobProfileArrayList.clear();
                    for (int i = 0; i < seekersListGSON.Seekers.size(); i++) {
                        HashMap<String, String> hMaps = new HashMap<String, String>();
                        hMaps.put("search_user_id", seekersListGSON.Seekers.get(i).user_id);
                        hMaps.put("search_user_name", seekersListGSON.Seekers.get(i).user_name);
                        hMaps.put("search_user_email", seekersListGSON.Seekers.get(i).user_email);
                        hMaps.put("search_user_address", seekersListGSON.Seekers.get(i).user_address);
                        hMaps.put("search_Labels", seekersListGSON.Seekers.get(i).job_type);
                        hMaps.put("search_video", seekersListGSON.Seekers.get(i).video);
                        hMaps.put("search_image", seekersListGSON.Seekers.get(i).image);
                        hMaps.put("search_Notes", seekersListGSON.Seekers.get(i).Notes);
                        jobProfileArrayList.add(hMaps);
                    }
                } else
                    jobProfileArrayList.clear();
            }

            if (!jobProfileArrayList.isEmpty() && jobProfileArrayList != null && jobProfileArrayList.size() != 0) {
                searchfeedVideoView.setVisibility(View.VISIBLE);
                like_info_dislike_button_set.setVisibility(View.VISIBLE);
                after_posting_searchImageView.setVisibility(View.GONE);
                searchDesc.setVisibility(View.GONE);
                searchImageView.setVisibility(View.GONE);
                after_posting_refreshButton.setVisibility(View.GONE);
                Uri uri = Uri.parse(con.getResources().getString(R.string.url_video) + jobProfileArrayList.get(0).get("search_video"));
                searchfeedVideoView.setVideoURI(uri);

            } else {
                after_posting_header.setVisibility(View.VISIBLE);
                searchfeedVideoView.setVisibility(View.GONE);
                after_posting_header.setText("Oferta : " + apfs.getData("jobName"));
                after_posting_searchImageView.setVisibility(View.VISIBLE);
                after_posting_refreshButton.setVisibility(View.VISIBLE);
                searchImageView.setVisibility(View.GONE);
                like_info_dislike_button_set.setVisibility(View.GONE);
                searchDesc.setText(con.getResources().getString(R.string.Por_ahora_no_hay_más_candidatos_para_esta_oferta_En_breve_recibirás_nuevos_vídeoCVs));
            }
        }
    }

    @Override
    public void onFail(String response, int code) {
        myProgressDialog.dismissProgress();
    }
}
