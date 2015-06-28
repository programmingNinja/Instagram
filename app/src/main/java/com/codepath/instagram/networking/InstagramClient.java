package com.codepath.instagram.networking;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;

public class InstagramClient extends OAuthBaseClient {
    private static final String REST_URL = "https://api.instagram.com/v1/";
    //private static final String CLIENT_ID = "e05c462ebd86446ea48a5af73769b602";

    public static final Class<? extends Api> REST_API_CLASS = InstagramApi.class;
    public static final String REST_CONSUMER_KEY = "e05c462ebd86446ea48a5af73769b602";
    public static final String REST_CONSUMER_SECRET = "7f18a14de6c241c2a9ccc9f4a3df4b35";
    public static final String REDIRECT_URI = "oauth://codepath.com";

    public static InstagramClient instagramClient;

    public InstagramClient(Context context) {
        super(context, REST_API_CLASS, REST_URL,
                REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REDIRECT_URI);
    }

    public void getPopularPosts(AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl("media/popular"), getDefaultRequestParams(), responseHandler);
    }

    public void getPostComments(String mediaId, AsyncHttpResponseHandler responseHandler) {
        String relativeUrl = String.format("media/%s/comments", mediaId);
        client.get(getAbsoluteUrl(relativeUrl), getDefaultRequestParams(), responseHandler);
    }

    public void getUserFeed(AsyncHttpResponseHandler responseHandler) {
        String relativeUrl = "users/self/feed";
        client.get(getAbsoluteUrl(relativeUrl), responseHandler);
    }

    public void getUserSearch(String searchTerm, AsyncHttpResponseHandler responseHandler) {
        String relativeUrl = "users/search";
        RequestParams params = getDefaultRequestParams();
        params.put("q", searchTerm);
        client.get(getAbsoluteUrl(relativeUrl), params, responseHandler);
    }

    public void getTagSearch(String searchTerm, AsyncHttpResponseHandler responseHandler) {
        String relativeUrl = "tags/search";
        RequestParams params = getDefaultRequestParams();
        params.put("q", searchTerm);
        client.get(getAbsoluteUrl(relativeUrl), params, responseHandler);
    }

    public void getTagRecentMedia(String tag, AsyncHttpResponseHandler responseHandler) {
        String relativeUrl = String.format("tags/%s/media/recent", tag);
        client.get(getAbsoluteUrl(relativeUrl), responseHandler);
    }

    public void getUserRecentMedia(String userId, AsyncHttpResponseHandler responseHandler) {
        String relativeUrl = String.format("users/%s/media/recent", userId);
        client.get(getAbsoluteUrl(relativeUrl), responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return REST_URL + relativeUrl;
    }

    private static RequestParams getDefaultRequestParams() {
        RequestParams params = new RequestParams();
        params.put("client_id", REST_CONSUMER_KEY);
        return params;
    }
}
