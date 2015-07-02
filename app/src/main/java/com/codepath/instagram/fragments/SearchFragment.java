package com.codepath.instagram.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.*;
import com.codepath.instagram.R;
import com.codepath.instagram.activities.HomeActivity;
import com.codepath.instagram.adapters.SearchFragmentStatePagerAdapter;

public class SearchFragment extends BaseFragment {

    private static final String TAG = SearchFragment.class.getSimpleName();
    private SearchFragmentStatePagerAdapter fragmentStatePagerAdapter;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initUi(view);

        return view;
    }

    private void initUi(View v) {
        ViewPager vpSearch = (ViewPager) v.findViewById(R.id.vpSearch);
        TabLayout tlSearchCategories = (TabLayout) v.findViewById(R.id.tlSearchTab);

        fragmentStatePagerAdapter = new SearchFragmentStatePagerAdapter(getChildFragmentManager());
        vpSearch.setAdapter(fragmentStatePagerAdapter);
        tlSearchCategories.setupWithViewPager(vpSearch);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchItem.expandActionView();
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query.trim())) {
                    handleSearchResult(searchItem, searchView, query.trim());
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        updateActionBarTitle(getString(R.string.str_search));

    }

    private void updateActionBarTitle(String title) {
        if (((HomeActivity) getActivity()).getSupportActionBar() != null) {
            ((HomeActivity) getActivity()).setActionBarTitle(title);
        }
    }

    private void handleSearchResult(MenuItem searchItem, SearchView searchView, String query) {
        updateActionBarTitle(query);
        searchView.clearFocus();
        searchView.setQuery("", false);
        searchView.setIconified(true);
        searchItem.collapseActionView();

        if (fragmentStatePagerAdapter != null) {
            SearchUsersResultFragment usersResultFragment =
                    (SearchUsersResultFragment) fragmentStatePagerAdapter.getRegisteredFragment(0);
            if (usersResultFragment != null) {
                usersResultFragment.fetchSearchResults(query);
            }

            SearchTagsResultFragment tagsResultFragment =
                    (SearchTagsResultFragment) fragmentStatePagerAdapter.getRegisteredFragment(1);
            if (tagsResultFragment != null) {
                tagsResultFragment.fetchSearchResults(query);
            }
        }
    }

}
