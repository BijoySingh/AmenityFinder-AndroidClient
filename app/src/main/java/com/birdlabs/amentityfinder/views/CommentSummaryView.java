package com.birdlabs.amentityfinder.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.birdlabs.amentityfinder.R;
import com.birdlabs.amentityfinder.items.CommentItem;
import com.birdlabs.basicproject.item.TimestampItem;
import com.birdlabs.basicproject.util.TimestampManager;

import org.w3c.dom.Text;

/**
 * The amenity view
 * Created by bijoy on 1/2/16.
 */
public class CommentSummaryView extends LinearLayout {
    public View view;

    public TextView comment;
    public TextView score;
    public TextView timestamp;

    public TextView author_name;
    public TextView useful;

    public View lower_panel;
    public View upper_panel;

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

        comment = (TextView) view.findViewById(R.id.comment);
        score = (TextView) view.findViewById(R.id.score);
        author_name = (TextView) view.findViewById(R.id.author_name);
        timestamp = (TextView) view.findViewById(R.id.timestamp);
        useful = (TextView) view.findViewById(R.id.useful);

        lower_panel = view.findViewById(R.id.lower_panel);
        upper_panel = view.findViewById(R.id.upper_panel);
    }

    public void setPlaceholder() {
        this.comment.setText("No posts yet, be the first one to post!");
        lower_panel.setVisibility(View.GONE);
        upper_panel.setVisibility(View.GONE);
    }



    public void set(CommentItem comment) {
        this.comment.setText(comment.comment);
        score.setText(comment.rating.toString());
        author_name.setText(comment.getDisplayName());
        useful.setText(comment.getHelpfulString());
        TimestampItem item = TimestampManager.getTimestampItem(comment.created);
        timestamp.setText(item.getTimeString(true));
    }
}
