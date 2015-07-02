package com.codepath.instagram.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.codepath.instagram.R;
import com.codepath.instagram.adapters.SearchResultsAdapter;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramSearchTag;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONObject;

import java.util.List;

public class SearchTagsResultFragment extends BaseSearchFragment {

    private static final String TAG = SearchTagsResultFragment.class.getSimpleName();

    public static SearchTagsResultFragment newInstance() {
        return new SearchTagsResultFragment();
    }

    public SearchTagsResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSearchType = SearchResultsAdapter.SearchType.SEARCH_TAGS;
        mSearchResultsAdapter = new SearchResultsAdapter(mSearchType, mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void bindDataToAdapter() {
        // Bind adapter to recycler view object
        rvSearchResults.setAdapter(mSearchResultsAdapter);
    }

    @Override
    protected void fetchSearchResults(String searchTerm) {

        if (Utils.isNetworkAvailable(mContext)) {
            instagramClient.getSearchResults(mSearchType, searchTerm, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    List<InstagramSearchTag> searchTags = Utils.decodeSearchTagsFromJsonResponse(response);
                    mSearchResultsAdapter.replaceAllTags(searchTags);
                    handleNoResult();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    showErrorMsg();
                }
            });
        } else {
            showErrorMsg(getString(R.string.err_no_internet));
        }
    }

}