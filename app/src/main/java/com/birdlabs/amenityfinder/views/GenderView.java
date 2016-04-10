package com.birdlabs.amenityfinder.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdlabs.amenityfinder.R;
import com.github.bijoysingh.starter.util.ResourceManager;

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
            label.setTextColor(ResourceManager.getColor(view.getContext(), R.color.primary_color));
            icon.setColorFilter(ResourceManager.getColor(view.getContext(), R.color.primary_color));
        } else {
            label.setTextColor(ResourceManager.getColor(view.getContext(), R.color.hint_text));
            icon.setColorFilter(ResourceManager.getColor(view.getContext(), R.color.hint_text));
        }
    }
}
