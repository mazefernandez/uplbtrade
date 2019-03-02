package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.GOOGLE_ACCOUNT;

/* Home Page (Item Catalog) */

public class HomeActivity extends AppCompatActivity {
    private GoogleAccountAdapter googleAdapter = new GoogleAccountAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
       // SearchView homeSearch = findViewById(R.id.home_search);

        /* Configure Google Sign in */
        final GoogleSignInClient googleSIC = googleAdapter.configureGoogleSIC(this);
        final GoogleSignInAccount account = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        /* Navigation bar */
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        break;
                    case R.id.navigation_profile:
                        item.setChecked(true);
                        Intent profile = new Intent(HomeActivity.this,ProfileActivity.class);
                        profile.putExtra(GOOGLE_ACCOUNT, account);
                        startActivity(profile);
                        break;
                }
                return false;
            }
        });
    }

}
