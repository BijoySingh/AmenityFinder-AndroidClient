package com.birdlabs.amenityfinder.items;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * the user item
 * Created by bijoy on 1/2/16.
 */
public class UserItem implements Serializable {
    public Integer id;
    public String name;
    public String email;
    public String picture;

    public UserItem(String jsonStr) throws JSONException {
        if (jsonStr == null || jsonStr.contentEquals("null")) {
            name = "Anonymous";
            return;
        }

        JSONObject json = new JSONObject(jsonStr);
        id = json.getInt("id");
        email = json.getString("email");
        name = json.getString("first_name") + " " + json.getString("last_name");
        picture = json.getString("picture");
    }
}
