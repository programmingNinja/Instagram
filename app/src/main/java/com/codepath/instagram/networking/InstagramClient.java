package com.codepath.instagram.networking;

import android.content.Context;
import android.util.Log;
import com.codepath.instagram.helpers.Constants;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.scribe.builder.api.Api;

public class InstagramClient extends OAuthBaseClient {

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static final String BASE_URL = "https://api.instagram.com/v1/";
    private static final String ENDPOINT_POPULAR_FEED =  "media/popular";
    private static final String ENDPOINT_SELF_FEED =  "users/self/feed";
    public static final Class<? extends Api> REST_API_CLASS = InstagramApi.class;
    public static final String REST_CONSUMER_KEY = Constants.CLIENT_ID;
    public static final String REST_CONSUMER_SECRET = Constants.CLIENT_SHARED_SECRET;
    public static final String REST_CALLBACK_URL = Constants.CLIENT_REDIRECT_URL;

    public InstagramClient(Context context) {
        super(context, REST_API_CLASS, BASE_URL,
                REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    public static void getSelfFeed(AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(ENDPOINT_SELF_FEED), responseHandler);
    }

    public void getUserFeed(AsyncHttpResponseHandler responseHandler) {
        String relativeUrl = "users/self/feed";
        client.get(getAbsoluteUrl(relativeUrl), responseHandler);
    }

    public static void getPopularFeed(AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(ENDPOINT_POPULAR_FEED), getDefaultRequestParams(), responseHandler);
    }

    public static void getPostComments(String mediaId, AsyncHttpResponseHandler responseHandler) {
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
}