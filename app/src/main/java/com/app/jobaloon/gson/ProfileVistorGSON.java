package com.app.jobaloon.gson;

import java.util.ArrayList;
import java.util.List;

public class ProfileVistorGSON {

	public String Result;
	public List<UserDetails> Details = new ArrayList<UserDetails>();

	public class UserDetails {
		public String from_userid;
		public String username;
		public String date;
		public int count;
	}

}
