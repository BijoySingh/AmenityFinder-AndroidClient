package com.birdlabs.amentityfinder.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.birdlabs.amentityfinder.R;
import com.birdlabs.amentityfinder.activity.GalleryActivity;
import com.birdlabs.amentityfinder.items.PhotoItem;
import com.birdlabs.amentityfinder.server.Access;
import com.birdlabs.amentityfinder.server.AccessInfo;
import com.birdlabs.amentityfinder.server.Links;
import com.birdlabs.amentityfinder.util.Preferences;
import com.birdlabs.amentityfinder.views.PhotoViewHolder;
import com.birdlabs.basicproject.Functions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.HashMap;

/**
 * The recycler view adapter for list of content
 * Created by bijoy on 8/4/15.
 */
public class PhotosAdapter extends RecyclerView.Adapter<PhotoViewHolder> {

    private Context context;
    private GalleryActivity activity;
    private Access access;
    private ImageLoader imageLoader;

    public PhotosAdapter(Context context,
                         GalleryActivity activity) {
        this.context = context;
        this.activity = activity;
        access = new Access(context);
        imageLoader = Functions.getImageLoader(context);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);
        return new PhotoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, final int position) {

        final PhotoItem data = activity.photos.get(position);
        holder.set(data);
        ImageAware logoImageAware = new ImageViewAware(holder.location_picture, false);
        imageLoader.displayImage(data.photo, logoImageAware);

        if (!data.anonymous && !data.author.picture.isEmpty() && !data.author.picture.contentEquals("null")) {
            ImageAware faceImageAware = new ImageViewAware(holder.author_picture, false);
            imageLoader.displayImage(data.author.picture, faceImageAware);
        }

        Integer userId = (new Preferences(context)).load(Preferences.Keys.USER_ID, -2);

        if ((!data.anonymous && data.author.id.equals(userId))
                || ( data.author !=null
                && data.author.id != null
                && data.author.id.equals(userId))) {
            holder.delete_photo.setVisibility(View.VISIBLE);
            holder.delete_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Image?")
                            .setMessage("Are you sure you want to delete this image? This action is irreversible.")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Access access = new Access(context);
                                    access.send(new AccessInfo(Links.getPicture(data.id), null,
                                                    AccessInfo.AccessIds.PHOTO_DELETE, true)
                                                    .setActivity(activity)
                                                    .setMethod(Request.Method.DELETE),
                                            new HashMap<String, Object>());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create().show();

                }
            });
        } else {
            holder.delete_photo.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return activity.getValues().size();
    }

}
