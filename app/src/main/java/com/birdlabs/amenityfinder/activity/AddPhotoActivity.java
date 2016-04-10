package com.birdlabs.amenityfinder.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.birdlabs.amenityfinder.R;
import com.birdlabs.amenityfinder.items.LocationItem;
import com.birdlabs.amenityfinder.server.Access;
import com.birdlabs.amenityfinder.server.AccessIds;
import com.birdlabs.amenityfinder.server.HttpFileUpload;
import com.birdlabs.amenityfinder.server.Links;
import com.facebook.appevents.AppEventsLogger;
import com.github.bijoysingh.starter.Functions;
import com.github.bijoysingh.starter.server.AccessItem;
import com.github.bijoysingh.starter.util.ImageManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class AddPhotoActivity extends AppCompatActivity {

    public static final String LOCATION_ITEM = "LOCATION_ITEM";

    Context context;
    View selectImage;
    Switch anonymous;
    Bitmap bitmap = null;
    ImageView locationImage;
    ImageManager imageManager;
    LocationItem locationItem;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_photo);
        context = this;

        locationItem = (LocationItem) getIntent().getSerializableExtra(LOCATION_ITEM);
        EditText location = (EditText) findViewById(R.id.location);
        location.setText(locationItem.title);

        anonymous = (Switch) findViewById(R.id.anonymous);
        setupCreateClick();
        setupImageHandler();
        imageManager = new ImageManager(this);
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

    public String getFilename() {
        return "photo_upload_" + System.currentTimeMillis() + ".jpg";
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
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(stream.toByteArray());

        HttpFileUpload fileUpload = new HttpFileUpload(
            Links.addPhoto(locationItem.id), anonymous.isChecked(), context, this);
        fileUpload.setupSend(inputStream, getFilename());
        fileUpload.execute();
    }

    public void handleResponse(String s) {
        Log.d(AddPhotoActivity.class.getSimpleName(), s);
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
        imageManager.showFileChooser();
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
