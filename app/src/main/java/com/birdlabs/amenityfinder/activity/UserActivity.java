package com.birdlabs.amenityfinder.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.birdlabs.amenityfinder.R;
import com.birdlabs.amenityfinder.adapter.ActivityAdapter;
import com.birdlabs.amenityfinder.server.Access;
import com.birdlabs.amenityfinder.server.AccessIds;
import com.birdlabs.amenityfinder.server.Filenames;
import com.birdlabs.amenityfinder.server.Links;
import com.birdlabs.amenityfinder.server.UserActivityItem;
import com.github.bijoysingh.starter.server.AccessItem;
import com.github.bijoysingh.starter.util.FileManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    ActivityAdapter adapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    List<UserActivityItem> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setupRecyclerView();
        logoutSetup();
        refreshList();
        requestItem();
    }

    public void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ActivityAdapter(this, this);
        recyclerView.setAdapter(adapter);
    }

    public void refreshList() {
        values = new ArrayList<>();
        try {
            String response = FileManager.read(this, Filenames.getUserActivity());

            JSONObject details = new JSONObject(response).getJSONObject("result");
            JSONArray items = details.getJSONArray("items");
            for (int index = 0; index < items.length(); index++) {
                UserActivityItem item = new UserActivityItem(items.getJSONObject(index), UserActivityItem.ITEM);
                values.add(item);
            }
            JSONArray photos = details.getJSONArray("photos");
            for (int index = 0; index < photos.length(); index++) {
                UserActivityItem item = new UserActivityItem(photos.getJSONObject(index), UserActivityItem.PHOTO);
                values.add(item);
            }
            JSONArray comments = details.getJSONArray("comments");
            for (int index = 0; index < comments.length(); index++) {
                UserActivityItem item = new UserActivityItem(comments.getJSONObject(index), UserActivityItem.COMMENT);
                values.add(item);
            }
            JSONArray ratings = details.getJSONArray("ratings");
            for (int index = 0; index < ratings.length(); index++) {
                UserActivityItem item = new UserActivityItem(ratings.getJSONObject(index), UserActivityItem.RATING);
                values.add(item);
            }
        } catch (Exception json) {
            Log.e(UserActivity.class.getSimpleName(), json.getMessage(), json);
        }

        adapter.notifyDataSetChanged();
    }

    public List<UserActivityItem> getValues() {
        if (values == null) {
            refreshList();
        }
        return values;
    }

    public void logoutSetup() {
        View logout = findViewById(R.id.logout);
        logout.setVisibility(View.GONE);
    }

    public void requestItem() {
        Access access = new Access(this);
        access.get(new AccessItem(
            Links.getActivity(),
            Filenames.getUserActivity(),
            AccessIds.PROFILE_ACTIVITY_GET,
            true).setActivity(this));
    }
}
