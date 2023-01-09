package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.adapters.ItemAdapter;
import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.User;
import com.mazefernandez.uplbtrade.picasso.CircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* Other Customer's Profile */

public class CustomerProfileActivity extends AppCompatActivity {
    private TextView profileName, profileAddress, contactNo;
    private ImageView profileImg;
    private RatingBar rating;
    private String customerEmail;
    ImageButton addFriend, flag;
    SearchView profileSearch;
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        /* Profile views */
        profileName = findViewById(R.id.profile_text);
        profileAddress = findViewById(R.id.profile_address);
        contactNo = findViewById(R.id.contactNo);
        profileImg = findViewById(R.id.profile_img);
        rating = findViewById(R.id.rating);
        addFriend = findViewById(R.id.add_friend);
        profileSearch = findViewById(R.id.profile_search);
        flag = findViewById(R.id.flag);
        recyclerView = findViewById(R.id.recycler_view);

        /* SharedPref to get customer email */
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        customerEmail = sharedPreferences.getString("customer_email", "-1");
        String customerName = sharedPreferences.getString("customer_name", "-1");

        /* Initialize Firebase database */
        databaseReference = FirebaseDatabase.getInstance().getReference();

        /* Display customer data */
        Customer customer = (Customer) getIntent().getSerializableExtra("CUSTOMER");
        displayCustomerItems(customer.getCustomerId());
        displayCustomer(customer);

        /* Check if users are already friends */
        databaseReference.child("users").child(customerEmail).child("friends").child(customerEmail).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                // users are not friends, set addFriend as visible
                if (String.valueOf(task.getResult().getValue()).equals("null")) {
                    addFriend.setVisibility(View.VISIBLE);
                }
                // users are friends, set addFriend as not visible
                else addFriend.setVisibility(View.GONE);
            }
        });

        /* Setup profile items */
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(CustomerProfileActivity.this,3);
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

        /* Message User */
        addFriend.setOnClickListener(v -> {
            String email = customer.getEmail().replace("@up.edu.ph","");
            /* Add user to list of friends */

            User user = new User(email);
            databaseReference.child("users").child(customerEmail).child("friends").child(email).setValue(user);
            User user2 = new User(customerEmail);
            databaseReference.child("users").child(email).child("friends").child(customerEmail).setValue(user2);

            Toast.makeText(this, "Added Friend", Toast.LENGTH_SHORT).show();
            addFriend.setVisibility(View.GONE);
        });

        flag.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerProfileActivity.this, ReportUserActivity.class);
            intent.putExtra("CUSTOMER",customer);
            startActivity(intent);
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
    private void displayCustomer(Customer customer) {
        if (customer.getImage().equals("placeholder")) {
            Picasso.get().load(R.drawable.placeholder).centerInside().fit().transform(new CircleTransformation()).into(profileImg);
        }
        else {
            Picasso.get().load(customer.getImage()).centerInside().fit().transform(new CircleTransformation()).into(profileImg);
        }
        String name = customer.getFirstName() + " " + customer.getLastName();
        profileAddress.setText(customer.getAddress());
        contactNo.setText(customer.getContactNo());
        float f = customer.getOverallRating().floatValue();
        rating.setRating(f);
        profileName.setText(name);
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
