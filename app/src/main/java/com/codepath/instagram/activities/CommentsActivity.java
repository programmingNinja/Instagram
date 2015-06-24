package com.codepath.instagram.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.instagram.R;
import com.codepath.instagram.adapters.InstagramCommentsAdapter;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.fragments.AlertDialogFragment;
import com.codepath.instagram.helpers.SimpleVerticalSpacerItemDecoration;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramComment;
import com.codepath.instagram.networking.InstagramClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {
    private static final String TAG = "CommentsActivity";

    public static final String EXTRA_MEDIA_ID = "mediaId";

    private String postMediaId;
    private List<InstagramComment> comments;
    private InstagramCommentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
        }
        RecyclerView rvInstagramComments = (RecyclerView)findViewById(R.id.rvInstagramComments);

        postMediaId = getIntent().getStringExtra(EXTRA_MEDIA_ID);
        comments = new ArrayList<>();

        adapter = new InstagramCommentsAdapter(comments);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        layoutManager.scrollToPosition(0);
        rvInstagramComments.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration =
                new SimpleVerticalSpacerItemDecoration(16);
        rvInstagramComments.addItemDecoration(itemDecoration);

        rvInstagramComments.setAdapter(adapter);

        fetchCommentsFromNetwork();
    }

    private void fetchCommentsFromNetwork() {
        MainApplication.getRestClient().getPostComments(postMediaId, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                comments.addAll(Utils.decodeCommentsFromJsonResponse(response));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                AlertDialogFragment.showAlertDialog(CommentsActivity.this, getString(R.string.network_error),
                        getString(R.string.network_error));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
