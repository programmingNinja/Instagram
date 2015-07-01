package com.codepath.instagram.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.database.InstagramClientDatabase;
import com.codepath.instagram.helpers.DividerItemDecoration;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPost;
import com.codepath.instagram.models.InstagramPosts;
import com.codepath.instagram.services.BackgroundFeedService;
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
    private InstagramClientDatabase database;

    public static PostsFragment newInstance() {
        return new PostsFragment();
    }

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        database = MainApplication.sharedApplication().getDatabase();
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        initUi(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        IntentFilter filter = new IntentFilter(BackgroundFeedService.ACTION_FETCH_NEW_POSTS);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(postsLocalBroadcastRcvr, filter);
        fetchUserFeed();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(postsLocalBroadcastRcvr);
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
            startPostService();
        } else {
            showErrorMsg(getString(R.string.err_no_internet));
            handlePostsReceived(database.getAllInstagramPosts());
        }
    }

    private void startPostService() {
        BackgroundFeedService.startActionFetchNewPosts(getActivity());
    }

    private void handleErrorResult() {
        showErrorMsg();
        swipeRefreshLayout.setRefreshing(false);
    }

    private BroadcastReceiver postsLocalBroadcastRcvr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                int resultCode = intent.getIntExtra(BackgroundFeedService.EXTRA_RESULT_CODE, Activity.RESULT_CANCELED);
                showProgressBar(false);
                if (resultCode == Activity.RESULT_OK) {
                    InstagramPosts postsWrapper = (InstagramPosts) intent.getSerializableExtra(BackgroundFeedService.EXTRA_RESULT_POSTS);
                    if (postsWrapper.posts != null && postsWrapper.posts.size() > 0) {
                        handlePostsReceived(postsWrapper.posts);
                    }
                } else {
                    handleErrorResult();
                }
            }
        }
    };

    private void handlePostsReceived(List<InstagramPost> posts) {
        mInstagramPostsList.clear();
        mInstagramPostsList.addAll(posts);
        mInstagramPostsAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        showProgressBar(false);
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
