package com.birdlabs.amenityfinder.server;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.birdlabs.amenityfinder.activity.AddLocationActivity;
import com.birdlabs.amenityfinder.activity.AddPhotoActivity;
import com.birdlabs.amenityfinder.activity.AddPostActivity;
import com.birdlabs.amenityfinder.activity.CommentsActivity;
import com.birdlabs.amenityfinder.activity.GalleryActivity;
import com.birdlabs.amenityfinder.activity.LocationActivity;
import com.birdlabs.amenityfinder.activity.LocationViewActivityBase;
import com.birdlabs.amenityfinder.activity.LoginActivity;
import com.birdlabs.amenityfinder.activity.MapActivity;
import com.birdlabs.amenityfinder.activity.UserActivity;
import com.birdlabs.amenityfinder.activity.UserProfileActivity;
import com.birdlabs.amenityfinder.util.Preferences;
import com.github.bijoysingh.starter.Functions;
import com.github.bijoysingh.starter.server.AccessItem;
import com.github.bijoysingh.starter.server.AccessManager;
import com.github.bijoysingh.starter.util.FileManager;

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
            if (access.type.equals(AccessIds.LOCATION_GET_POSTS)) {
                ((LocationViewActivityBase) access.activity).refreshComments();
            } else if (access.type.equals(AccessIds.LOCATION_GET_USER_POST)) {
                ((AddPostActivity) access.activity).handleResponse(response);
            } else if (access.type.equals(AccessIds.PHOTO_GET)) {
                ((LocationViewActivityBase) access.activity).refreshPhotos();
            } else if (access.type.equals(AccessIds.STAR_GET)) {
                ((LocationViewActivityBase) access.activity).refreshStars();
            } else if (access.type.equals(AccessIds.PROFILE_GET)) {
                ((UserProfileActivity) access.activity).refreshItem();
            } else if (access.type.equals(AccessIds.PROFILE_ACTIVITY_GET)) {
                ((UserActivity) access.activity).refreshList();
            }
        }
    }

    @Override
    public void handleSendResponse(AccessItem access, JSONObject jsonObject) {
        Log.d(Access.class.getSimpleName(), "handleSendResponse called");
        writeInFile(jsonObject.toString(), access);

        if (access.activity != null) {
            if (access.type.equals(AccessIds.LOCATION_GET)) {
                Log.d(Access.class.getSimpleName(), "type is location_get");
                ((MapActivity) access.activity).refreshMap();
            } else if (access.type.equals(AccessIds.FB_LOGIN)) {
                ((LoginActivity) access.activity).handleResponse(jsonObject);
            } else if (access.type.equals(AccessIds.LOCATION_POST)) {
                ((AddLocationActivity) access.activity).handleResponse(jsonObject);
            } else if (access.type.equals(AccessIds.POST_POST)) {
                ((AddPostActivity) access.activity).handleResponse();
            } else if (access.type.equals(AccessIds.POST_UPVOTE)
                || access.type.equals(AccessIds.POST_DOWNVOTE)
                || access.type.equals(AccessIds.POST_FLAG)) {
                ((CommentsActivity) access.activity).updateItem(jsonObject);
            } else if (access.type.equals(AccessIds.PHOTO_UPVOTE)
                || access.type.equals(AccessIds.PHOTO_DOWNVOTE)
                || access.type.equals(AccessIds.PHOTO_FLAG)) {
                ((GalleryActivity) access.activity).updateItem(jsonObject);
            } else if (access.type.equals(AccessIds.PHOTO_DELETE)) {
                ((GalleryActivity) access.activity).handleDelete(jsonObject);
            }
        } else if (access.type.equals(AccessIds.LOCATION_FLAG)) {
            Functions.makeToast(context, "Flagged as Inappropriate");
        }
    }

    @Override
    public void handleGetError(AccessItem access, VolleyError volleyError) {
        try {
            if (access.activity != null) {
                if (access.type.equals(AccessIds.LOCATION_GET_USER_POST)) {
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
                if (access.type.equals(AccessIds.LOCATION_POST)) {
                    ((AddLocationActivity) access.activity).handleErrorMessage();
                } else if (access.type.equals(AccessIds.LOCATION_POST)) {
                    ((AddPostActivity) access.activity).handleErrorMessage();
                } else if (access.type.equals(AccessIds.POST_UPVOTE)) {
                    Functions.makeToast(context, "Error Up Voting!");
                } else if (access.type.equals(AccessIds.POST_DOWNVOTE)) {
                    Functions.makeToast(context, "Error Down Voting!");
                } else if (access.type.equals(AccessIds.PHOTO_POST)) {
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
