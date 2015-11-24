package com.app.jobaloon.gson;

/**
 * Created by Shadila on 21-Nov-15.
 */
public class EditJobSeekerGSON {
    public boolean Result;

    public Details Details;

    public class Details {
        public String user_id,
                user_name,
                user_email,
                password,
                user_address, user_type,
                oauthToken, experience1, experience2, experience3,
                video,image,distance,Jobdescription;
//        public boolean Jobpost;

    }
}
