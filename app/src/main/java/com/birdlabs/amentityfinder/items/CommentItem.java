package com.birdlabs.amentityfinder.items;

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
    public Integer likes;
    public Integer dislikes;
    public String created;

    public CommentItem(String comment,
                       UserItem author,
                       Double rating,
                       Integer likes,
                       Integer dislikes,
                       String created) {
        this.comment = comment;
        this.author = author;
        this.rating = rating;
        this.likes = likes;
        this.dislikes = dislikes;
        this.created = created;
    }

    public CommentItem(JSONObject json) throws JSONException {
        id = json.getInt("id");
        comment = json.getString("comment");
        rating = json.getDouble("rating");
        anonymous = json.getBoolean("is_anonymous");
        author = new UserItem(json.getString("user"));
        likes = json.getInt("upvotes_count");
        dislikes = json.getInt("downvotes_count");
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
}
