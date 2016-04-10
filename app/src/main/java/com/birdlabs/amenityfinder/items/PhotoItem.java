package com.birdlabs.amenityfinder.items;

import android.net.Uri;
import android.webkit.URLUtil;

import com.birdlabs.amenityfinder.server.Links;
import com.github.bijoysingh.starter.json.JsonField;
import com.github.bijoysingh.starter.json.JsonModel;

import org.json.JSONObject;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * item for comment
 * Created by bijoy on 1/2/16.
 */
public class PhotoItem extends JsonModel {
    @JsonField
    public Integer id;

    @JsonField
    public Integer item;

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

    @JsonField
    public String picture;


    public PhotoItem(JSONObject json) throws Exception {
        super(json);
        author = new UserItem(json.getString("author"));
    }

    public String getLink() {
        if (URLUtil.isHttpUrl(picture) || URLUtil.isHttpsUrl(picture)) {
            return picture;
        } else {
            String url = Links.getHomeUrl() + picture;
            url = url.replace("//media/", "/media/");
            return url;
        }
    }

    public boolean isAnonymous() {
        return (anonymous != null && anonymous) || author == null;
    }

    public String getDisplayName() {
        if (isAnonymous() && author.id == null) {
            return "Anonymous";
        }
        return author.name;
    }
}
