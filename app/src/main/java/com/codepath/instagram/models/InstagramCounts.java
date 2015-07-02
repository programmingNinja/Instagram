package com.codepath.instagram.models;

import org.json.JSONObject;

import java.io.Serializable;

public class InstagramCounts implements Serializable {

    public int media, follows, followed_by;

    public InstagramCounts() {
    }

    public static InstagramCounts fromJson(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        InstagramCounts counts = new InstagramCounts();
        counts.media = jsonObject.optInt("media", 0);
        counts.follows = jsonObject.optInt("follows", 0);
        counts.followed_by = jsonObject.optInt("followed_by", 0);
        return counts;
    }

}
