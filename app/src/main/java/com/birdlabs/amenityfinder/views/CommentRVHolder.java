package com.birdlabs.amenityfinder.views;

import android.content.Context;
import android.view.View;

import com.birdlabs.amenityfinder.activity.CommentsActivity;
import com.birdlabs.amenityfinder.items.CommentItem;
import com.birdlabs.amenityfinder.server.Access;
import com.birdlabs.amenityfinder.server.AccessIds;
import com.birdlabs.amenityfinder.server.Links;
import com.github.bijoysingh.starter.recyclerview.RVHolder;
import com.github.bijoysingh.starter.server.AccessItem;
import com.github.bijoysingh.starter.util.ImageLoaderManager;

import java.util.HashMap;

/**
 * The Recycler View Holder for the comments
 * Created by bijoy on 1/2/16.
 */
public class CommentRVHolder extends RVHolder<CommentItem> {

    CommentView commentView;

    public CommentRVHolder(Context context, View itemView) {
        super(context, itemView);
        commentView = new CommentView(itemView);
    }

    public void populate(final CommentItem data,
                         final Access access,
                         final ImageLoaderManager loader,
                         final CommentsActivity activity) {
        super.populate(data);
        commentView.set(data);
        commentView.downvote_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                access.send(new AccessItem(Links.getPostDownvote(data.id),
                        null, AccessIds.POST_DOWNVOTE, true).setActivity(activity),
                    new HashMap<String, Object>());
            }
        });

        commentView.upvote_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                access.send(new AccessItem(Links.getPostUpvote(data.id),
                        null, AccessIds.POST_UPVOTE, true).setActivity(activity),
                    new HashMap<String, Object>());
            }
        });

        commentView.flag_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                access.send(new AccessItem(Links.getPostFlag(data.id),
                        null, AccessIds.POST_FLAG, true).setActivity(activity),
                    new HashMap<String, Object>());
            }
        });

        if (!data.anonymous && !data.author.picture.isEmpty()
            && !data.author.picture.contentEquals("null")) {
            loader.displayImage(data.author.picture, commentView.author_picture);
        }
    }
}
