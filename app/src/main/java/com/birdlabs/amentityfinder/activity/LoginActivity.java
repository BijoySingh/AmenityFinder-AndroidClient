package com.birdlabs.amentityfinder.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.birdlabs.amentityfinder.R;
import com.birdlabs.amentityfinder.server.Access;
import com.birdlabs.amentityfinder.server.AccessInfo;
import com.birdlabs.amentityfinder.server.Links;
import com.birdlabs.amentityfinder.util.Preferences;
import com.birdlabs.basicproject.Functions;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    Context context;
    LoginActivity activity;
    Preferences preferences;
    View skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login_layout);

        context = this;
        activity = this;
        preferences = new Preferences(context);

        callbackManager = CallbackManager.Factory.create();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (preferences.load(Preferences.Keys.FB_LOGIN, false)
                && preferences.load(Preferences.Keys.SERVER_LOGIN, false)) {
            openMapActivity();
            return;
        } else if (preferences.load(Preferences.Keys.FB_LOGIN, false) ||
                accessToken != null) {
            preferences.save(Preferences.Keys.FB_LOGIN, true);
            logIntoServer(accessToken.getToken());
            startDialog();
            LinearLayout loginButton = (LinearLayout) findViewById(R.id.login_fb_button);
            loginButton.setVisibility(View.GONE);
        } else {
            final LoginManager manager = LoginManager.getInstance();
            manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    String token = loginResult.getAccessToken().getToken();
                    Log.d(LoginActivity.class.getSimpleName(), token);

                    preferences.save(Preferences.Keys.FB_LOGIN, true);
                    preferences.save(Preferences.Keys.SERVER_LOGIN, false);
                    logIntoServer(token);
                    startDialog();
                }

                @Override
                public void onCancel() {
                    Functions.makeToast(context, "Please login to facebook to add posts.");
                    preferences.save(Preferences.Keys.FB_LOGIN, false);
                    preferences.save(Preferences.Keys.SERVER_LOGIN, false);
                }

                @Override
                public void onError(FacebookException exception) {
                    Functions.makeToast(context,
                            "Error Occurred - " + exception.getMessage());
                    Log.d(LoginActivity.class.getSimpleName(), "onError Called");
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

        skip = findViewById(R.id.skip_button);

        skip.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        preferences.save(Preferences.Keys.FB_LOGIN, false);
                                        openMapActivity();
                                    }
                                }

        );
    }

    public void logIntoServer(String token) {
        Map<String, Object> authMap = new HashMap<>();
        authMap.put("access_token", token);
        Access access = new Access(context);
        access.send(new AccessInfo(Links.getLogin(), null,
                AccessInfo.AccessIds.FB_LOGIN, false).setActivity(activity), authMap);
    }

    public void startDialog() {
        ProgressDialog.show(context, "Signing you in",
                "Please wait, while we get things ready.", true).setCancelable(true);
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
