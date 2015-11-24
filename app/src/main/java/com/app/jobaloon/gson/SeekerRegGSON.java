package com.app.jobaloon.gson;

/**
 * Created by Shadila on 21-Nov-15.
 */
public class SeekerRegGSON {
    public boolean Result;

    public Details Details;

    public class Details {
        public String user_id,
                user_name,
                user_email,
                password,
                user_address, job_type,oauthToken,
                user_type, experience1, experience2, experience3,distance;
//        public boolean Jobpost;

    }
}
