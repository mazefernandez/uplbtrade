package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter;
import com.mazefernandez.uplbtrade.adapters.TransactionAdapter;
import com.mazefernandez.uplbtrade.models.Transaction;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.GOOGLE_ACCOUNT;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

/* Shows a customer's past purchases in the application */

public class TransactionsActivity extends AppCompatActivity {
    RecyclerView buyer;
    RecyclerView seller;
    TransactionAdapter buyerAdapter;
    TransactionAdapter sellerAdapter;

    private final GoogleAccountAdapter googleAdapter = new GoogleAccountAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        buyer = findViewById(R.id.buyer);
        seller = findViewById(R.id.seller);

        /* Configure Google Sign in */
        final GoogleSignInClient googleSIC = googleAdapter.configureGoogleSIC(this);
        final GoogleSignInAccount account = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        /* SharedPref to save customer_id */
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        int sessionId = pref.getInt("customer_id", -1);

        ArrayList<Transaction> transactions = new ArrayList<>();
        buyerAdapter = new TransactionAdapter(transactions);
        buyer.setAdapter(buyerAdapter);
        sellerAdapter = new TransactionAdapter(transactions);
        seller.setAdapter(sellerAdapter);

        /* Show customer's transactions for items they bought */
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TransactionsActivity.this,LinearLayoutManager.HORIZONTAL,false);
        buyer.setLayoutManager(layoutManager);

        /* retrieve bought transactions */
        UPLBTrade.retrofitClient.getBuyerTransactions(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<List<Transaction>> call, @NonNull Response<List<Transaction>> response) {
                ArrayList<Transaction> transactions = (ArrayList<Transaction>) response.body();
                assert transactions != null;
                ArrayList<Transaction> buyerArrayList = new ArrayList<>(transactions);
                buyerAdapter = new TransactionAdapter(buyerArrayList);
                buyer.setAdapter(buyerAdapter);
                System.out.println("Buyer Transactions received");
            }
            @Override
            public void onFailure(@NonNull retrofit2.Call<List<Transaction>> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, sessionId);

        /* Show customer's transactions for items they sold */
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(TransactionsActivity.this,LinearLayoutManager.HORIZONTAL,false);
        seller.setLayoutManager(layoutManager2);

        /* retrieve bought transactions */
        UPLBTrade.retrofitClient.getSellerTransactions(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<List<Transaction>> call, @NonNull Response<List<Transaction>> response) {
                ArrayList<Transaction> transactions = (ArrayList<Transaction>) response.body();
                assert transactions != null;
                ArrayList<Transaction> sellerArrayList = new ArrayList<>(transactions);
                sellerAdapter = new TransactionAdapter(sellerArrayList);
                seller.setAdapter(sellerAdapter);
                System.out.println("Seller Transactions received");
            }
            @Override
            public void onFailure(@NonNull retrofit2.Call<List<Transaction>> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, sessionId);

        /* Navigation bar */
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_transactions);
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    item.setChecked(true);
                    Intent home = new Intent(TransactionsActivity.this,HomeActivity.class);
                    home.putExtra(GOOGLE_ACCOUNT, account);
                    startActivity(home);
                    return true;
                case R.id.navigation_inbox:
                    item.setChecked(true);
                    Intent inbox = new Intent(TransactionsActivity.this, MessagesActivity.class);
                    inbox.putExtra(GOOGLE_ACCOUNT,account);
                    startActivity(inbox);
                    return true;
                case R.id.navigation_offers:
                    item.setChecked(true);
                    Intent offer = new Intent(TransactionsActivity.this, OffersActivity.class);
                    offer.putExtra(GOOGLE_ACCOUNT,account);
                    startActivity(offer);
                    return true;
                case R.id.navigation_profile:
                    item.setChecked(true);
                    Intent profile = new Intent(TransactionsActivity.this, ProfileActivity.class);
                    profile.putExtra(GOOGLE_ACCOUNT,account);
                    startActivity(profile);
                    return true;
                case R.id.navigation_transactions:
                    return true;
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
