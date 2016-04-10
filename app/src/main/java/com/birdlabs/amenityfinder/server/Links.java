package com.birdlabs.amenityfinder.server;

/**
 * the links
 * Created by bijoy on 1/2/16.
 */
public class Links {
    public static String getHomeUrl() {
        return "http://192.168.0.105:8008/";
        //return "https://af.thecodershub.com/";
    }

    public static String getLogin() {
        return getHomeUrl() + "api/account/login/";
    }

    public static String getLocation(Integer location) {
        return getHomeUrl() + "api/item/" + location + "/";
    }

    public static String getLocations() {
        return getHomeUrl() + "api/item/search_bounding_box/";
    }

    public static String addLocation() {
        return getHomeUrl() + "api/item/";
    }

    public static String getUsersPost(Integer location) {
        return getHomeUrl() + "api/item/" + location + "/get_user_comment/";
    }

    public static String getLocationPosts(Integer location) {
        return getHomeUrl() + "api/item/" + location + "/get_comments/";
    }

    public static String getLocationPhotos(Integer location) {
        return getHomeUrl() + "api/item/" + location + "/get_photos/";
    }

    public static String getLocationStars(Integer location) {
        return getHomeUrl() + "api/item/" + location + "/get_stars/";
    }

    public static String getPostLink() {
        return getHomeUrl() + "api/comment/";
    }

    public static String getPictureLink() {
        return getHomeUrl() + "api/photo/";
    }

    public static String getPostLink(Integer post) {
        return getHomeUrl() + "api/comment/" + post + "/";
    }

    public static String getPostUpvote(Integer post) {
        return getHomeUrl() + "api/comment/" + post + "/upvote/";
    }

    public static String getPostDownvote(Integer post) {
        return getHomeUrl() + "api/comment/" + post + "/downvote/";
    }

    public static String getPostUnvote(Integer post) {
        return getHomeUrl() + "api/comment/" + post + "/unvote/";
    }

    public static String getPostFlag(Integer post) {
        return getHomeUrl() + "api/comment/" + post + "/flag/";
    }

    public static String getPostUnflag(Integer post) {
        return getHomeUrl() + "api/comment/" + post + "/unflag/";
    }

    public static String getPhotoUpvote(Integer post) {
        return getHomeUrl() + "api/photo/" + post + "/upvote/";
    }

    public static String getPhotoDownvote(Integer post) {
        return getHomeUrl() + "api/photo/" + post + "/downvote/";
    }

    public static String getPhotoUnvote(Integer post) {
        return getHomeUrl() + "api/photo/" + post + "/unvote/";
    }

    public static String getPhotoFlag(Integer post) {
        return getHomeUrl() + "api/photo/" + post + "/flag/";
    }

    public static String getPhotoUnflag(Integer post) {
        return getHomeUrl() + "api/photo/" + post + "/unflag/";
    }

    public static String flagLocation(Integer location) {
        return getHomeUrl() + "api/item/" + location + "/add_flag/";
    }

    public static String getPicture(Integer picture) {
        return getHomeUrl() + "api/photo/" + picture + "/";
    }

    public static String getProfile() {
        return getHomeUrl() + "api/account/get_profile/";
    }

    public static String getActivity() {
        return getHomeUrl() + "api/account/get_activity/";
    }

    public static String addComment(Integer location) {
        return getHomeUrl() + "api/item/" + location + "/add_comment/";
    }

    public static String addPhoto(Integer location) {
        return getHomeUrl() + "api/item/" + location + "/add_photo/";
    }

    public static String addRating(Integer location) {
        return getHomeUrl() + "api/item/" + location + "/add_rating/";
    }

}
