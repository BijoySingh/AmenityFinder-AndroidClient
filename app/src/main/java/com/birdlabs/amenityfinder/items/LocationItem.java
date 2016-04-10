package com.birdlabs.amenityfinder.items;

import com.birdlabs.amenityfinder.R;
import com.github.bijoysingh.starter.json.JsonField;
import com.github.bijoysingh.starter.json.JsonModel;
import com.github.bijoysingh.starter.util.LocaleManager;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * the location item
 * Created by bijoy on 1/2/16.
 */
public class LocationItem extends JsonModel implements Serializable {

    public static final String DEGREE = "\u00b0";

    @JsonField
    public Integer id;

    @JsonField
    public String title;

    @JsonField
    public String description;

    @JsonField
    public Double rating;

    @JsonField
    public Double latitude;

    @JsonField
    public Double longitude;

    @JsonField
    public Boolean male;

    @JsonField
    public Boolean female;

    @JsonField(field = "is_free")
    public Boolean isFree;

    @JsonField(field = "is_anonymous")
    public Boolean isAnonymous;

    @JsonField(field = "flags")
    public Integer flags;

    @JsonField
    public Integer status;

    @JsonField
    public String timestamp;

    public UserItem author;

    public LocationItem(JSONObject json) throws Exception {
        super(json);
        author = new UserItem(json.getString("author"));
    }

    public static Integer getGenderDrawable(Boolean male, Boolean female) {
        if (male && female) {
            return R.drawable.washroom;
        } else if (male) {
            return R.drawable.male;
        } else if (female) {
            return R.drawable.female;
        } else {
            return R.drawable.washroom;
        }
    }

    public static String getFreeString(Boolean isFree) {
        return isFree ? "Free" : "Paid";
    }

    public String getLocation() {
        return String.format("%.3f\u00b0N, %.3f\u00b0E", latitude, longitude);
    }

    public Integer getGenderDrawable() {
        return getGenderDrawable(male, female);
    }

    public String getSnippet() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("male", male);
        map.put("female", female);
        map.put("rating", rating);
        map.put("is_free", isFree);

        return new JSONObject(map).toString();
    }
}
