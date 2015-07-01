package com.codepath.instagram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.codepath.instagram.R;
import com.codepath.instagram.networking.InstagramClient;
import com.codepath.instagram.services.BackgroundFeedService;
import com.codepath.oauth.OAuthLoginActivity;

public class LoginActivity extends OAuthLoginActivity<InstagramClient> {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private Button btLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btLogin = (Button) findViewById(R.id.btLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToRest();
            }
        });
    }

    // Method to be called to begin the authentication process
    // assuming user is not authenticated.
    // Typically used as an event listener for a button for the user to press.
    public void loginToRest() {
        getClient().connect();
    }

    @Override
    public void onLoginSuccess() {
        if (getClient().isAuthenticated()) {
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
            BackgroundFeedService.startActionGetUserInfo(this);
            this.finish();
        }
    }

    // Fires if the authentication process fails for any reason.
    @Override
    public void onLoginFailure(Exception e) {
        Log.e(TAG, "Login Failed - " + e.getMessage());
        e.printStackTrace();
    }
}
