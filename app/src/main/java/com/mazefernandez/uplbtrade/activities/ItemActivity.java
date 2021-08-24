package com.mazefernandez.uplbtrade.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.Offer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.BitmapFactory.decodeByteArray;

/* Item details */

public class ItemActivity extends AppCompatActivity {
    private TextView itemOwner;
    private TextView itemName;
    private TextView itemDesc;
    private TextView itemPrice;
    private ImageView itemImg;
    private TextView itemCondition;
    private Button makeOffer;
    private Button seeOffer;
    private int itemId;
    private int sessionId;
    private int sellerId;
    private Offer offer;

    /* Edit item details */
    ActivityResultLauncher<Intent> editItem = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
       if (result.getResultCode() == Activity.RESULT_OK) {
           Intent intent = result.getData();
           assert intent != null;
           Bundle editInfo = intent.getExtras();
           assert editInfo != null;
           String name = editInfo.getString("NAME");
           String desc = editInfo.getString("DESC");
           String price = editInfo.getString("PRICE");
           String condition = editInfo.getString("CONDITION");
           String image = editInfo.getString("IMAGE");

           Bitmap bm = stringToBitMap(image);

           itemName.setText(name);
           itemDesc.setText(desc);
           itemPrice.setText(price);
           itemCondition.setText(condition);
           itemImg.setImageBitmap(bm);

           assert price != null;
           Double double_price = Double.parseDouble(price);
           /* Save to database */
           Item item = new Item(name, desc, double_price, image, condition);
           UPLBTrade.retrofitClient.updateItem(new Callback<Item>() {
               @Override
               public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                   System.out.println("Updated Item");
               }

               @Override
               public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                   System.out.println("Failed to update Item");
                   System.out.println(t.getMessage());
               }
           }, item, itemId);
       }
    });

    /* Create a new offer for item */
    ActivityResultLauncher<Intent> addOffer = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = result.getData();
            assert intent != null;
            Bundle offerInfo = intent.getExtras();
            if (offerInfo != null) {
                double offerPrice = offerInfo.getDouble("PRICE");
                String offerMessage = offerInfo.getString("MESSAGE");

                Offer offer = new Offer(offerPrice, "Pending", offerMessage, itemId, sessionId, sellerId);
                makeOffer(offer);
            }
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        /* SharedPref to save customer_id */
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        sessionId = pref.getInt("customer_id",-1);

        /* Item activity views */
        itemOwner = findViewById(R.id.item_owner);
        itemName = findViewById(R.id.item_name);
        itemDesc = findViewById(R.id.item_desc);
        itemPrice = findViewById(R.id.offer);
        itemImg = findViewById(R.id.item_img);
        itemCondition = findViewById(R.id.item_condition);
        ImageButton itemEdit = findViewById(R.id.item_edit);
        ImageButton itemDelete = findViewById(R.id.item_delete);
        makeOffer = findViewById(R.id.make_offer);
        seeOffer = findViewById(R.id.see_offer);

        /* Default visibility for offer */
        seeOffer.setVisibility(View.GONE);
        /* Retrieve item data */
        Item item = (Item) getIntent().getSerializableExtra("ITEM");
        assert item != null;
        itemId = item.getItemId();
        sellerId = item.getCustomerId();
        getOwner(item);

        /* Set View Visibilities */
        UPLBTrade.retrofitClient.getOfferBuying(new Callback<List<Offer>>() {
            @Override
            public void onResponse(@NonNull Call<List<Offer>> call, @NonNull Response<List<Offer>> response) {
                ArrayList<Offer> offers = (ArrayList<Offer>) response.body();
                assert offers != null;
                for (int i = 0; i<offers.size(); i++) {
                    if (offers.get(i).getItemId() == itemId) {
                        offer = new Offer(offers.get(i),offers.get(i).getOfferId());
                        seeOffer.setVisibility(View.VISIBLE);
                        makeOffer.setVisibility(View.GONE);
                        break;
                    }
                    else {
                        seeOffer.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Offer>> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, sessionId);

        /* Check if customer owns this item */
        if (sessionId == sellerId) {
            makeOffer.setVisibility(View.GONE);
            seeOffer.setVisibility(View.GONE);
        }
        else {
            makeOffer.setVisibility(View.VISIBLE);
            itemEdit.setVisibility(View.GONE);
            itemDelete.setVisibility(View.GONE);
        }

        /* Edit item */
        itemEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ItemActivity.this, EditItemActivity.class);
            Bundle itemInfo = new Bundle();
            String owner = itemOwner.getText().toString();
            String name = itemName.getText().toString();
            String desc = itemDesc.getText().toString();
            String price = itemPrice.getText().toString();
            /* TODO FIX ITEM IMG */
            String condition = itemCondition.getText().toString();

            itemInfo.putString("OWNER",owner);
            itemInfo.putString("NAME",name);
            itemInfo.putString("DESC",desc);
            itemInfo.putString("PRICE",price);
            itemInfo.putString("CONDITION",condition);

            intent.putExtras(itemInfo);
            editItem.launch(intent);
            seeOffer.setVisibility(View.GONE);
        });

        /* Delete item */
        itemDelete.setOnClickListener(v -> confirmDelete());

        /* Make offer */
        makeOffer.setOnClickListener(v -> {
            Intent intent = new Intent(ItemActivity.this, MakeOfferActivity.class);
            Bundle offerInfo = new Bundle();
            String owner = itemOwner.getText().toString();
            String name = itemName.getText().toString();

            offerInfo.putString("OWNER", owner);
            offerInfo.putString("NAME", name);
            intent.putExtras(offerInfo);
            // TODO pass image as well
            addOffer.launch(intent);
        });

        /* View current offer */
        seeOffer.setOnClickListener(v -> {
            Intent intent = new Intent(ItemActivity.this, OfferActivity.class);
            intent.putExtra("OFFER",offer);
            startActivity(intent);
        });
    }

    /* Display Item Details */
    private void displayItem(Customer customer, Item item) {
        itemOwner.setText(String.format("%s %s", customer.getFirstName(), customer.getLastName()));
        itemName.setText(item.getItemName());
        itemDesc.setText(item.getDescription());
        String price = item.getPrice().toString();
        itemPrice.setText(price);
        if (item.getImage() == null) {
            itemImg.setImageResource(R.drawable.placeholder);
        }
        else {
            itemImg.setImageBitmap(stringToBitMap(item.getImage()));
        }
        itemCondition.setText(item.getCondition());
    }

    public Bitmap stringToBitMap(String encodedString){
        try{
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            return decodeByteArray(encodeByte, 0, encodeByte.length);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    /* Retrieve owner of item */
    private void getOwner(final Item item) {
        UPLBTrade.retrofitClient.getCustomer(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                Customer customer = response.body();
                assert customer != null;
                displayItem(customer,item);
                System.out.println("Retrieved owner of item");
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, item.getCustomerId());
    }

    /* Make an offer for the item */
    private void makeOffer(Offer offer) {
        UPLBTrade.retrofitClient.addOffer(new Callback<Offer>() {
            @Override
            public void onResponse(@NonNull Call<Offer> call, @NonNull Response<Offer> response) {
                System.out.println("Created new offer");
            }

            @Override
            public void onFailure(@NonNull Call<Offer> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, offer);
    }

    /* Delete item confirmation */
    private void confirmDelete() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Item");
        builder.setMessage("Do you really want to delete your item?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteItem();
            Toast.makeText(getApplicationContext(), "Item has been deleted", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            finish();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            Toast.makeText(getApplicationContext(), "Delete cancelled", Toast.LENGTH_SHORT).show();
            dialog.cancel();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /* Delete user's item */
    private void deleteItem(){
        UPLBTrade.retrofitClient.deleteItem(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                System.out.println("Deleted Item");
            }

            @Override
            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                System.out.println("Failed to delete item");
                System.out.println(t.getMessage());
            }
        }, itemId);
    }
}
