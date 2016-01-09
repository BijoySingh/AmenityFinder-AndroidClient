package com.birdlabs.amentityfinder.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.birdlabs.amentityfinder.R;
import com.birdlabs.amentityfinder.activity.CommentsActivity;
import com.birdlabs.amentityfinder.items.CommentItem;
import com.birdlabs.amentityfinder.server.Access;
import com.birdlabs.amentityfinder.server.AccessInfo;
import com.birdlabs.amentityfinder.server.Links;
import com.birdlabs.amentityfinder.views.CommentRVHolder;
import com.birdlabs.basicproject.Functions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.HashMap;

/**
 * The recycler view adapter for list of content
 * Created by bijoy on 8/4/15.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentRVHolder> {

    private Context context;
    private CommentsActivity activity;
    private Access access;
    private ImageLoader imageLoader;

    public CommentsAdapter(Context context,
                           CommentsActivity activity) {
        this.context = context;
        this.activity = activity;
        access = new Access(context);
        imageLoader = Functions.getImageLoader(context);
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
        holder.view.downvote_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                access.send(new AccessInfo(Links.getPostDownvote(data.id),
                                null, AccessInfo.AccessIds.POST_DOWNVOTE, true).setActivity(activity),
                        new HashMap<String, Object>());
            }
        });
        holder.view.upvote_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                access.send(new AccessInfo(Links.getPostUpvote(data.id),
                                null, AccessInfo.AccessIds.POST_UPVOTE, true).setActivity(activity),
                        new HashMap<String, Object>());
            }
        });

        if (!data.anonymous && !data.author.picture.isEmpty() && !data.author.picture.contentEquals("null")) {
            ImageAware faceImageAware = new ImageViewAware(holder.view.author_picture, false);
            imageLoader.displayImage(data.author.picture, faceImageAware);
        }
    }

    @Override
    public int getItemCount() {
        return activity.getValues().size();
    }

}
