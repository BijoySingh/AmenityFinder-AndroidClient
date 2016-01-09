package com.birdlabs.amentityfinder.server;

/**
 * the links
 * Created by bijoy on 1/2/16.
 */
public class Links {
    public static String getHomeUrl() {
        //return "http://192.168.0.105:8000/";
        return "https://amfi.thecodershub.com/";
    }

    public static String getLogin() {
        return getHomeUrl() + "api/account/login/";
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

    public static String getUsersPost(Integer location) {
        return getHomeUrl() + "api/location/" + location + "/current_post/";
    }

    public static String getLocationPosts(Integer location) {
        return getHomeUrl() + "api/location/" + location + "/get_posts/";
    }

    public static String getLocationPhotos(Integer location) {
        return getHomeUrl() + "api/location/" + location + "/get_pictures/";
    }

    public static String getPostLink() {
        return getHomeUrl() + "api/post/";
    }

    public static String getPictureLink() {
        return getHomeUrl() + "api/picture/";
    }

    public static String getPostLink(Integer post) {
        return getHomeUrl() + "api/post/" + post + "/";
    }

    public static String getPostUpvote(Integer post) {
        return getHomeUrl() + "api/post/" + post + "/upvote/";
    }

    public static String getPostDownvote(Integer post) {
        return getHomeUrl() + "api/post/" + post + "/downvote/";
    }

    public static String flagLocation(Integer location) {
        return getHomeUrl() + "api/location/" + location + "/flag_post/";
    }

}
