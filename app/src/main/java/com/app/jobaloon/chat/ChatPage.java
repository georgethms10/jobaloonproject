package com.app.jobaloon.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.jobaloon.company.SearchPageNew;
import com.google.gson.Gson;
import com.app.jobaloon.company.EditCompanyProfile;
import com.app.jobaloon.gson.ChatPageGSON;
import com.app.jobaloon.jobseeker.EditJobSeekerProfile;
import com.app.jobaloon.main.R;
import com.app.jobaloon.utils.AppPreferences;
import com.app.jobaloon.utils.FragmentActions;
import com.app.jobaloon.utils.HideKeyBoard;
import com.app.jobaloon.utils.MyCrouton;
import com.app.jobaloon.utils.MyProgressDialog;
import com.app.jobaloon.utils.UncaughtExceptionReporter;
import com.app.jobaloon.utils.VolleyForAll;
import com.app.jobaloon.utils.VolleyOnResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by SICS-Dpc2 on 02-Feb-15.
 */
public class ChatPage extends FragmentActions implements VolleyOnResponseListener {
    private static final int FULL_CHAT = 1, LAST_CHAT = 2, SEND_MESSAGE = 3, REFRESH = 4;
    @InjectView(R.id.chatPageListView)
    ListView chatPageListView;
    @InjectView(R.id.chatTitle)
    TextView chatTitle;
    @InjectView(R.id.messageEdit)
    EditText messageEdit;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipe_container;
    private String selectedUser;
    private MyCrouton myCrouton;
    private AppPreferences preferences;
    private VolleyForAll volleyForAll;
    private MyProgressDialog myProgressDialog;
    private int index = 0;
    private MyChatPageAdapter myChatPageAdapter;
    private List<ChatPageGSON.MessageData> messageList;
    private CountDownTimer countDownTimer;
    private String lastChatId = "";
    private ImageView leftActionImage, rightActionImage;
    private TextView countVisitorsText;
    private FrameLayout frameLayout;
    private boolean fromSearch = false;
    FragmentActivity con;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_page, container, false);
        ButterKnife.inject(this, view);
        view.setClickable(true);

        myProgressDialog = new MyProgressDialog(getActivity());
        myProgressDialog.setProgress();
        myCrouton = new MyCrouton();
        con = getActivity();

        Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionReporter(
                con));

        con.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        preferences = new AppPreferences(getActivity(), "JobBoxData");
        volleyForAll = new VolleyForAll(getActivity(), this);
        selectedUser = getArguments().getString("selectedUser");
//        autoMessage = getArguments().getString("autoMessage");//from searchPage - Like Button click
//
//        if (autoMessage != null)
//            fromSearch = true;
//        else fromSearch = false;

        messageList = new ArrayList<ChatPageGSON.MessageData>();
        String fullChatListURL = con.getResources().getString(R.string.url_domain);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", "chatView");
        params.put("userid", preferences.getData("user_id"));
        params.put("userid1", selectedUser);
        params.put("chatid", "");
        params.put("index", "" + index);
        volleyForAll.volleyToNetwork(fullChatListURL, VolleyForAll.HttpRequestType.HTTP_POST, params, FULL_CHAT);
        messageEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    sendNow();
                }
                return false;
            }
        });
        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                try {
                    String lastChatURL = con.getResources().getString(R.string.url_domain);
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("type", "chatView");
                    params.put("userid", preferences.getData("user_id"));
                    params.put("userid1", selectedUser);
                    params.put("chatid", lastChatId);
                    params.put("index", "0");
                    volleyForAll.volleyToNetwork(lastChatURL, VolleyForAll.HttpRequestType.HTTP_POST, params, LAST_CHAT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                countDownTimer.start();
            }
        }.start();

        swipe_container.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipe_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                ++index;
                String fullChatListURL = con.getResources().getString(R.string.url_domain);
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("type", "chatView");
                params.put("userid", preferences.getData("user_id"));
                params.put("userid1", selectedUser);
                params.put("chatid", "");
                params.put("index", "" + index);
                volleyForAll.volleyToNetwork(fullChatListURL, VolleyForAll.HttpRequestType.HTTP_POST, params, REFRESH);
            }

        });
        return view;
    }

    @OnClick(R.id.backButton)
    public void goBack() {
        SearchPageNew.navigated = false;
        new HideKeyBoard(getActivity());
        getFragmentManager().popBackStack();
//        getActivity().finish();
//        addFragment(new SearchPageNew(), R.id.frame, false, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, "SearchPage");
//        addFragment(new ChatList(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "ChatList");
        if (fromSearch) {
            leftActionImage.setClickable(true);
            rightActionImage.setClickable(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        countDownTimer.cancel();
        myProgressDialog.dismissProgress();
        SearchPageNew.navigated = false;
    }

    private void makeActionBarSuitable() {
        View actionBarView = getActivity().getActionBar().getCustomView();
        leftActionImage = (ImageView) actionBarView.findViewById(R.id.leftActionImage);
        rightActionImage = (ImageView) actionBarView.findViewById(R.id.rightActionImage);
        rightActionImage.setVisibility(View.VISIBLE);
        countVisitorsText = (TextView) actionBarView.findViewById(R.id.countVisitorsText);
        frameLayout = (FrameLayout) actionBarView.findViewById(R.id.frameLayout);
        if (preferences.getData("user_type").equalsIgnoreCase("Company"))
            countVisitorsText.setVisibility(View.GONE);
        frameLayout.setVisibility(View.GONE);
        leftActionImage.setClickable(true);
        rightActionImage.setClickable(true);
        leftActionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
                if (preferences.getData("user_type").equalsIgnoreCase("Company"))
                    addFragmentNew(new EditCompanyProfile(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "EditCompanyProfile", con);
                else
                    replaceFragmentNew(new EditJobSeekerProfile(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "EditJobSeekerProfile", con);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });
        rightActionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
                //removing this fragment and adding chat list again
                getActivity().getFragmentManager().popBackStack();
                addFragment(new ChatList(), R.id.frame, true, FragmentTransaction.TRANSIT_ENTER_MASK, "chat");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (countDownTimer != null)
            countDownTimer.cancel();
        if (preferences.getData("user_type").equalsIgnoreCase("Company"))
            leftActionImage.setImageResource(R.drawable.settings);
        else
            leftActionImage.setImageResource(R.drawable.settings);
        if (fromSearch) {
            leftActionImage.setClickable(true);
            rightActionImage.setClickable(true);
            // calling broadcast
            Intent intent = new Intent("backFromChatList");
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        } else {
            leftActionImage.setClickable(true);
            rightActionImage.setClickable(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        makeActionBarSuitable();
        if (messageList != null && messageList.size() != 0)
            countDownTimer.start();
    }

    @Override
    public void onVolleyResponse(JSONObject result, int code) {
        myProgressDialog.dismissProgress();
        if (result != null) {
            switch (code) {
                case FULL_CHAT:
                    parseFullChat(result);
                    break;
                case LAST_CHAT:
                    parseLastChat(result);
                    break;
                case SEND_MESSAGE:
                    parseSendMessage(result);
                    break;
                case REFRESH:
                    loadMoreChat(result);
                    break;
                default:
                    break;
            }
        } else
            myCrouton.showCrouton(con, con.getResources().getString(R.string.failed_to_get_data_from_server), 2);
    }

    @Override
    public void onVolleyError(String result, int code) {
        myProgressDialog.dismissProgress();
        myCrouton.showCrouton(con, result, 1);
    }

    private void parseFullChat(JSONObject result) {
        myChatPageAdapter = new MyChatPageAdapter(getActivity(), messageList, preferences.getData("user_id"));
        chatPageListView.setAdapter(myChatPageAdapter);
        chatTitle.setText("Chat: " + result.optString("user_name"));
        swipe_container.setRefreshing(false);
        if (result.optBoolean("Result")) {
            ChatPageGSON chatPageGSON = (new Gson()).fromJson(
                    result.toString(), ChatPageGSON.class);
            if (chatPageGSON != null) {
                messageList.addAll(0, chatPageGSON.Details);
                lastChatId = messageList.get(messageList.size() - 1).id;
                myChatPageAdapter.notifyDataSetChanged();
                chatPageListView.setSelection(messageList.size() - 1);
            }
            //starting timer for last chat
            countDownTimer.start();
        }
//        if (!autoMessage.equals("")) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setTitle(getString(R.string.auto_message_confirm))
//                    .setMessage(autoMessage)
//                    .setPositiveButton(getString(R.string.send_this_message), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //send auto message
//                            messageEdit.setText(autoMessage);
//                            sendNow();
//                            dialog.cancel();
//                        }
//                    }).setNegativeButton(getString(R.string.send_another_message), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            }).show();
//        }
    }

    private void loadMoreChat(JSONObject result) {
        myChatPageAdapter = new MyChatPageAdapter(getActivity(), messageList, preferences.getData("user_id"));
        chatPageListView.setAdapter(myChatPageAdapter);
        chatTitle.setText("Chat: " + result.optString("user_name"));
        swipe_container.setRefreshing(false);
        if (result.optBoolean("Result")) {
            ChatPageGSON chatPageGSON = (new Gson()).fromJson(
                    result.toString(), ChatPageGSON.class);
            if (chatPageGSON != null) {
                messageList.addAll(0, chatPageGSON.Details);
                myChatPageAdapter.notifyDataSetChanged();
            }
        }
    }

    private void parseLastChat(JSONObject result) {
        if (result.optBoolean("Result")) {
            JSONArray jsonArray = result.optJSONArray("Details");
            int size = jsonArray.length();
            for (int i = 0; i < size; i++) {
                result = jsonArray.optJSONObject(i);
                if (!result.optString("fromId").equals(preferences.getData("user_id"))) {
                    ChatPageGSON chatPageGSON = new ChatPageGSON();
                    ChatPageGSON.MessageData details = chatPageGSON.new MessageData();
                    details.id = result.optString("id");
                    details.fromId = result.optString("fromId");
                    details.Message = result.optString("Message");
                    details.readUnread = result.optString("readUnread");
                    details.toId = result.optString("toId");
                    messageList.add(details);
                    lastChatId = result.optString("id");
                    try {
                        myChatPageAdapter.notifyDataSetChanged();
                        chatPageListView.setSelection(messageList.size() - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        countDownTimer.start();
    }

    private void parseSendMessage(JSONObject result) {
        if (!result.optBoolean("Result")) {
            try {
                messageList.remove(messageList.size() - 1);
                myChatPageAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.sendBtn)
    public void send() {
        sendNow();
    }

    private void sendNow() {
        String message = messageEdit.getText().toString().trim();
        if (!message.equals("")) {
            new HideKeyBoard(getActivity());
            messageEdit.setText("");
            //storing it locally and showing it
            ChatPageGSON chatPageGSON = new ChatPageGSON();
            ChatPageGSON.MessageData details = chatPageGSON.new MessageData();
            details.id = "0";
            details.fromId = preferences.getData("user_id");
            details.Message = message;
            details.readUnread = "0";
            details.toId = selectedUser;
            messageList.add(details);
            myChatPageAdapter.notifyDataSetChanged();
            chatPageListView.setSelection(messageList.size() - 1);
            //updating the values in sever
            //utf-8 encoding
            try {
                message = URLEncoder.encode(message, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String sendURL = getResources().getString(R.string.url_domain);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("type", "insertChat");
            params.put("userid", preferences.getData("user_id"));
            params.put("userid1", selectedUser);
            params.put("message", message);
            volleyForAll.volleyToNetwork(sendURL, VolleyForAll.HttpRequestType.HTTP_POST, params, SEND_MESSAGE);
//            autoMessage = "";
        }
    }
}
