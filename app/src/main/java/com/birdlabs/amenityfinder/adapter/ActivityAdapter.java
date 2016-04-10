package com.birdlabs.amenityfinder.adapter;

import android.content.Context;

import com.birdlabs.amenityfinder.R;
import com.birdlabs.amenityfinder.activity.UserActivity;
import com.birdlabs.amenityfinder.server.UserActivityItem;
import com.birdlabs.amenityfinder.views.ActivityRVHolder;
import com.github.bijoysingh.starter.recyclerview.RVAdapter;

import java.util.List;

/**
 * Adapter for user activity
 * Created by bijoy on 4/9/16.
 */
public class ActivityAdapter extends RVAdapter<UserActivityItem, ActivityRVHolder> {

    UserActivity activity;

    public ActivityAdapter(Context context, UserActivity activity) {
        super(context, R.layout.user_activity_item, ActivityRVHolder.class);
        this.activity = activity;
    }

    @Override
    public List<UserActivityItem> getValues() {
        return activity.getValues();
    }
}
