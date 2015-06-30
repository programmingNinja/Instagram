package com.codepath.instagram.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;
import com.codepath.instagram.R;
import com.codepath.instagram.core.MainApplication;
import com.codepath.instagram.networking.InstagramClient;

public class BaseFragment extends Fragment {

    protected InstagramClient instagramClient;

    public interface ProgressBarTriggerListener {
        void showProgressBar(boolean show);
    }

    protected ProgressBarTriggerListener listener;
    protected Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        instagramClient = MainApplication.getRestClient();
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (ProgressBarTriggerListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + ProgressBarTriggerListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected void showErrorMsg() {
        showErrorMsg(getString(R.string.err_cannotload));
    }

    protected void showErrorMsg(String errorMsg) {
        Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
        showProgressBar(false);
    }

    protected void showProgressBar(boolean show) {
        if (listener != null) {
            listener.showProgressBar(show);
        }
    }
}
