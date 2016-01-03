package com.birdlabs.amentityfinder.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.birdlabs.amentityfinder.R;
import com.birdlabs.amentityfinder.activity.LocationActivity;
import com.birdlabs.amentityfinder.items.CommentItem;
import com.birdlabs.amentityfinder.views.CommentRVHolder;

/**
 * The recycler view adapter for list of content
 * Created by bijoy on 8/4/15.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentRVHolder> {

    private Context context;
    private LocationActivity activity;

    public CommentsAdapter(Context context,
                              LocationActivity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    public CommentRVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);
        return new CommentRVHolder(v);
    }

    @Override
    public void onBindViewHolder(CommentRVHolder holder, final int position) {

        final CommentItem data = activity.getValues().get(position);
        holder.view.set(data);
    }

    @Override
    public int getItemCount() {
        return activity.getValues().size();
    }

}
