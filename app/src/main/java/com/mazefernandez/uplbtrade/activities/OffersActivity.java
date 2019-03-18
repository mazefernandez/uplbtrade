package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.adapters.OfferAdapter;
import com.mazefernandez.uplbtrade.models.Offer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.GOOGLE_ACCOUNT;

/* Buying and Selling Offers */

public class OffersActivity extends AppCompatActivity {
    private int sessionId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        /* Offers Views */
        RecyclerView buying = findViewById(R.id.buying);
        RecyclerView selling = findViewById(R.id.selling);

        /*SharedPref to save customer_id*/
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        sessionId = pref.getInt("customer_id",-1);

        /* Get Google Account */
        final GoogleSignInAccount account = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        /* Show customer's offers for items to be bought */
        ArrayList<Offer> buyingArrayList = new ArrayList<>();
        OfferAdapter buyingAdapter = new OfferAdapter(buyingArrayList);
        displayOffersBuying(buyingArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(OffersActivity.this,LinearLayoutManager.HORIZONTAL,false);
        buying.setLayoutManager(layoutManager);
        buying.setAdapter(buyingAdapter);

        /* Show customer's offers for items to be sold */
        ArrayList<Offer> sellingArrayList = new ArrayList<>();
        OfferAdapter sellingAdapter = new OfferAdapter(sellingArrayList);
        displayOffersSelling(sellingArrayList);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(OffersActivity.this,LinearLayoutManager.HORIZONTAL,false);
        selling.setLayoutManager(layoutManager2);
        selling.setAdapter(sellingAdapter);

        /* Navigation bar */
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_offers);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        item.setChecked(true);
                        Intent home = new Intent(OffersActivity.this,HomeActivity.class);
                        home.putExtra(GOOGLE_ACCOUNT, account);
                        startActivity(home);
                        break;
                    case R.id.navigation_offers:
                        break;
                    case R.id.navigation_profile:
                        item.setChecked(true);
                        Intent profile = new Intent(OffersActivity.this,ProfileActivity.class);
                        profile.putExtra(GOOGLE_ACCOUNT, account);
                        startActivity(profile);
                        break;
                }
                return false;
            }
        });
    }

    /* Display items from catalog */
    private void displayOffersBuying(final ArrayList<Offer> offerArrayList) {
        UPLBTrade.retrofitClient.getOfferBuying(new Callback<List<Offer>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<List<Offer>> call, @NonNull Response<List<Offer>> response) {
                ArrayList<Offer> offers = (ArrayList<Offer>) response.body();
                offerArrayList.clear();
                offerArrayList.addAll(offers);
                System.out.println("Retrieved all offers: buying");
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<List<Offer>> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, sessionId);
    }

    /* Display items from profile */
    private void displayOffersSelling(final ArrayList<Offer> offerArrayList) {
        UPLBTrade.retrofitClient.getOfferSelling(new Callback<List<Offer>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<List<Offer>> call, @NonNull Response<List<Offer>> response) {
                offerArrayList.clear();
                offerArrayList.addAll(response.body());
                System.out.println("Retrieved all offers: buying");
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<List<Offer>> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, sessionId);
    }
}
