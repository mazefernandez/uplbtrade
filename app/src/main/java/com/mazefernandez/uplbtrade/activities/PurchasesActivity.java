package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.GOOGLE_ACCOUNT;

/* Shows a customer's past purchases in the application */

public class PurchasesActivity extends AppCompatActivity {

    private final GoogleAccountAdapter googleAdapter = new GoogleAccountAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases);

        /* Configure Google Sign in */
        final GoogleSignInClient googleSIC = googleAdapter.configureGoogleSIC(this);
        final GoogleSignInAccount account = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        /* Navigation bar */
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_purchases);
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    item.setChecked(true);
                    Intent home = new Intent(PurchasesActivity.this,HomeActivity.class);
                    home.putExtra(GOOGLE_ACCOUNT, account);
                    startActivity(home);
                    break;
                case R.id.navigation_offers:
                    item.setChecked(true);
                    Intent offer = new Intent(PurchasesActivity.this, OffersActivity.class);
                    offer.putExtra(GOOGLE_ACCOUNT,account);
                    startActivity(offer);
                    break;
                case R.id.navigation_profile:
                    break;
                case R.id.navigation_purchases:
                    item.setChecked(true);
                    Intent purchase = new Intent(PurchasesActivity.this, PurchasesActivity.class);
                    startActivity(purchase);
                    break;
            }
            return false;
        });
    }
}
