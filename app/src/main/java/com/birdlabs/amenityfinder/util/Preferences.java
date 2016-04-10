package com.birdlabs.amenityfinder.util;

import android.content.Context;

import com.github.bijoysingh.starter.util.PreferenceManager;

/**
 * the preferences
 * Created by bijoy on 1/4/16.
 */
public class Preferences extends PreferenceManager {

    public Preferences(Context context) {
        super(context);
    }

    @Override
    public String getPreferencesFolder() {
        return "AMENITY_FINDER";
    }

    public Boolean isServerLogged() {
        return load(Preferences.Keys.SERVER_LOGIN, false);
    }

    public Boolean isSocialNetworkLoggedIn() {
        return load(Preferences.Keys.SOCIAL_NETWORK_LOGIN, false);
    }

    public Boolean isLoggedIn() {
        return isServerLogged() && isSocialNetworkLoggedIn();
    }

    public void serverLogin(Boolean login) {
        save(Preferences.Keys.SERVER_LOGIN, login);
    }

    public void socialNetworkLogin(Boolean login) {
        save(Preferences.Keys.SOCIAL_NETWORK_LOGIN, login);
    }

    public Integer getUserId() {
        return load(Keys.USER_ID, -1);
    }

    public class Keys {
        public static final String USER_ID = "USER_ID";
        public static final String AUTH_TOKEN = "AUTH_TOKEN";
        public static final String SERVER_LOGIN = "SERVER_LOGIN";
        public static final String SOCIAL_NETWORK_LOGIN = "SOCIAL_NETWORK_LOGIN";
    }
}
