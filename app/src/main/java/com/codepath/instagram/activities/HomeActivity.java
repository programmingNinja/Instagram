package com.codepath.instagram.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.codepath.instagram.R;
import com.codepath.instagram.adapters.InstagramPostsAdapter;
import com.codepath.instagram.helpers.DividerItemDecoration;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPost;
import com.codepath.instagram.networking.InstagramClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends BaseActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    public static final String SAMPLE_JSON_FILE_NAME = "popular.json";

    private Context mContext;
    private RecyclerView mRecyclerView;
    private List<InstagramPost> mInstagramPostsList;
    private InstagramPostsAdapter mInstagramPostsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.activity_home);

        //Get Handle to your UI elements
        initUi();

        // Use this if you want to load sample JSON Data from popular.json file
        // loadSampleJsonObject();

        //Load the actual popular feed endpoint by using the InstagramClient.
        fetchPopularFeed();
    }

    private void fetchPopularFeed() {
        if (Utils.isNetworkAvailable(this.getApplicationContext())) {
            InstagramClient.getPopularFeed(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mInstagramPostsList.addAll(Utils.decodePostsFromJsonResponse(response));
                    mInstagramPostsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(int statusCode,
                                      Header[] headers,
                                      String responseString,
                                      Throwable throwable) {
                    Log.e(TAG, ">onFailure\n\n" + "Status Code - " + statusCode
                            + "\n\n Failure reason: \n\n" + responseString);
                    showErrorMsg();
                }

                @Override
                public void onFailure(int statusCode,
                                      Header[] headers,
                                      Throwable throwable,
                                      JSONArray errorResponse) {
                    Log.e(TAG, ">onFailure\n\n" + "Status Code - " + statusCode
                            + "\n\n Failure reason: \n\n" + " null JSONArray response");
                    showErrorMsg();
                }

                @Override
                public void onFailure(int statusCode,
                                      Header[] headers,
                                      Throwable throwable,
                                      JSONObject errorResponse) {
                    Log.e(TAG, ">onFailure\n\n" + "Status Code - " + statusCode
                            + "\n\n Failure reason: \n\n" + " null JSONObject response");
                    showErrorMsg();
                }
            });
        } else {
            showErrorMsg(getString(R.string.err_no_internet));
        }
    }

    private void loadSampleJsonObject() {

        JSONObject jsonObject = null;

        try {
            jsonObject = Utils.loadJsonFromAsset(this, SAMPLE_JSON_FILE_NAME);
            Log.d(TAG, jsonObject.toString());
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error loading sample file - " + e.getMessage());
            e.printStackTrace();
        }

        if (jsonObject != null) {
            decodeJsonToModel(jsonObject);
        }
    }

    private void decodeJsonToModel(JSONObject jsonObject) {
        mInstagramPostsList = Utils.decodePostsFromJsonResponse(jsonObject);
        Log.d(TAG, "Loaded " + mInstagramPostsList.size() + " sample posts.");
    }

    private void initUi() {
        //Get the handle to the recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //Initiate the list and adapter
        mInstagramPostsList = new ArrayList<>();
        mInstagramPostsAdapter = new InstagramPostsAdapter(mInstagramPostsList, mContext);

        configureRecyclerView();
    }

    private void configureRecyclerView() {

        // Setup layout manager for items
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        // Control orientation of the items
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //Customize the position you want to default scroll to
        layoutManager.scrollToPosition(0);

        // Attach layout manager to the RecyclerView
        mRecyclerView.setLayoutManager(layoutManager);

        // allows for optimizations if all item views are of the same size:
        mRecyclerView.setHasFixedSize(true);

        // Reference : https://gist.githubusercontent.com/alexfu/0f464fc3742f134ccd1e/raw/abe729359e5b3691f2fe56445644baf0e40b35ba/DividerItemDecoration.java
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        // RecyclerView uses this by default. You can add custom animations by using RecyclerView.ItemAnimator()
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        bindDataToAdapter();

    }

    private void bindDataToAdapter() {
        // Bind adapter to recycler view object
        mRecyclerView.setAdapter(mInstagramPostsAdapter);
    }
}
