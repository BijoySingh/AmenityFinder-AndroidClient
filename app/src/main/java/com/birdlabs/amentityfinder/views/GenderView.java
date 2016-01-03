package com.birdlabs.amentityfinder.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdlabs.amentityfinder.R;

/**
 * The amenity view
 * Created by bijoy on 1/2/16.
 */
public class GenderView {
    public View view;

    public ImageView icon;
    public TextView label;


    public GenderView(View v) {
        view = v;
        icon = (ImageView) view.findViewById(R.id.icon);
        label = (TextView) view.findViewById(R.id.label);
    }

    public void set(String label, Integer icon) {
        this.label.setText(label);
        this.icon.setImageResource(icon);
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        this.label.setOnClickListener(clickListener);
        this.icon.setOnClickListener(clickListener);
    }

    public void setStatus(boolean activated) {
        if (activated) {
            label.setTextColor(view.getResources().getColor(R.color.primary_color));
            icon.setColorFilter(view.getResources().getColor(R.color.primary_color));
        } else {
            label.setTextColor(view.getResources().getColor(R.color.hint_text));
            icon.setColorFilter(view.getResources().getColor(R.color.hint_text));
        }
    }
}
