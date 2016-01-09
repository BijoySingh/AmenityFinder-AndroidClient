package com.birdlabs.amentityfinder.server;

import android.app.Activity;

import com.birdlabs.basicproject.server.AccessItem;

/**
 * the access information
 * Created by bijoy on 1/2/16.
 */
public class AccessInfo extends AccessItem {

    public static class AccessIds {
        public static final Integer LOCATION_GET = 0;
        public static final Integer LOCATION_POST = 1;
        public static final Integer LOCATION_FLAG = 2;
        public static final Integer LOCATION_GET_USER_POST = 3;
        public static final Integer LOCATION_GET_POSTS = 4;

        public static final Integer POST_GET = 5;
        public static final Integer POST_POST = 6;
        public static final Integer POST_UPVOTE = 7;
        public static final Integer POST_DOWNVOTE = 8;

        public static final Integer PHOTO_POST = 9;
        public static final Integer PHOTO_GET = 10;

        public static final Integer FB_LOGIN = 11;

    }

    public AccessInfo(String url, String filename, Integer type, Boolean authenticated) {
        super(url, filename, type, authenticated);
    }

}
