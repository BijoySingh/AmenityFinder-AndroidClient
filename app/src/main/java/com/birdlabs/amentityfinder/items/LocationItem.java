package com.birdlabs.amentityfinder.items;

import com.birdlabs.amentityfinder.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * the location item
 * Created by bijoy on 1/2/16.
 */
public class LocationItem implements Serializable {
    public String name;
    public Integer id;
    public Double rating;
    public Double latitude;
    public Double longitude;
    public Boolean male;
    public Boolean female;
    public Boolean isFree;
    public Integer flags;
    public String created;

    public LocationItem(String name, Integer id, Double rating, Double latitude, Double longitude,
                        Boolean male, Boolean female, Boolean isFree, Integer flags, String created) {
        this.name = name;
        this.id = id;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
        this.male = male;
        this.female = female;
        this.isFree = isFree;
        this.created = created;
        this.flags = flags;
    }

    public LocationItem(JSONObject json) throws JSONException {
        id = json.getInt("id");
        latitude = json.getDouble("latitude");
        longitude = json.getDouble("longitude");
        name = json.getString("name");
        isFree = json.getBoolean("is_free");
        rating = json.getDouble("rating");
        male = json.getBoolean("male");
        female = json.getBoolean("female");
        flags = json.getInt("flag_count");
        created = json.getString("created");
    }

    public String getLocation() {
        return latitude + "\u00b0, " + longitude + "\u00b0";
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

    public Integer getGenderDrawable() {
        return getGenderDrawable(male, female);
    }

    public static String getFreeString(Boolean isFree) {
        if (isFree) {
            return "Free";
        }
        return "Paid";
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

    public static LocationItem getPlaceholder() {
        return new LocationItem("LCC IIT Bombay", 1,
                4.65, 23.4342, 32.42323, true, true,
                true, 12, "13 January, 2014");
    }
}
