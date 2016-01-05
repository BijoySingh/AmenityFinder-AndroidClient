package com.birdlabs.amentityfinder.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.birdlabs.amentityfinder.R;
import com.birdlabs.amentityfinder.util.Preferences;
import com.birdlabs.basicproject.Functions;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    Context context;
    Preferences preferences;
    View skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login_layout);

        context = this;
        preferences = new Preferences(context);

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String userId = loginResult.getAccessToken().getUserId();

                preferences.save(Preferences.Keys.FB_LOGIN, true);
                preferences.save(Preferences.Keys.FB_USER_ID, userId);

                openMapActivity();
            }

            @Override
            public void onCancel() {
                Functions.makeToast(getApplicationContext(),
                        "Please login to facebook to add posts.");
                preferences.save(Preferences.Keys.FB_LOGIN, false);
            }

            @Override
            public void onError(FacebookException exception) {
                Functions.makeToast(getApplicationContext(),
                        "Error Occurred - " + exception.getMessage());
            }
        });

        skip = findViewById(R.id.skip_button);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.save(Preferences.Keys.FB_LOGIN, false);
                openMapActivity();
            }
        });
    }

    public void openMapActivity() {
        Intent intent = new Intent(context, MapActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }
}
