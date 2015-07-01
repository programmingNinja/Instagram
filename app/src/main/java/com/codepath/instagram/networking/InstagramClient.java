package com.codepath.instagram.networking;

import android.content.Context;
import android.util.Log;
import com.codepath.instagram.adapters.SearchResultsAdapter;
import com.codepath.instagram.helpers.Constants;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import org.scribe.builder.api.Api;

public class InstagramClient extends OAuthBaseClient {

    private static final String BASE_URL = "https://api.instagram.com/v1/";
    public static final Class<? extends Api> REST_API_CLASS = InstagramApi.class;
    public static final String REST_CONSUMER_KEY = Constants.CLIENT_ID;
    public static final String REST_CONSUMER_SECRET = Constants.CLIENT_SHARED_SECRET;
    public static final String REST_CALLBACK_URL = Constants.CLIENT_REDIRECT_URL;
    private static final String ENDPOINT_POPULAR_FEED =  "media/popular";
    private static final String ENDPOINT_SELF_FEED =  "users/self/feed";
    private static final String ENDPOINT_SEARCH_TAGS =  "tags/search";
    private static final String ENDPOINT_SEARCH_USERS =  "users/search";
    private static final String ENDPOINT_POST_COMMENT = "media/%s/comments";
    private static final String ENDPOINT_USER_INFO = "users/self";
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private SyncHttpClient syncHttpClient = new SyncHttpClient();

    public InstagramClient(Context context) {
        super(context, REST_API_CLASS, BASE_URL,
                REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    public void getPopularFeed(AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(ENDPOINT_POPULAR_FEED), getDefaultRequestParams(), responseHandler);
    }

    public void getSearchResults(SearchResultsAdapter.SearchType searchType, String searchTerm, JsonHttpResponseHandler responseHandler) {
        String endpoint = searchType == SearchResultsAdapter.SearchType.SEARCH_TAGS ? ENDPOINT_SEARCH_TAGS : ENDPOINT_SEARCH_USERS;
        RequestParams params = getDefaultRequestParams();
        params.put("q", searchTerm);
        client.get(getAbsoluteUrl(endpoint), params, responseHandler);
    }

    public void getPostComments(String mediaId, AsyncHttpResponseHandler responseHandler) {
        String relativeUrl = String.format("media/%s/comments", mediaId);
        client.get(getAbsoluteUrl(relativeUrl), getDefaultRequestParams(), responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        Log.d("url---", BASE_URL + relativeUrl);
        return BASE_URL + relativeUrl;
    }

    private static RequestParams getDefaultRequestParams() {
        return new RequestParams(KEY_CLIENT_ID, Constants.CLIENT_ID);
    }

    public void getTagRecentMedia(String tag, AsyncHttpResponseHandler responseHandler) {
        String relativeUrl = String.format("tags/%s/media/recent", tag);
        client.get(getAbsoluteUrl(relativeUrl), responseHandler);
    }

    public void getUserRecentMedia(String userId, AsyncHttpResponseHandler responseHandler) {
        String relativeUrl = String.format("users/%s/media/recent", userId);
        client.get(getAbsoluteUrl(relativeUrl), responseHandler);
    }

    public void getUserInfo(JsonHttpResponseHandler responseHandler) {
        syncHttpClient.get(getAbsoluteUrl(ENDPOINT_USER_INFO),  getAccessTokenParams(), responseHandler);
    }

    public void getUserFeedSynchronously(AsyncHttpResponseHandler responseHandler) {
        syncHttpClient.get(getAbsoluteUrl(ENDPOINT_SELF_FEED), getAccessTokenParams(), responseHandler);
    }

    public RequestParams getAccessTokenParams() {
        return new RequestParams(KEY_ACCESS_TOKEN, client.getAccessToken().getToken());
    }

    public void postNewUserComment(String postMediaId, String commentText, JsonHttpResponseHandler responseHandler) {
        String relativeUrl = String.format(ENDPOINT_POST_COMMENT, postMediaId);
        RequestParams params = new RequestParams("text", commentText);
        client.post(getAbsoluteUrl(relativeUrl), params, responseHandler);
    }
}