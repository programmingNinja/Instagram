package com.codepath.instagram.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.instagram.R;
import com.codepath.instagram.adapters.InstagramPostsAdapter;
import com.codepath.instagram.helpers.SimpleVerticalSpacerItemDecoration;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private List<InstagramPost> posts;
    private InstagramPostsAdapter instagramPostsAdapter;

    RecyclerView rvInstagramPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        rvInstagramPhotos = (RecyclerView) findViewById(R.id.rvInstagramPosts);

        posts = new ArrayList<>();
        instagramPostsAdapter = new InstagramPostsAdapter(posts);

        fetchPopularPosts();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        layoutManager.scrollToPosition(0);
        rvInstagramPhotos.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration = new SimpleVerticalSpacerItemDecoration(24);
        rvInstagramPhotos.addItemDecoration(itemDecoration);

        rvInstagramPhotos.setAdapter(instagramPostsAdapter);
    }

    private void fetchPopularPosts() {
        String fileName = "popular.json";
        try {
            JSONObject jsonObject = Utils.loadJsonFromAsset(this, fileName);
            posts.addAll(Utils.decodePostsFromJson(jsonObject));
            instagramPostsAdapter.notifyDataSetChanged();
        } catch (JSONException | IOException e){
            e.printStackTrace();
            Log.wtf(TAG, "Unable to load json");
        }
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
