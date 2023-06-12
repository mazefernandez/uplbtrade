package com.mazefernandez.uplbtrade.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter;
import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.GOOGLE_ACCOUNT;
import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.TAG;

import java.util.Objects;
/* Login Customers through Google Account */

public class LoginActivity extends AppCompatActivity {
    private final GoogleAccountAdapter googleAdapter = new GoogleAccountAdapter();
    DatabaseReference database;

    /* login to the application */
    ActivityResultLauncher<Intent> login = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = result.getData();

            final GoogleSignInAccount account = googleAdapter.getAccount(intent);
            /* Restrict login to UP mail domain*/
            if (checkDomain(account)) {
                onLoggedIn(account);
            } else {
                Toast.makeText(this, "Please use UP mail to sign in", Toast.LENGTH_LONG).show();
                signOut();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* Login Views */
        SignInButton signInButton = findViewById(R.id.sign_in);

        /* Configure Google Client */
        final GoogleSignInClient googleSIC = googleAdapter.configureGoogleSIC(this);

        /* Adds login intent to sign in button */
        signInButton.setOnClickListener(view -> {
            try {
                Intent signIn = googleSIC.getSignInIntent();
                login.launch(signIn);
            }
            catch (Exception e) {
                e.getStackTrace();
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
            Log.w(TAG,"No user logged in");
        }
    }

    /* Proceed to HOME after sign in */
    private void onLoggedIn(GoogleSignInAccount account) {
        checkCustomer(account);
        /* SharedPref to save email */
        SharedPreferences pref = this.getSharedPreferences("uplbtrade", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        String email = Objects.requireNonNull(account.getEmail()).replace("@up.edu.ph","");
        editor.putString("customer_email", email);
        editor.putString("customer_name", account.getGivenName() + " " + account.getFamilyName());
        editor.apply();

        /* Get customer data */
        UPLBTrade.retrofitClient.getCustomerByEmail(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                Customer customer = response.body();
                assert customer != null;
                int customerId = customer.getCustomerId();
                saveSessionId(customerId);
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                System.out.println("Get Customer by email Failed");
                System.out.println(t.getMessage());
            }
        }, account.getEmail() );

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
                System.out.println("Customer exists");
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

        /* Upload image to firebase storage */
        Uri filepath = account.getPhotoUrl();
        String image;
        if (filepath == null) {
            image = "placeholder";
        }
        else {
            image = filepath.toString();
        }
        Customer customer = new Customer(image,account.getGivenName(), account.getFamilyName(), account.getEmail());


        // firebase database to save user for messaging
        database = FirebaseDatabase.getInstance().getReference();
        String email = Objects.requireNonNull(account.getEmail()).replace("@up.edu.ph","");
        writeNewUser(email, account.getGivenName() + " " + account.getFamilyName());

        UPLBTrade.retrofitClient.addCustomer(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                System.out.println("Added new customer");
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
        googleSIC.signOut().addOnCompleteListener(task -> {
            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    public void writeNewUser(String email, String name) {
        User user = new User(name);

        database.child("users").child(email).setValue(user);
    }

    public void saveSessionId(int sessionId) {
        SharedPreferences pref = this.getSharedPreferences("uplbtrade", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("customer_id", sessionId);
        editor.apply();
    }
}
