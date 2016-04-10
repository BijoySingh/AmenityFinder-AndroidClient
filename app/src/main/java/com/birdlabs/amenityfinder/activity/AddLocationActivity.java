package com.birdlabs.amenityfinder.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.birdlabs.amenityfinder.server.Links;
import com.birdlabs.amenityfinder.views.GenderView;
import com.facebook.appevents.AppEventsLogger;
import com.github.bijoysingh.starter.Functions;
import com.github.bijoysingh.starter.server.AccessItem;
import com.github.bijoysingh.starter.util.ResourceManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AddLocationActivity extends ActivityBase implements OnMapReadyCallback {

    public static final String LOCATION_ITEM = "LOCATION_ITEM";
    Switch paid;
    TextView done;
    GoogleMap map;
    Boolean isFree;
    Boolean hasMale;
    Double latitude;
    Double longitude;
    Integer mode = 0;
    View lowerLayout;
    View upperLayout;
    Switch anonymous;
    String geoDecoder;
    Boolean hasFemale;
    EditText location;
    Boolean updateMode;
    Marker addedMarker;
    Boolean isAnonymous;
    EditText description;
    TextView locationName;
    WashroomType selected;
    LocationItem locationItem;
    LatLng locationCoordinates;
    BitmapDescriptor descriptor;
    ProgressDialog progressDialog;
    FloatingActionButton nextState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_location);

        paid = (Switch) findViewById(R.id.paid);
        anonymous = (Switch) findViewById(R.id.anonymous);
        location = (EditText) findViewById(R.id.location);
        description = (EditText) findViewById(R.id.description);

        upperLayout = findViewById(R.id.upper_panel);
        upperLayout.setVisibility(View.VISIBLE);

        lowerLayout = findViewById(R.id.lower_panel);
        lowerLayout.setVisibility(View.GONE);

        done = (TextView) findViewById(R.id.done);
        locationName = (TextView) findViewById(R.id.location_name);

        nextState = (FloatingActionButton) findViewById(R.id.next_state);
        nextState.setBackgroundTintList(ColorStateList.valueOf(
            ResourceManager.getColor(getApplicationContext(), R.color.accent)));

        locationItem = (LocationItem) getIntent().getSerializableExtra(LOCATION_ITEM);
        if (locationItem == null) {
            updateMode = false;
        } else {
            updateMode = true;
            location.setText(locationItem.title);
            locationName.setText(locationItem.title);
            description.setText(locationItem.description);
            paid.setChecked(!locationItem.isFree);
            anonymous.setChecked(locationItem.isAnonymous);
        }

        descriptor = getBitmapDescriptor();

        setupMap();

        ImageView back = (ImageView) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void setupMap() {
        setupWashroomTypes();
        setupCreateClick();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void setupCreateClick() {
        View.OnClickListener publishLocation = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == 0) {
                    mode = 1;
                    upperLayout.setVisibility(View.GONE);
                    lowerLayout.setVisibility(View.VISIBLE);
                    latitude = locationCoordinates.latitude;
                    longitude = locationCoordinates.longitude;
                    done.setText(getResources().getString(R.string.done));
                } else {
                    isFree = !paid.isChecked();
                    isAnonymous = anonymous.isChecked();
                    new AlertDialog.Builder(context)
                        .setTitle(R.string.add_location_question)
                        .setMessage(getResources().getString(R.string.about_to_add_location)
                            + " '" + location.getText().toString() + "'. "
                            + getResources().getString(R.string.are_you_sure))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sendData();
                                dialogInterface.cancel();
                                progressDialog = ProgressDialog.show(context,
                                    getResources().getString(R.string.adding_location),
                                    getResources().getString(R.string.please_wait),
                                    true);
                                progressDialog.setCancelable(true);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .create()
                        .show();
                }
            }
        };

        TextView create = (TextView) findViewById(R.id.create);
        TextView done = (TextView) findViewById(R.id.done);
        create.setOnClickListener(publishLocation);
        done.setOnClickListener(publishLocation);
        nextState.setOnClickListener(publishLocation);
    }

    public void sendData() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", location.getText().toString());
        map.put("description", description.getText().toString());
        map.put("longitude", longitude);
        map.put("latitude", latitude);
        map.put("is_free", isFree);
        map.put("is_anonymous", isAnonymous);
        map.put("male", hasMale);
        map.put("female", hasFemale);

        Access access = new Access(this);
        String link = Links.addLocation();
        Integer mode = Request.Method.POST;
        if (updateMode) {
            mode = Request.Method.PUT;
            link = Links.getLocation(locationItem.id);
        }

        access.send(new AccessItem(link, null, AccessIds.LOCATION_POST, true)
            .setActivity(this)
            .setMethod(mode), map);
    }

    public void handleResponse(JSONObject json) {
        try {
            progressDialog.dismiss();
            Intent intent = new Intent(context, LocationActivity.class);
            intent.putExtra(LocationActivity.LOCATION_ITEM, new LocationItem(json));
            startActivity(intent);
            finish();
        } catch (Exception exception) {
            progressDialog.dismiss();
            Functions.makeToast(context, "Something went wrong!");
        }
    }

    public void handleErrorMessage() {
        progressDialog.dismiss();
        Functions.makeToast(context, "Something went wrong, please confirm network connection");
    }

    private void setMaleWashroom(GenderView both, GenderView male, GenderView female) {
        hasMale = true;
        hasFemale = false;
        male.setStatus(true);
        both.setStatus(false);
        female.setStatus(false);
        selected = WashroomType.MALE;
    }

    private void setFemaleWashroom(GenderView both, GenderView male, GenderView female) {
        hasMale = false;
        hasFemale = true;
        both.setStatus(false);
        male.setStatus(false);
        female.setStatus(true);
        selected = WashroomType.FEMALE;
    }

    private void setBothWashroom(GenderView both, GenderView male, GenderView female) {
        hasMale = true;
        hasFemale = true;
        both.setStatus(true);
        male.setStatus(false);
        female.setStatus(false);
        selected = WashroomType.BOTH;
    }

    public void setupWashroomTypes() {
        final GenderView both = new GenderView(findViewById(R.id.both));
        final GenderView male = new GenderView(findViewById(R.id.male));
        final GenderView female = new GenderView(findViewById(R.id.female));

        both.set(getResources().getString(R.string.both), R.drawable.washroom);
        both.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBothWashroom(both, male, female);
            }
        });

        male.set(getResources().getString(R.string.male), R.drawable.male);
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMaleWashroom(both, male, female);
            }
        });

        female.set(getResources().getString(R.string.female), R.drawable.female);
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFemaleWashroom(both, male, female);
            }
        });

        if (updateMode) {
            hasMale = locationItem.male;
            hasFemale = locationItem.female;
            if (hasMale && hasFemale) {
                setBothWashroom(both, male, female);
            } else if (hasMale) {
                setMaleWashroom(both, male, female);
            } else if (hasFemale) {
                setFemaleWashroom(both, male, female);
            }
        } else {
            setBothWashroom(both, male, female);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                geoDecoder = getDetails(latLng);
                if (!updateMode) {
                    location.setText(geoDecoder);
                }
                locationName.setText(geoDecoder);
                addMarker(latLng, false);
            }
        });
        zoomToUserLocation(map);
    }

    /**
     * Zoom to user's location or to previously added location
     *
     * @param map the google map
     */
    public void zoomToUserLocation(GoogleMap map) {
        if (updateMode) {
            LatLng latLng = new LatLng(locationItem.latitude, locationItem.longitude);
            addMarker(latLng, true);
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);

            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            String provider;

            if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                provider = LocationManager.NETWORK_PROVIDER;
            } else {
                provider = LocationManager.GPS_PROVIDER;
            }

            Location location = lm.getLastKnownLocation(provider);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            geoDecoder = getDetails(latLng);
            this.location.setText(geoDecoder);
            locationName.setText(geoDecoder);
            addMarker(latLng, true);
        }
    }

    /**
     * Gets the Geocoder information for a location
     *
     * @param latLng the coordinates
     * @return the string to show
     */
    public String getDetails(LatLng latLng) {
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String featured = addresses.get(0).getFeatureName();
            String subLocality = addresses.get(0).getSubLocality();
            return featured + ", " + subLocality;
        } catch (Exception exception) {
            Log.e(AddLocationActivity.class.getSimpleName(), exception.getMessage(), exception);
        }
        return "";
    }

    /**
     * Adds a marker on the map
     *
     * @param latLng the coordinates
     * @param zoom   should zoom the map or not
     */
    public void addMarker(LatLng latLng, boolean zoom) {
        locationCoordinates = latLng;

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(geoDecoder);
        markerOptions.icon(descriptor);

        if (addedMarker != null) {
            addedMarker.remove();
        }

        if (zoom) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        } else {
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }

        addedMarker = map.addMarker(markerOptions);
    }

    @Override
    public void onBackPressed() {
        if (mode == 0) {
            super.onBackPressed();
        } else {
            mode = 0;
            upperLayout.setVisibility(View.VISIBLE);
            lowerLayout.setVisibility(View.GONE);
            done.setText(R.string.next);
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

    public enum WashroomType {
        BOTH, MALE, FEMALE
    }
}
