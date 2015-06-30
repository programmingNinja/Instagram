package com.codepath.instagram.fragments;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.codepath.instagram.R;
import com.codepath.instagram.adapters.InstagramPhotosAdapter;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramPost;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

public class PhotoGridFragment extends BaseFragment {

    private static final String TAG = PhotoGridFragment.class.getSimpleName();
    private static final int NUM_COLUMNS = 3;

    private static final String ARG_USER_ID = "userId";
    private static final String ARG_TAG = "tag";

    private ArrayList<InstagramPost> posts;
    private InstagramPhotosAdapter instagramPhotosAdapter;

    RecyclerView rvPhotoGrid;

    private String userId;
    private String tag;

    public static PhotoGridFragment newInstance(String userId, String tag) {
        PhotoGridFragment fragment = new PhotoGridFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        args.putString(ARG_TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    public PhotoGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            tag = getArguments().getString(ARG_TAG);
        }
        posts = new ArrayList<>();
        instagramPhotosAdapter = new InstagramPhotosAdapter(posts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos_grid, container, false);
        rvPhotoGrid = (RecyclerView)view.findViewById(R.id.rvPhotoGrid);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), NUM_COLUMNS);
        rvPhotoGrid.setLayoutManager(layoutManager);
        rvPhotoGrid.setAdapter(instagramPhotosAdapter);
        fetchPhotos();
        return view;
    }

    private void fetchPhotos() {
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                instagramPhotosAdapter.clear();
                instagramPhotosAdapter.addAll(Utils.decodePostsFromJsonResponse(response));
                instagramPhotosAdapter.notifyDataSetChanged();
                showProgressBar(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                showErrorMsg();
            }
        };

        if (Utils.isNetworkAvailable(mContext)) {
            if (userId != null) {
                instagramClient.getUserRecentMedia(userId, responseHandler);
            } else if (tag != null) {
                instagramClient.getTagRecentMedia(tag, responseHandler);
            }
        } else {
            showErrorMsg(getString(R.string.err_no_internet));
        }
    }
}