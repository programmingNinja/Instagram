package com.codepath.instagram.core;

import android.app.Application;
import android.content.Context;
import com.codepath.instagram.database.InstagramClientDatabase;
import com.codepath.instagram.models.InstagramUser;
import com.codepath.instagram.networking.InstagramClient;
import com.facebook.drawee.backends.pipeline.Fresco;

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getSimpleName();
    private static MainApplication instance;
    private static Context context;
    private InstagramClientDatabase database;
    private InstagramUser currentUser;

    public static MainApplication sharedApplication() {
        assert(instance != null);
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        MainApplication.context = this;
        Fresco.initialize(this);
        super.onCreate();
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
