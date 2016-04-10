package com.birdlabs.amenityfinder.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.birdlabs.amenityfinder.R;
import com.birdlabs.amenityfinder.server.Access;
import com.birdlabs.amenityfinder.server.AccessIds;
import com.birdlabs.amenityfinder.server.Links;
import com.birdlabs.amenityfinder.util.Preferences;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.github.bijoysingh.starter.Functions;
import com.github.bijoysingh.starter.server.AccessItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends ActivityBase {

    LoginActivity activity;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login_layout);

        activity = this;

        View loginContainer = findViewById(R.id.login_container);
        View loginWith = findViewById(R.id.login_with_text);
        View skip = findViewById(R.id.skip_button);

        callbackManager = CallbackManager.Factory.create();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (preferences.isLoggedIn()) {
            skip.setVisibility(View.GONE);
            loginWith.setVisibility(View.GONE);
            loginContainer.setVisibility(View.GONE);
            openPendingActivity(600);
        } else if (preferences.isSocialNetworkLoggedIn() || accessToken != null) {
            loginWith.setVisibility(View.GONE);
            loginContainer.setVisibility(View.GONE);
            preferences.save(Preferences.Keys.SOCIAL_NETWORK_LOGIN, true);
            logIntoServer(accessToken.getToken());
            startDialog();
        } else {
            setupFacebookLogin();
        }

        skip.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    preferences.save(Preferences.Keys.SOCIAL_NETWORK_LOGIN, false);
                    openMapActivity();
                }
            });
    }

    public void setupFacebookLogin() {
        final LoginManager manager = LoginManager.getInstance();
        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String token = loginResult.getAccessToken().getToken();
                preferences.socialNetworkLogin(true);
                preferences.serverLogin(false);
                logIntoServer(token);
                startDialog();
            }

            @Override
            public void onCancel() {
                Functions.makeToast(context, "Please login to facebook to add posts.");
                preferences.socialNetworkLogin(false);
                preferences.serverLogin(false);
            }

            @Override
            public void onError(FacebookException exception) {
                Functions.makeToast(context, "Error Occurred - " + exception.getMessage());
            }
        });

        LinearLayout loginButton = (LinearLayout) findViewById(R.id.login_fb_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> permissions = new ArrayList<>();
                permissions.add("email");
                manager.logInWithReadPermissions(activity, permissions);
            }
        });
    }

    public void logIntoServer(String token) {
        Map<String, Object> authMap = new HashMap<>();
        authMap.put("access_token", token);
        Access access = new Access(context);
        access.send(new AccessItem(Links.getLogin(), null,
            AccessIds.FB_LOGIN, false).setActivity(activity), authMap);
    }

    public void startDialog() {
        ProgressDialog.show(context, "Signing you in",
            "Please wait, while we get things ready.", true).setCancelable(true);
    }

    public void openPendingActivity(Integer delay) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                openMapActivity();
            }
        }, delay);
    }

    public void openMapActivity() {
        Intent intent = new Intent(context, MapActivity.class);
        startActivity(intent);
        finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void handleResponse(JSONObject json) {
        try {
            if (json.getBoolean("success")) {
                String authToken = json.getString("token");
                Integer userId = json.getInt("uid");
                preferences.save(Preferences.Keys.AUTH_TOKEN, authToken);
                preferences.save(Preferences.Keys.USER_ID, userId);
                preferences.save(Preferences.Keys.SERVER_LOGIN, true);
                openMapActivity();
            } else {
                Functions.makeToast(context, "Login into server failed!");
            }
        } catch (JSONException exception) {
            Functions.makeToast(context, "Something went wrong! Please try again");
            Log.e(LoginActivity.class.getSimpleName(), exception.getMessage(), exception);
        }
    }
}
