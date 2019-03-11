package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter;
import com.mazefernandez.uplbtrade.adapters.OfferAdapter;
import com.mazefernandez.uplbtrade.models.Offer;

import java.util.ArrayList;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.GOOGLE_ACCOUNT;

public class OfferActivity extends AppCompatActivity {
    private RecyclerView buying;
    private RecyclerView selling;
    private ArrayList<Offer> buyingArrayList;
    private ArrayList<Offer> sellingArrayList;
    private OfferAdapter buyingAdapter;
    private OfferAdapter sellingAdapter;

    private GoogleAccountAdapter googleAdapter = new GoogleAccountAdapter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        buying = findViewById(R.id.buying);
        selling = findViewById(R.id.selling);

        /* Configure Google Sign in */
        final GoogleSignInClient googleSIC = googleAdapter.configureGoogleSIC(this);
        final GoogleSignInAccount account = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        /* Show customer's offers for items to be bought */
        buyingArrayList = new ArrayList<>();
        buyingAdapter = new OfferAdapter(buyingArrayList);
        displayOffers(buyingArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(OfferActivity.this,LinearLayoutManager.HORIZONTAL,false);
        buying.setLayoutManager(layoutManager);
        buying.setAdapter(buyingAdapter);

        /* Show customer's offers for items to be sold */
        sellingArrayList = new ArrayList<>();
        sellingAdapter = new OfferAdapter(sellingArrayList);
        displayOffers(sellingArrayList);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(OfferActivity.this,LinearLayoutManager.HORIZONTAL,false);
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
                        Intent home = new Intent(OfferActivity.this,HomeActivity.class);
                        home.putExtra(GOOGLE_ACCOUNT, account);
                        startActivity(home);
                        break;
                    case R.id.navigation_offers:
                        break;
                    case R.id.navigation_profile:
                        item.setChecked(true);
                        Intent profile = new Intent(OfferActivity.this,ProfileActivity.class);
                        profile.putExtra(GOOGLE_ACCOUNT, account);
                        startActivity(profile);
                        break;
                }
                return false;
            }
        });
    }

    /* Display items from catalog */
    private void displayOffers(ArrayList offerArrayList) {
        offerArrayList.add(new Offer(1,100.00,"Pending","Please, I need it", 1));
        offerArrayList.add(new Offer(2,200.00,"Accepted","Hello sana okay lang yung new price",2));
        offerArrayList.add(new Offer(3,130.30,"Pending","", 3));
    }
}
