package com.birdlabs.amenityfinder.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdlabs.amenityfinder.R;
import com.birdlabs.amenityfinder.items.CommentItem;
import com.birdlabs.amenityfinder.util.Functions;
import com.github.bijoysingh.starter.item.TimestampItem;
import com.github.bijoysingh.starter.util.LocaleManager;
import com.github.bijoysingh.starter.util.TimestampManager;

/**
 * The amenity view
 * Created by bijoy on 1/2/16.
 */
public class CommentView {

    public View view;
    public TextView flags;
    public TextView score;
    public TextView comment;
    public TextView upvotes;
    public TextView timestamp;
    public TextView downvotes;
    public ImageView flag_icon;
    public TextView author_name;
    public ImageView upvote_icon;
    public ImageView downvote_icon;
    public ImageView author_picture;
    public ImageView location_picture;


    public CommentView(View v) {
        view = v;
        score = (TextView) view.findViewById(R.id.score);
        flags = (TextView) view.findViewById(R.id.flags);
        comment = (TextView) view.findViewById(R.id.comment);
        upvotes = (TextView) view.findViewById(R.id.upvotes);
        downvotes = (TextView) view.findViewById(R.id.downvotes);
        timestamp = (TextView) view.findViewById(R.id.timestamp);
        flag_icon = (ImageView) view.findViewById(R.id.flag_icon);
        upvote_icon = (ImageView) view.findViewById(R.id.like_icon);
        author_name = (TextView) view.findViewById(R.id.author_name);
        downvote_icon = (ImageView) view.findViewById(R.id.dislike_icon);
        author_picture = (ImageView) view.findViewById(R.id.author_picture);
        location_picture = (ImageView) view.findViewById(R.id.location_picture);
    }

    public void set(CommentItem comment) {
        this.comment.setText(comment.description);
        author_name.setText(comment.getDisplayName());
        // score.setText(Functions.setPrecision(comment.rating, 1));
        upvotes.setText(LocaleManager.toString(comment.upvotes));
        downvotes.setText(LocaleManager.toString(comment.downvotes));
        flags.setText(LocaleManager.toString(comment.flags));
        author_picture.setImageResource(R.drawable.face);

        TimestampItem item = TimestampManager.getTimestampItem(comment.timestamp);
        timestamp.setText(item.getTimeString(true));
    }
}
