package com.codepath.instagram.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codepath.instagram.R;
import com.codepath.instagram.activities.HomeActivity;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.helpers.Utils;
import com.codepath.instagram.models.InstagramUser;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONObject;

public class UserProfileFragment extends BaseFragment {

    private static final String TAG = UserProfileFragment.class.getSimpleName();
    private static final String ARG_USER_OBJECT = "ARG_USER_OBJECT";

    private InstagramUser mUser = null;
    private boolean isSelfUser = false;
    private SimpleDraweeView sdvProfileImage;
    private TextView tvPostCount, tvProfileFollowerCount, tvProfileFollowingCount, tvUserFullName, tvUserBio;
    private RelativeLayout rlNoResult, rlProfileContainer;

    public static UserProfileFragment newInstance(InstagramUser user) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_OBJECT, user);
        fragment.setArguments(args);
        return fragment;
    }

    public static UserProfileFragment newInstance() {
        return newInstance(null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = (InstagramUser) getArguments().getSerializable(ARG_USER_OBJECT);
        }

        if (mUser == null) {
            mUser = MainApplication.sharedApplication().getCurrentUser();
            isSelfUser = true;
        } else {
            getUserInfo();
        }
    }

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initUi(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_home, menu);

        if (isSelfUser) {
            ((HomeActivity) getActivity()).setActionBarTitle(mUser.userName.toUpperCase());
        }
    }

    private void initUi(View v) {
        tvPostCount = (TextView) v.findViewById(R.id.tvPostCount);
        tvProfileFollowerCount = (TextView) v.findViewById(R.id.tvProfileFollowerCount);
        tvProfileFollowingCount = (TextView) v.findViewById(R.id.tvProfileFollowingCount);
        tvUserFullName = (TextView) v.findViewById(R.id.tvUserFullName);
        tvUserBio = (TextView) v.findViewById(R.id.tvUserBio);
        sdvProfileImage = (SimpleDraweeView) v.findViewById(R.id.sdvProfileImage);
        rlProfileContainer = (RelativeLayout) v.findViewById(R.id.rlProfileContainer);
        rlNoResult = (RelativeLayout) v.findViewById(R.id.rlNoResult);

        if (isSelfUser) {
            populateViews();
        }
    }

    private void populateViews() {
        rlNoResult.setVisibility(View.GONE);
        rlProfileContainer.setVisibility(View.VISIBLE);
        tvPostCount.setText(Utils.formatNumberForDisplay(mUser.counts.media));
        tvProfileFollowerCount.setText(Utils.formatNumberForDisplay(mUser.counts.followed_by));
        tvProfileFollowingCount.setText(Utils.formatNumberForDisplay(mUser.counts.follows));

        sdvProfileImage.setImageURI(null);
        sdvProfileImage.setImageURI(Uri.parse(mUser.profilePictureUrl));

        if (!TextUtils.isEmpty(mUser.fullName)) {
            tvUserFullName.setText(mUser.fullName);
        } else {
            tvUserFullName.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(mUser.bio)) {
            tvUserBio.setText(mUser.bio);
        } else {
            tvUserBio.setVisibility(View.GONE);
        }

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(R.id.flContainer, PhotoGridFragment.newInstance(mUser.userId, ""));
        ft.commit();
    }

    public void getUserInfo() {
        if (Utils.isNetworkAvailable(getActivity())) {
            instagramClient.getUserProfile(mUser.userId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONObject jsonUser = response.optJSONObject("data");
                    InstagramUser user = InstagramUser.fromJson(jsonUser);
                    if (user != null) {
                        mUser = user;
                        populateViews();
                        Log.d(TAG, "Successfully retrieved user info - " + user.fullName + " @" + user.userName);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    showErrorMsg();
                    rlProfileContainer.setVisibility(View.GONE);
                    rlNoResult.setVisibility(View.VISIBLE);
                }
            });
        } else {
            showErrorMsg(getString(R.string.err_no_internet));
        }
    }
}
