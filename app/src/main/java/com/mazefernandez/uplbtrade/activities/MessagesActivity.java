package com.mazefernandez.uplbtrade.activities;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.GOOGLE_ACCOUNT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.adapters.UserAdapter;
import com.mazefernandez.uplbtrade.models.User;
import com.mazefernandez.uplbtrade.picasso.CircleTransformation;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class MessagesActivity extends AppCompatActivity {
    BottomNavigationView navigation;
    RecyclerView userList;
    UserAdapter userAdapter;
    ArrayList<User> users = new ArrayList<>();
    ImageView profileImg;
    TextView profileName;
    DatabaseReference databaseReference;
    String customerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        /* Get Google Account */
//        final GoogleSignInAccount account = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        /* Initialize Firebase database */
        databaseReference = FirebaseDatabase.getInstance().getReference();

        /* SharedPref to get customer email */
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        customerEmail = sharedPreferences.getString("customer_email", "-1");

        /* Messages activity views */
        profileImg = findViewById(R.id.profile_img);
        profileName = findViewById(R.id.profile_name);
        userList = findViewById(R.id.userList);

        /* Set up recyclerview chats */
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MessagesActivity.this,1);
        userList.setLayoutManager(layoutManager);

//        displayCustomer(account);

        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // iterate through customer's friends
                    for (DataSnapshot dataSnapshot : snapshot.child(customerEmail).child("friends").getChildren()) {
                        String tempEmail = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                        User user = new User(tempEmail);
                        users.add(user);
                    }
                    userAdapter = new UserAdapter(users);
                    userList.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Failed to load users" + error.getMessage());
            }
        });

        /* Navigation bar */
//        navigation = findViewById(R.id.navigation);
//        navigation.setSelectedItemId(R.id.navigation_home);
//        navigation.setOnNavigationItemSelectedListener(item -> {
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    item.setChecked(true);
//                    Intent home = new Intent(MessagesActivity.this, HomeActivity.class);
//                    home.putExtra(GOOGLE_ACCOUNT,account);
//                    startActivity(home);
//                    return true;
//                case R.id.navigation_inbox:
//                    return true;
//                case R.id.navigation_offers:
//                    item.setChecked(true);
//                    Intent offer = new Intent(MessagesActivity.this, OffersActivity.class);
//                    offer.putExtra(GOOGLE_ACCOUNT,account);
//                    startActivity(offer);
//                    return true;
//                case R.id.navigation_profile:
//                    item.setChecked(true);
//                    Intent profile = new Intent(MessagesActivity.this,ProfileActivity.class);
//                    profile.putExtra(GOOGLE_ACCOUNT, account);
//                    startActivity(profile);
//                    return true;
//                case R.id.navigation_transactions:
//                    item.setChecked(true);
//                    Intent purchase = new Intent(MessagesActivity.this, TransactionsActivity.class);
//                    purchase.putExtra(GOOGLE_ACCOUNT, account);
//                    startActivity(purchase);
//                    return true;
//            }
//            return false;
//        });
    }

    private void displayCustomer(@NotNull GoogleSignInAccount account) {
        if (account.getPhotoUrl() == null) {
            Picasso.get().load(R.drawable.placeholder).centerInside().fit().transform(new CircleTransformation()).into(profileImg);
        } else {
            Picasso.get().load(account.getPhotoUrl()).centerInside().fit().transform(new CircleTransformation()).into(profileImg);
        }
        profileName.setText(account.getDisplayName());
    }
}