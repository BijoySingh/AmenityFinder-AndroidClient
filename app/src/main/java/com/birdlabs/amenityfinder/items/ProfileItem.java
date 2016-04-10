package com.birdlabs.amenityfinder.items;

import com.github.bijoysingh.starter.json.JsonField;
import com.github.bijoysingh.starter.json.JsonModel;

import org.json.JSONObject;

/**
 * User Json Item
 * Created by bijoy on 3/6/16.
 */
public class ProfileItem extends JsonModel {
    @JsonField
    public Integer id;

    @JsonField
    public String username;

    @JsonField
    public String first_name;

    @JsonField
    public String last_name;

    @JsonField
    public String email;

    @JsonField
    public String picture;

    public Integer level;
    public String levelTitle;

    @JsonField
    public Integer reputation;

    @JsonField
    public Integer photos;

    @JsonField
    public Integer ratings;

    @JsonField
    public Integer comments;

    @JsonField
    public Integer items;

    public ProfileItem(JSONObject response) throws Exception {
        super(response);

        JSONObject level = response.getJSONObject("level");
        this.level = level.getInt("type");
        this.levelTitle = level.getString("title");
    }

    public String getName() {
        return first_name + " " + last_name;
    }

}
