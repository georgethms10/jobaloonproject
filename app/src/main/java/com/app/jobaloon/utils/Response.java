package com.app.jobaloon.utils;


public interface Response {
	
	
	  void onSuccess(String response, int code);
	  
	  void onFail(String response, int code);

}
