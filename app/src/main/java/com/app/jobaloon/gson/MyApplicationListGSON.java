package com.app.jobaloon.gson;

import java.util.List;

/**
 * Created by SICS-Dpc2 on 13-Apr-15.
 */
public class MyApplicationListGSON {
    public boolean Result;
    public List<Details> Details;

    public class Details {
        public String id,Role,
                start_date,
                end_date,
                working_days,
                location,
                distance,
                jobDescription,
                compony_id,
                user_name;


    }
}
