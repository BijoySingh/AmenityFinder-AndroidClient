package com.birdlabs.amentityfinder.activity;

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
import com.birdlabs.amentityfinder.R;
import com.birdlabs.amentityfinder.items.CommentItem;
import com.birdlabs.amentityfinder.items.LocationItem;
import com.birdlabs.amentityfinder.server.Access;
import com.birdlabs.amentityfinder.server.AccessInfo;
import com.birdlabs.amentityfinder.server.Links;
import com.birdlabs.basicproject.Functions;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class AddPostActivity extends AppCompatActivity {

    public static final String LOCATION_ITEM = "LOCATION_ITEM";
    LocationItem locationItem;

    EditText comment;
    Switch anonymous;
    RatingBar ratingBar;
    Context context;
    ProgressDialog progressDialog;
    Boolean olderLoaded;
    Integer method;
    String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_post);
        context = this;

        locationItem = (LocationItem) getIntent().getSerializableExtra(LOCATION_ITEM);
        EditText location = (EditText) findViewById(R.id.location);
        location.setText(locationItem.name);

        comment = (EditText) findViewById(R.id.comment);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        anonymous = (Switch) findViewById(R.id.anonymous);
        olderLoaded = false;
        method = Request.Method.POST;
        link = Links.getPostLink();
        setupCreateClick();
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

    public void sendData() {
        Map<String, Object> map = new HashMap<>();
        map.put("location", locationItem.id);
        map.put("comment", comment.getText().toString());
        map.put("rating", ratingBar.getRating());
        map.put("is_anonymous", anonymous.isChecked());

        Access access = new Access(this);
        access.send(new AccessInfo(link, null, AccessInfo.AccessIds.POST_POST, true)
                .setActivity(this)
                .setMethod(method), map);
    }

    public void handleResponse() {
        progressDialog.dismiss();
        finish();
    }

    public void requestExistingPost() {
        progressDialog = ProgressDialog.show(context,
                "Loading Post", "Please Wait...", true);
        progressDialog.setCancelable(true);

        Access access = new Access(this);
        access.get(new AccessInfo(Links.getUsersPost(locationItem.id),
                null, AccessInfo.AccessIds.LOCATION_GET_USER_POST, true)
                .setActivity(this));
    }

    public void handleResponse(String response) {
        Log.d(Access.class.getSimpleName(), response);

        olderLoaded = true;
        progressDialog.dismiss();
        try {
            JSONObject json = new JSONObject(response);
            if (json.getBoolean("success")) {
                CommentItem item = new CommentItem(json.getJSONObject("result"));
                comment.setText(item.comment);
                anonymous.setChecked(item.anonymous);
                ratingBar.setRating(item.rating.floatValue());
                method = Request.Method.PUT;
                link = Links.getPostLink(item.id);
            }
        } catch (JSONException exception) {
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
