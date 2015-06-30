package com.codepath.instagram.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.codepath.instagram.R;
import com.codepath.instagram.adapters.SearchResultsAdapter;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.helpers.DividerItemDecoration;
import com.codepath.instagram.networking.InstagramClient;

public abstract class BaseSearchFragment extends Fragment {

    protected InstagramClient instagramClient;
    protected RecyclerView rvSearchResults;
    protected Context mContext;
    protected SearchResultsAdapter mSearchResultsAdapter;
    protected SearchResultsAdapter.SearchType mSearchType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        instagramClient = MainApplication.getRestClient();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        rvSearchResults = (RecyclerView) view.findViewById(R.id.rvSearchResults);
        configureRecyclerView();
        return view;
    }

    private void configureRecyclerView() {

        // Setup layout manager for items
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);

        // Control orientation of the items
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //Customize the position you want to default scroll to
        layoutManager.scrollToPosition(0);

        // Attach layout manager to the RecyclerView
        rvSearchResults.setLayoutManager(layoutManager);

        // allows for optimizations if all item views are of the same size:
        rvSearchResults.setHasFixedSize(true);

        // Reference : https://gist.githubusercontent.com/alexfu/0f464fc3742f134ccd1e/raw/abe729359e5b3691f2fe56445644baf0e40b35ba/DividerItemDecoration.java
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST);
        rvSearchResults.addItemDecoration(itemDecoration);

        // RecyclerView uses this by default. You can add custom animations by using RecyclerView.ItemAnimator()
        rvSearchResults.setItemAnimator(new DefaultItemAnimator());

        bindDataToAdapter();
    }

    protected abstract void bindDataToAdapter();

    protected abstract void fetchSearchResults(String query);

    protected void showErrorMsg() {
        showErrorMsg(getString(R.string.err_cannotload));
    }

    protected void showErrorMsg(String errorMsg) {
        Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
    }
}
