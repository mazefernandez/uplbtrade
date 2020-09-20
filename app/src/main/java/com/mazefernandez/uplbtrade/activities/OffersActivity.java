package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    RecyclerView buying;
    RecyclerView selling;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        /* Offers Views */
        buying = findViewById(R.id.buying);
        selling = findViewById(R.id.selling);

        /*SharedPref to save customer_id*/
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        int sessionId = pref.getInt("customer_id", -1);

        /* Get Google Account */
        final GoogleSignInAccount account = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        /* Show customer's offers for items to be bought */
        ArrayList<Offer> buyingArrayList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(OffersActivity.this,LinearLayoutManager.HORIZONTAL,false);
        buying.setLayoutManager(layoutManager);
        OfferAdapter buyingAdapter = new OfferAdapter(buyingArrayList);
        buying.setAdapter(buyingAdapter);

        /* retrieve buying offers */
        UPLBTrade.retrofitClient.getOfferBuying(new Callback<List<Offer>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<List<Offer>> call, @NonNull Response<List<Offer>> response) {
                ArrayList<Offer> offers = (ArrayList<Offer>) response.body();
                assert offers != null;
                ArrayList<Offer> buyingArrayList = new ArrayList<>(offers);
                OfferAdapter buyingAdapter = new OfferAdapter(buyingArrayList);
                buying.setAdapter(buyingAdapter);
            }
            @Override
            public void onFailure(@NonNull retrofit2.Call<List<Offer>> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, sessionId);


        /* Show customer's offers for items to be sold */
        ArrayList<Offer> sellingArrayList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(OffersActivity.this,LinearLayoutManager.HORIZONTAL,false);
        selling.setLayoutManager(layoutManager2);
        OfferAdapter sellingAdapter = new OfferAdapter(sellingArrayList);
        selling.setAdapter(sellingAdapter);

        UPLBTrade.retrofitClient.getOfferSelling(new Callback<List<Offer>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<List<Offer>> call, @NonNull Response<List<Offer>> response) {
                ArrayList<Offer> offers = (ArrayList<Offer>) response.body();
                assert offers != null;
                ArrayList<Offer> sellingArrayList = new ArrayList<>(offers);

                for (int i=0; i<sellingArrayList.size(); i++) {
                    if (sellingArrayList.get(i).getStatus().equals("Declined")) {
                        sellingArrayList.remove(i);
                    }
                }
                OfferAdapter sellingAdapter = new OfferAdapter(sellingArrayList);
                selling.setAdapter(sellingAdapter);
                System.out.println("Retrieved all offers: buying");
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<List<Offer>> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, sessionId);

        /* Navigation bar */
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_offers);
        navigation.setOnNavigationItemSelectedListener(item -> {
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
                case R.id.navigation_purchases:
                    item.setChecked(true);
                    Intent purchase = new Intent(OffersActivity.this, PurchasesActivity.class);
                    startActivity(purchase);
                    break;
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
}
