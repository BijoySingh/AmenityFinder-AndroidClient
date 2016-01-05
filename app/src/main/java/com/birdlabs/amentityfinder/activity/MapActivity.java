package com.birdlabs.amentityfinder.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.birdlabs.amentityfinder.R;
import com.birdlabs.amentityfinder.items.LocationItem;
import com.birdlabs.amentityfinder.server.Access;
import com.birdlabs.amentityfinder.server.AccessInfo;
import com.birdlabs.amentityfinder.server.Filenames;
import com.birdlabs.amentityfinder.server.Links;
import com.birdlabs.amentityfinder.views.InfoAdapter;
import com.birdlabs.basicproject.Functions;
import com.birdlabs.basicproject.util.FileManager;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MapActivity extends ActionBarActivity implements OnMapReadyCallback {

    MapActivity activity;
    Context context;
    FloatingActionButton addLocation;
    FloatingActionButton closestLocation;
    List<LocationItem> locations;
    LocationItem closestItem;
    float closestDistance;

    GoogleMap map;
    Set<Integer> existingLocations;
    LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        context = this;
        activity = this;
        setupFabs();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void setupFabs() {
        addLocation = (FloatingActionButton) findViewById(R.id.add_location);
        addLocation.setBackgroundTintList(ColorStateList.valueOf(getApplicationContext().getResources().getColor(R.color.accent)));
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddLocationActivity.class);
                startActivity(intent);
            }
        });


        closestLocation = (FloatingActionButton) findViewById(R.id.closest_location);
        closestLocation.setBackgroundTintList(ColorStateList.valueOf(getApplicationContext().getResources().getColor(R.color.accent)));
        closestLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findClosestMatch();
                if (closestItem != null) {
                    Intent intent = new Intent(context, LocationActivity.class);
                    intent.putExtra(LocationActivity.LOCATION_ITEM, closestItem);
                    startActivity(intent);
                } else {
                    Functions.makeToast(context, "Could not locate the user, please try again");
                    Log.e(MapActivity.class.getSimpleName(), "CLOSEST ITEM null");
                }
            }
        });
    }

    public void findClosestMatch() {
        for (LocationItem item : locations) {
            float[] distances = {0.0f, 0.0f, 0.0f};
            if (closestItem == null && currentLocation != null) {
                closestItem = item;
                LatLng latLng = new LatLng(item.latitude, item.longitude);
                Location.distanceBetween(currentLocation.latitude, currentLocation.longitude,
                        closestItem.latitude, closestItem.longitude, distances);
                closestDistance = distances[0];
            } else if (currentLocation != null) {
                Location.distanceBetween(currentLocation.latitude, currentLocation.longitude,
                        item.latitude, item.longitude, distances);
                float distance = distances[0];
                if (distance < closestDistance) {
                    closestItem = item;
                    closestDistance = distance;
                }
            }
        }
    }

    public void refreshMap() {
        if (existingLocations == null) {
            existingLocations = new HashSet<>();
        }

        if (locations == null) {
            locations = new ArrayList<>();
        }

        try {
            String response = FileManager.read(context, Filenames.getLocations());
            JSONObject object = new JSONObject(response);
            JSONArray array = object.getJSONArray("results");
            for (int position = 0; position < array.length(); position++) {
                JSONObject json = array.getJSONObject(position);
                LocationItem item = new LocationItem(json);
                if (!existingLocations.contains(item.id)) {
                    locations.add(item);
                    existingLocations.add(item.id);
                }
            }
        } catch (JSONException json) {
            Log.e(MapActivity.class.getSimpleName(), json.getMessage(), json);
        }

        createMapLabels();
    }

    public void createMapLabels() {
        if (map != null) {
            for (LocationItem item : locations) {
                LatLng latLng = new LatLng(item.latitude, item.longitude);
                map.addMarker(new MarkerOptions().position(latLng).title(item.name)
                        .snippet(item.getSnippet()));

            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setInfoWindowAdapter(new InfoAdapter(this));
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                try {
                    JSONObject jsonObject = new JSONObject(marker.getSnippet());
                    Integer id = jsonObject.getInt("id");
                    for (LocationItem item : locations) {
                        if (item.id == id) {
                            Intent intent = new Intent(context, LocationActivity.class);
                            intent.putExtra(LocationActivity.LOCATION_ITEM, item);
                            startActivity(intent);
                            break;
                        }
                    }

                } catch (JSONException exception) {
                    ;
                }

            }
        });

        zoomToUserLocation(map);
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("lat_min", bounds.southwest.latitude);
                map.put("long_min", bounds.southwest.longitude);
                map.put("lat_max", bounds.northeast.latitude);
                map.put("long_max", bounds.northeast.longitude);

                Access access = new Access(context);
                access.send(new AccessInfo(Links.getLocations(), Filenames.getLocations(),
                        AccessInfo.AccessIds.LOCATION_GET, false).setActivity(activity), map);
            }
        });

        refreshMap();
    }

    public void zoomToUserLocation(GoogleMap map) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);

            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            String provider = lm.getBestProvider(new Criteria(), true);

            if (provider == null) {
                return;
            }

            Location location = lm.getLastKnownLocation(provider);
            double lat= location.getLatitude();
            double lng = location.getLongitude();
            currentLocation = new LatLng(lat, lng);

            Log.d(MapActivity.class.getSimpleName(), currentLocation.toString());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
        }
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

}
