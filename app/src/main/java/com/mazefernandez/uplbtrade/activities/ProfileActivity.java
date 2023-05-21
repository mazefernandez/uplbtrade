package com.mazefernandez.uplbtrade.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter;
import com.mazefernandez.uplbtrade.adapters.ItemAdapter;
import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.picasso.CircleTransformation;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.GOOGLE_ACCOUNT;

/* Customer's Profile */

public class ProfileActivity extends AppCompatActivity {
    private TextView profileName, profileAddress, contactNo;
    private ImageView profileImg;
    private RatingBar rating;
    private int sessionId;
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    BottomNavigationView navigation;
    DatabaseReference databaseReference;

    private final GoogleAccountAdapter googleAdapter = new GoogleAccountAdapter();

    /* Add new item to customer's shop */
    ActivityResultLauncher<Intent> insertItem = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
       if (result.getResultCode() == Activity.RESULT_OK) {
           Intent intent = result.getData();

           assert intent != null;
           if (intent.getIntExtra("CHECK",-1) == 1) {
               Toast.makeText(this, "Added new item", Toast.LENGTH_SHORT).show();
           }
           finish();
           startActivity(getIntent());
       }
    });

    /* Edit profile details */
    ActivityResultLauncher<Intent> editProfile = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = result.getData();

            assert intent != null;
            if (intent.getIntExtra("CHECK",-1) == 1) {
                Toast.makeText(this, "Edited profile", Toast.LENGTH_SHORT).show();
            }
            finish();
            startActivity(getIntent());
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /* Profile views */
        profileName = findViewById(R.id.profile_text);
        profileAddress = findViewById(R.id.profile_address);
        contactNo = findViewById(R.id.contactNo);
        profileImg = findViewById(R.id.profile_img);
        rating = findViewById(R.id.rating);
        ImageButton message = findViewById(R.id.message);

        SearchView profileSearch = findViewById(R.id.profile_search);
        Button editCustomer = findViewById(R.id.editCustomer);
        FloatingActionButton addItem = findViewById(R.id.addItem);
        ImageButton settings = findViewById(R.id.settings);
        recyclerView = findViewById(R.id.recycler_view);

        /* Configure Google Sign in */
        final GoogleSignInClient googleSIC = googleAdapter.configureGoogleSIC(this);
        final GoogleSignInAccount account = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);
        assert account != null;

        /* SharedPref to save customer_id */
        SharedPreferences pref = this.getSharedPreferences("uplbtrade", MODE_PRIVATE);
        int sessionId = pref.getInt("customer_id", -1);

        /* Initialize Firebase database */
        databaseReference = FirebaseDatabase.getInstance().getReference();

        /* Display current Customer */
        displayCustomer(account);
        displayCustomerItems(sessionId);

        /* Setup profile items */
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ProfileActivity.this,3);
        recyclerView.setLayoutManager(layoutManager);

        /* Set up search view */
        profileSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        /* Dropdown menu for settings */
        final PopupMenu settingsMenu = new PopupMenu(this, settings);
        final Menu menu = settingsMenu.getMenu();

        menu.add(0,0,0, "Rate App");
        menu.add(0,1,0, "Chat with Admin");
        menu.add(0,2,0,"Sign Out");

        settingsMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                /* Review application */
                case 0:
                    Intent review = new Intent(ProfileActivity.this, ReviewAppActivity.class);
                    startActivity(review);
                    return true;
                /* Admin Chat */
                case 1:
                    Intent chat = new Intent(ProfileActivity.this, AdminChatActivity.class);
                    startActivity(chat);
                    return true;
                /* Sign out */
                case 2:
                    googleSIC.signOut().addOnCompleteListener(task -> {
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    });
                    return true;
            }
            return false;
        });

        /* Edit Customer info */
        editCustomer.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            Bundle userInfo = new Bundle();
            String address = profileAddress.getText().toString();
            String contact = contactNo.getText().toString();
            userInfo.putParcelable(GOOGLE_ACCOUNT,account);
            userInfo.putString("ADDRESS",address);
            userInfo.putString("CONTACT",contact);
            userInfo.putInt("ID", sessionId);

            intent.putExtras(userInfo);
            editProfile.launch(intent);
        });

        /* Upload Item */
        addItem.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, AddItemActivity.class);
            intent.putExtra("CUSTOMER_ID",sessionId);
            insertItem.launch(intent);
        });

        /* Sign Out */
        settings.setOnClickListener(view -> settingsMenu.show());

        /* Message User */
        message.setOnClickListener(v -> {
            /* Add user to list of friends */

            Intent intent = new Intent(ProfileActivity.this, MessagesActivity.class);
            startActivity(intent);
        });


        /* Navigation bar */
        navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_profile);
        navigation.setOnNavigationItemSelectedListener(item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                item.setChecked(true);
                Intent home = new Intent(ProfileActivity.this,HomeActivity.class);
                home.putExtra(GOOGLE_ACCOUNT, account);
                startActivity(home);
                return true;
            case R.id.navigation_inbox:
                item.setChecked(true);
                Intent inbox = new Intent(ProfileActivity.this, MessagesActivity.class);
                inbox.putExtra(GOOGLE_ACCOUNT,account);
                startActivity(inbox);
                return true;
            case R.id.navigation_offers:
                item.setChecked(true);
                Intent offer = new Intent(ProfileActivity.this, OffersActivity.class);
                offer.putExtra(GOOGLE_ACCOUNT,account);
                startActivity(offer);
                return true;
            case R.id.navigation_profile:
                return true;
            case R.id.navigation_transactions:
                item.setChecked(true);
                Intent purchase = new Intent(ProfileActivity.this, TransactionsActivity.class);
                purchase.putExtra(GOOGLE_ACCOUNT, account);
                startActivity(purchase);
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

    /* Display current customer data */
    private void displayCustomer(@NotNull GoogleSignInAccount account) {
        if (account.getPhotoUrl() == null) {
            Picasso.get().load(R.drawable.placeholder).centerInside().fit().transform(new CircleTransformation()).into(profileImg);
        }
        else {
            Picasso.get().load(account.getPhotoUrl()).centerInside().fit().transform(new CircleTransformation()).into(profileImg);
            System.out.println(account.getPhotoUrl());
        }
        profileName.setText(account.getDisplayName());

        final SharedPreferences pref = this.getSharedPreferences("uplbtrade", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        /* Get customer data */
        UPLBTrade.retrofitClient.getCustomerByEmail(new Callback<Customer>() {

            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                Customer customer = response.body();
                assert customer != null;
                profileAddress.setText(customer.getAddress());
                contactNo.setText(customer.getContactNo());
                double rate = customer.getOverallRating();
                float r = (float) rate;
                rating.setRating(r);
                sessionId = customer.getCustomerId();
                editor.putInt("customer_id", sessionId);
                editor.apply();
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                System.out.println("Get Customer by email Failed");
                System.out.println(t.getMessage());
            }
        }, account.getEmail() );
    }
    public void displayCustomerItems(int customerId){
        /* Show customer items */
        UPLBTrade.retrofitClient.getCustomerItems(new Callback<List<Item>>() {
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
        }, customerId);
    }
}
