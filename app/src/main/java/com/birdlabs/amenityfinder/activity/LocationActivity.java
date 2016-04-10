package com.birdlabs.amenityfinder.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.birdlabs.amenityfinder.R;
import com.birdlabs.amenityfinder.items.CommentItem;
import com.birdlabs.amenityfinder.items.LocationItem;
import com.birdlabs.amenityfinder.items.PhotoItem;
import com.birdlabs.amenityfinder.server.Access;
import com.birdlabs.amenityfinder.server.AccessIds;
import com.birdlabs.amenityfinder.server.Links;
import com.birdlabs.amenityfinder.util.Functions;
import com.birdlabs.amenityfinder.views.CommentSummaryView;
import com.github.bijoysingh.starter.item.TimestampItem;
import com.github.bijoysingh.starter.server.AccessItem;
import com.github.bijoysingh.starter.util.ImageLoaderManager;
import com.github.bijoysingh.starter.util.LocaleManager;
import com.github.bijoysingh.starter.util.ResourceManager;
import com.github.bijoysingh.starter.util.TimestampManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.HashMap;

/**
 * Activity displaying the locations
 * Created by bijoy on 1/2/16.
 */
public class LocationActivity extends LocationViewActivityBase {

    View reviews;
    View addPhoto;
    View photoImage;
    View addPostButton;
    View editLocation;
    TextView tooMuchFlags;
    View getDirections;
    View flagInappropriate;
    ImageLoader imageLoader;
    ImageView locationImage;
    LinearLayout commentLayout;
    FloatingActionButton addPost;
    ProgressBar[] progressBars = new ProgressBar[5];
    TextView[] counts = new TextView[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_layout);

        flagInappropriate = findViewById(R.id.inappropriate);
        imageLoader = ImageLoaderManager.getImageLoader(context);
        locationItem = (LocationItem) getIntent().getSerializableExtra(LOCATION_ITEM);
        locationImage = (ImageView) findViewById(R.id.location_image);
        photoImage = findViewById(R.id.photo_image);

        setupFabs();
        setupPanel();
        setupCommentsPanel();
        setupRatings();
        setupAuthor();
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

        name.setText(locationItem.title);
        gender.setImageResource(locationItem.getGenderDrawable());
        score.setText(Functions.setPrecision(locationItem.rating, 1));
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
                access.send(new AccessItem(Links.flagLocation(locationItem.id),
                    null, AccessIds.LOCATION_FLAG, true), new HashMap<String, Object>());
            }
        });
        tooMuchFlags = (TextView) findViewById(R.id.too_much_flags);
        if (locationItem.status == 0) {
            tooMuchFlags.setText(R.string.is_verified);
        } else if (locationItem.status == 1) {
            tooMuchFlags.setText(R.string.rate_to_verify);
        } else {
            tooMuchFlags.setVisibility(View.GONE);
        }
    }

    public void setupAuthor() {
        TextView authorName = (TextView) findViewById(R.id.item_author_name);
        TextView authorTimestamp = (TextView) findViewById(R.id.item_timestamp);
        ImageView authorPicture = (ImageView) findViewById(R.id.item_author_picture);

        authorName.setText(locationItem.author.name);
        TimestampItem item = TimestampManager.getTimestampItem(locationItem.timestamp);

        if (locationItem.author.id != null && locationItem.isAnonymous) {
            String timeString = item.getTimeString(true) + " (Posted Anonymously)";
            authorTimestamp.setText(timeString);
        } else {
            authorTimestamp.setText(item.getTimeString(true));
        }



        if (locationItem.author.picture != null && !locationItem.author.picture.isEmpty()) {
            ImageAware authorImageAware = new ImageViewAware(authorPicture, false);
            imageLoader.displayImage(locationItem.author.picture, authorImageAware);
        }
    }

    public void setupRatings() {
        progressBars[0] = (ProgressBar) findViewById(R.id.progress_1);
        progressBars[1] = (ProgressBar) findViewById(R.id.progress_2);
        progressBars[2] = (ProgressBar) findViewById(R.id.progress_3);
        progressBars[3] = (ProgressBar) findViewById(R.id.progress_4);
        progressBars[4] = (ProgressBar) findViewById(R.id.progress_5);

        counts[0] = (TextView) findViewById(R.id.counts_1);
        counts[1] = (TextView) findViewById(R.id.counts_2);
        counts[2] = (TextView) findViewById(R.id.counts_3);
        counts[3] = (TextView) findViewById(R.id.counts_4);
        counts[4] = (TextView) findViewById(R.id.counts_5);
    }

    public void setupFabs() {
        addPost = (FloatingActionButton) findViewById(R.id.add_post);
        addPost.setBackgroundTintList(ColorStateList.valueOf(ResourceManager.getColor(getApplicationContext(), R.color.accent)));
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (preferences.isLoggedIn()) {
                    Intent intent = new Intent(getApplicationContext(), AddPostActivity.class);
                    intent.putExtra(AddPostActivity.LOCATION_ITEM, locationItem);
                    startActivity(intent);
                } else {
                    requestLogin();
                }
            }
        });

        addPostButton = findViewById(R.id.add_post_button);
        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddPostActivity.class);
                intent.putExtra(AddPostActivity.LOCATION_ITEM, locationItem);
                startActivity(intent);
            }
        });

        getDirections = findViewById(R.id.get_directions);
        getDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=" + locationItem.latitude + "," + locationItem.longitude));
                startActivity(intent);
            }
        });

        addPhoto = findViewById(R.id.add_photo);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddPhotoActivity.class);
                intent.putExtra(AddPostActivity.LOCATION_ITEM, locationItem);
                startActivity(intent);
            }
        });

        editLocation = findViewById(R.id.edit_location);
        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddLocationActivity.class);
                intent.putExtra(AddLocationActivity.LOCATION_ITEM, locationItem);
                startActivity(intent);
            }
        });

        if (!preferences.isLoggedIn()) {
            addPostButton.setVisibility(View.GONE);
            editLocation.setVisibility(View.GONE);
            addPhoto.setVisibility(View.GONE);
        }

        if (locationItem.author.id == null) {
            editLocation.setVisibility(View.GONE);
        }
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

        if (photos != null && photos.size() != 0) {
            PhotoItem item = photos.get(0);
            photoImage.setVisibility(View.VISIBLE);
            ImageAware logoImageAware = new ImageViewAware(locationImage, false);
            imageLoader.displayImage(item.getLink(), logoImageAware);
        }
    }

    @Override
    public void addStars() {
        for (int star = 0; star < 5; star++) {
            long progress = Math.round(100.0 * stars[star] / (1.0 * stars[5]));
            progressBars[star].setProgress((int) progress);
            counts[star].setText(LocaleManager.toString(stars[star]));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestData();
    }
}
