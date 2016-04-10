package com.birdlabs.amenityfinder.activity;

import android.Manifest;

import com.github.bijoysingh.starter.util.PermissionManager;

/**
 * Map activity base
 * Created by bijoy on 3/5/16.
 */
public class MapActivityBase extends ActivityBase {
    PermissionManager manager;

    public void confirmPermissions() {
        manager = new PermissionManager(this,
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE});
        manager.requestPermissions();
    }
}
