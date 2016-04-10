package com.birdlabs.amenityfinder.server;

import com.github.bijoysingh.starter.json.JsonField;
import com.github.bijoysingh.starter.json.JsonModel;

import org.json.JSONObject;

/**
 * Created by bijoy on 4/9/16.
 */
public class UserActivityItem extends JsonModel {

    public static final Integer PHOTO = 0;
    public static final Integer COMMENT = 1;
    public static final Integer ITEM = 2;
    public static final Integer RATING = 3;


    @JsonField
    public Integer id;

    @JsonField
    public Integer xp;

    @JsonField
    public String title;

    @JsonField
    public String timestamp;

    public Integer type;

    public UserActivityItem(JSONObject json, Integer type) throws Exception {
        super(json);
        this.type = type;
    }
}
