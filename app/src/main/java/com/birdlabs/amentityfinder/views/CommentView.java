package com.birdlabs.amentityfinder.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdlabs.amentityfinder.R;
import com.birdlabs.amentityfinder.items.CommentItem;
import com.birdlabs.basicproject.item.TimestampItem;
import com.birdlabs.basicproject.util.TimestampManager;

import java.util.Calendar;

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

    public TextView upvotes;
    public ImageView upvote_icon;

    public TextView downvotes;
    public ImageView downvote_icon;


    public CommentView(View v) {
        view = v;
        comment = (TextView) view.findViewById(R.id.comment);
        score = (TextView) view.findViewById(R.id.score);
        author_name = (TextView) view.findViewById(R.id.author_name);
        upvotes = (TextView) view.findViewById(R.id.upvotes);
        downvotes = (TextView) view.findViewById(R.id.downvotes);
        timestamp = (TextView) view.findViewById(R.id.timestamp);

        author_picture = (ImageView) view.findViewById(R.id.author_picture);
        upvote_icon = (ImageView) view.findViewById(R.id.like_icon);
        downvote_icon = (ImageView) view.findViewById(R.id.dislike_icon);
        location_picture = (ImageView) view.findViewById(R.id.location_picture);
    }

    public void set(CommentItem comment) {
        this.comment.setText(comment.comment);
        score.setText(comment.rating.toString());
        author_name.setText(comment.author.name);
        upvotes.setText(comment.upvotes.toString());
        downvotes.setText(comment.downvotes.toString());

        author_picture.setImageResource(R.drawable.ic_face_black_36dp);

        TimestampItem item = TimestampManager.getTimestampItem(comment.created);
        timestamp.setText(item.getTimeString(true));


    }
}
