package com.birdlabs.amentityfinder.server;

import android.content.Context;

import com.android.volley.VolleyError;
import com.birdlabs.amentityfinder.activity.LocationActivity;
import com.birdlabs.amentityfinder.activity.MapActivity;
import com.birdlabs.basicproject.server.AccessItem;
import com.birdlabs.basicproject.server.AccessManager;
import com.birdlabs.basicproject.util.FileManager;

import org.json.JSONObject;

import java.util.HashMap;
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
        return new HashMap<>();
    }

    @Override
    public void handleGetResponse(AccessItem accessItem, String response) {
        final AccessInfo access = (AccessInfo) accessItem;
        writeInFile(response, accessItem);

        if (access.activity != null) {
            if (accessItem.type.equals(AccessInfo.AccessIds.LOCATION_GET)) {
                ((MapActivity) access.activity).refreshMap();
            } else if (accessItem.type.equals(AccessInfo.AccessIds.POST_GET)) {
                ((LocationActivity) access.activity).refreshList();
            }
        }
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

    public void writeInFile(String response, AccessItem accessItem) {
        if (accessItem.filename != null && !accessItem.filename.isEmpty()) {
            FileManager.write(context, accessItem.filename, response);
        }
    }
}
