package com.mazefernandez.uplbtrade.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter;
import com.mazefernandez.uplbtrade.models.Customer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        if (account != null && checkDomain(account)) {
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
                    /* Restrict login to UP mail domain*/
                    if (checkDomain(account)) {
                        onLoggedIn(account);
                    }
                    else {
                        Toast.makeText(this, "Please use UP mail to sign in", Toast.LENGTH_LONG).show();
                        signOut();
                    }
            }
    }
    /* Proceed to HOME after sign in */
    private void onLoggedIn(GoogleSignInAccount account) {
        checkCustomer(account);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(GOOGLE_ACCOUNT, account);
        startActivity(intent);
        finish();
    }
    /* Check if customer is new */
    private void checkCustomer(final GoogleSignInAccount account) {
        String email = account.getEmail();
        UPLBTrade.retrofitClient.getCustomerByEmail(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                if (response.isSuccessful()) {
                    System.out.println("Customer exists");
                }
                else {
                    System.out.println("find customer by email error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                addCustomer(account);
                System.out.println("Find Customer by email Failed");
                System.out.println(t.getMessage());
            }
        }, email);
    }

    /* Add new customer */
    private void addCustomer(GoogleSignInAccount account) {
        Customer customer = new Customer(account.getGivenName(), account.getFamilyName(), account.getEmail());

        UPLBTrade.retrofitClient.addCustomer(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                if (response.isSuccessful()) {
                    System.out.println("Added new customer");
                }
                else {
                    System.out.println("add new customer error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                System.out.println("Add Customer Failed");
                System.out.println(t.getMessage());
            }
        }, customer);
    }

    /* Check if the customer follows the domain needed for the app */
    public boolean checkDomain(GoogleSignInAccount account) {
        String email = account.getEmail();
        assert email != null;
        String[] split = email.split("@");
        String domain = split[1];
        return domain.equals("up.edu.ph");
    }
    /* Sign out the customer */
    public void signOut(){
        GoogleSignInClient googleSIC = googleAdapter.configureGoogleSIC(this);
        googleSIC.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
