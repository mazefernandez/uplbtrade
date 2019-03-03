package com.mazefernandez.uplbtrade.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter;
import com.mazefernandez.uplbtrade.adapters.ItemAdapter;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.picasso.CircleTransformation;
import com.mazefernandez.uplbtrade.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.GOOGLE_ACCOUNT;
import static com.mazefernandez.uplbtrade.models.RequestCode.ADD_ITEM;
import static com.mazefernandez.uplbtrade.models.RequestCode.EDIT_PROFILE;
/* Customer's Profile */

public class ProfileActivity extends AppCompatActivity {
    private TextView profileName, profileAddress, contactNo;
    private ImageView profileImg;
    private RatingBar rating;
    private SearchView profileSearch;
    private ImageButton editCustomer;
    private FloatingActionButton addItem;
    private RecyclerView recyclerView;

    private ArrayList<Item> itemArrayList;
    private ItemAdapter itemAdapter;

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
        editCustomer = findViewById(R.id.editCustomer);
        addItem = findViewById(R.id.addItem);
        recyclerView = findViewById(R.id.recycler_view);

        /* Configure Google Sign in */
        final GoogleSignInClient googleSIC = googleAdapter.configureGoogleSIC(this);
        final GoogleSignInAccount account = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        displayCustomer(account);

        /* Show customer items */
        itemArrayList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemArrayList);
        displayItems(itemArrayList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ProfileActivity.this,3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemAdapter);

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
                case 2:
                    Bundle editInfo = data.getExtras();
                    assert editInfo != null;
                    String editAddress = editInfo.getString("NEW_ADDRESS");
                    String editContactNo = editInfo.getString("NEW_CONTACT");

                    TextView profileAddress = findViewById(R.id.profile_address);
                    TextView contactNo = findViewById(R.id.contactNo);
                    profileAddress.setText(editAddress);
                    contactNo.setText(editContactNo);
                    break;
                /* Results from add item */
                case 3:
                    if (getIntent().hasExtra("NAME")) {
                        Bundle itemInfo = data.getExtras();
                        String itemName = itemInfo.getString("NAME");
                        String itemDesc = itemInfo.getString("DESC");
                        String itemPrice = itemInfo.getString("PRICE");
                        Double price = Double.parseDouble(itemPrice);
                        String itemCondition = itemInfo.getString("CONDITION");
                        itemArrayList.add(new Item(4, itemName, itemDesc, price, null, itemCondition, 1));
                        itemAdapter.notifyItemInserted(itemArrayList.size() - 1);
                        Toast.makeText(this, "Added new item", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
    }

    /* Display current customer data */
    private void displayCustomer(GoogleSignInAccount account) {
        Picasso.get().load(account.getPhotoUrl()).centerInside().fit().transform(new CircleTransformation()).into(profileImg);
        profileName.setText(account.getDisplayName());
        profileAddress.setText("Pedro R. Sandoval Ave, Los Baños, Laguna");
        contactNo.setText("09876543210");
        rating.setRating((float) 3.5);
    }

    /* Display customer's items */
    private void displayItems(ArrayList itemArrayList) {
        itemArrayList.add(new Item(1,"TC7 Mathbook","",200.00, null,"Used but good",1));
        itemArrayList.add(new Item(2,"CASIO Scientific Calculator","",400.00, null,"Old",1));
        itemArrayList.add(new Item(3,"Hum 3 Reader","",150.00, null,"Never used",1));
    }

}
