package com.mazefernandez.uplbtrade.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.adapters.ItemAdapter;
import com.mazefernandez.uplbtrade.models.Id;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.Tag;
import com.mazefernandez.uplbtrade.models.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.GOOGLE_ACCOUNT;
import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.TAG;

/* Home Page (Item Catalog) */

public class HomeActivity extends AppCompatActivity {
    int sessionId;
    String customerEmail;
    Boolean customerBought;
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    BottomNavigationView navigation;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkGoogleServices();

        /* Get Google Account */
        final GoogleSignInAccount account = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        final SharedPreferences pref = this.getSharedPreferences("uplbtrade", MODE_PRIVATE);

        /* SharedPref to save customer_id */
        sessionId = pref.getInt("customer_id",-1);
        /* SharedPref to get customer email */
        customerEmail = pref.getString("customer_email", "-1");

        /* Home Views */
        SearchView homeSearch = findViewById(R.id.home_search);
        recyclerView = findViewById(R.id.recycler_view);
        ImageButton showRecommended = findViewById(R.id.filter);

        /* Set up home items */
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(HomeActivity.this,2);
        recyclerView.setLayoutManager(layoutManager);

        /* Get tags from users' buyer interactions */
        getTransactionList(sessionId);
        /* Check if user has previously bought items */
        checkBought();
        customerBought = pref.getBoolean("customer_bought",false);

        /* Dropdown menu for filter */
        final PopupMenu filterMenu = new PopupMenu(this, showRecommended);
        final Menu menu = filterMenu.getMenu();

        menu.add(0, 0, 0, "Recommended Items");
        menu.add(0, 1, 0, "All Items");

        filterMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                /* Recommended Items */
                case 0:
                    if (customerBought) {
                        checkBought();
                    }
                    else {
                        Toast.makeText(this,"Purchase Items to see Recommendations", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                /* All Items */
                case 1:
                    /* Get info from database and display customer items */
                    allItems();
                    return true;
            }
            return false;
        });

        showRecommended.setOnClickListener(v -> filterMenu.show());

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
    private void checkGoogleServices() {
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
    /* Show all items in home page */
    private void allItems() {
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
    }
    /* Look for buyer transactions for user */
    private void getTransactionList(int sessionId){
        UPLBTrade.retrofitClient.getBuyerTransactions(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(@NonNull Call<List<Transaction>> call, @NonNull Response<List<Transaction>> response) {
                assert response.body() != null;
                ArrayList<Transaction> transactionList = new ArrayList<>(response.body());
                StringBuilder ids = new StringBuilder();
                for (Transaction transaction: transactionList) {
                    ids.append(transaction.getItemId()).append("-");
                }
                if (!ids.toString().isEmpty()) {
                    databaseReference.child("users").child(customerEmail).child("bought").setValue(ids.toString());
                    System.out.println("Received buyer transactions");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Transaction>> call, @NonNull Throwable t) {
                System.out.println("Get Transactions Failed");
                System.out.println(t.getMessage());
            }
        }, sessionId);

    }
    /* Check if user has bought items before */
    private void checkBought() {
        databaseReference.child("users").child(customerEmail).child("bought").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                // users has not bought any items
                if (String.valueOf(task.getResult().getValue()).equals("null")) {
                    final SharedPreferences pref = this.getSharedPreferences("uplbtrade", MODE_PRIVATE);
                    final SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("customer_bought", false);
                    editor.apply();
                    allItems();
                }
                else {
                    // user has bought items
                    String items = String.valueOf(task.getResult().getValue());
                    String[] split = items.split("-");
                    for (String str : split) {
                        int id = Integer.parseInt(str);
                        retrieveTags(id);
                    }
                    final SharedPreferences pref = this.getSharedPreferences("uplbtrade", MODE_PRIVATE);
                    final SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("customer_bought", true);
                    editor.apply();
                }
            }
        });
    }
    /* Retrieve users' tags from realtime database */
    private void retrieveTags(int itemId) {
        UPLBTrade.retrofitClient.getTagsFromItem(new Callback<List<Tag>>() {
            @Override
            public void onResponse(@NonNull Call<List<Tag>> call, @NonNull Response<List<Tag>> response) {
                assert response.body() != null;
                ArrayList<Tag> tags = new ArrayList<>(response.body());
                for (Tag tag : tags){
                    databaseReference.child("users").child(customerEmail).child("tags").child(tag.getTagName()).setValue(true);
                }
                splitTags();
                System.out.println("Received Tags");
            }

            @Override
            public void onFailure(@NonNull Call<List<Tag>> call, @NonNull Throwable t) {
                System.out.println("Get Tags Failed");
                System.out.println(t.getMessage());
            }
        }, itemId);
    }
    /* Separate the tags from the realtime database */
    private void splitTags() {
        databaseReference.child("users").child(customerEmail).child("tags").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting tags", task.getException());
            }
            else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                // users has no favorite tags
                if (String.valueOf(task.getResult().getValue()).equals("null")) {
                    allItems();
                }
                // user has favorite tags
                else {
                    String tags = String.valueOf(task.getResult().getValue());
                    String regex = "[{},]";
                    String replaced = tags.replaceAll(regex,"");
                    String[] split = replaced.split("=true");
                    for (int i=0; i<split.length; i++) {
                        split[i] = split[i].trim();
                    }
                    ArrayList<String> tagList = new ArrayList<>(Arrays.asList(split));
                    recommendedItems(tagList);
                }
            }
        });
    }
    /* Get the ids of recommended items */
    private void recommendedItems(ArrayList<String> tags) {
        UPLBTrade.retrofitClient.searchTagItems(new Callback<List<Id>>() {
            @Override
            public void onResponse(@NonNull Call<List<Id>> call, @NonNull Response<List<Id>> response) {
                assert response.body() != null;
                ArrayList<Id> idList = new ArrayList<>(response.body());
                StringBuilder ids = new StringBuilder();
                for (Id id: idList) {
                    ids.append(id.getItemId()).append("-");
                }
                databaseReference.child("users").child(customerEmail).child("items").setValue(ids.toString());
                splitItems();
                System.out.println("Received recommended item ids");
            }
            @Override
            public void onFailure(@NonNull Call<List<Id>> call, @NonNull Throwable t) {
                System.out.println("Get Item Ids Failed");
                System.out.println(t.getMessage());
            }
        }, tags);
    }
    /* Split the item ids*/
    private void splitItems() {
        databaseReference.child("users").child(customerEmail).child("items").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));

                /* Get the item ids */
                String itemIds = String.valueOf(task.getResult().getValue());
                String[] split = itemIds.split("-");
                ArrayList<Integer> ids = new ArrayList<>();
                for (String str : split) {
                    Integer id = Integer.parseInt(str);
                    ids.add(id);
                }
                populateItems(ids);
            }
        });
    }
    private void populateItems(ArrayList<Integer> ids){
        UPLBTrade.retrofitClient.getItemsByIds(new Callback<List<Item>>() {
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {
                ArrayList<Item> items = (ArrayList<Item>) response.body();
                if (items != null) {
                    ArrayList<Item> itemList = new ArrayList<>(items);
                    itemAdapter = new ItemAdapter(itemList);
                    recyclerView.setAdapter(itemAdapter);
                }
                System.out.println("Received Items by Ids");
            }
            @Override
            public void onFailure(@NonNull Call<List<Item>> call, @NonNull Throwable t) {
                System.out.println("Get Items by Ids Failed");
                System.out.println(t.getMessage());
            }
        }, ids);
    }
}
