package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
    private String customerEmail, customerName;
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
        ImageButton message = findViewById(R.id.messageButton);

        SearchView profileSearch = findViewById(R.id.profile_search);
        ImageButton flag = findViewById(R.id.flag);
        recyclerView = findViewById(R.id.recycler_view);

        /* SharedPref to get customer email */
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        customerEmail = sharedPreferences.getString("customer_email", "-1");
        customerName = sharedPreferences.getString("customer_name", "-1");

        /* Initialize Firebase database */
        databaseReference = FirebaseDatabase.getInstance().getReference();

        /* Display customer data */
        Customer customer = (Customer) getIntent().getSerializableExtra("CUSTOMER");
        displayCustomerItems(customer.getCustomerId());
        displayCustomer(customer);

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
        message.setOnClickListener(v -> {
            String email = customer.getEmail().replace("@up.edu.ph","");
            /* Add user to list of friends */

            User user = new User(customer.getFirstName() + " " + customer.getLastName());
            databaseReference.child("users").child(customerEmail).child("friends").child(email).setValue(user);
            User user2 = new User(customerName);
            databaseReference.child("users").child(email).child("friends").child(customerEmail).setValue(user2);

            /* Create chat room */
            String chatRoomKey = databaseReference.child("chats").push().getKey();
            assert chatRoomKey != null;
            String memberKey1 = databaseReference.child("chats").child(chatRoomKey).child("members").push().getKey();
            assert memberKey1 != null;
            databaseReference.child("chats").child(chatRoomKey).child("members").child(memberKey1).setValue(customerEmail);
            String memberKey2 = databaseReference.child("chats").child(chatRoomKey).child("members").push().getKey();
            assert memberKey2 != null;
            databaseReference.child("chats").child(chatRoomKey).child("members").child(memberKey2).setValue(email);

            /* Add chat to list of chats for user */
            String chatKey = databaseReference.child("users").child(customerEmail).child("chats").push().getKey();
            assert chatKey != null;
            String chatKey2 = databaseReference.child("users").child(email).child("chats").push().getKey();
            assert chatKey2 != null;

            databaseReference.child("users").child(customerEmail).child("chats").child(chatKey).setValue(chatRoomKey);
            databaseReference.child("users").child(email).child("chats").child(chatKey2).setValue(chatRoomKey);

            Intent intent = new Intent(CustomerProfileActivity.this, MessagesActivity.class);
            startActivity(intent);
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
            /* Firebase instances */
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();

            /* retrieve image from firebase */
            StorageReference ref = storageReference.child("profiles/" + customer.getImage());

            final long ONE_MEGABYTE = 1024 * 1024 * 5;
            ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                System.out.println("Successfully read image");
                profileImg.setImageBitmap(bitmap);
            }).addOnFailureListener(fail -> System.out.println("Failed to read image" + fail));

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
