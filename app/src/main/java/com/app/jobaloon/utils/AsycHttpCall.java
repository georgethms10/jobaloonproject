package com.app.jobaloon.utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by sics on 11/20/2015.
 */
public class AsycHttpCall {

    private static AsycHttpCall instance;

    public static AsycHttpCall getInstance()
    {
        if (instance==null)
            instance=new AsycHttpCall();

        return instance;
    }


    public void CallApiPost(String url, RequestParams params, final Response resp, final int code) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBodys) {
                try {
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
                    String responsebody = new String(responseBodys);
                    System.out.println("response is " + responsebody);
                    resp.onFail(responsebody, code);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void CallApi(String url2, final int code, final Response resp) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setConnectTimeout(30000);
        client.get(url2, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                // TODO Auto-generated method stub
                try {
                    System.out.println("response code "+arg0);
                    String responsebody = new String(arg2);
                    System.out.println("response is " +responsebody);
                    resp.onFail(responsebody, code);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                // TODO Auto-generated method stub
                try {
                    System.out.println("response code "+arg0);

                    String responsebody = new String(arg2);
                    System.out.println("response is " +responsebody);
                    resp.onSuccess(responsebody, code);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

    }




}
