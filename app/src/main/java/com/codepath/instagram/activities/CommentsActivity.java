package com.codepath.instagram.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.codepath.instagram.R;
import com.codepath.instagram.adapters.InstagramCommentsAdapter;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.helpers.DividerItemDecoration;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramComment;
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
    private ImageButton btnSubmitComment;
    private EditText etComment;
    private MenuItem miActionProgressItem;

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
            instagramClient.getPostComments(postMediaId, new JsonHttpResponseHandler() {

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
        btnSubmitComment = (ImageButton) findViewById(R.id.btnSubmitComment);
        etComment = (EditText) findViewById(R.id.etComment);
        mCommentsList = new ArrayList<>();
        mInstagramCommentsAdapter = new InstagramCommentsAdapter(mCommentsList, mContext);

        configureRecyclerView();
        addListenersAndWatchers();
    }

    private void addListenersAndWatchers() {

        etComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    postNewComment();
                    return true;
                }
                return false;
            }
        });


        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                btnSubmitComment.setEnabled(!TextUtils.isEmpty(s));
            }
        });

        btnSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNewComment();
            }
        });
    }

    private void postNewComment() {
        String commentText = etComment.getText().toString();
        if (!TextUtils.isEmpty(commentText)) {
            final InstagramComment comment = constructCommentObject(commentText);

            if (Utils.isNetworkAvailable(mContext)) {
                showProgressBar(true);
                hideKeyboard();
                MainApplication.getRestClient().postNewUserComment(postMediaId, commentText, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Toast.makeText(CommentsActivity.this, "Successfully submitted comment", Toast.LENGTH_SHORT).show();
                        etComment.setText("");
                        mCommentsList.add(comment);
                        mInstagramCommentsAdapter.notifyDataSetChanged();
                        showProgressBar(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        showErrorMsg("Failed to submit comment");
                        showProgressBar(false);
                    }
                });
            }
        } else {
            showErrorMsg("Comment cannot be empty!");
            showProgressBar(false);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etComment.getWindowToken(), 0);
    }

    private InstagramComment constructCommentObject(String commentText) {
        final InstagramComment comment = new InstagramComment();
        comment.user = MainApplication.sharedApplication().getCurrentUser();
        comment.text = commentText;
        comment.createdTime = System.currentTimeMillis() / 1000;
        return comment;
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        return true;
    }


    public void showProgressBar(boolean show) {
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(show);
        }
    }

}
