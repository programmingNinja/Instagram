package com.codepath.instagram.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.codepath.instagram.R;
import com.codepath.instagram.adapters.InstagramPostsAdapter;
import com.codepath.instagram.helpers.DividerItemDecoration;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPost;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends BaseFragment {

    private static final String TAG = PostsFragment.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private List<InstagramPost> mInstagramPostsList;
    private InstagramPostsAdapter mInstagramPostsAdapter;

    public static PostsFragment newInstance() {
        return new PostsFragment();
    }

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        initUi(view);
        fetchUserFeed();

        return view;
    }

    private void initUi(View v) {
        //Get the handle to the recycler view
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swpContainer);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchUserFeed();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //Initiate the list and adapter
        mInstagramPostsList = new ArrayList<>();
        mInstagramPostsAdapter = new InstagramPostsAdapter(mInstagramPostsList, mContext);

        configureRecyclerView();
    }

    private void configureRecyclerView() {

        // Setup layout manager for items
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);

        // Control orientation of the items
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //Customize the position you want to default scroll to
        layoutManager.scrollToPosition(0);

        // Attach layout manager to the RecyclerView
        mRecyclerView.setLayoutManager(layoutManager);

        // allows for optimizations if all item views are of the same size:
        mRecyclerView.setHasFixedSize(true);

        // Reference : https://gist.githubusercontent.com/alexfu/0f464fc3742f134ccd1e/raw/abe729359e5b3691f2fe56445644baf0e40b35ba/DividerItemDecoration.java
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        // RecyclerView uses this by default. You can add custom animations by using RecyclerView.ItemAnimator()
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        bindDataToAdapter();

    }

    private void bindDataToAdapter() {
        // Bind adapter to recycler view object
        mRecyclerView.setAdapter(mInstagramPostsAdapter);
    }

    private void fetchUserFeed() {
        if (Utils.isNetworkAvailable(mContext)) {
            showProgressBar(true);
            instagramClient.getSelfFeed(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mInstagramPostsList.clear();
                    mInstagramPostsList.addAll(Utils.decodePostsFromJsonResponse(response));
                    mInstagramPostsAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    showProgressBar(false);
                }

                @Override
                public void onFailure(int statusCode,
                                      Header[] headers,
                                      String responseString,
                                      Throwable throwable) {
                    Log.e(TAG, ">onFailure\n\n" + "Status Code - " + statusCode
                            + "\n\n Failure reason: \n\n" + responseString);
                    handleErrorResult();
                }

                @Override
                public void onFailure(int statusCode,
                                      Header[] headers,
                                      Throwable throwable,
                                      JSONArray errorResponse) {
                    Log.e(TAG, ">onFailure\n\n" + "Status Code - " + statusCode
                            + "\n\n Failure reason: \n\n" + " null JSONArray response");
                    handleErrorResult();
                }

                @Override
                public void onFailure(int statusCode,
                                      Header[] headers,
                                      Throwable throwable,
                                      JSONObject errorResponse) {
                    Log.e(TAG, ">onFailure\n\n" + "Status Code - " + statusCode
                            + "\n\n Failure reason: \n\n" + " null JSONObject response");
                    throwable.printStackTrace();
                    handleErrorResult();
                }
            });
        } else {
            showErrorMsg(getString(R.string.err_no_internet));
        }
    }

    private void handleErrorResult() {
        showErrorMsg();
        swipeRefreshLayout.setRefreshing(false);
    }

    public static final String SAMPLE_JSON_FILE_NAME = "popular.json";

    private void loadSampleJsonObject() {

        JSONObject jsonObject = null;

        try {
            jsonObject = Utils.loadJsonFromAsset(mContext, SAMPLE_JSON_FILE_NAME);
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
}
