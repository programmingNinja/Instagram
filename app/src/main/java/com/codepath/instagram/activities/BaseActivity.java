package com.codepath.instagram.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.codepath.instagram.R;

public class BaseActivity extends AppCompatActivity  {

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    protected void showErrorMsg() {
        showErrorMsg(getString(R.string.err_cannotload));
    }

    protected void showErrorMsg(String errorMsg) {
        Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
    }
}
