package com.birdlabs.amentityfinder.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdlabs.amentityfinder.R;
import com.birdlabs.amentityfinder.items.UserItem;

/**
 * The amenity view
 * Created by bijoy on 1/2/16.
 */
public class CommentView {
    public View view;

    public ImageView location_picture;
    public TextView comment;
    public TextView score;
    public TextView timestamp;

    public TextView author_name;
    public ImageView author_picture;

    public TextView likes;
    public ImageView like_icon;

    public TextView dislikes;
    public ImageView dislike_icon;


    public CommentView(View v) {
        view = v;
        comment = (TextView) view.findViewById(R.id.comment);
        score = (TextView) view.findViewById(R.id.score);
        author_name = (TextView) view.findViewById(R.id.author_name);
        likes = (TextView) view.findViewById(R.id.likes);
        dislikes = (TextView) view.findViewById(R.id.dislikes);
        timestamp = (TextView) view.findViewById(R.id.timestamp);

        author_picture = (ImageView) view.findViewById(R.id.author_picture);
        like_icon = (ImageView) view.findViewById(R.id.like_icon);
        dislike_icon = (ImageView) view.findViewById(R.id.dislike_icon);
        location_picture = (ImageView) view.findViewById(R.id.location_picture);
    }
}
