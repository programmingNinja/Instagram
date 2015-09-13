package com.codepath.instagram.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.instagram.R;
import com.codepath.instagram.adapters.InstagramCommentsAdapter;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.fragments.AlertDialogFragment;
import com.codepath.instagram.helpers.SimpleVerticalSpacerItemDecoration;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramComment;
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
  private RecyclerView rvInstagramComments;
  private EditText etComment;
  private Button btnSubmitComment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comments);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setElevation(0);
    }
    rvInstagramComments = (RecyclerView) findViewById(R.id.rvInstagramComments);
    etComment = (EditText) findViewById(R.id.etComment);
    btnSubmitComment = (Button) findViewById(R.id.btnSubmitComment);
    postMediaId = getIntent().getStringExtra(EXTRA_MEDIA_ID);

    btnSubmitComment.setEnabled(false);
    configureEditText();

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

  private void configureEditText() {
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
  }

  private void fetchCommentsFromNetwork() {
    MainApplication.getRestClient().getPostComments(postMediaId, new JsonHttpResponseHandler() {

      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        comments.addAll(Utils.decodeCommentsFromJsonResponse(response));
        adapter.notifyDataSetChanged();
        scrollToEndOfComments();
      }

      @Override
      public void onFailure(
              int statusCode,
              Header[] headers,
              Throwable throwable,
              JSONObject errorResponse) {
        AlertDialogFragment.showAlertDialog(
                getSupportFragmentManager(),
                getString(R.string.network_error),
                getString(R.string.network_error));
      }
    });
  }

  private void scrollToEndOfComments() {
    rvInstagramComments.scrollToPosition(comments.size() - 1);
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

  private void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager) this.getSystemService(
            Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(etComment.getWindowToken(), 0);
  }

  public void submitComment(View view) {

    String commentText = etComment.getText().toString();
    InstagramComment comment = new InstagramComment();

    comment.user = MainApplication.sharedApplication().getCurrentUser();
    comment.text = commentText;
    comment.createdTime = System.currentTimeMillis() / 1000;
    comments.add(comment);
    adapter.notifyDataSetChanged();

    etComment.setText("");
    hideKeyboard();
    scrollToEndOfComments();

    MainApplication.getRestClient().postPostComment(
            postMediaId,
            commentText,
            new JsonHttpResponseHandler() {

              @Override
              public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(
                        CommentsActivity.this,
                        "Success in submitting comment",
                        Toast.LENGTH_SHORT)
                        .show();
                etComment.setText("");
                scrollToEndOfComments();
              }

              @Override
              public void onFailure(
                      int statusCode,
                      Header[] headers,
                      Throwable throwable,
                      JSONObject errorResponse) {
                Toast.makeText(
                        CommentsActivity.this,
                        "Failure in submitting comment",
                        Toast.LENGTH_SHORT)
                        .show();
              }
            });
  }
}
