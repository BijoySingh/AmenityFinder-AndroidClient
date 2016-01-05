package com.birdlabs.amentityfinder.views;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdlabs.amentityfinder.R;
import com.birdlabs.amentityfinder.items.LocationItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bijoy on 1/3/16.
 */
public class InfoAdapter implements GoogleMap.InfoWindowAdapter {

    private final View myContentsView;

    public InfoAdapter(Activity activity) {
        myContentsView = activity.getLayoutInflater().inflate(R.layout.map_info_window, null);
    }

    @Override
    public View getInfoContents(Marker marker) {
        TextView title = ((TextView) myContentsView.findViewById(R.id.title));
        TextView cost = ((TextView) myContentsView.findViewById(R.id.cost));
        TextView rating = ((TextView) myContentsView.findViewById(R.id.rating));
        ImageView image = ((ImageView) myContentsView.findViewById(R.id.gender));

        title.setText(marker.getTitle());
        try {
            JSONObject json = new JSONObject(marker.getSnippet());
            cost.setText(LocationItem.getFreeString(json.getBoolean("is_free")));
            rating.setText("" + json.getDouble("rating"));
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
