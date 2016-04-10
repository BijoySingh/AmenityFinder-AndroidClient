package com.birdlabs.amenityfinder.views;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdlabs.amenityfinder.R;
import com.birdlabs.amenityfinder.items.LocationItem;
import com.birdlabs.amenityfinder.util.Functions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Map Information Click Adapter
 * Created by bijoy on 1/3/16.
 */
public class InfoAdapter implements GoogleMap.InfoWindowAdapter {

    private final View myContentsView;

    public InfoAdapter(Activity activity) {
        myContentsView = activity.getLayoutInflater().inflate(R.layout.map_info_window, null);
    }

    @Override
    public View getInfoContents(Marker marker) {
        TextView cost = ((TextView) myContentsView.findViewById(R.id.cost));
        TextView title = ((TextView) myContentsView.findViewById(R.id.title));
        TextView rating = ((TextView) myContentsView.findViewById(R.id.rating));
        ImageView image = ((ImageView) myContentsView.findViewById(R.id.gender));

        title.setText(marker.getTitle());
        try {
            JSONObject json = new JSONObject(marker.getSnippet());
            rating.setText(Functions.setPrecision(json.getDouble("rating"), 1));
            cost.setText(LocationItem.getFreeString(json.getBoolean("is_free")));
            image.setImageResource(LocationItem.getGenderDrawable(json.getBoolean("male"), json.getBoolean("female")));
        } catch (JSONException exception) {
            Log.e(InfoAdapter.class.getSimpleName(), exception.getMessage(), exception);
        }

        return myContentsView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // TODO Auto-generated method stub
        return null;
    }

}
