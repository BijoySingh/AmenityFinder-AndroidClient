package com.birdlabs.amenityfinder.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.birdlabs.amenityfinder.R;
import com.birdlabs.amenityfinder.items.CommentItem;
import com.birdlabs.amenityfinder.items.LocationItem;
import com.birdlabs.amenityfinder.server.Access;
import com.birdlabs.amenityfinder.server.AccessIds;
import com.birdlabs.amenityfinder.server.Links;
import com.facebook.appevents.AppEventsLogger;
import com.github.bijoysingh.starter.Functions;
import com.github.bijoysingh.starter.server.AccessItem;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class AddPostActivity extends AppCompatActivity {

    public static final String LOCATION_ITEM = "LOCATION_ITEM";

    Context context;
    EditText comment;
    Switch anonymous;
    Boolean olderLoaded;
    RatingBar ratingBar;
    LocationItem locationItem;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_post);
        context = this;

        locationItem = (LocationItem) getIntent().getSerializableExtra(LOCATION_ITEM);

        EditText location = (EditText) findViewById(R.id.location);
        location.setText(locationItem.title);

        anonymous = (Switch) findViewById(R.id.anonymous);
        comment = (EditText) findViewById(R.id.comment);
        olderLoaded = false;

        setupRatingBar();
        setupCreateClick();
    }

    public void setupRatingBar() {
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setStepSize(1);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                sendRating();
            }
        });
    }

    public void setupCreateClick() {
        View.OnClickListener publishPost = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
                progressDialog = ProgressDialog.show(context,
                    "Adding Post", "Please Wait...", true);
                progressDialog.setCancelable(true);
            }
        };

        TextView publish = (TextView) findViewById(R.id.publish);
        TextView done = (TextView) findViewById(R.id.done);
        publish.setOnClickListener(publishPost);
        done.setOnClickListener(publishPost);
    }

    public void sendRating() {
        Map<String, Object> map = new HashMap<>();
        map.put("rating", ratingBar.getRating());
        map.put("is_anonymous", false);

        Access access = new Access(this);
        access.send(new AccessItem(Links.addRating(locationItem.id),
            null, AccessIds.POST_RATING, true)
            .setActivity(this), map);
    }

    public void sendData() {
        Map<String, Object> map = new HashMap<>();
        map.put("description", comment.getText().toString());
        map.put("is_anonymous", anonymous.isChecked());

        Access access = new Access(this);
        access.send(new AccessItem(Links.addComment(locationItem.id),
            null, AccessIds.POST_POST, true)
            .setActivity(this), map);
    }

    public void handleResponse() {
        progressDialog.dismiss();
        finish();
    }

    public void requestExistingPost() {
        progressDialog = ProgressDialog.show(context, "Loading Post", "Please Wait...", true);
        progressDialog.setCancelable(true);

        Access access = new Access(this);
        access.get(new AccessItem(Links.getUsersPost(locationItem.id),
            null, AccessIds.LOCATION_GET_USER_POST, true)
            .setActivity(this));
    }

    public void handleResponse(String response) {
        Log.d(Access.class.getSimpleName(), response);

        olderLoaded = true;
        progressDialog.dismiss();
        try {
            JSONObject json = new JSONObject(response);
            if (json.getBoolean("has_comment")) {
                CommentItem item = new CommentItem(json.getJSONObject("comment"));
                comment.setText(item.description);
                anonymous.setChecked(item.anonymous);
            }
            if (json.getBoolean("has_rating")) {
                JSONObject item = json.getJSONObject("rating");
                ratingBar.setRating(item.getInt("rating"));
            }
        } catch (Exception exception) {
            Log.e(AddPostActivity.class.getSimpleName(), exception.getMessage(), exception);
        }
    }

    public void handleErrorMessage() {
        progressDialog.dismiss();
        Functions.makeToast(context, "Something went wrong, please confirm network connection");
    }


    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        if (!olderLoaded) {
            requestExistingPost();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

}
