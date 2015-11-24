package com.app.jobaloon.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

public class NetworkCall {

    String url;
    Response resp;
    ProgressDialog progress;
    int code;
    Context context;

    public NetworkCall(String url, Response resp, int code, RequestParams params, Context c) {
        this.url = url;
        this.resp = resp;
        this.code = code;
        context = c;
//		CallApi(url);
        progress = new ProgressDialog(c);

        CallApiPost(url, params);


    }


    public NetworkCall(String url, Response resp, int code, Context c) {
        this.url = url;
        this.resp = resp;
        this.code = code;
        context = c;
        progress = new ProgressDialog(c);
//		CallApi(url);


        CallApi(url);


    }

    private void CallApiPost(String url, RequestParams params) {
        progress.setMessage("Executing call");
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBodys) {
                try {
                    progress.dismiss();
                    String responsebody = new String(responseBodys);
                    System.out.println("response is " + responsebody);
                    resp.onSuccess(responsebody, code);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBodys, Throwable error) {
                try {
                    progress.dismiss();
                    String responsebody = new String(responseBodys);
                    System.out.println("response is " + responsebody);
                    resp.onFail(responsebody, code);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void CallApi(String url2) {
        progress.setMessage("Executing call");
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setConnectTimeout(30000);
        client.get(url2, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                // TODO Auto-generated method stub
                try {
                    progress.dismiss();
                    String responsebody = new String(arg2);
                    resp.onFail(responsebody, code);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                try {
                    progress.dismiss();
                    String responsebody = new String(arg2);
                    resp.onSuccess(responsebody, code);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

    }

}
