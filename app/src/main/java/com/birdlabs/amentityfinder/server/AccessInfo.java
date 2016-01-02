package com.birdlabs.amentityfinder.server;

import com.birdlabs.basicproject.server.AccessItem;

/**
 * the access information
 * Created by bijoy on 1/2/16.
 */
public class AccessInfo extends AccessItem {

    public AccessInfo(String url, String filename, Integer type, Boolean authenticated) {
        super(url, filename, type, authenticated);
    }
}
