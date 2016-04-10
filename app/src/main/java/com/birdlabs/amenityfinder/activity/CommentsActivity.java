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
import com.birdlabs.amenityfinder.adapter.CommentsAdapter;
import com.birdlabs.amenityfinder.items.CommentItem;
import com.birdlabs.amenityfinder.items.LocationItem;
import com.birdlabs.amenityfinder.util.Functions;
import com.github.bijoysingh.starter.util.ResourceManager;

import org.json.JSONObject;

import java.util.List;

/**
 * Activity displaying the locations
 * Created by bijoy on 1/2/16.
 */
public class CommentsActivity extends LocationViewActivityBase {

    CommentsAdapter adapter;
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

        adapter = new CommentsAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    public void setupPanel() {
        TextView name = (TextView) findViewById(R.id.location);
        ImageView gender = (ImageView) findViewById(R.id.gender);
        TextView score = (TextView) findViewById(R.id.score);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        name.setText(locationItem.title);
        gender.setImageResource(locationItem.getGenderDrawable());
        score.setText(Functions.setPrecision(locationItem.rating, 1));
        ratingBar.setRating(locationItem.rating.floatValue());
    }

    @Override
    public void addItems() {
        adapter.notifyDataSetChanged();
    }

    public List<CommentItem> getValues() {
        if (comments == null) {
            refreshList();
        }
        return comments;
    }

    public void setupFabs() {
        addPost = (FloatingActionButton) findViewById(R.id.add_post);
        addPost.setBackgroundTintList(ColorStateList.valueOf(
            ResourceManager.getColor(context, R.color.accent)));
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddPostActivity.class);
                intent.putExtra(AddPostActivity.LOCATION_ITEM, locationItem);
                startActivity(intent);
            }
        });
    }

    public void updateItem(JSONObject json) {
        try {
            CommentItem item = new CommentItem(json.getJSONObject("result"));
            Integer position = 0;
            for (CommentItem comment : comments) {
                if (comment.id.equals(item.id)) {
                    comments.set(position, item);
                    adapter.notifyItemChanged(position);
                    return;
                }
                position++;
            }

            comments.add(item);
            adapter.notifyItemInserted(position);
        } catch (Exception exception) {
            Log.d(LocationActivity.class.getSimpleName(), json.toString());
            Log.e(LocationActivity.class.getSimpleName(), exception.getMessage(), exception);
        }
    }
}
