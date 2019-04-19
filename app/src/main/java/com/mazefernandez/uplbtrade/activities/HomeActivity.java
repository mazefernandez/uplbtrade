package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

/* Home Page (Item Catalog) */

public class HomeActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /* Home Views */
        SearchView homeSearch = findViewById(R.id.home_search);
        recyclerView = findViewById(R.id.recycler_view);

        /* Get Google Account */
        final GoogleSignInAccount account = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        /* Set up home items */
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(HomeActivity.this,3);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<Item> itemList = new ArrayList<>();
        ItemAdapter itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);

        /* Show customer items */
        UPLBTrade.retrofitClient.getItems(new Callback<List<Item>>() {
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {
                ArrayList<Item> items = (ArrayList<Item>) response.body();
                assert items != null;
                ArrayList<Item> itemList = new ArrayList<>(items);
                ItemAdapter itemAdapter = new ItemAdapter(itemList);
                recyclerView.setAdapter(itemAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<Item>> call, @NonNull Throwable t) {
                System.out.println("Get Items Failed");
                System.out.println(t.getMessage());
            }
        });

        /* Navigation bar */
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        break;
                    case R.id.navigation_offers:
                        item.setChecked(true);
                        Intent offer = new Intent(HomeActivity.this, OffersActivity.class);
                        offer.putExtra(GOOGLE_ACCOUNT,account);
                        startActivity(offer);
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
