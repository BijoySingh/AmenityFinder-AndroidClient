package com.birdlabs.amentityfinder.server;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.birdlabs.amentityfinder.activity.AddLocationActivity;
import com.birdlabs.amentityfinder.activity.AddPhotoActivity;
import com.birdlabs.amentityfinder.activity.AddPostActivity;
import com.birdlabs.amentityfinder.activity.CommentsActivity;
import com.birdlabs.amentityfinder.activity.GalleryActivity;
import com.birdlabs.amentityfinder.activity.LocationActivity;
import com.birdlabs.amentityfinder.activity.LocationViewActivityBase;
import com.birdlabs.amentityfinder.activity.LoginActivity;
import com.birdlabs.amentityfinder.activity.MapActivity;
import com.birdlabs.amentityfinder.util.Preferences;
import com.birdlabs.basicproject.Functions;
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
        Preferences preferences = new Preferences(context);
        Map<String, String> map = new HashMap<>();
        map.put("token-auth", preferences.load(Preferences.Keys.AUTH_TOKEN));
        Log.d("Access", map.toString());
        return map;
    }

    @Override
    public void handleGetResponse(AccessItem access, String response) {
        Log.d(Access.class.getSimpleName(), "handleGetResponse called");
        writeInFile(response, access);

        if (access.activity != null) {
            if (access.type.equals(AccessInfo.AccessIds.LOCATION_GET_POSTS)) {
                ((LocationViewActivityBase) access.activity).refreshList();
            } else if (access.type.equals(AccessInfo.AccessIds.LOCATION_GET_USER_POST)) {
                ((AddPostActivity) access.activity).handleResponse(response);
            } else if (access.type.equals(AccessInfo.AccessIds.PHOTO_GET)) {
                ((LocationViewActivityBase) access.activity).refreshList();
            }
        }
    }

    @Override
    public void handleSendResponse(AccessItem access, JSONObject jsonObject) {
        Log.d(Access.class.getSimpleName(), "handleSendResponse called");
        writeInFile(jsonObject.toString(), access);

        if (access.activity != null) {
            if (access.type.equals(AccessInfo.AccessIds.LOCATION_GET)) {
                Log.d(Access.class.getSimpleName(), "type is location_get");
                ((MapActivity) access.activity).refreshMap();
            } else if (access.type.equals(AccessInfo.AccessIds.FB_LOGIN)) {
                ((LoginActivity) access.activity).handleResponse(jsonObject);
            } else if (access.type.equals(AccessInfo.AccessIds.LOCATION_POST)) {
                ((AddLocationActivity) access.activity).handleResponse(jsonObject);
            } else if (access.type.equals(AccessInfo.AccessIds.POST_POST)) {
                ((AddPostActivity) access.activity).handleResponse();
            } else if (access.type.equals(AccessInfo.AccessIds.POST_UPVOTE)) {
                ((CommentsActivity) access.activity).updateItem(jsonObject);
            } else if (access.type.equals(AccessInfo.AccessIds.POST_DOWNVOTE)) {
                ((CommentsActivity) access.activity).updateItem(jsonObject);
            } else if (access.type.equals(AccessInfo.AccessIds.PHOTO_POST)) {
                ((AddPhotoActivity) access.activity).handleResponse();
            } else if (access.type.equals(AccessInfo.AccessIds.PHOTO_DELETE)) {
                ((GalleryActivity) access.activity).handleDelete(jsonObject);
            }
        }
    }

    @Override
    public void handleGetError(AccessItem access, VolleyError volleyError) {
        try {
            if (access.activity != null) {
                if (access.type.equals(AccessInfo.AccessIds.LOCATION_GET_USER_POST)) {
                    ((AddPostActivity) access.activity).handleErrorMessage();
                }
            }

            Log.e(Access.class.getSimpleName(), "Error: " + access.url);
            String message = new String(volleyError.networkResponse.data);
            Log.e(Access.class.getSimpleName(), "SERVER_ERROR <" + message + ">");
        } catch (Exception exception) {
            Log.e(Access.class.getSimpleName(), exception.getMessage(), exception);
        }
    }

    @Override
    public void handleSendError(AccessItem access, VolleyError volleyError) {
        try {
            Log.e(Access.class.getSimpleName(), "Error: " + access.url);

            if (access.activity != null) {
                if (access.type.equals(AccessInfo.AccessIds.LOCATION_POST)) {
                    ((AddLocationActivity) access.activity).handleErrorMessage();
                } else if (access.type.equals(AccessInfo.AccessIds.LOCATION_POST)) {
                    ((AddPostActivity) access.activity).handleErrorMessage();
                } else if (access.type.equals(AccessInfo.AccessIds.POST_UPVOTE)) {
                    Functions.makeToast(context, "Error Upvoting!");
                } else if (access.type.equals(AccessInfo.AccessIds.POST_DOWNVOTE)) {
                    Functions.makeToast(context, "Error Downvoting!");
                } else if (access.type.equals(AccessInfo.AccessIds.PHOTO_POST)) {
                    ((AddPhotoActivity) access.activity).handleErrorResponse();
                }
            }

            String message = new String(volleyError.networkResponse.data);
            Log.e(Access.class.getSimpleName(), "SERVER_ERROR <" + message + ">");
        } catch (Exception exception) {
            Log.e(Access.class.getSimpleName(), exception.getMessage(), exception);
        }
    }

    public void writeInFile(String response, AccessItem accessItem) {
        if (accessItem.filename != null && !accessItem.filename.isEmpty()) {
            FileManager.write(context, accessItem.filename, response);
        }
    }
}
