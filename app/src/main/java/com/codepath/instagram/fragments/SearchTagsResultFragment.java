package com.codepath.instagram.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.instagram.R;
import com.codepath.instagram.adapters.SearchTagResultsAdapter;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.helpers.DividerItemDecoration;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramSearchTag;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.List;

public class SearchTagsResultFragment extends Fragment {
  private static final String TAG = "SearchTagResultsFragment";

  RecyclerView rvSearchResults;

  protected SearchTagResultsAdapter searchResultsAdapter;

  public static SearchTagsResultFragment newInstance() {
    return new SearchTagsResultFragment();
  }

  public SearchTagsResultFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    searchResultsAdapter = new SearchTagResultsAdapter();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_search_tags_result, container, false);
    rvSearchResults = (RecyclerView) view.findViewById(R.id.rvSearchResults);

    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),
            LinearLayoutManager.VERTICAL, false);
    rvSearchResults.setLayoutManager(layoutManager);

    RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(view.getContext(),
            DividerItemDecoration.VERTICAL_LIST);
    rvSearchResults.addItemDecoration(itemDecoration);

    rvSearchResults.setAdapter(searchResultsAdapter);

    return view;
  }

  public void fetchSearchResults(String searchTerm) {
    MainApplication.getRestClient().getTagSearch(searchTerm, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        List<InstagramSearchTag> searchTags = Utils.decodeSearchTagsFromJsonResponse(response);
        searchResultsAdapter.replaceAll(searchTags);
      }

      @Override
      public void onFailure(
              int statusCode,
              Header[] headers,
              Throwable throwable,
              JSONObject errorResponse) {
        Log.wtf(TAG, "Network request failed");
      }
    });
  }
}
