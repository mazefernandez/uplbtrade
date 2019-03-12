package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.text.style.SubscriptSpan;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter;
import com.mazefernandez.uplbtrade.adapters.OfferAdapter;
import com.mazefernandez.uplbtrade.models.Offer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.GOOGLE_ACCOUNT;

public class OffersActivity extends AppCompatActivity {
    private RecyclerView buying;
    private RecyclerView selling;
    private ArrayList<Offer> buyingArrayList;
    private ArrayList<Offer> sellingArrayList;
    private OfferAdapter buyingAdapter;
    private OfferAdapter sellingAdapter;

    private GoogleAccountAdapter googleAdapter = new GoogleAccountAdapter();

    private int sessionId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        /*SharedPref to save customer_id*/
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        sessionId = pref.getInt("customer_id",-1);

        buying = findViewById(R.id.buying);
        selling = findViewById(R.id.selling);

        /* Configure Google Sign in */
        final GoogleSignInClient googleSIC = googleAdapter.configureGoogleSIC(this);
        final GoogleSignInAccount account = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        /* Show customer's offers for items to be bought */
        buyingArrayList = new ArrayList<>();
        buyingAdapter = new OfferAdapter(buyingArrayList);
        displayOffersBuying(buyingArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(OffersActivity.this,LinearLayoutManager.HORIZONTAL,false);
        buying.setLayoutManager(layoutManager);
        buying.setAdapter(buyingAdapter);

        /* Show customer's offers for items to be sold */
        sellingArrayList = new ArrayList<>();
        sellingAdapter = new OfferAdapter(sellingArrayList);
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
    private void displayOffersBuying(final ArrayList offerArrayList) {
        UPLBTrade.retrofitClient.getOfferBuying(new Callback<List<Offer>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<List<Offer>> call, @NonNull Response<List<Offer>> response) {
                offerArrayList.addAll(response.body());
                System.out.println("Retrieved all offers: buying");
            }

            @Override
            public void onFailure(retrofit2.Call<List<Offer>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        }, sessionId);
    }

    private void displayOffersSelling(final ArrayList offerArrayList) {
        UPLBTrade.retrofitClient.getOfferSelling(new Callback<List<Offer>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<List<Offer>> call, @NonNull Response<List<Offer>> response) {
                offerArrayList.addAll(response.body());
                System.out.println("Retrieved all offers: buying");
            }

            @Override
            public void onFailure(retrofit2.Call<List<Offer>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        }, sessionId);
    }
}
