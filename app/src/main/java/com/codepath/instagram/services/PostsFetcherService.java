package com.codepath.instagram.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPost;
import com.codepath.instagram.models.InstagramPosts;
import com.codepath.instagram.persistence.InstagramClientDatabase;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.List;

public class PostsFetcherService extends IntentService {
  private static final String TAG = "PostsFetcherService";

  public static final String EXTRA_RESULT_CODE = "resultCode";
  public static final String EXTRA_RESULT_POSTS = "posts";

  public static final String ACTION = "com.codepath.instagram.services.PostsFetcherService";
  private InstagramClientDatabase database;

  public PostsFetcherService() {
    super("PostsFetcherService");
    database = InstagramClientDatabase.getInstance(this);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    final Intent resultIntent = new Intent(ACTION);
    MainApplication.getRestClient().getUserFeedSynchronously(new JsonHttpResponseHandler() {

      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        super.onSuccess(statusCode, headers, response);

        List<InstagramPost> posts = Utils.decodePostsFromJsonResponse(response);

        InstagramPosts postsWrapper = new InstagramPosts();
        postsWrapper.posts = posts;

        database.emptyAllTables();
        database.addInstagramPosts(posts);

        resultIntent.putExtra(EXTRA_RESULT_CODE, Activity.RESULT_OK);
        resultIntent.putExtra(EXTRA_RESULT_POSTS, postsWrapper);
        LocalBroadcastManager.getInstance(PostsFetcherService.this).sendBroadcast(resultIntent);
      }

      @Override
      public void onFailure(
              int statusCode,
              Header[] headers,
              Throwable throwable,
              JSONObject errorResponse) {
        Log.d(TAG, "Failure on request");
        resultIntent.putExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED);
        LocalBroadcastManager.getInstance(PostsFetcherService.this).sendBroadcast(resultIntent);
      }
    });
  }
}
