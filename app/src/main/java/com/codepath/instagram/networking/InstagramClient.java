package com.codepath.instagram.networking;

import android.content.Context;
import android.util.Log;
import com.codepath.instagram.adapters.SearchResultsAdapter;
import com.codepath.instagram.helpers.Constants;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.scribe.builder.api.Api;

public class InstagramClient extends OAuthBaseClient {

    private static final String BASE_URL = "https://api.instagram.com/v1/";
    private static final String ENDPOINT_POPULAR_FEED =  "media/popular";
    private static final String ENDPOINT_SELF_FEED =  "users/self/feed";
    private static final String ENDPOINT_SEARCH_TAGS =  "tags/search";
    private static final String ENDPOINT_SEARCH_USERS =  "users/search";
    public static final Class<? extends Api> REST_API_CLASS = InstagramApi.class;
    public static final String REST_CONSUMER_KEY = Constants.CLIENT_ID;
    public static final String REST_CONSUMER_SECRET = Constants.CLIENT_SHARED_SECRET;
    public static final String REST_CALLBACK_URL = Constants.CLIENT_REDIRECT_URL;

    public InstagramClient(Context context) {
        super(context, REST_API_CLASS, BASE_URL,
                REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    public void getSelfFeed(AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(ENDPOINT_SELF_FEED), responseHandler);
    }

    public void getUserFeed(AsyncHttpResponseHandler responseHandler) {
        String relativeUrl = "users/self/feed";
        RequestParams params = new RequestParams("access_token", client.getAccessToken().getToken());
        client.get(getAbsoluteUrl(relativeUrl), params, responseHandler);
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
        RequestParams params = new RequestParams();
        params.put("client_id", Constants.CLIENT_ID);
        return params;
    }

    public void getTagRecentMedia(String tag, AsyncHttpResponseHandler responseHandler) {
        String relativeUrl = String.format("tags/%s/media/recent", tag);
        client.get(getAbsoluteUrl(relativeUrl), responseHandler);
    }

    public void getUserRecentMedia(String userId, AsyncHttpResponseHandler responseHandler) {
        String relativeUrl = String.format("users/%s/media/recent", userId);
        client.get(getAbsoluteUrl(relativeUrl), responseHandler);
    }
}