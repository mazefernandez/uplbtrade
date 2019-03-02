package com.mazefernandez.uplbtrade.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.GOOGLE_ACCOUNT;
import static com.mazefernandez.uplbtrade.models.RequestCode.LOGIN;
import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.TAG;
/* Login Customers through Google Account */

public class LoginActivity extends AppCompatActivity {
    private GoogleAccountAdapter googleAdapter = new GoogleAccountAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* Login Views */
        SignInButton signInButton = findViewById(R.id.sign_in);

        /* Configure Google Client */
        final GoogleSignInClient googleSIC = googleAdapter.configureGoogleSIC(this);

        /* Adds login intent to sign in button */
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signIn = googleSIC.getSignInIntent();
                startActivityForResult(signIn, LOGIN);
            }
        });
    }
    /* Verify that current user is logged in */
    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Toast.makeText(this, "User is already logged in", Toast.LENGTH_SHORT).show();
            onLoggedIn(account);
        } else {
            Log.d(TAG, "No user logged in");
        }
    }
    /* Fetch requested data from Google account */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 1:
                    final GoogleSignInAccount account = googleAdapter.getAccount(data);
                    onLoggedIn(account);
            }
    }
    /* Proceed to HOME after sign in */
    private void onLoggedIn(GoogleSignInAccount account) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(GOOGLE_ACCOUNT, account);
        startActivity(intent);
        finish();
    }
}
