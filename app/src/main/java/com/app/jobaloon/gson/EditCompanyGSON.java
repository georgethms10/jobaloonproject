package com.app.jobaloon.gson;

/**
 * Created by Shadila U on 23-Nov-15.
 */
public class EditCompanyGSON {
    public boolean Result;

    public Details Details;

    public class Details{
        public String user_id,user_name,user_email,password,
                user_address,user_type,oauthToken;
    }
}
