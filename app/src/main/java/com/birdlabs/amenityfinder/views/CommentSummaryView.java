package com.birdlabs.amenityfinder.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.birdlabs.amenityfinder.R;
import com.birdlabs.amenityfinder.items.CommentItem;
import com.birdlabs.amenityfinder.util.Functions;
import com.github.bijoysingh.starter.item.TimestampItem;
import com.github.bijoysingh.starter.util.TimestampManager;

/**
 * The amenity view
 * Created by bijoy on 1/2/16.
 */
public class CommentSummaryView extends LinearLayout {

    public View view;
    public TextView score;
    public TextView useful;
    public TextView comment;
    public View lower_panel;
    public View upper_panel;
    public TextView timestamp;
    public TextView author_name;

    public CommentSummaryView(Context context) {
        super(context);
        init(context);
    }

    public CommentSummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommentSummaryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public CommentSummaryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.comment_summary_item, this, true);

        score = (TextView) view.findViewById(R.id.score);
        useful = (TextView) view.findViewById(R.id.useful);
        comment = (TextView) view.findViewById(R.id.comment);
        timestamp = (TextView) view.findViewById(R.id.timestamp);
        author_name = (TextView) view.findViewById(R.id.author_name);

        lower_panel = view.findViewById(R.id.lower_panel);
        upper_panel = view.findViewById(R.id.upper_panel);
    }

    public void setPlaceholder() {
        this.comment.setText(R.string.no_posts_yet);
        lower_panel.setVisibility(View.GONE);
        upper_panel.setVisibility(View.GONE);
    }


    public void set(CommentItem comment) {
        this.comment.setText(comment.description);
        useful.setText(comment.getHelpfulString());
        author_name.setText(comment.getDisplayName());
        // score.setText(Functions.setPrecision(comment.rating, 1));

        TimestampItem item = TimestampManager.getTimestampItem(comment.timestamp);
        timestamp.setText(item.getTimeString(true));
    }
}
