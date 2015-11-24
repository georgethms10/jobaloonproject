package com.app.jobaloon.gson;

import java.util.List;

/**
 * Created by SICS-Dpc2 on 31-Jan-15.
 */
public class ChatListGSON {
    public boolean Result;
    public List<Details> Details;

    public class Details {
        public String
                user_id,
                user_name,
                date,image;
        public int unReadcount;
    }
}
