package com.mazefernandez.uplbtrade.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter;
import com.mazefernandez.uplbtrade.adapters.ItemAdapter;
import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.picasso.CircleTransformation;
import com.mazefernandez.uplbtrade.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.GOOGLE_ACCOUNT;
import static com.mazefernandez.uplbtrade.models.RequestCode.ADD_ITEM;
import static com.mazefernandez.uplbtrade.models.RequestCode.EDIT_PROFILE;
/* Customer's Profile */

public class ProfileActivity extends AppCompatActivity {
    private TextView profileName, profileAddress, contactNo;
    private ImageView profileImg;
    private RatingBar rating;
    private SearchView profileSearch;

    private int customerId;
    private List<Item> itemList;

    private GoogleAccountAdapter googleAdapter = new GoogleAccountAdapter();

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
        profileSearch = findViewById(R.id.profile_search);
        ImageButton editCustomer = findViewById(R.id.editCustomer);
        FloatingActionButton addItem = findViewById(R.id.addItem);
        ImageButton settings = findViewById(R.id.settings);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        /* Configure Google Sign in */
        final GoogleSignInClient googleSIC = googleAdapter.configureGoogleSIC(this);
        final GoogleSignInAccount account = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        /*SharedPref to save customer_id*/
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        /* Retrieve current Customer */
        displayCustomer(account);

        /* Show customer items */
        itemList = new ArrayList<>();
        ItemAdapter itemAdapter = new ItemAdapter(itemList);
        displayItems(itemList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ProfileActivity.this,3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemAdapter);

        /* Search Items */
        profileSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        /* Edit Customer info */
        editCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                Bundle userInfo = new Bundle();
                String address = profileAddress.getText().toString();
                String contact = contactNo.getText().toString();
                userInfo.putParcelable(GOOGLE_ACCOUNT,account);
                userInfo.putString("ADDRESS",address);
                userInfo.putString("CONTACT",contact);

                intent.putExtras(userInfo);
                startActivityForResult(intent,EDIT_PROFILE);
            }
        });

        /* Upload Item */
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, AddItemActivity.class);
                startActivityForResult(intent, ADD_ITEM);
            }
        });

        /* Sign Out */
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSIC.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
        });

        /* Navigation bar */
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_profile);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    item.setChecked(true);
                    Intent home = new Intent(ProfileActivity.this,HomeActivity.class);
                    home.putExtra(GOOGLE_ACCOUNT, account);
                    startActivity(home);
                    break;
                case R.id.navigation_offers:
                    item.setChecked(true);
                    Intent offer = new Intent(ProfileActivity.this, OfferActivity.class);
                    offer.putExtra(GOOGLE_ACCOUNT,account);
                    startActivity(offer);
                    break;
                case R.id.navigation_profile:
                    break;
            }
            return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                /* Results from edit activity */
                case EDIT_PROFILE:
                    Bundle editInfo = data.getExtras();
                    assert editInfo != null;
                    String editAddress = editInfo.getString("NEW_ADDRESS");
                    String editContactNo = editInfo.getString("NEW_CONTACT");

                    TextView profileAddress = findViewById(R.id.profile_address);
                    TextView contactNo = findViewById(R.id.contactNo);
                    profileAddress.setText(editAddress);
                    contactNo.setText(editContactNo);
                    Customer customer = new Customer(editAddress, editContactNo);
                    UPLBTrade.retrofitClient.updateCustomer(new Callback<Customer>() {
                        @Override
                        public void onResponse(Call<Customer> call, Response<Customer> response) {
                            System.out.println("Updated Customer");
                        }

                        @Override
                        public void onFailure(Call<Customer> call, Throwable t) {
                            System.out.println("Failed to update customer");
                            System.out.println(t.getMessage());
                        }
                    }, customer, customerId);
                    break;
                /* Results from add item */
                case ADD_ITEM:
                    Bundle itemInfo = data.getExtras();
                    if (itemInfo != null) {
                        String itemName = itemInfo.getString("NAME");
                        String itemDesc = itemInfo.getString("DESC");
                        String itemPrice = itemInfo.getString("PRICE");
                        Double price = Double.parseDouble(itemPrice);
                        String itemCondition = itemInfo.getString("CONDITION");
                        Item item = new Item(itemName, itemDesc, price, null, itemCondition, customerId);

                        UPLBTrade.retrofitClient.addItem(new Callback<Item>() {
                            @Override
                            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                                System.out.println("Added Item");
                            }
                            @Override
                            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                                System.out.println("Failed to add item");
                                System.out.println(t.getMessage());
                            }
                        }, item);
                        itemList.clear();
                        displayItems(itemList);
                        Toast.makeText(this, "Added new item", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
    }
    @Override
    public void onResume() {
        super.onResume();
        itemList.clear();
        displayItems(itemList);
    }

    /* Display current customer data */
    private void displayCustomer(GoogleSignInAccount account) {
        Picasso.get().load(account.getPhotoUrl()).centerInside().fit().transform(new CircleTransformation()).into(profileImg);
        profileName.setText(account.getDisplayName());

        /*SharedPref to save customer_id*/
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        UPLBTrade.retrofitClient.getCustomerByEmail(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                profileAddress.setText(response.body().getAddress());
                contactNo.setText(response.body().getcontactNo());
                double rate = response.body().getoverallRating();
                float r = (float) rate;
                rating.setRating(r);
                customerId = response.body().getcustomerId();
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

    /* Display customer's items */
    private void displayItems(final List itemList) {
        int customerId;
        /*SharedPref to save customer_id*/
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        customerId = pref.getInt("customer_id", -1);
        UPLBTrade.retrofitClient.getCustomerItems(new Callback<List<Item>>() {
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {
                if (response.isSuccessful()) {
                    itemList.clear();
                    itemList.addAll(response.body());
                }
                else {
                    System.out.println("display items error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Item>> call, @NonNull Throwable t) {
                System.out.println("Get Items Failed");
                System.out.println(t.getMessage());
            }
        }, customerId);
    }
}
