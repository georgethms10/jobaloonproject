package com.app.jobaloon.gson;

import java.util.List;

public class SearchGSON {
    public boolean Result;
    public int searchid;
    public List<Details> Details;

    public class Details {
        public String
                user_id,
                user_name,
                user_email,
                password,
                user_address,
                chat_notification,
                profile_visitor,
                Labels,
                user_type,
                dateOfJoin,
                Last_activity,
                Last_activity_date,
                lattitude,
                longitude, video, Notes, image;
    }
}
