package com.birdlabs.amenityfinder.adapter;

import com.birdlabs.amenityfinder.R;
import com.birdlabs.amenityfinder.activity.CommentsActivity;
import com.birdlabs.amenityfinder.items.CommentItem;
import com.birdlabs.amenityfinder.server.Access;
import com.birdlabs.amenityfinder.views.CommentRVHolder;
import com.github.bijoysingh.starter.recyclerview.RVAdapter;
import com.github.bijoysingh.starter.util.ImageLoaderManager;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * The recycler view adapter for list of content
 * Created by bijoy on 8/4/15.
 */
public class CommentsAdapter extends RVAdapter<CommentItem, CommentRVHolder> {

    private Access access;
    private ImageLoaderManager loader;
    private CommentsActivity activity;

    public CommentsAdapter(CommentsActivity activity) {
        super(activity, R.layout.comment_item, CommentRVHolder.class);
        this.activity = activity;
        access = new Access(context);
        loader = new ImageLoaderManager(context);
    }

    @Override
    public List<CommentItem> getValues() {
        return activity.getValues();
    }

    @Override
    public void onBindViewHolder(CommentRVHolder holder, final int position) {
        final CommentItem data = getValues().get(position);
        holder.populate(data, access, loader, activity);
    }
}
