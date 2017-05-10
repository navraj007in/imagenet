package com.infodart.instaproject.model;

import org.json.JSONObject;

/**
 * Created by navraj.singh on 5/4/2017.
 */

public class Profile extends Users {

    public static Profile ParseJSON(String json) {
        Profile users = new Profile();
        try {
            JSONObject jsonObject = new JSONObject(json);
            if(jsonObject.has("_id"))
                users.set_id(jsonObject.getString("_id"));
            if(jsonObject.has("name"))
                users.setName(jsonObject.getString("name"));
            if(jsonObject.has("userId"))
                users.setName(jsonObject.getString("userId"));
            if(jsonObject.has("description"))
                users.setName(jsonObject.getString("description"));
            if(jsonObject.has("image"))
                users.setName(jsonObject.getString("image"));
            if(jsonObject.has("followersList"))
                users.setFollowers(jsonObject.getString("followersList"));
            if(jsonObject.has("following"))
                users.setFollowing(jsonObject.getString("following"));
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return users;
    }

}
