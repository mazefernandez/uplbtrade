package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter;
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
    private GoogleAccountAdapter googleAdapter = new GoogleAccountAdapter();
    private Button signOut;

    private RecyclerView recyclerView;
    private ArrayList<Item> itemList;
    private ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SearchView homeSearch = findViewById(R.id.home_search);
        recyclerView = findViewById(R.id.recycler_view);
        Button signOut = findViewById(R.id.sign_out);

        /* Configure Google Sign in */
        final GoogleSignInClient googleSIC = googleAdapter.configureGoogleSIC(this);
        final GoogleSignInAccount account = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        /* Show customer items */
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList);
        displayItems(itemList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(HomeActivity.this,3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemAdapter);

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
                        Intent offer = new Intent(HomeActivity.this, OfferActivity.class);
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

    /* Display customers' items */
    private void displayItems(final ArrayList<Item> itemList) {
        UPLBTrade.retrofitClient.getItems(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful()) {
                    itemList.addAll(response.body());
                }
                else {
                    System.out.println("display items error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                System.out.println("Get Items Failed");
                System.out.println(t.getMessage());
            }
        });
    }
}
