package com.birdlabs.amentityfinder.server;

/**
 * the links
 * Created by bijoy on 1/2/16.
 */
public class Links {
    public static String getHomeUrl() {
        return "http://192.168.0.102:8000/";
        //return "https://af.thecodershub.com/";
    }

    public static String getLocation(Integer location) {
        return getHomeUrl() + "api/location/" + location + "/";
    }

    public static String getLocations() {
        return getHomeUrl() + "api/location/search_by_bbox/";
    }

    public static String addLocation() {
        return getHomeUrl() + "api/location/";
    }

    public static String patchLocation(Integer location) {
        return getHomeUrl() + "api/location/" + location + "/";
    }


    public static String getLocationPosts(Integer location) {
        return getHomeUrl() + "api/location/" + location + "/get_posts/";
    }

    public static String getPostLink() {
        return getHomeUrl() + "api/post/";
    }
}
