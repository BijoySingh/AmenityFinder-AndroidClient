package com.birdlabs.amentityfinder.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdlabs.amentityfinder.R;
import com.birdlabs.amentityfinder.items.CommentItem;
import com.birdlabs.amentityfinder.items.PhotoItem;
import com.birdlabs.basicproject.item.TimestampItem;
import com.birdlabs.basicproject.util.TimestampManager;

/**
 * The amenity view
 * Created by bijoy on 1/2/16.
 */
public class PhotoViewHolder extends RecyclerView.ViewHolder {
    public View view;

    public ImageView location_picture;
    public TextView timestamp;

    public TextView author_name;
    public ImageView author_picture;
    public ImageView delete_photo;

    public PhotoViewHolder(View v) {
        super(v);
        view = v;
        author_name = (TextView) view.findViewById(R.id.author_name);
        timestamp = (TextView) view.findViewById(R.id.timestamp);

        author_picture = (ImageView) view.findViewById(R.id.author_picture);
        location_picture = (ImageView) view.findViewById(R.id.location_picture);
        delete_photo  = (ImageView) view.findViewById(R.id.delete_image);
    }

    public void set(PhotoItem photo) {
        author_name.setText(photo.getDisplayName());
        author_picture.setImageResource(R.drawable.ic_face_black_36dp);

        TimestampItem item = TimestampManager.getTimestampItem(photo.created);
        timestamp.setText(item.getTimeString(true));
    }
}
