package com.birdlabs.amentityfinder.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.birdlabs.amentityfinder.views.GenderView;
import com.birdlabs.basicproject.Functions;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AddLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    public enum WashroomType {
        BOTH, MALE, FEMALE
    }

    Context context;
    WashroomType selected;
    GoogleMap map;

    EditText location;
    Switch paid;
    Double longitude;
    Double latitude;
    Boolean isFree;
    Boolean hasMale;
    Boolean hasFemale;
    String geoDecoder;
    LatLng locationCoordinates;
    ProgressDialog progressDialog;
    View lowerLayout;
    View upperLayout;
    TextView done;
    TextView locationName;
    FloatingActionButton nextState;

    Integer mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.add_location);

        location = (EditText) findViewById(R.id.location);
        paid = (Switch) findViewById(R.id.paid);

        upperLayout = findViewById(R.id.upper_panel);
        upperLayout.setVisibility(View.VISIBLE);

        lowerLayout = findViewById(R.id.lower_panel);
        lowerLayout.setVisibility(View.GONE);

        done = (TextView) findViewById(R.id.done);
        locationName = (TextView) findViewById(R.id.location_name);

        nextState = (FloatingActionButton) findViewById(R.id.next_state);
        nextState.setBackgroundTintList(ColorStateList.valueOf(getApplicationContext().getResources().getColor(R.color.accent)));

        setupWashroomTypes();
        setupCreateClick();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageView back = (ImageView) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void setupCreateClick() {
        View.OnClickListener publishLocation = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == 0) {
                    longitude = locationCoordinates.longitude;
                    latitude = locationCoordinates.latitude;
                    mode = 1;
                    upperLayout.setVisibility(View.GONE);
                    lowerLayout.setVisibility(View.VISIBLE);
                    done.setText("DONE");
                } else {
                    isFree = !paid.isChecked();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("Add Location?")
                            .setMessage("You are about to add the location '"
                                    + location.getText().toString()
                                    + "'. Are you sure?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    sendData();
                                    dialogInterface.cancel();
                                    progressDialog = ProgressDialog.show(context,
                                            "Adding Location", "Please Wait...", true);
                                    progressDialog.setCancelable(true);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
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
        map.put("name", location.getText().toString());
        map.put("longitude", longitude);
        map.put("latitude", latitude);
        map.put("is_free", isFree);
        map.put("male", hasMale);
        map.put("female", hasFemale);

        Access access = new Access(this);
        access.send(new AccessInfo(Links.addLocation(), null, AccessInfo.AccessIds.LOCATION_POST, true)
                .setActivity(this)
                .setMethod(Request.Method.POST), map);
    }

    public void handleResponse(JSONObject json) {
        try {
            progressDialog.dismiss();
            Intent intent = new Intent(context, LocationActivity.class);
            intent.putExtra(LocationActivity.LOCATION_ITEM, new LocationItem(json));
            startActivity(intent);
            finish();
        } catch (JSONException exception) {
            progressDialog.dismiss();
            Functions.makeToast(context, "Something went wrong!");
        }
    }

    public void handleErrorMessage() {
        progressDialog.dismiss();
        Functions.makeToast(context, "Something went wrong, please confirm network connection");
    }

    public void setupWashroomTypes() {
        final GenderView both = new GenderView(findViewById(R.id.both));
        final GenderView male = new GenderView(findViewById(R.id.male));
        final GenderView female = new GenderView(findViewById(R.id.female));

        both.set("Both", R.drawable.washroom);
        both.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                both.setStatus(true);
                male.setStatus(false);
                female.setStatus(false);
                hasMale = true;
                hasFemale = true;
                selected = WashroomType.BOTH;
            }
        });

        male.set("Male", R.drawable.male);
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                both.setStatus(false);
                male.setStatus(true);
                female.setStatus(false);
                hasMale = true;
                hasFemale = false;
                selected = WashroomType.MALE;
            }
        });

        female.set("Female", R.drawable.female);
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                both.setStatus(false);
                male.setStatus(false);
                female.setStatus(true);
                hasMale = false;
                hasFemale = true;
                selected = WashroomType.FEMALE;
            }
        });

        both.setStatus(true);
        hasMale = true;
        hasFemale = true;
        selected = WashroomType.BOTH;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                geoDecoder = getDetails(latLng);
                location.setText(geoDecoder);
                locationName.setText(geoDecoder);
                addMarker(latLng, false);
            }
        });
        zoomToUserLocation(map);
    }

    public void zoomToUserLocation(GoogleMap map) {

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

    public void addMarker(LatLng latLng, boolean zoom) {
        locationCoordinates = latLng;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(geoDecoder);
        map.clear();
        if (zoom) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        } else {
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        map.addMarker(markerOptions);
    }

    @Override
    public void onBackPressed() {
        if (mode == 0) {
            super.onBackPressed();
        } else {
            mode = 0;
            upperLayout.setVisibility(View.VISIBLE);
            lowerLayout.setVisibility(View.GONE);
            done.setText("NEXT");
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
