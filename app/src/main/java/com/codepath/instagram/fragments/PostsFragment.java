package com.codepath.instagram.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.instagram.R;
import com.codepath.instagram.adapters.InstagramPostsAdapter;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.helpers.SimpleVerticalSpacerItemDecoration;
import com.codepath.instagram.models.InstagramPost;
import com.codepath.instagram.models.InstagramPosts;
import com.codepath.instagram.persistence.InstagramClientDatabase;
import com.codepath.instagram.services.PostsFetcherService;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment {
  private static final String TAG = "PostsFragment";

  private InstagramPostsAdapter instagramPostsAdapter;
  InstagramClientDatabase database;

  private OnFragmentInteractionListener listener;

  RecyclerView rvInstagramPosts;
  SwipeRefreshLayout swpContainer;

  public static PostsFragment newInstance() {
    return new PostsFragment();
  }

  public PostsFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    List<InstagramPost> posts = new ArrayList<>();
    instagramPostsAdapter = new InstagramPostsAdapter(posts);
    database = InstagramClientDatabase.getInstance(MainApplication.sharedApplication());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_posts, container, false);

    rvInstagramPosts = (RecyclerView) view.findViewById(R.id.rvInstagramPosts);
    swpContainer = (SwipeRefreshLayout) view.findViewById(R.id.swpContainer);

    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),
            LinearLayoutManager.VERTICAL, false);
    layoutManager.scrollToPosition(0);
    rvInstagramPosts.setLayoutManager(layoutManager);

    RecyclerView.ItemDecoration itemDecoration = new SimpleVerticalSpacerItemDecoration(10);
    rvInstagramPosts.addItemDecoration(itemDecoration);

    rvInstagramPosts.setAdapter(instagramPostsAdapter);
    swpContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        fetchPosts();
      }
    });

    swpContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);

    fetchPosts();
    return view;
  }

  private void fetchPosts() {
    if (!isNetworkAvailable()) {
      AlertDialogFragment.showAlertDialog(
              getChildFragmentManager(),
              getString(R.string.network_error),
              getString(R.string.network_unavailable));
      List<InstagramPost> posts = database.getAllInstagramPosts();
      instagramPostsAdapter.replaceAll(posts);

      return;
    }

    if (listener != null) {
      listener.showProgressBar();
    }
    startPostsFetcherService();
  }

  private boolean isNetworkAvailable() {
    ConnectivityManager connectivityManager
            = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      listener = (OnFragmentInteractionListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
              + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    // Register for the particular broadcast based on ACTION string
    IntentFilter filter = new IntentFilter(PostsFetcherService.ACTION);
    LocalBroadcastManager.getInstance(getActivity()).registerReceiver(postsFetchedReceiver, filter);
  }

  @Override
  public void onPause() {
    super.onPause();
    // Unregister the listener when the application is paused
    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(postsFetchedReceiver);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    listener = null;
  }

  public interface OnFragmentInteractionListener {
    void showProgressBar();

    void hideProgressBar();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_home, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  private void handleNewPostsReceivedSuccess(List<InstagramPost> newPosts) {
    if (listener != null) {
      listener.hideProgressBar();
    }
    instagramPostsAdapter.replaceAll(newPosts);
    swpContainer.setRefreshing(false);
  }

  private void handlerNewPostsReceivedFailure() {
    if (listener != null) {
      listener.hideProgressBar();
    }
    AlertDialogFragment.showAlertDialog(
            getChildFragmentManager(),
            getString(R.string.network_error),
            getString(R.string.network_error));
    swpContainer.setRefreshing(false);
  }

  private void startPostsFetcherService() {
    Activity activity = getActivity();
    if (activity != null) {
      Intent intent = new Intent(activity, PostsFetcherService.class);
      activity.startService(intent);
    }
  }

  private BroadcastReceiver postsFetchedReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      int resultCode = intent.getIntExtra(
              PostsFetcherService.EXTRA_RESULT_CODE, Activity.RESULT_CANCELED);
      if (resultCode == Activity.RESULT_OK) {
        InstagramPosts postsWrapper = (InstagramPosts) intent.getSerializableExtra(
                PostsFetcherService.EXTRA_RESULT_POSTS);
        handleNewPostsReceivedSuccess(postsWrapper.posts);
      } else {
        handlerNewPostsReceivedFailure();
      }
    }
  };
}
