package com.codepath.instagram.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.database.InstagramClientDatabase;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPost;
import com.codepath.instagram.models.InstagramPosts;
import com.codepath.instagram.models.InstagramUser;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONObject;

import java.util.List;

public class BackgroundFeedService extends IntentService {

    private static final String TAG = BackgroundFeedService.class.getSimpleName();
    public static final String ACTION_FETCH_NEW_POSTS = "com.codepath.instagram.services.action.ACTION_FETCH_NEW_POSTS";
    public static final String ACTION_FETCH_USER_INFO = "com.codepath.instagram.services.action.ACTION_FETCH_USER_INFO";
    public static final String EXTRA_RESULT_CODE = "com.codepath.instagram.services.extra.EXTRA_RESULT_CODE";
    public static final String EXTRA_RESULT_POSTS = "com.codepath.instagram.services.extra.EXTRA_RESULT_POSTS";
    private InstagramClientDatabase database;

    public static void startActionFetchNewPosts(Context context) {
        Intent intent = new Intent(context, BackgroundFeedService.class);
        intent.setAction(ACTION_FETCH_NEW_POSTS);
        context.startService(intent);
    }

    public static void startActionGetUserInfo(Context context) {
        Intent intent = new Intent(context, BackgroundFeedService.class);
        intent.setAction(ACTION_FETCH_USER_INFO);
        context.startService(intent);
    }

    public BackgroundFeedService() {
        super(TAG);
        database = MainApplication.sharedApplication().getDatabase();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_NEW_POSTS.equals(action)) {
                handleActionGetNewPosts(new Intent(action));
            } else if (ACTION_FETCH_USER_INFO.equals(action)) {
                handleActionGetUserInfo();
            }
        }
    }

    private void handleActionGetUserInfo() {
        if (Utils.isNetworkAvailable(getApplicationContext())) {
            MainApplication.getRestClient().getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONObject jsonUser = response.optJSONObject("data");
                    InstagramUser user = InstagramUser.fromJson(jsonUser);
                    if (user != null) {
                        MainApplication.sharedApplication().setCurrentUser(user);
                        Log.d(TAG, "Successfully saved user info - " + user.fullName + " @" + user.userName);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(TAG, "Error retrieving user info");
                }
            });
        }
    }

    private void handleActionGetNewPosts(final Intent resultIntent) {
        if (Utils.isNetworkAvailable(getApplicationContext())) {
            MainApplication.getRestClient().getUserFeedSynchronously(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    List<InstagramPost> posts = Utils.decodePostsFromJsonResponse(response);
                    database.replacePosts(posts);
                    resultIntent.putExtra(EXTRA_RESULT_CODE, Activity.RESULT_OK);
                    resultIntent.putExtra(EXTRA_RESULT_POSTS, new InstagramPosts(posts));
                    LocalBroadcastManager.getInstance(BackgroundFeedService.this).sendBroadcast(resultIntent);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(TAG, "Error retrieving user feed");
                    resultIntent.putExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED);
                    LocalBroadcastManager.getInstance(BackgroundFeedService.this).sendBroadcast(resultIntent);
                }
            });
        }
    }
}
