package com.app.jobaloon.gson;

/**
 * Created by Shadila on 24-Nov-15.
 */
public class UploadVideoGSON {
    public boolean Result;

    public Details Details;

    public class Details {
        public String user_id, user_name, user_email, password, user_address,
                user_type, video, image, oauthToken, Jobdescription, experience1, experience2,
                experience3, distance, Labels;
    }
}
