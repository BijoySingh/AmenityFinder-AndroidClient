package com.birdlabs.amenityfinder.views;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdlabs.amenityfinder.R;
import com.birdlabs.amenityfinder.server.UserActivityItem;
import com.github.bijoysingh.starter.item.TimestampItem;
import com.github.bijoysingh.starter.recyclerview.RVHolder;
import com.github.bijoysingh.starter.util.TimestampManager;

import org.w3c.dom.Text;

/**
 * User Activity View Holder
 * Created by bijoy on 4/9/16.
 */
public class ActivityRVHolder extends RVHolder<UserActivityItem> {

    ImageView actionIcon;
    TextView action;
    TextView location;
    TextView experience;
    TextView timestamp;

    public ActivityRVHolder(Context context, View itemView) {
        super(context, itemView);
        action = (TextView) itemView.findViewById(R.id.action);
        actionIcon = (ImageView) itemView.findViewById(R.id.action_icon);
        location = (TextView) itemView.findViewById(R.id.location);
        experience = (TextView) itemView.findViewById(R.id.experience);
        timestamp = (TextView) itemView.findViewById(R.id.time);
    }

    @Override
    public void populate(UserActivityItem data) {
        super.populate(data);

        if (data.type.equals(UserActivityItem.COMMENT)) {
            action.setText("You added a comment for location");
            actionIcon.setImageResource(R.drawable.ic_comment_white_36dp);
        } else if (data.type.equals(UserActivityItem.PHOTO)) {
            action.setText("You added a photo for location");
            actionIcon.setImageResource(R.drawable.ic_photo_camera_white_24dp);
        } else if (data.type.equals(UserActivityItem.RATING)) {
            action.setText("You added a rating for location");
            actionIcon.setImageResource(R.drawable.ic_star_half_white_36dp);
        } else if (data.type.equals(UserActivityItem.ITEM)) {
            action.setText("You added a location");
            actionIcon.setImageResource(R.drawable.ic_location_searching_white_36dp);
        }

        location.setText(data.title);

        TimestampItem item = TimestampManager.getTimestampItem(data.timestamp);
        timestamp.setText(item.getTimeString(false));

        String xp = "";
        if (data.xp < 0) {
            xp = data.xp + "";
        } else if (data.xp > 0) {
            xp = "+" + data.xp;
        }

        experience.setText(xp);
    }
}
