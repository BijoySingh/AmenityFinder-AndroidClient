package com.birdlabs.amentityfinder.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.birdlabs.amentityfinder.R;
import com.birdlabs.amentityfinder.server.Access;
import com.birdlabs.amentityfinder.server.AccessInfo;
import com.birdlabs.amentityfinder.server.Links;
import com.birdlabs.amentityfinder.views.GenderView;

import java.util.HashMap;
import java.util.Map;


public class AddLocationActivity extends AppCompatActivity {

    public enum WashroomType {
        BOTH, MALE, FEMALE
    }

    Context context;
    WashroomType selected;

    EditText location;
    Switch paid;
    Double longitude;
    Double latitude;
    Boolean isFree;
    Boolean hasMale;
    Boolean hasFemale;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.add_location);

        location = (EditText) findViewById(R.id.location);
        paid = (Switch) findViewById(R.id.paid);

        setupWashroomTypes();
        setupCreateClick();
    }

    public void setupCreateClick() {
        View.OnClickListener publishLocation = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                longitude = 32.42143;
                latitude = 34.53232;
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
        };

        TextView create = (TextView) findViewById(R.id.create);
        TextView done = (TextView) findViewById(R.id.done);
        create.setOnClickListener(publishLocation);
        done.setOnClickListener(publishLocation);
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
        access.send(new AccessInfo(Links.getLocation(), null, AccessInfo.AccessIds.LOCATION_POST, true)
                .setMethod(Request.Method.POST), map);
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

}
