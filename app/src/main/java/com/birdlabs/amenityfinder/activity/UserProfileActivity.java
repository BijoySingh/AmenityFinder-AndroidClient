package com.birdlabs.amenityfinder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdlabs.amenityfinder.R;
import com.birdlabs.amenityfinder.items.ProfileItem;
import com.birdlabs.amenityfinder.server.Access;
import com.birdlabs.amenityfinder.server.AccessIds;
import com.birdlabs.amenityfinder.server.Filenames;
import com.birdlabs.amenityfinder.server.Links;
import com.github.bijoysingh.starter.server.AccessItem;
import com.github.bijoysingh.starter.util.FileManager;
import com.github.bijoysingh.starter.util.ImageLoaderManager;
import com.github.bijoysingh.starter.util.LocaleManager;

import org.json.JSONObject;

/**
 * The user activity
 * Created by bijoy on 3/5/16.
 */
public class UserProfileActivity extends ActivityBase {
    TextView name;
    TextView level;
    ImageView levelImage;
    ImageView profilePicture;
    ScoreItem locations;
    ScoreItem posts;
    ScoreItem photos;
    ScoreItem ratings;
    ScoreItem banned;
    ScoreItem reputation;
    ProfileItem profileItem;
    ImageLoaderManager loader;
    TextView viewActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_layout);

        loader = new ImageLoaderManager(context);
        logoutSetup();
        setupViews();
        refreshItem();
        requestItem();
    }

    public void setupViews() {
        name = (TextView) findViewById(R.id.name);
        level = (TextView) findViewById(R.id.level);
        profilePicture = (ImageView) findViewById(R.id.profile_picture);
        locations = new ScoreItem(R.id.locations);
        posts = new ScoreItem(R.id.posts);
        photos = new ScoreItem(R.id.photos);
        ratings = new ScoreItem(R.id.ratings);
        reputation = new ScoreItem(R.id.reputation);
        banned = new ScoreItem(R.id.banned);
        levelImage = (ImageView) findViewById(R.id.level_image);
        viewActivity = (TextView) findViewById(R.id.view_activity);

        viewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(context, UserActivity.class);
                startActivity(activity);
            }
        });
    }

    public void loadProfileItem() {
        String profile = "";
        try {
            profile = FileManager.read(context, Filenames.getProfile());
            profileItem = new ProfileItem(new JSONObject(profile).getJSONObject("result"));
        } catch (Exception e) {
            Log.d(UserProfileActivity.class.getSimpleName(), profile);
            Log.e(UserProfileActivity.class.getSimpleName(), e.getMessage(), e);
        }
    }

    public void refreshItem() {
        loadProfileItem();
        if (profileItem == null) {
            return;
        }

        name.setText(profileItem.getName());
        level.setText(profileItem.levelTitle);
        locations.set(R.string.locations_added, profileItem.items);
        posts.set(R.string.posts_added, profileItem.comments);
        photos.set(R.string.photos_added, profileItem.photos);
        ratings.set(R.string.ratings_added, profileItem.ratings);
        reputation.set(R.string.reputation, profileItem.reputation);
        if (profileItem.level == 0) {
            banned.set(R.string.banned, getString(R.string.yes));
            banned.view.setVisibility(View.VISIBLE);
        } else {
            banned.view.setVisibility(View.GONE);
        }

        if (profileItem.level == 0) {
            levelImage.setImageResource(R.drawable.ic_do_not_disturb_white_36dp);
        } else if (profileItem.level == 1) {
            levelImage.setImageResource(R.drawable.ic_error_white_36dp);
        } else if (profileItem.level == 2) {
            levelImage.setImageResource(R.drawable.ic_airline_seat_recline_normal_white_36dp);
        } else if (profileItem.level == 3) {
            levelImage.setImageResource(R.drawable.ic_verified_user_white_36dp);
        } else if (profileItem.level == 4) {
            levelImage.setImageResource(R.drawable.ic_whatshot_white_36dp);
        }

        loader.displayImage(profileItem.picture, profilePicture);
    }

    public void requestItem() {
        Access access = new Access(context);
        access.get(new AccessItem(
            Links.getProfile(),
            Filenames.getProfile(),
            AccessIds.PROFILE_GET,
            true).setActivity(this));
    }

    public void logoutSetup() {
        View logout = findViewById(R.id.logout);
        logout.setVisibility(View.GONE);
        View logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
    }

    public class ScoreItem {
        public View view;
        public TextView title;
        public TextView score;

        public ScoreItem(Integer id) {
            view = findViewById(id);
            title = (TextView) view.findViewById(R.id.title);
            score = (TextView) view.findViewById(R.id.score);
        }

        public void set(String title, String score) {
            this.title.setText(title);
            this.score.setText(score);
        }

        public void set(Integer title, String score) {
            set(getString(title), score);
        }

        public void set(Integer title, Integer score) {
            set(getString(title), LocaleManager.toString(score));
        }
    }
}
