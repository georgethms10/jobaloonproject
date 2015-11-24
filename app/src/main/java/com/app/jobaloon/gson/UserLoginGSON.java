package com.app.jobaloon.gson;

import java.util.List;

/**
 * Created by Shadila on 20-Nov-15.
 */
public class UserLoginGSON {
    public boolean Result;

    public Details Details;

    public class Details {
        public String user_id,
                user_name,
                user_email,
                password,
                user_address, user_type,
                oauthToken, experience1, experience2, experience3,
                video,image,JobLabel,distance,location,lastSearchid,Jobdescription,JobId;
        public boolean Jobpost;

    }
}
