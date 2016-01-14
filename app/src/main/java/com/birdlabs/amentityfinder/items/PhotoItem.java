package com.birdlabs.amentityfinder.items;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * item for comment
 * Created by bijoy on 1/2/16.
 */
public class PhotoItem {
    public Integer id;
    public Integer location;
    public Boolean anonymous;
    public UserItem author;
    public String created;
    public String photo;
    public Integer flags;


    public PhotoItem(JSONObject json) throws JSONException {
        id = json.getInt("id");
        anonymous = json.getBoolean("is_anonymous");
        author = new UserItem(json.getString("user"));
        created = json.getString("created");
        location = json.getInt("location");
        photo = json.getString("photo");
        flags = json.getInt("flags");
    }

    public String getDisplayName() {
        if (anonymous) {
            return "Anonymous";
        }
        return author.name;
    }
}
