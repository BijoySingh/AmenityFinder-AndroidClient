package com.birdlabs.amentityfinder.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.birdlabs.amentityfinder.R;
import com.birdlabs.amentityfinder.items.LocationItem;
import com.birdlabs.amentityfinder.server.Access;
import com.birdlabs.amentityfinder.server.AccessInfo;
import com.birdlabs.amentityfinder.server.Links;
import com.birdlabs.basicproject.Functions;
import com.birdlabs.basicproject.util.ImageManager;
import com.facebook.appevents.AppEventsLogger;

import java.util.HashMap;
import java.util.Map;


public class AddPhotoActivity extends AppCompatActivity {

    public static final String LOCATION_ITEM = "LOCATION_ITEM";
    public static final Integer PICK_IMAGE_REQUEST = 1390;

    Bitmap bitmap = null;
    LocationItem locationItem;

    ImageView locationImage;
    View selectImage;

    Switch anonymous;
    Context context;
    ProgressDialog progressDialog;
    ImageManager imageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_photo);
        context = this;

        locationItem = (LocationItem) getIntent().getSerializableExtra(LOCATION_ITEM);
        EditText location = (EditText) findViewById(R.id.location);
        location.setText(locationItem.name);

        anonymous = (Switch) findViewById(R.id.anonymous);
        setupCreateClick();
        setupImageHandler();
        imageManager = new ImageManager();
    }

    public void setupImageHandler() {
        locationImage = (ImageView) findViewById(R.id.location_image);
        selectImage = findViewById(R.id.choose_image_file);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        };

        locationImage.setOnClickListener(clickListener);
        selectImage.setOnClickListener(clickListener);
    }

    public void setupCreateClick() {
        View.OnClickListener publishPost = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();

                if (bitmap == null) {
                    Functions.makeToast(context, "Please choose an image");
                }

                progressDialog = ProgressDialog.show(context,
                        "Adding Photo", "Please Wait...", true);
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
        map.put("photo", ImageManager.toBase64(bitmap));
        map.put("is_anonymous", anonymous.isChecked());

        Access access = new Access(this);
        access.send(new AccessInfo(Links.getPictureLink(), null, AccessInfo.AccessIds.PHOTO_POST, true)
                .setActivity(this)
                .setMethod(Request.Method.POST), map);
    }

    public void handleResponse() {
        progressDialog.dismiss();
        finish();
    }

    public void handleErrorResponse() {
        progressDialog.dismiss();
        Functions.makeToast(context, "Something went wrong!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    private void showFileChooser() {
        imageManager.showFileChooser(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            Bitmap bmp = imageManager.handleResponse(requestCode, resultCode, data);
            if (bmp != null) {
                bitmap = ImageManager.getScaledBitmapWithHeight(bmp, 256);
                locationImage.setImageBitmap(bitmap);
            }
        } catch (Exception exception) {
            Log.e(AddPhotoActivity.class.getSimpleName(), exception.getMessage(), exception);
        }
    }


}
