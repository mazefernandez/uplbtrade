package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.GOOGLE_ACCOUNT;

/* Home Page (Item Catalog) */

public class HomeActivity extends AppCompatActivity {
    private GoogleAccountAdapter googleAdapter = new GoogleAccountAdapter();
    private Button signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
       // SearchView homeSearch = findViewById(R.id.home_search);
        Button signOut = findViewById(R.id.sign_out);

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

        /* Sign-out implementation */
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSIC.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //brings user to login after sign out
                        Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
        });
    }

}
