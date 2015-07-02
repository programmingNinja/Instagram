package com.codepath.instagram.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.codepath.instagram.R;
import com.codepath.instagram.fragments.UserProfileFragment;
import com.codepath.instagram.models.InstagramUser;

public class ProfileActivity extends BaseActivity implements UserProfileFragment.ProgressBarTriggerListener {

    public static final String EXTRA_USER_OBJECT = "KEY_USER_OBJECT";
    private InstagramUser mUser;
    private MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (getIntent() != null) {
            mUser = (InstagramUser) getIntent().getSerializableExtra(EXTRA_USER_OBJECT);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(mUser.userName);
            setSupportActionBar(toolbar);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, UserProfileFragment.newInstance(mUser))
                    .commit();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // 'home' is the id for the icon click in the action bar (i.e. up/back).
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showProgressBar(boolean show) {
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(show);
        }
    }
}
