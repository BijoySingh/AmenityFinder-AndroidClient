package com.birdlabs.amentityfinder.server;

import android.content.Context;
import android.util.Log;

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
    public void handleGetResponse(AccessItem access, String response) {
        Log.d(Access.class.getSimpleName(), "handleGetResponse called");
        writeInFile(response, access);

        if (access.activity != null) {
            if (access.type.equals(AccessInfo.AccessIds.LOCATION_GET_POSTS)) {
                ((LocationActivity) access.activity).refreshList();
            }
        }
    }

    @Override
    public void handleSendResponse(AccessItem access, JSONObject jsonObject) {
        Log.d(Access.class.getSimpleName(), "handleSendResponse called");
        writeInFile(jsonObject.toString(), access);

        if (access.activity != null) {
            Log.d(Access.class.getSimpleName(), "activity is not null");
            if (access.type.equals(AccessInfo.AccessIds.LOCATION_GET)) {
                Log.d(Access.class.getSimpleName(), "type is location_get");
                ((MapActivity) access.activity).refreshMap();
            }
        }

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
