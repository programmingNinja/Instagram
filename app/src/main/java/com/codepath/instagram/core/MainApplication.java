package com.codepath.instagram.core;

import android.app.Application;

import com.codepath.instagram.models.InstagramUser;
import com.codepath.instagram.networking.InstagramClient;
import com.codepath.instagram.persistence.InstagramClientDatabase;
import com.facebook.drawee.backends.pipeline.Fresco;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    private static MainApplication instance;

    private InstagramClientDatabase database;
    private InstagramUser currentUser;

    public static MainApplication sharedApplication() {
        assert(instance != null);
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        Fresco.initialize(this);
        database = new InstagramClientDatabase(this);
    }

    public static InstagramClient getRestClient() {
        return (InstagramClient) InstagramClient.getInstance(InstagramClient.class, sharedApplication());
    }

    public InstagramClientDatabase getDatabase() {
        return database;
    }

    public InstagramUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(InstagramUser user) {
        this.currentUser = user;
    }
}
