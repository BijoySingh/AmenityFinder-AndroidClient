package com.birdlabs.amentityfinder.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.birdlabs.amentityfinder.R;
import com.birdlabs.amentityfinder.items.LocationItem;
import com.birdlabs.amentityfinder.server.Access;
import com.birdlabs.amentityfinder.server.AccessInfo;
import com.birdlabs.amentityfinder.server.Links;

import java.util.HashMap;
import java.util.Map;


public class AddPostActivity extends AppCompatActivity {

    public static final String LOCATION_ITEM = "LOCATION_ITEM";
    LocationItem locationItem;

    EditText comment;
    Switch anonymous;
    RatingBar ratingBar;
    Context context;

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
        setupCreateClick();
    }

    public void setupCreateClick() {
        View.OnClickListener publishPost = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
                ProgressDialog progressDialog = ProgressDialog.show(context,
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
        access.send(new AccessInfo(Links.getPostLink(), null, AccessInfo.AccessIds.POST_POST, true)
                .setMethod(Request.Method.POST), map);
    }

}
