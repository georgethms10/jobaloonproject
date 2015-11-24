package com.app.jobaloon.gson;

import java.util.List;

/**
 * Created by SICS-Dpc2 on 02-Feb-15.
 */
public class ChatPageGSON {
    public boolean Result;
    public String user_name;
    public List<MessageData> Details;

    public class MessageData {
        public String id,
                fromId,
                toId,
                Message,
                readUnread;
    }
}
