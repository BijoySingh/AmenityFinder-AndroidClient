package com.birdlabs.amentityfinder.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.birdlabs.amentityfinder.R;
import com.birdlabs.amentityfinder.items.CommentItem;
import com.birdlabs.amentityfinder.items.LocationItem;
import com.birdlabs.amentityfinder.items.PhotoItem;
import com.birdlabs.amentityfinder.server.Access;
import com.birdlabs.amentityfinder.server.AccessInfo;
import com.birdlabs.amentityfinder.server.Links;
import com.birdlabs.amentityfinder.views.CommentSummaryView;
import com.birdlabs.basicproject.Functions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.HashMap;

/**
 * actity displaying the locations
 * Created by bijoy on 1/2/16.
 */
public class LocationActivity extends LocationViewActivityBase {

    FloatingActionButton addPost;
    FloatingActionButton addPhoto;
    FloatingActionButton editLocation;
    FloatingActionButton getDirections;
    LinearLayout commentLayout;
    ImageView locationImage;
    View photoImage;
    View reviews;
    ImageLoader imageLoader;
    View flagInappropriate;
    View tooMuchFlags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_layout);
        context = this;

        locationItem = (LocationItem) getIntent().getSerializableExtra(LOCATION_ITEM);
        locationImage = (ImageView) findViewById(R.id.location_image);
        photoImage = findViewById(R.id.photo_image);
        flagInappropriate = findViewById(R.id.inappropriate);
        imageLoader = Functions.getImageLoader(context);

        setupFabs();
        setupPanel();
        setupCommentsPanel();
        refreshList();
    }

    public void setupCommentsPanel() {
        reviews = findViewById(R.id.reviews);
        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentsActivity.class);
                intent.putExtra(CommentsActivity.LOCATION_ITEM, locationItem);
                startActivity(intent);
            }
        });

        commentLayout = (LinearLayout) findViewById(R.id.comments);
    }

    public void setupPanel() {
        TextView name = (TextView) findViewById(R.id.location);
        TextView location = (TextView) findViewById(R.id.latitude_longitude);
        ImageView gender = (ImageView) findViewById(R.id.gender);
        TextView score = (TextView) findViewById(R.id.score);
        TextView isFree = (TextView) findViewById(R.id.is_free);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        name.setText(locationItem.name);
        gender.setImageResource(locationItem.getGenderDrawable());
        score.setText(locationItem.rating.toString());
        location.setText(locationItem.getLocation());
        ratingBar.setRating(locationItem.rating.floatValue());
        photoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GalleryActivity.class);
                intent.putExtra(LocationViewActivityBase.LOCATION_ITEM, locationItem);
                startActivity(intent);
            }
        });
        isFree.setText(locationItem.isFree ? "FREE" : "PAID");

        flagInappropriate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Access access = new Access(context);
                access.send(new AccessInfo(Links.flagLocation(locationItem.id),
                        null, AccessInfo.AccessIds.LOCATION_FLAG, true)
                        , new HashMap<String, Object>());
            }
        });
        tooMuchFlags = findViewById(R.id.too_much_flags);
        if (locationItem.flags > 10) {
            tooMuchFlags.setVisibility(View.VISIBLE);
        }
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

        getDirections = (FloatingActionButton) findViewById(R.id.get_directions);
        getDirections.setBackgroundTintList(ColorStateList.valueOf(getApplicationContext().getResources().getColor(R.color.accent)));
        getDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + locationItem.latitude + "," + locationItem.longitude));
                startActivity(intent);
            }
        });

        addPhoto = (FloatingActionButton) findViewById(R.id.add_photo);
        addPhoto.setBackgroundTintList(ColorStateList.valueOf(getApplicationContext().getResources().getColor(R.color.accent)));
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddPhotoActivity.class);
                intent.putExtra(AddPostActivity.LOCATION_ITEM, locationItem);
                startActivity(intent);
            }
        });

        editLocation = (FloatingActionButton) findViewById(R.id.edit_location);
        editLocation.setBackgroundTintList(ColorStateList.valueOf(getApplicationContext().getResources().getColor(R.color.accent)));
        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddLocationActivity.class);
                intent.putExtra(AddLocationActivity.LOCATION_ITEM, locationItem);
                startActivity(intent);
            }
        });
    }

    @Override
    public void addItems() {
        commentLayout.removeAllViews();
        if (comments.isEmpty()) {
            CommentSummaryView commentView = new CommentSummaryView(context);
            commentView.setPlaceholder();
            commentLayout.addView(commentView);
            return;
        }

        for (CommentItem comment : comments) {
            CommentSummaryView commentView = new CommentSummaryView(context);
            commentView.set(comment);
            commentLayout.addView(commentView);
        }
    }

    @Override
    public void addImages() {
        if (photos.isEmpty()) {
            photoImage.setVisibility(View.GONE);
        }

        for (PhotoItem item : photos) {
            ImageAware logoImageAware = new ImageViewAware(locationImage, false);
            imageLoader.displayImage(item.photo, logoImageAware);
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestData();
    }
}
