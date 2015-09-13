package com.codepath.instagram.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.codepath.instagram.R;
import com.codepath.instagram.fragments.SearchTagsResultFragment;
import com.codepath.instagram.fragments.SearchUsersResultFragment;
import com.codepath.instagram.helpers.SmartFragmentStatePagerAdapter;

public class SearchFragmentStatePagerAdapter extends SmartFragmentStatePagerAdapter {
  private static final int NUM_PAGES = 2;

  private Context context;

  public SearchFragmentStatePagerAdapter(FragmentManager fragmentManager, Context context) {
    super(fragmentManager);
    this.context = context;
  }

  @Override
  public int getCount() {
    return NUM_PAGES;
  }

  @Override
  public Fragment getItem(int position) {
    switch (position) {
      case 0:
        return SearchUsersResultFragment.newInstance();
      case 1:
        return SearchTagsResultFragment.newInstance();
      default:
        return null;
    }
  }

  @Override
  public CharSequence getPageTitle(int position) {
    switch (position) {
      case 0:
        return context.getString(R.string.search_users);
      case 1:
        return context.getString(R.string.search_tags);
      default:
        return "";
    }
  }
}
