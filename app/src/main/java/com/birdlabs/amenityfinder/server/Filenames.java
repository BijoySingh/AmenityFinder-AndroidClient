package com.birdlabs.amenityfinder.server;

/**
 * File names list
 * Created by bijoy on 1/2/16.
 */
public class Filenames {
    public static String getFilenameLink() {
        return "";
    }

    public static String getLocationPosts(Integer locationId) {
        return "location_posts_" + locationId + ".txt";
    }


    public static String getLocationPhotos(Integer locationId) {
        return "location_photos_" + locationId + ".txt";
    }

    public static String getLocationStars(Integer locationId) {
        return "location_stars_" + locationId + ".txt";
    }

    public static String getLocations() {
        return "locations.txt";
    }

    public static String getProfile() {
        return "profile.txt";
    }

    public static String getUserActivity() {
        return "activity.txt";
    }
}
