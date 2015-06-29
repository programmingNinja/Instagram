package com.codepath.instagram.activities;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.codepath.instagram.R;
import com.codepath.instagram.adapters.InstagramCommentsAdapter;
import com.codepath.instagram.helpers.DividerItemDecoration;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramComment;
import com.codepath.instagram.networking.InstagramClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends BaseActivity {

    public static final String KEY_MEDIA_ID = "key_media_id";
    private static final String TAG = CommentsActivity.class.getSimpleName();

    private String postMediaId;
    private RecyclerView mRecyclerView;
    private List<InstagramComment> mCommentsList;
    private InstagramCommentsAdapter mInstagramCommentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_comments);

        if (getIntent() != null) {
            postMediaId = getIntent().getStringExtra(KEY_MEDIA_ID);
        }

        //Get Handle to your UI elements
        initUi();

        //Fetch comments for the post
        fetchPostComments();
    }

    private void fetchPostComments() {
        if (Utils.isNetworkAvailable(this.getApplicationContext())) {
            InstagramClient.getPostComments(postMediaId, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mCommentsList.addAll(Utils.decodeCommentsFromJsonResponse(response));
                    mInstagramCommentsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    showErrorMsg();
                }
            });
        } else {
            showErrorMsg(getString(R.string.err_no_internet));
        }
    }

    private void initUi() {
        //Get the handle to the recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.rvComments);
        mCommentsList = new ArrayList<>();
        mInstagramCommentsAdapter = new InstagramCommentsAdapter(mCommentsList, mContext);

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
        mRecyclerView.setAdapter(mInstagramCommentsAdapter);
    }

}
