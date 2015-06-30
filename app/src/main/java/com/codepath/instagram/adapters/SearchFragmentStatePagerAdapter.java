package com.codepath.instagram.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.codepath.instagram.fragments.SearchTagsResultFragment;
import com.codepath.instagram.fragments.SearchUsersResultFragment;
import com.codepath.instagram.helpers.SmartFragmentStatePagerAdapter;

public class SearchFragmentStatePagerAdapter extends SmartFragmentStatePagerAdapter {

    private static final String SEARCH_USER = "USERS";
    private static final String SEARCH_TAGS = "TAGS";

    private static final String[] TAB_SEARCH = {
            SEARCH_USER,
            SEARCH_TAGS
    };

    public SearchFragmentStatePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return TAB_SEARCH.length;
    }

    @Override
    public Fragment getItem(int position) {

        String item = TAB_SEARCH[position];

        if (SEARCH_TAGS.equalsIgnoreCase(item)) {
            return SearchTagsResultFragment.newInstance();
        } else if (SEARCH_USER.equalsIgnoreCase(item)) {
            return SearchUsersResultFragment.newInstance();
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_SEARCH[position];
    }
}