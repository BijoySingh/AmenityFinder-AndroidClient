package com.birdlabs.amentityfinder.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdlabs.amentityfinder.R;
import com.birdlabs.amentityfinder.adapter.CommentsAdapter;
import com.birdlabs.amentityfinder.items.CommentItem;
import com.birdlabs.amentityfinder.items.LocationItem;
import com.birdlabs.amentityfinder.server.Access;
import com.birdlabs.amentityfinder.server.AccessInfo;
import com.birdlabs.amentityfinder.server.Filenames;
import com.birdlabs.amentityfinder.server.Links;
import com.birdlabs.basicproject.server.AccessItem;
import com.birdlabs.basicproject.util.FileManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * actity displaying the locations
 * Created by bijoy on 1/2/16.
 */
public class LocationActivity extends AppCompatActivity {

    public static final String LOCATION_ITEM = "LOCATION_ITEM";

    Context context;
    LocationItem locationItem;
    FloatingActionButton addPost;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    CommentsAdapter adapter;
    List<CommentItem> comments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_layout);
        context = this;

        locationItem = (LocationItem) getIntent().getSerializableExtra(LOCATION_ITEM);

        refreshList();
        setupFabs();
        setupPanel();
        setupRecyclerView();
    }

    public void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CommentsAdapter(this, this);
        recyclerView.setAdapter(adapter);
    }

    public void setupPanel() {
        TextView name = (TextView) findViewById(R.id.location);
        TextView location = (TextView) findViewById(R.id.latitude_longitude);
        ImageView gender = (ImageView) findViewById(R.id.gender);
        TextView score = (TextView) findViewById(R.id.score);

        name.setText(locationItem.name);
        gender.setImageResource(locationItem.getGenderDrawable());
        score.setText(locationItem.rating.toString());
        location.setText(locationItem.getLocation());
    }

    public void refreshList() {
        comments = new ArrayList<>();
        try {
            String response = FileManager.read(context, Filenames.getLocationPosts(locationItem.id));
            JSONArray array = new JSONArray(response);
            for (int position = 0; position < array.length(); position++) {
                JSONObject json = array.getJSONObject(position);
                CommentItem commentItem = new CommentItem(json);
                comments.add(commentItem);
            }
        } catch (JSONException json) {
            Log.e(LocationActivity.class.getSimpleName(), json.getMessage(), json);
        }

        adapter.notifyDataSetChanged();
    }

    public void requestData() {
        Access access = new Access(context);
        access.get(new AccessItem(Links.getLocationPosts(locationItem.id),
                Filenames.getLocationPosts(locationItem.id),
                AccessInfo.AccessIds.LOCATION_GET_POSTS,
                false));
    }

    public List<CommentItem> getValues() {
        if (comments == null) {
            refreshList();
        }
        return comments;
    }

    public void setupFabs() {
        addPost = (FloatingActionButton) findViewById(R.id.add_post);
        addPost.setBackgroundTintList(ColorStateList.valueOf(getApplicationContext().getResources().getColor(R.color.accent)));
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddPostActivity.class);
                intent.putExtra(AddPostActivity.LOCATION_ITEM, locationItem);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestData();
    }
}
