package com.birdlabs.amenityfinder.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.birdlabs.amenityfinder.R;
import com.birdlabs.amenityfinder.util.Preferences;
import com.facebook.login.LoginManager;
import com.github.bijoysingh.starter.util.ImageManager;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Basic Activity functions and variables
 * Created by bijoy on 1/25/16.
 */
public abstract class ActivityBase extends AppCompatActivity {

    Context context;
    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        preferences = new Preferences(context);
    }

    public BitmapDescriptor getBitmapDescriptor() {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(80, 80, conf);
        Canvas canvas = new Canvas(bmp);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_marker);
        bitmap = ImageManager.resizeBitmap(bitmap, 80, 80);
        canvas.drawBitmap(bitmap, 0, 0, new Paint());

        return BitmapDescriptorFactory.fromBitmap(bmp);
    }

    public void requestLogin() {
        new AlertDialog.Builder(context)
            .setTitle(R.string.login_required)
            .setMessage(R.string.login_required_message)
            .setPositiveButton(R.string.go_to_login, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    logoutUser();
                }
            })
            .create()
            .show();
    }

    public void logoutUser() {
        preferences.socialNetworkLogin(false);
        preferences.serverLogin(false);
        preferences.save(Preferences.Keys.AUTH_TOKEN, "");
        LoginManager.getInstance().logOut();

        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
