package com.birdlabs.amenityfinder.activity;

import android.util.Log;

import com.birdlabs.amenityfinder.items.CommentItem;
import com.birdlabs.amenityfinder.items.LocationItem;
import com.birdlabs.amenityfinder.items.PhotoItem;
import com.birdlabs.amenityfinder.server.Access;
import com.birdlabs.amenityfinder.server.AccessIds;
import com.birdlabs.amenityfinder.server.Filenames;
import com.birdlabs.amenityfinder.server.Links;
import com.facebook.appevents.AppEventsLogger;
import com.github.bijoysingh.starter.server.AccessItem;
import com.github.bijoysingh.starter.util.FileManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Location view activity's common functions.
 * Created by bijoy on 1/6/16.
 */
public class LocationViewActivityBase extends ActivityBase {

    public static final String LOCATION_ITEM = "LOCATION_ITEM";

    public LocationItem locationItem;
    public List<PhotoItem> photos = new ArrayList<>();
    public List<CommentItem> comments = new ArrayList<>();
    public Integer[] stars = new Integer[6];

    public void refreshComments() {
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
        } catch (Exception json) {
            Log.e(LocationActivity.class.getSimpleName(), json.getMessage(), json);
        }
        addItems();
    }

    public void refreshPhotos() {
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
        } catch (Exception json) {
            Log.e(LocationActivity.class.getSimpleName(), json.getMessage(), json);
        }
        addImages();
    }

    public void refreshStars() {
        stars = new Integer[]{0, 0, 0, 0, 0, 1};
        try {
            String response = FileManager.read(context, Filenames.getLocationStars(locationItem.id));
            Log.d("Response Stars", response);
            JSONObject starsJson = new JSONObject(response).getJSONObject("results");
            stars[0] = starsJson.getInt("1");
            stars[1] = starsJson.getInt("2");
            stars[2] = starsJson.getInt("3");
            stars[3] = starsJson.getInt("4");
            stars[4] = starsJson.getInt("5");
            stars[5] = stars[0] + stars[1] + stars[2] + stars[3] + stars[4];
        } catch (Exception json) {
            Log.e(LocationActivity.class.getSimpleName(), json.getMessage(), json);
        }
        addStars();
    }

    public void refreshList() {
        refreshComments();
        refreshPhotos();
        refreshStars();
    }

    public void addItems() {

    }

    public void addImages() {

    }

    public void addStars() {

    }

    public void requestData() {
        Access access = new Access(context);
        access.get(new AccessItem(Links.getLocationPosts(locationItem.id),
            Filenames.getLocationPosts(locationItem.id),
            AccessIds.LOCATION_GET_POSTS,
            true).setActivity(this));

        access.get(new AccessItem(Links.getLocationPhotos(locationItem.id),
            Filenames.getLocationPhotos(locationItem.id),
            AccessIds.PHOTO_GET,
            true).setActivity(this));

        access.get(new AccessItem(Links.getLocationStars(locationItem.id),
            Filenames.getLocationStars(locationItem.id),
            AccessIds.STAR_GET,
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
