package com.app.jobaloon.gson;

import java.util.List;

/**
 * Created by Shadila on 23-Nov-15.
 */
public class JobListGSON {
    public boolean Result;

    public List<Details> Details;

    public class Details {
        public String id, Role, start_date, end_date, working_days,
                location, distance, jobDescription, compony_id,
                additional_information, distances, user_name;
    }
}
