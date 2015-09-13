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
import com.codepath.instagram.adapters.SearchUserResultsAdapter;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.helpers.DividerItemDecoration;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramUser;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.List;

public class SearchUsersResultFragment extends Fragment {
  private static final String TAG = "SearchUserResultsFragment";

  RecyclerView rvSearchResults;

  protected SearchUserResultsAdapter searchResultsAdapter;

  public static SearchUsersResultFragment newInstance() {
    return new SearchUsersResultFragment();
  }

  public SearchUsersResultFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    searchResultsAdapter = new SearchUserResultsAdapter();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_search_users_result, container, false);
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
    MainApplication.getRestClient().getUserSearch(searchTerm, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        List<InstagramUser> users = Utils.decodeUsersFromJsonResponse(response);
        searchResultsAdapter.replaceAll(users);
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
