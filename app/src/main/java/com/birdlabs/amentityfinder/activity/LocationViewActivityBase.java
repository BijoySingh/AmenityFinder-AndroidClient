package com.birdlabs.amentityfinder.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.birdlabs.amentityfinder.items.CommentItem;
import com.birdlabs.amentityfinder.items.LocationItem;
import com.birdlabs.amentityfinder.items.PhotoItem;
import com.birdlabs.amentityfinder.server.Access;
import com.birdlabs.amentityfinder.server.AccessInfo;
import com.birdlabs.amentityfinder.server.Filenames;
import com.birdlabs.amentityfinder.server.Links;
import com.birdlabs.basicproject.util.FileManager;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bijoy on 1/6/16.
 */
public class LocationViewActivityBase extends AppCompatActivity {

    public LocationItem locationItem;
    public static final String LOCATION_ITEM = "LOCATION_ITEM";
    public List<CommentItem> comments = new ArrayList<>();
    public List<PhotoItem> photos = new ArrayList<>();
    public Context context;

    public void refreshList() {
        comments = new ArrayList<>();
        try {
            String response = FileManager.read(context, Filenames.getLocationPosts(locationItem.id));
            Log.d("Response Reviews", response);
            JSONObject page = new JSONObject(response);
            JSONArray array = page.getJSONArray("results");
            for (int position = 0; position < array.length(); position++) {
                JSONObject json = array.getJSONObject(position);
                CommentItem commentItem = new CommentItem(json);
                comments.add(commentItem);
            }
        } catch (JSONException json) {
            Log.e(LocationActivity.class.getSimpleName(), json.getMessage(), json);
        }

        photos = new ArrayList<>();
        try {
            String response = FileManager.read(context, Filenames.getLocationPhotos(locationItem.id));
            Log.d("Response Gallery", response);
            JSONObject page = new JSONObject(response);
            JSONArray array = page.getJSONArray("results");
            for (int position = 0; position < array.length(); position++) {
                JSONObject json = array.getJSONObject(position);
                PhotoItem photoItem = new PhotoItem(json);
                photos.add(photoItem);
            }
        } catch (JSONException json) {
            Log.e(LocationActivity.class.getSimpleName(), json.getMessage(), json);
        }

        addItems();
        addImages();
    }

    public void addItems() {
        return;
    }

    public void addImages() {
        return;
    }

    public void requestData() {
        Access access = new Access(context);
        access.get(new AccessInfo(Links.getLocationPosts(locationItem.id),
                Filenames.getLocationPosts(locationItem.id),
                AccessInfo.AccessIds.LOCATION_GET_POSTS,
                true).setActivity(this));

        access.get(new AccessInfo(Links.getLocationPhotos(locationItem.id),
                Filenames.getLocationPhotos(locationItem.id),
                AccessInfo.AccessIds.PHOTO_GET,
                true).setActivity(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }
}
