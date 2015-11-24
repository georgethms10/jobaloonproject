package com.app.jobaloon.utils;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * class to universalize the volley library
 */
public class VolleyForAll {
    VolleyOnResponseListener volleyListenerCallBack;
    Context context;
    JsonObjectRequest jsObjRequest;

    public VolleyForAll(Context context,
                        VolleyOnResponseListener volleyListenerCallBack) {
        this.context = context;
        this.volleyListenerCallBack = volleyListenerCallBack;

    }

    /**
     * @param url
     * @param type
     * @param params HttpRequestType can be HttpRequestType.HTTP_GET or
     *               HttpRequestType.HTTP_POST to determine the Request method
     *               params must be null for GET method. For POST method it must be
     *               parameters
     */
    public void volleyToNetwork(String url, HttpRequestType type,
                                HashMap<String, String> params, int code) {
        RequestQueue queue = Volley.newRequestQueue(context);
        if (type == HttpRequestType.HTTP_GET) {
            volleyGet(url, queue, code);
        } else {
            volleyPost(url, params, queue, code);
        }

    }

    private void volleyGet(String url, RequestQueue queue, final int code) {
        jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        volleyListenerCallBack.onVolleyResponse(response, code);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                volleyListenerCallBack.onVolleyError(error.toString(),
                        code);
            }
        });
        int socketTimeout = 200000;// 200 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        queue.add(jsObjRequest);
    }

    private void volleyPost(String url, HashMap<String, String> params,
                            RequestQueue queue, final int code) {
        CustomRequest jsoCustomRequest = new CustomRequest(Method.POST, url,
                params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                volleyListenerCallBack.onVolleyResponse(response, code);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                volleyListenerCallBack.onVolleyError(error.toString(),
                        code);
            }
        });
        int socketTimeout = 200000;// 200 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsoCustomRequest.setRetryPolicy(policy);
        queue.add(jsoCustomRequest);
    }

    public static enum HttpRequestType {
        HTTP_GET, HTTP_POST
    }
}

class CustomRequest extends Request<JSONObject> {

    private Listener<JSONObject> listener;
    private Map<String, String> params;

    public CustomRequest(String url, Map<String, String> params,
                         Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }

    public CustomRequest(int method, String url, Map<String, String> params,
                         Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }

    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return params;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            setShouldCache(false);
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }

}
