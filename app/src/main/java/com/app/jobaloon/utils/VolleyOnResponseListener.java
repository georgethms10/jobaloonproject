package com.app.jobaloon.utils;

import org.json.JSONObject;

/**
 * 
 * @author Niyaz Sajjad callback listener for volley response
 */
public interface VolleyOnResponseListener {

	public void onVolleyResponse(JSONObject result, int code);

	public void onVolleyError(String result, int code);
}
