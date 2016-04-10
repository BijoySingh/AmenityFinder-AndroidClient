package com.birdlabs.amenityfinder.views;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.birdlabs.amenityfinder.R;
import com.birdlabs.amenityfinder.activity.GalleryActivity;
import com.birdlabs.amenityfinder.items.PhotoItem;
import com.birdlabs.amenityfinder.server.Access;
import com.birdlabs.amenityfinder.server.AccessIds;
import com.birdlabs.amenityfinder.server.Links;
import com.birdlabs.amenityfinder.util.Preferences;
import com.github.bijoysingh.starter.item.TimestampItem;
import com.github.bijoysingh.starter.recyclerview.RVHolder;
import com.github.bijoysingh.starter.server.AccessItem;
import com.github.bijoysingh.starter.util.ImageLoaderManager;
import com.github.bijoysingh.starter.util.LocaleManager;
import com.github.bijoysingh.starter.util.TimestampManager;

import java.util.HashMap;

/**
 * The amenity view
 * Created by bijoy on 1/2/16.
 */
public class PhotoViewHolder extends RVHolder<PhotoItem> {
    public TextView timestamp;
    public TextView author_name;
    public ImageView delete_photo;
    public ImageView author_picture;
    public ImageView location_picture;

    public TextView flags;
    public TextView upvotes;
    public TextView downvotes;
    public ImageView flag_icon;
    public ImageView upvote_icon;
    public ImageView downvote_icon;

    public PhotoViewHolder(Context context, View view) {
        super(context, view);
        timestamp = (TextView) view.findViewById(R.id.timestamp);
        author_name = (TextView) view.findViewById(R.id.author_name);
        delete_photo = (ImageView) view.findViewById(R.id.delete_image);
        author_picture = (ImageView) view.findViewById(R.id.author_picture);
        location_picture = (ImageView) view.findViewById(R.id.location_picture);

        flags = (TextView) view.findViewById(R.id.flags);
        upvotes = (TextView) view.findViewById(R.id.upvotes);
        downvotes = (TextView) view.findViewById(R.id.downvotes);
        flag_icon = (ImageView) view.findViewById(R.id.flag_icon);
        upvote_icon = (ImageView) view.findViewById(R.id.like_icon);
        downvote_icon = (ImageView) view.findViewById(R.id.dislike_icon);
    }

    public void set(PhotoItem photo) {
        author_name.setText(photo.getDisplayName());
        author_picture.setImageResource(R.drawable.face);
        flags.setText(LocaleManager.toString(photo.flags));
        upvotes.setText(LocaleManager.toString(photo.upvotes));
        downvotes.setText(LocaleManager.toString(photo.downvotes));

        TimestampItem item = TimestampManager.getTimestampItem(photo.timestamp);
        if (photo.isAnonymous() && photo.author.id != null) {
            timestamp.setText(item.getTimeString(true) + " (Posted anonymously)");
        } else {
            timestamp.setText(item.getTimeString(true));
        }

    }

    public void populate(final PhotoItem data,
                         final ImageLoaderManager loader,
                         final GalleryActivity activity) {
        super.populate(data);

        set(data);
        loader.displayImage(data.getLink(), location_picture);

        final Access access = new Access(context);
        downvote_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                access.send(new AccessItem(Links.getPhotoDownvote(data.id),
                        null, AccessIds.PHOTO_DOWNVOTE, true).setActivity(activity),
                    new HashMap<String, Object>());
            }
        });

        upvote_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                access.send(new AccessItem(Links.getPhotoUpvote(data.id),
                        null, AccessIds.PHOTO_UPVOTE, true).setActivity(activity),
                    new HashMap<String, Object>());
            }
        });

        flag_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                access.send(new AccessItem(Links.getPhotoFlag(data.id),
                        null, AccessIds.PHOTO_FLAG, true).setActivity(activity),
                    new HashMap<String, Object>());
            }
        });

        if (!data.isAnonymous()
            && !data.author.picture.isEmpty()
            && !data.author.picture.contentEquals("null")) {
            loader.displayImage(data.author.picture, author_picture);
        }

        Integer userId = (new Preferences(context)).load(Preferences.Keys.USER_ID, -2);

        if ((!data.isAnonymous() && data.author.id.equals(userId))
            || (data.author != null
            && data.author.id != null
            && data.author.id.equals(userId))) {
            delete_photo.setVisibility(View.VISIBLE);
            delete_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createDeletePhotoDialog(data, activity);
                }
            });
        } else {
            delete_photo.setVisibility(View.GONE);
        }
    }

    public void createDeletePhotoDialog(final PhotoItem data, final GalleryActivity activity) {
        new AlertDialog.Builder(context)
            .setTitle(R.string.delete_image_question)
            .setMessage(R.string.delete_image_detailed_qn)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Access access = new Access(context);
                    access.send(new AccessItem(Links.getPicture(data.id), null,
                            AccessIds.PHOTO_DELETE, true)
                            .setActivity(activity)
                            .setMethod(Request.Method.DELETE),
                        new HashMap<String, Object>());
                }
            })
            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            })
            .create()
            .show();
    }
}
