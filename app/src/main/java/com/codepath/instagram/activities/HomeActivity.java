package com.codepath.instagram.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.instagram.R;
import com.codepath.instagram.adapters.HomeFragmentStatePagerAdapter;
import com.codepath.instagram.fragments.PostsFragment;
import com.codepath.instagram.helpers.NonSwipeableViewPager;


public class HomeActivity extends AppCompatActivity implements PostsFragment.OnFragmentInteractionListener {
    private static final String TAG = "HomeActivity";

    TabLayout tlHomeBar;
    NonSwipeableViewPager vpHome;
    MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
        }

        vpHome = (NonSwipeableViewPager)findViewById(R.id.vpHome);
        tlHomeBar = (TabLayout)findViewById(R.id.tlHomeBar);

        FragmentStatePagerAdapter fragmentStatePagerAdapter =
                new HomeFragmentStatePagerAdapter(getSupportFragmentManager(), this);
        vpHome.setAdapter(fragmentStatePagerAdapter);
        tlHomeBar.setupWithViewPager(vpHome);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        ProgressBar progressBar =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
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

    public void showProgressBar() {
        // Show progress item
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(true);
        }
    }

    public void hideProgressBar() {
        // Hide progress item
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(false);
        }
    }
}
