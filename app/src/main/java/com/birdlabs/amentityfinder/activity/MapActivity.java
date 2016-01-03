package com.birdlabs.amentityfinder.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.birdlabs.amentityfinder.R;
import com.birdlabs.amentityfinder.items.LocationItem;


public class MapActivity extends ActionBarActivity {

    FloatingActionButton addLocation;
    FloatingActionButton currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        setupFabs();
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


        currentLocation = (FloatingActionButton) findViewById(R.id.current_location);
        currentLocation.setBackgroundTintList(ColorStateList.valueOf(getApplicationContext().getResources().getColor(R.color.accent)));
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                intent.putExtra(LocationActivity.LOCATION_ITEM, LocationItem.getPlaceholder());
                startActivity(intent);
            }
        });
    }

    public void refreshMap() {
        
    }

}
