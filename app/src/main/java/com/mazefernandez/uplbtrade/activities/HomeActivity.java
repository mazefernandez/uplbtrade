package com.mazefernandez.uplbtrade.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import com.mazefernandez.uplbtrade.models.Tag;
import com.mazefernandez.uplbtrade.models.Transaction;

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
    ArrayList<Tag> tagList = new ArrayList<>();
    ArrayList<Transaction> transactionList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkGoogleServices();

        /* SharedPref to save customer_id */
        final SharedPreferences pref = this.getSharedPreferences("uplbtrade", MODE_PRIVATE);
        int sessionId = pref.getInt("customer_id",-1);

        /* Home Views */
        SearchView homeSearch = findViewById(R.id.home_search);
        recyclerView = findViewById(R.id.recycler_view);

        /* Set up home items */
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(HomeActivity.this,2);
        recyclerView.setLayoutManager(layoutManager);

        /* Get info from database and display customer items */
        UPLBTrade.retrofitClient.getItems(new Callback<List<Item>>() {
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {
                ArrayList<Item> items = (ArrayList<Item>) response.body();
                if (items != null) {
                    ArrayList<Item> itemList = new ArrayList<>(items);
                    itemAdapter = new ItemAdapter(itemList);
                    recyclerView.setAdapter(itemAdapter);
                }
                System.out.println("Received Items");
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
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (itemAdapter != null) {
                    itemAdapter.getFilter().filter(query);
                    itemAdapter.notifyDataSetChanged();
                }
                return false;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextChange(String query) {
                if (itemAdapter != null) {
                    itemAdapter.getFilter().filter(query);
                    itemAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        /* Get tags from users' buyer interactions */
        getTransactionList(sessionId);

        for (Transaction transaction: transactionList) {
            getTagList(transaction.getItemId());
        }
        


        /* Navigation bar */
        navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_profile) {
                item.setChecked(true);
                Intent profile = new Intent(HomeActivity.this, ProfileActivity.class);
                profile.putExtra(GOOGLE_ACCOUNT, account);
                startActivity(profile);
                return true;
            } else if (item.getItemId() == R.id.navigation_inbox) {
                item.setChecked(true);
                Intent inbox = new Intent(HomeActivity.this, MessagesActivity.class);
                inbox.putExtra(GOOGLE_ACCOUNT, account);
                startActivity(inbox);
                return true;
            } else if (item.getItemId() == R.id.navigation_offers) {
                item.setChecked(true);
                Intent offer = new Intent(HomeActivity.this, OffersActivity.class);
                offer.putExtra(GOOGLE_ACCOUNT, account);
                startActivity(offer);
                return true;
            } else if (item.getItemId() == R.id.navigation_transactions) {
                item.setChecked(true);
                Intent purchase = new Intent(HomeActivity.this, TransactionsActivity.class);
                purchase.putExtra(GOOGLE_ACCOUNT, account);
                startActivity(purchase);
                return true;
            }
            else return item.getItemId() == R.id.navigation_home;
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

    public void getTransactionList(int sessionId){
        UPLBTrade.retrofitClient.getBuyerTransactions(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(@NonNull Call<List<Transaction>> call, @NonNull Response<List<Transaction>> response) {
                assert response.body() != null;
                transactionList.addAll(response.body());
                System.out.println("Received buyer transactions");
            }

            @Override
            public void onFailure(@NonNull Call<List<Transaction>> call, @NonNull Throwable t) {
                System.out.println("Get Transactions Failed");
                System.out.println(t.getMessage());
            }
        }, sessionId);

    }

    public void getTagList(int itemId) {
        UPLBTrade.retrofitClient.getTagsFromItem(new Callback<List<Tag>>() {
            @Override
            public void onResponse(@NonNull Call<List<Tag>> call, @NonNull Response<List<Tag>> response) {
                assert response.body() != null;
                tagList.addAll(response.body());
                System.out.println("Received Tags");
            }

            @Override
            public void onFailure(@NonNull Call<List<Tag>> call, @NonNull Throwable t) {
                System.out.println("Get Tags Failed");
                System.out.println(t.getMessage());
            }
        }, itemId);
    }
}
