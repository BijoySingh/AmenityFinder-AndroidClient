package com.birdlabs.amentityfinder.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdlabs.amentityfinder.R;

/**
 * The amenity view
 * Created by bijoy on 1/2/16.
 */
public class AmenityView {
    public View view;
    public TextView label;
    public TextView distance;
    public ImageView icon;

    public AmenityView(View v) {
        view = v;
        label = (TextView) view.findViewById(R.id.label);
        distance = (TextView) view.findViewById(R.id.distance);
        icon = (ImageView) view.findViewById(R.id.icon);
    }
}
