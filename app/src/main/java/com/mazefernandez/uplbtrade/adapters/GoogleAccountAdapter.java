package com.mazefernandez.uplbtrade.adapters;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

/* Manage Google Sign In */

public class GoogleAccountAdapter {
    public static final String TAG = "UPLB Trade", GOOGLE_ACCOUNT = "google_account";

    public GoogleAccountAdapter() {}

    public GoogleSignInClient configureGoogleSIC(Activity activity){
        /* Requests user's Google ID, email address, and profile */
        GoogleSignInOptions googleSIO = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        /* Configures client with options specified by googleSignInOptions */
        return GoogleSignIn.getClient(activity, googleSIO);
    }

    /* Verify account login */
    public GoogleSignInAccount getAccount(Intent intent) {
        GoogleSignInAccount account = null;
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            account = task.getResult(ApiException.class);
            Log.w(TAG, "SigninResult:success");
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
        return account;
    }

}
