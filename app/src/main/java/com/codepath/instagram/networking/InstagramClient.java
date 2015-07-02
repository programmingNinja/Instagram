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
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String ENDPOINT_POPULAR_FEED =  "media/popular";
    private static final String ENDPOINT_SELF_FEED =  "users/self/feed";
    private static final String ENDPOINT_SEARCH_TAGS =  "tags/search";
    private static final String ENDPOINT_SEARCH_USERS =  "users/search";
    private static final String ENDPOINT_POST_COMMENT = "media/%s/comments";
    private static final String ENDPOINT_USER_INFO = "users/%s";
    private static final String ENDPOINT_TAGS_RECENT_MEDIA = "tags/%s/media/recent";
    private static final String ENDPOINT_USERS_RECENT_MEDIA = "users/%s/media/recent";
    private static final String ENDPOINT_POST_LIKE = "media/media-id/likes";
    private static final String ENDPOINT_SELF_USER_LIKED_MEDIA = "users/self/media/liked";
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
        String relativeUrl = String.format(ENDPOINT_POST_COMMENT, mediaId);
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
        String relativeUrl = String.format(ENDPOINT_TAGS_RECENT_MEDIA, tag);
        client.get(getAbsoluteUrl(relativeUrl), responseHandler);
    }

    public void getUserRecentMedia(String userId, AsyncHttpResponseHandler responseHandler) {
        String relativeUrl = String.format(ENDPOINT_USERS_RECENT_MEDIA, userId);
        client.get(getAbsoluteUrl(relativeUrl), responseHandler);
    }

    public void getUserSelfLikedMedia(AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(ENDPOINT_SELF_USER_LIKED_MEDIA), responseHandler);
    }

    public void getUserInfo(JsonHttpResponseHandler responseHandler) {
        String relativeUrl = String.format(ENDPOINT_USER_INFO, "self");
        syncHttpClient.get(getAbsoluteUrl(relativeUrl),  getAccessTokenParams(), responseHandler);
    }

    public void getUserProfile(String userId, JsonHttpResponseHandler responseHandler) {
        String relativeUrl = String.format(ENDPOINT_USER_INFO, userId);
        client.get(getAbsoluteUrl(relativeUrl), responseHandler);
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

    public void postLikeMedia(String mediaId, JsonHttpResponseHandler responseHandler) {
        String relativeUrl = String.format(ENDPOINT_POST_LIKE, mediaId);
        client.post(getAbsoluteUrl(relativeUrl), responseHandler);
    }
}