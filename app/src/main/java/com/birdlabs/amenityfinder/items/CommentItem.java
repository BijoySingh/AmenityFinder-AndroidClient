package com.birdlabs.amenityfinder.items;

import com.github.bijoysingh.starter.json.JsonField;
import com.github.bijoysingh.starter.json.JsonModel;

import org.json.JSONObject;

/**
 * item for comment
 * Created by bijoy on 1/2/16.
 */
public class CommentItem extends JsonModel {

    @JsonField
    public Integer id;

    @JsonField
    public String description;

    @JsonField(field = "is_anonymous")
    public Boolean anonymous;

    public UserItem author;

    @JsonField
    public Integer upvotes;

    @JsonField
    public Integer downvotes;

    @JsonField
    public Integer flags;

    @JsonField
    public String timestamp;

    public CommentItem(JSONObject json) throws Exception {
        super(json);
        author = new UserItem(json.getString("author"));
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
