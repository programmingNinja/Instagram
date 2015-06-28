package com.codepath.instagram.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPost;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment {
    private static final String TAG = "PostsFragment";

    private List<InstagramPost> posts;
    private InstagramPostsAdapter instagramPostsAdapter;

    private OnFragmentInteractionListener listener;

    RecyclerView rvInstagramPosts;

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
        posts = new ArrayList<>();
        instagramPostsAdapter = new InstagramPostsAdapter(posts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        rvInstagramPosts = (RecyclerView)view.findViewById(R.id.rvInstagramPosts);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        layoutManager.scrollToPosition(0);
        rvInstagramPosts.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration = new SimpleVerticalSpacerItemDecoration(10);
        rvInstagramPosts.addItemDecoration(itemDecoration);

        rvInstagramPosts.setAdapter(instagramPostsAdapter);
        fetchPosts();
        return view;
    }

    private void fetchPosts() {
        if (!isNetworkAvailable()) {
            AlertDialogFragment.showAlertDialog(getChildFragmentManager(), getString(R.string.network_error),
                    getString(R.string.network_unavailable));
            return;
        }

        if (listener != null) {
            listener.showProgressBar();
        }

        MainApplication.getRestClient().getUserFeed(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (listener != null) {
                    listener.hideProgressBar();
                }
                posts.addAll(Utils.decodePostsFromJsonResponse(response));
                instagramPostsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (listener != null) {
                    listener.hideProgressBar();
                }
                AlertDialogFragment.showAlertDialog(getChildFragmentManager(), getString(R.string.network_error),
                        getString(R.string.network_error));
            }
        });
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
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnFragmentInteractionListener {
        public void showProgressBar();
        public void hideProgressBar();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
