package com.birdlabs.amenityfinder.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.birdlabs.amenityfinder.R;
import com.birdlabs.amenityfinder.items.LocationItem;
import com.birdlabs.amenityfinder.server.Access;
import com.birdlabs.amenityfinder.server.AccessIds;
import com.birdlabs.amenityfinder.server.Filenames;
import com.birdlabs.amenityfinder.server.Links;
import com.birdlabs.amenityfinder.util.Preferences;
import com.birdlabs.amenityfinder.views.GenderView;
import com.birdlabs.amenityfinder.views.InfoAdapter;
import com.facebook.appevents.AppEventsLogger;
import com.github.bijoysingh.starter.Functions;
import com.github.bijoysingh.starter.server.AccessItem;
import com.github.bijoysingh.starter.util.FileManager;
import com.github.bijoysingh.starter.util.PermissionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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


public class MapActivity extends MapActivityBase implements OnMapReadyCallback {

    private static final Integer ANY = 0;
    private static final Integer BOTH = 1;
    private static final Integer MALE = 2;
    private static final Integer FEMALE = 3;

    View filter;
    GoogleMap map;
    MapActivity activity;
    View closestLocation;
    float closestDistance;
    LatLng currentLocation;
    Preferences preferences;
    LocationItem closestItem;
    BitmapDescriptor descriptor;
    List<LocationItem> locations;
    List<LocationItem> filteredlocations;
    Set<Integer> existingLocations;
    FloatingActionButton addLocation;
    Map<Integer, Marker> markers = new HashMap<>();
    Integer mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        mode = ANY;

        confirmPermissions();
        setupVariables();
        setupButtons();
    }

    public void setupVariables() {
        activity = this;
        preferences = new Preferences(context);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void setupButtons() {
        addLocation = (FloatingActionButton) findViewById(R.id.add_location);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (preferences.isLoggedIn()) {
                    Intent intent = new Intent(getApplicationContext(), AddLocationActivity.class);
                    startActivity(intent);
                } else {
                    requestLogin();
                }
            }
        });

        filter = findViewById(R.id.filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilterDialog();
            }
        });

        closestLocation = findViewById(R.id.closest_location);
        closestLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findClosestMatch();
                if (closestItem != null) {
                    Intent intent = new Intent(context, LocationActivity.class);
                    intent.putExtra(LocationActivity.LOCATION_ITEM, closestItem);
                    startActivity(intent);
                } else if (currentLocation == null) {
                    Functions.makeToast(context, "Could not locate the user, please try again.");
                } else {
                    Functions.makeToast(context, "No locations near your locality." +
                        " Zoom out to find more areas.");
                }
            }
        });

        TextView logout = (TextView) findViewById(R.id.logout);
        ImageView account = (ImageView) findViewById(R.id.account);
        if (preferences.isLoggedIn()) {
            logout.setVisibility(View.GONE);
            account.setVisibility(View.VISIBLE);
            account.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent userIntent = new Intent(context, UserProfileActivity.class);
                    startActivity(userIntent);
                }
            });
        } else {
            account.setVisibility(View.GONE);
            logout.setVisibility(View.VISIBLE);
            logout.setText(getResources().getString(R.string.login));
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logoutUser();
                }
            });
        }
    }

    public void findClosestMatch() {
        for (LocationItem item : locations) {
            float[] distances = {0.0f, 0.0f, 0.0f};
            if (closestItem == null && currentLocation != null) {
                closestItem = item;
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

        boolean somethingChanged = false;
        try {
            String response = FileManager.read(context, Filenames.getLocations());
            JSONObject object = new JSONObject(response);
            JSONArray array = object.getJSONArray("results");
            for (int position = 0; position < array.length(); position++) {
                JSONObject json = array.getJSONObject(position);
                LocationItem item = new LocationItem(json);

                int matchPosition = 0;
                for (LocationItem existingItem : locations) {
                    if (item.id.equals(existingItem.id)) {
                        break;
                    }
                    matchPosition += 1;
                }

                if (matchPosition == locations.size()) {
                    locations.add(item);
                    existingLocations.add(item.id);
                    somethingChanged = true;
                } else {
                    LocationItem existing = locations.get(matchPosition);
                    if (existing.male != item.male
                        || existing.female != item.female
                        || existing.isFree != item.isFree
                        || !existing.rating.equals(item.rating)) {
                        somethingChanged = true;
                    }

                    locations.set(position, item);
                }
            }
        } catch (Exception json) {
            Log.e(MapActivity.class.getSimpleName(), json.getMessage(), json);
        }

        if (somethingChanged) {
            filterMarkers();
            createMapLabels();
        }
    }

    public void filterMarkers() {
        filteredlocations = new ArrayList<>();
        for (LocationItem item : locations) {
            if (mode.equals(ANY)) {
                filteredlocations.add(item);
            } else if (mode.equals(BOTH) && item.male && item.female) {
                filteredlocations.add(item);
            } else if (mode.equals(MALE) && item.male && !item.female) {
                filteredlocations.add(item);
            } else if (mode.equals(FEMALE) && !item.male && item.female) {
                filteredlocations.add(item);
            }
        }
    }

    public void createMapLabels() {
        if (map != null) {
            Set<Integer> keys = new HashSet<>();
            keys.addAll(markers.keySet());
            for (Integer key : keys) {
                markers.get(key).remove();
                markers.remove(key);
            }

            for (LocationItem item : filteredlocations) {
                LatLng latLng = new LatLng(item.latitude, item.longitude);
                Marker marker = map.addMarker(new MarkerOptions().position(latLng).title(item.title)
                    .icon(descriptor)
                    .snippet(item.getSnippet()));
                markers.put(item.id, marker);
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
                        if (item.id.equals(id)) {
                            Intent intent = new Intent(context, LocationActivity.class);
                            intent.putExtra(LocationActivity.LOCATION_ITEM, item);
                            startActivity(intent);
                            break;
                        }
                    }

                } catch (JSONException exception) {
                    Log.e(MapActivity.class.getSimpleName(), exception.getMessage(), exception);
                }

            }
        });

        zoomToUserLocation(map);
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                requestLocationByBoundingBox();
            }
        });

        refreshMap();
    }

    public void requestLocationByBoundingBox() {
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        Map<String, Object> map = new HashMap<>();
        map.put("min_latitude", bounds.southwest.latitude);
        map.put("min_longitude", bounds.southwest.longitude);
        map.put("max_latitude", bounds.northeast.latitude);
        map.put("max_longitude", bounds.northeast.longitude);

        Access access = new Access(context);
        access.send(new AccessItem(Links.getLocations(), Filenames.getLocations(),
            AccessIds.LOCATION_GET, true).setActivity(activity), map);
    }

    @SuppressWarnings("NewApi")
    public void zoomToUserLocation(GoogleMap map) {

        if (manager.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            map.setMyLocationEnabled(true);
            Location location = getLastKnownLocation();
            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                currentLocation = new LatLng(lat, lng);

                Log.d(MapActivity.class.getSimpleName(), currentLocation.toString());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            manager.requestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionManager.REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                zoomToUserLocation(map);
            } else {
                Toast.makeText(this,
                    "Permission Denied, the application may not work", Toast.LENGTH_SHORT)
                    .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @SuppressWarnings("all")
    private Location getLastKnownLocation() {
        Location bestLocation = null;

        if (manager.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = locationManager.getProviders(true);

            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }

                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }
        }
        return bestLocation;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (map != null) {
            requestLocationByBoundingBox();
        }
        descriptor = getBitmapDescriptor();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    public void openFilterDialog() {
        try {
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.filter_dialog, null);
            AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setNegativeButton(R.string.reset, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mode = ANY;
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        filterMarkers();
                        createMapLabels();
                    }
                })
                .create();

            final GenderView both = new GenderView(dialogView.findViewById(R.id.both));
            final GenderView male = new GenderView(dialogView.findViewById(R.id.male));
            final GenderView female = new GenderView(dialogView.findViewById(R.id.female));

            if (mode.equals(MALE)) {
                male.setStatus(true);
            } else if (mode.equals(FEMALE)) {
                female.setStatus(true);
            } else if (mode.equals(BOTH)) {
                both.setStatus(true);
            }

            both.set(getString(R.string.both), R.drawable.washroom);
            both.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mode.equals(BOTH)) {
                        both.setStatus(false);
                        mode = ANY;
                    } else {
                        both.setStatus(true);
                        male.setStatus(false);
                        female.setStatus(false);
                        mode = BOTH;
                    }
                }
            });


            male.set(getString(R.string.male), R.drawable.male);
            male.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mode.equals(MALE)) {
                        male.setStatus(false);
                        mode = ANY;
                    } else {
                        both.setStatus(false);
                        male.setStatus(true);
                        female.setStatus(false);
                        mode = MALE;
                    }
                }
            });


            female.set(getString(R.string.female), R.drawable.female);
            female.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mode.equals(FEMALE)) {
                        female.setStatus(false);
                        mode = ANY;
                    } else {
                        both.setStatus(false);
                        male.setStatus(false);
                        female.setStatus(true);
                        mode = FEMALE;
                    }
                }
            });

            dialog.show();
        } catch (Exception e) {
            Log.e(MapActivity.class.getSimpleName(), e.getMessage(), e);
            Functions.makeToast(context, "Sorry, this feature is not available on your phone!");
        }
    }

}
