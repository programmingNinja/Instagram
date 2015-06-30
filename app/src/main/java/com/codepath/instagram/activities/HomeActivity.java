package com.codepath.instagram.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.Menu;
import android.view.MenuItem;
import com.codepath.instagram.R;
import com.codepath.instagram.adapters.HomeFragmentStatePagerAdapter;
import com.codepath.instagram.fragments.PostsFragment;
import com.codepath.instagram.helpers.NonSwipeableViewPager;


public class HomeActivity extends BaseActivity implements PostsFragment.ProgressBarTriggerListener {

    TabLayout tlTabBar;
    NonSwipeableViewPager nsvpHome;
    MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Get Handle to your UI elements
        initUi();
    }

    private void initUi() {
        tlTabBar = (TabLayout)findViewById(R.id.tlTabBar);
        nsvpHome = (NonSwipeableViewPager)findViewById(R.id.nsvpHome);

        FragmentStatePagerAdapter fragmentStatePagerAdapter = new HomeFragmentStatePagerAdapter(getSupportFragmentManager(), this);
        nsvpHome.setAdapter(fragmentStatePagerAdapter);
        tlTabBar.setupWithViewPager(nsvpHome);
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
