package com.birdlabs.amentityfinder.server;

import android.content.Context;

import com.android.volley.VolleyError;
import com.birdlabs.basicproject.server.AccessItem;
import com.birdlabs.basicproject.server.AccessManager;

import org.json.JSONObject;

import java.util.Map;

/**
 * the server access
 * Created by bijoy on 1/2/16.
 */
public class Access extends AccessManager {

    public Access(Context context) {
        super(context);
    }

    @Override
    public Map<String, String> getAuthenticationData() {
        return null;
    }

    @Override
    public void handleGetResponse(AccessItem accessItem, JSONObject jsonObject) {

    }

    @Override
    public void handleSendResponse(AccessItem accessItem, JSONObject jsonObject) {

    }

    @Override
    public void handleGetError(AccessItem accessItem, VolleyError volleyError) {

    }

    @Override
    public void handleSendError(AccessItem accessItem, VolleyError volleyError) {

    }
}
