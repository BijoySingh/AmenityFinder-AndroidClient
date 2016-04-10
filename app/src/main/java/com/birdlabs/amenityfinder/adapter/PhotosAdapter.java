package com.birdlabs.amenityfinder.adapter;

import android.content.Context;

import com.birdlabs.amenityfinder.R;
import com.birdlabs.amenityfinder.activity.GalleryActivity;
import com.birdlabs.amenityfinder.items.PhotoItem;
import com.birdlabs.amenityfinder.views.PhotoViewHolder;
import com.github.bijoysingh.starter.recyclerview.RVAdapter;
import com.github.bijoysingh.starter.util.ImageLoaderManager;

import java.util.List;

/**
 * The recycler view adapter for list of content
 * Created by bijoy on 8/4/15.
 */
public class PhotosAdapter extends RVAdapter<PhotoItem, PhotoViewHolder> {

    private ImageLoaderManager loader;
    private GalleryActivity activity;

    public PhotosAdapter(Context context, GalleryActivity activity) {
        super(context, R.layout.photo_item, PhotoViewHolder.class);
        this.activity = activity;
        loader = new ImageLoaderManager(context);
    }

    @Override
    public List<PhotoItem> getValues() {
        return activity.photos;
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, final int position) {

        final PhotoItem data = getValues().get(position);
        holder.populate(data, loader, activity);
    }

    @Override
    public int getItemCount() {
        return activity.getValues().size();
    }

}
