package com.app.jobaloon.gson;

import java.util.List;

/**
 * Created by Shadila on 24-Nov-15.
 */
public class JobSeekersListGSON {

    public String result;
    public List<Seekers> Seekers;

    public class Seekers {
        public String user_id, user_name, user_email, user_address, job_type,
                video, image, experience1, experience2, experience3, distance,Notes;
    }
}
