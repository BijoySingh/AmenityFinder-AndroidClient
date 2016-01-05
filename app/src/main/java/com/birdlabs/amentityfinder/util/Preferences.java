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
        public static final String FB_USER_ID = "FB_USER_ID";
    }

    public Preferences(Context context) {
        super(context);
    }

    @Override
    public String getPreferencesFolder() {
        return "AMENITY_FINDER";
    }
}
