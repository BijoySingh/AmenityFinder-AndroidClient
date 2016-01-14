package com.birdlabs.amentityfinder.items;

import com.birdlabs.basicproject.util.TimestampManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * item for comment
 * Created by bijoy on 1/2/16.
 */
public class CommentItem {
    public Integer id;
    public String comment;
    public Boolean anonymous;
    public UserItem author;
    public Double rating;
    public Integer upvotes;
    public Integer downvotes;
    public String created;

    public CommentItem(String comment,
                       UserItem author,
                       Double rating,
                       Integer upvotes,
                       Integer downvotes,
                       String created) {
        this.comment = comment;
        this.author = author;
        this.rating = rating;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.created = created;
    }

    public CommentItem(JSONObject json) throws JSONException {
        id = json.getInt("id");
        comment = json.getString("comment");
        rating = json.getDouble("rating");
        anonymous = json.getBoolean("is_anonymous");
        author = new UserItem(json.getString("user"));
        upvotes = json.getInt("upvotes");
        downvotes = json.getInt("downvotes");
        created = json.getString("created");
    }

    public static CommentItem getPlaceholder() {
        return new CommentItem("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. " +
                "Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus " +
                "et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, " +
                "ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis" +
                " enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu.",
                UserItem.getPlaceholder(), 3.5, 3, 0, "1 January, 2016");
    }



    public String getHelpfulString() {
        if (upvotes == 0 && downvotes == 0) {
            return "Be the first one to rate this";
        } else if (upvotes >= downvotes) {
            if (upvotes == 1) {
                return upvotes + " person found this relevant";
            }
            return upvotes + " people found this relevant";
        } else if (upvotes < downvotes) {
            if (downvotes == 1) {
                return downvotes + " person found this not relevant";
            }
            return downvotes + " people found this not relevant";
        }
        return "";
    }

    public String getDisplayName() {
        if (anonymous) {
            return "Anonymous";
        }
        return author.name;
    }
}
