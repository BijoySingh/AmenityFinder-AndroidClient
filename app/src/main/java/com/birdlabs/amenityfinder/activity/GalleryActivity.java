package com.birdlabs.amenityfinder.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.birdlabs.amenityfinder.R;
import com.birdlabs.amenityfinder.adapter.PhotosAdapter;
import com.birdlabs.amenityfinder.items.CommentItem;
import com.birdlabs.amenityfinder.items.LocationItem;
import com.birdlabs.amenityfinder.items.PhotoItem;
import com.github.bijoysingh.starter.Functions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * actity displaying the locations
 * Created by bijoy on 1/2/16.
 */
public class GalleryActivity extends LocationViewActivityBase {

    PhotosAdapter adapter;
    RecyclerView recyclerView;
    FloatingActionButton addPost;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_reviews);
        context = this;

        locationItem = (LocationItem) getIntent().getSerializableExtra(LOCATION_ITEM);

        setupFabs();
        setupRecyclerView();
        setupPanel();

        refreshList();
    }

    public void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PhotosAdapter(this, this);
        recyclerView.setAdapter(adapter);
    }

    public void setupPanel() {
        TextView name = (TextView) findViewById(R.id.location);
        ImageView gender = (ImageView) findViewById(R.id.gender);
        TextView score = (TextView) findViewById(R.id.score);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        name.setText(locationItem.title);
        gender.setImageResource(locationItem.getGenderDrawable());
        score.setText(com.birdlabs.amenityfinder.util.Functions.setPrecision(locationItem.rating, 1));
        ratingBar.setRating(locationItem.rating.floatValue());
    }

    public void handleDelete(JSONObject json) {
        try {
            Integer deletedImage = json.getInt("id");
            Integer position = 0;
            for (PhotoItem item : photos) {
                if (item.id.equals(deletedImage)) {
                    break;
                }
                position++;
            }

            if (position < photos.size()) {
                photos.remove(position.intValue());
                adapter.notifyItemRemoved(position);
            }

        } catch (JSONException exception) {
            Functions.makeToast(context, "Something went wrong!");
        }
    }

    @Override
    public void addItems() {
        adapter.notifyDataSetChanged();
    }

    public List<PhotoItem> getValues() {
        if (photos == null) {
            refreshList();
        }
        return photos;
    }

    public void updateItem(JSONObject json) {
        try {
            PhotoItem item = new PhotoItem(json.getJSONObject("result"));
            Integer position = 0;
            for (PhotoItem photo : photos) {
                if (photo.id.equals(item.id)) {
                    photos.set(position, item);
                    adapter.notifyItemChanged(position);
                    return;
                }
                position++;
            }

            photos.add(item);
            adapter.notifyItemInserted(position);
        } catch (Exception exception) {
            Log.d(LocationActivity.class.getSimpleName(), json.toString());
            Log.e(LocationActivity.class.getSimpleName(), exception.getMessage(), exception);
        }
    }

    public void setupFabs() {
        addPost = (FloatingActionButton) findViewById(R.id.add_post);
        addPost.setBackgroundTintList(ColorStateList.valueOf(getApplicationContext().getResources().getColor(R.color.accent)));
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (preferences.isLoggedIn()) {
                    Intent intent = new Intent(getApplicationContext(), AddPhotoActivity.class);
                    intent.putExtra(AddPostActivity.LOCATION_ITEM, locationItem);
                    startActivity(intent);
                } else {
                    requestLogin();
                }
            }
        });
    }

}
