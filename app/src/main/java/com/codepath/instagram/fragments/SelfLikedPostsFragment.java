package com.codepath.instagram.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import com.codepath.instagram.R;
import com.codepath.instagram.activities.HomeActivity;
import com.codepath.instagram.adapters.InstagramPostsAdapter;
import com.codepath.instagram.helpers.DividerItemDecoration;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPost;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelfLikedPostsFragment extends BaseFragment {

    private static final String TAG = SelfLikedPostsFragment.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private List<InstagramPost> mInstagramPostsList;
    private InstagramPostsAdapter mInstagramPostsAdapter;

    public static SelfLikedPostsFragment newInstance() {
        return new SelfLikedPostsFragment();
    }

    public SelfLikedPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((HomeActivity) getActivity()).setActionBarTitle(getString(R.string.posts_you_liked));

        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        initUi(view);

        fetchPostsLiked();

        return view;
    }

    private void initUi(View v) {
        //Get the handle to the recycler view
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swpContainer);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPostsLiked();
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

    private void fetchPostsLiked() {
        if (Utils.isNetworkAvailable(mContext)) {
            showProgressBar(true);
            fetchSelfLikedPosts();
        } else {
            showErrorMsg(getString(R.string.err_no_internet));
        }
    }

    private void fetchSelfLikedPosts() {
        if (Utils.isNetworkAvailable(getActivity())) {
            instagramClient.getUserSelfLikedMedia(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    List<InstagramPost> posts = Utils.decodePostsFromJsonResponse(response);
                    handlePostsReceived(posts);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorJson) {
                    Log.e(TAG, "Error retrieving user feed " + statusCode + " - " + errorJson.toString());
                    handleErrorResult();
                }
            });
        }
    }

    private void handleErrorResult() {
        showErrorMsg();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_home, menu);
        ((HomeActivity) getActivity()).setActionBarTitle(getString(R.string.posts_you_liked));
    }

    private void handlePostsReceived(List<InstagramPost> posts) {
        mInstagramPostsList.clear();
        mInstagramPostsList.addAll(posts);
        mInstagramPostsAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        showProgressBar(false);
    }
}
