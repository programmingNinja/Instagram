package com.codepath.instagram.networking;

import android.util.Log;
import com.codepath.instagram.helpers.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class InstagramClient {

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static final String BASE_URL = "https://api.instagram.com/v1/";
    private static final String ENDPOINT_POPULAR_FEED =  "media/popular";

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