package com.birdlabs.amentityfinder.util;

import android.content.Context;

import com.birdlabs.basicproject.util.PreferenceManager;

/**
 * the preferences
 * Created by bijoy on 1/4/16.
 */
public class Preferences extends PreferenceManager {

    public class Keys {
        public static final String FB_LOGIN = "FB_LOGIN";
        public static final String SERVER_LOGIN = "SERVER_LOGIN";
        public static final String AUTH_TOKEN = "AUTH_TOKEN";
        public static final String USER_ID = "USER_ID";
    }

    public Preferences(Context context) {
        super(context);
    }

    @Override
    public String getPreferencesFolder() {
        return "AMENITY_FINDER";
    }
}
