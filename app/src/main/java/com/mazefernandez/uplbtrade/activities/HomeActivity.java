package com.mazefernandez.uplbtrade.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.adapters.ItemAdapter;
import com.mazefernandez.uplbtrade.models.Item;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.GOOGLE_ACCOUNT;
import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.TAG;

/* Home Page (Item Catalog) */

public class HomeActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkGoogleServices();

        /* Home Views */
        SearchView homeSearch = findViewById(R.id.home_search);
        recyclerView = findViewById(R.id.recycler_view);

        /* Set up home items */
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(HomeActivity.this,3);
        recyclerView.setLayoutManager(layoutManager);

        /* Get info from database and display customer items */
        UPLBTrade.retrofitClient.getItems(new Callback<List<Item>>() {
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {
                ArrayList<Item> items = (ArrayList<Item>) response.body();
                assert items != null;
                ArrayList<Item> itemList = new ArrayList<>(items);
                itemAdapter = new ItemAdapter(itemList);
                recyclerView.setAdapter(itemAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<Item>> call, @NonNull Throwable t) {
                System.out.println("Get Items Failed");
                System.out.println(t.getMessage());
            }
        });

        /* Get Google Account */
        final GoogleSignInAccount account = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        /* Set up home search view */
        homeSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (itemAdapter != null) {
                    itemAdapter.getFilter().filter(query);
                    itemAdapter.notifyDataSetChanged();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (itemAdapter != null) {
                    itemAdapter.getFilter().filter(query);
                    itemAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        /* Navigation bar */
        navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_offers:
                    item.setChecked(true);
                    Intent offer = new Intent(HomeActivity.this, OffersActivity.class);
                    offer.putExtra(GOOGLE_ACCOUNT,account);
                    startActivity(offer);
                    return true;
                case R.id.navigation_profile:
                    item.setChecked(true);
                    Intent profile = new Intent(HomeActivity.this,ProfileActivity.class);
                    profile.putExtra(GOOGLE_ACCOUNT, account);
                    startActivity(profile);
                    return true;
//                case R.id.navigation_purchases:
//                    item.setChecked(true);
//                    Intent purchase = new Intent(HomeActivity.this, PurchasesActivity.class);
//                    startActivity(purchase);
//                    return true;
            }
            return false;
        });
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    /* Check to see if user has correct version of google maps*/
    public void checkGoogleServices() {
        Log.w(TAG,"checkGoogleServices: Verifying Google Play Services Version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);
        /* User's Google Play Services i*/
        if (available == ConnectionResult.SUCCESS) {
            Log.w(TAG,"checkGoogleServices: Google Play Services is enabled");
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.w(TAG,"checkGoogleServices: Error occurred, but resolvable");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this,available,9001);
            assert dialog != null;
            dialog.show();
        }
        else {
            Toast.makeText(this,"There's a problem with your Google Play Services, maps may not work.", Toast.LENGTH_SHORT).show();
        }
    }
}
