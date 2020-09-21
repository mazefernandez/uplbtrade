package com.mazefernandez.uplbtrade.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private int customerId;
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    BottomNavigationView navigation;

    private GoogleAccountAdapter googleAdapter = new GoogleAccountAdapter();

    /* AR Launchers - used to replace onActivityResults */
    ActivityResultLauncher<Intent> editProfile = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = result.getData();

            assert intent != null;
            Bundle editInfo = intent.getExtras();
            assert editInfo != null;
            String editAddress = editInfo.getString("NEW_ADDRESS");
            String editContactNo = editInfo.getString("NEW_CONTACT");

            TextView profileAddress = findViewById(R.id.profile_address);
            TextView contactNo = findViewById(R.id.contactNo);
            profileAddress.setText(editAddress);
            contactNo.setText(editContactNo);
            Customer customer = new Customer(editAddress, editContactNo);

            /* UPDATE customer info in database */
            UPLBTrade.retrofitClient.updateCustomer(new Callback<Customer>() {
                @Override
                public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                    System.out.println("Updated Customer");
                }

                @Override
                public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                    System.out.println("Failed to update customer");
                    System.out.println(t.getMessage());
                }
            }, customer, customerId);
            finish();
            startActivity(getIntent());
        }
    });

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

        SearchView profileSearch = findViewById(R.id.profile_search);
        Button editCustomer = findViewById(R.id.editCustomer);
        FloatingActionButton addItem = findViewById(R.id.addItem);
        ImageButton settings = findViewById(R.id.settings);
        ImageButton flag = findViewById(R.id.flag);
        recyclerView = findViewById(R.id.recycler_view);

        /* Configure Google Sign in */
        final GoogleSignInClient googleSIC = googleAdapter.configureGoogleSIC(this);
        final GoogleSignInAccount account = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);
        assert account != null;

        /* Display current Customer */
        displayCustomer(account);

        /*SharedPref to save customer_id*/
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        customerId = pref.getInt("customer_id", -1);

        /* TODO: Fix different view Own profile vs Another profile */
        /* TODO: Change Customer Name from Google Sign in to from database */


        /* Show customer items */
        UPLBTrade.retrofitClient.getCustomerItems(new Callback<List<Item>>() {
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {

                ArrayList<Item> items = (ArrayList<Item>) response.body();
                assert items != null;
                ArrayList<Item> itemList = new ArrayList<>();
                itemList.clear();
                itemList.addAll(items);
                itemAdapter = new ItemAdapter(itemList);
                recyclerView.setAdapter(itemAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<Item>> call, @NonNull Throwable t) {
                System.out.println("Get Items Failed");
                System.out.println(t.getMessage());
            }
        }, customerId);

        /* Setup profile items */
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ProfileActivity.this,3);
        recyclerView.setLayoutManager(layoutManager);

        /* Set up search view */
        profileSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (itemAdapter != null) {
                    itemAdapter.getFilter().filter(query);
                    itemAdapter.notifyDataSetChanged();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (itemAdapter != null) {
                    itemAdapter.getFilter().filter(query);
                    itemAdapter.notifyDataSetChanged();
                }
                return false;
            }
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

            intent.putExtras(userInfo);
            editProfile.launch(intent);
        });

        /* Upload Item */
        addItem.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, AddItemActivity.class);
            intent.putExtra("CUSTOMER_ID",customerId);
            insertItem.launch(intent);
        });

        /* Sign Out */
        settings.setOnClickListener(v -> googleSIC.signOut().addOnCompleteListener(task -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }));

        flag.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ReportUserActivity.class);
            intent.putExtra("CUSTOMER_ID",customerId);
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
            case R.id.navigation_offers:
                item.setChecked(true);
                Intent offer = new Intent(ProfileActivity.this, OffersActivity.class);
                offer.putExtra(GOOGLE_ACCOUNT,account);
                startActivity(offer);
                return true;
            case R.id.navigation_profile:
                return true;
            case R.id.navigation_purchases:
                item.setChecked(true);
                Intent purchase = new Intent(ProfileActivity.this, PurchasesActivity.class);
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
        Picasso.get().load(account.getPhotoUrl()).centerInside().fit().transform(new CircleTransformation()).into(profileImg);
        profileName.setText(account.getDisplayName());

        /*SharedPref to save customer_id */
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        /* Get customer data */
        UPLBTrade.retrofitClient.getCustomerByEmail(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                Customer customer = response.body();
                assert customer != null;
                profileAddress.setText(customer.getAddress());
                contactNo.setText(customer.getcontactNo());
                double rate = customer.getoverallRating();
                float r = (float) rate;
                rating.setRating(r);
                customerId = customer.getcustomerId();
                editor.putInt("customer_id", customerId);
                editor.apply();
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                System.out.println("Get Customer by email Failed");
                System.out.println(t.getMessage());
            }
        }, account.getEmail() );
    }
}
