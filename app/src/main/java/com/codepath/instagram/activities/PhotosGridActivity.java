package com.codepath.instagram.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import com.codepath.instagram.R;
import com.codepath.instagram.fragments.PhotoGridFragment;

public class PhotosGridActivity extends BaseActivity implements PhotoGridFragment.ProgressBarTriggerListener {

    private static final String TAG = "PhotoGridActivity";
    private MenuItem miActionProgressItem;
    public static final String EXTRA_USER_ID = "KEY_USER_ID";
    public static final String EXTRA_SEARCH_TAG = "KEY_SEARCH_ID";

    private String userId, searchTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_grid);

        if (getIntent() != null) {
            userId = getIntent().getStringExtra(EXTRA_USER_ID);
            searchTag = getIntent().getStringExtra(EXTRA_SEARCH_TAG);

            String title = !TextUtils.isEmpty(userId) ? ("@" + userId) : ("#" + searchTag);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Search Results for " + title);
            }
        }

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frPhotoGridContainer, PhotoGridFragment.newInstance(userId, searchTag));
            ft.commit();
        }
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


    @Override
    public void showProgressBar(boolean show) {
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(show);
        }
    }
}
