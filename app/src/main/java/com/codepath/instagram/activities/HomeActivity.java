package com.codepath.instagram.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.codepath.instagram.R;
import com.codepath.instagram.adapters.InstagramPostsAdapter;
import com.codepath.instagram.helpers.DividerItemDecoration;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPost;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    public static final String SAMPLE_JSON_FILE_NAME = "popular.json";

    private RecyclerView recyclerView;
    private List<InstagramPost> samplePostList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        loadSampleJsonObject();

        //Get Handle to your UI elements
        initUi();
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
        samplePostList = Utils.decodePostsFromJsonResponse(jsonObject);
        Log.d(TAG, "Loaded " + samplePostList.size() + " sample posts.");
    }

    private void initUi() {
        //Get the handle to the recycler view
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
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
        recyclerView.setLayoutManager(layoutManager);

        // allows for optimizations if all item views are of the same size:
        recyclerView.setHasFixedSize(true);

        // Reference : https://gist.githubusercontent.com/alexfu/0f464fc3742f134ccd1e/raw/abe729359e5b3691f2fe56445644baf0e40b35ba/DividerItemDecoration.java
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);

        // RecyclerView uses this by default. You can add custom animations by using RecyclerView.ItemAnimator()
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        bindDataToAdapter();
    }

    private void bindDataToAdapter() {
        // Bind adapter to recycler view object
        recyclerView.setAdapter(new InstagramPostsAdapter(samplePostList, this.getApplicationContext()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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
