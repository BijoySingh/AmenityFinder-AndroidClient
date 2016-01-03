package com.birdlabs.amentityfinder.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by bijoy on 1/2/16.
 */
public class CommentRVHolder extends RecyclerView.ViewHolder {
    public CommentView view;


    public CommentRVHolder(View itemView) {
        super(itemView);
        view = new CommentView(itemView);
    }
}
