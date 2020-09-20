package com.mazefernandez.uplbtrade.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.Offer;

import retrofit2.Call;
import retrofit2.Callback;

public class OfferActivity extends AppCompatActivity {
    private ImageView offerImg;
    private TextView offerName;
    private TextView originalPrice;
    private TextView offerPrice;
    private TextView offerStatus;
    private TextView offerMessage;
    private TextView offerSeller;
    private TextView offerBuyer;
    private Button decline;
    private Button accept;
    private Button meetUp;
    private ImageButton deleteOffer;
    private int itemId;
    private int offerId;
    private String price;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        /*SharedPref to save customer_id*/
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        int sessionId = pref.getInt("customer_id", -1);

        /* Offer Views */
        offerImg = findViewById(R.id.offer_img);
        offerName = findViewById(R.id.offer_name);
        originalPrice = findViewById(R.id.original_price);
        offerPrice = findViewById(R.id.offer_price);
        offerStatus = findViewById(R.id.offer_status);
        offerMessage = findViewById(R.id.offer_message);
        offerSeller = findViewById(R.id.offer_seller);
        offerBuyer = findViewById(R.id.offer_buyer);
        decline = findViewById(R.id.decline);
        accept = findViewById(R.id.accept);
        deleteOffer = findViewById(R.id.offer_delete);
        meetUp = findViewById(R.id.meet_up);


        /* Retrieve offer data */
        final Offer offer = (Offer) getIntent().getSerializableExtra("OFFER");
        assert offer != null;
        itemId = offer.getitemId();
        int buyerId = offer.getBuyerId();
        int sellerId = offer.getSellerId();
        offerId = offer.getofferId();

        /* Initialize views */
        if (sessionId == buyerId) {
            decline.setVisibility(View.GONE);
            accept.setVisibility(View.GONE);
        }
        else {
            deleteOffer.setVisibility(View.GONE);
        }

        if (!offer.getStatus().equals("Accepted")) {
            meetUp.setVisibility(View.GONE);
        }
        else {
            decline.setVisibility(View.GONE);
            accept.setVisibility(View.GONE);
        }

        /* Initialize offer data */
        getCustomers(buyerId, sellerId);
        displayOffer(offer);

        /* Decline Offer */
        decline.setOnClickListener(v -> confirmDecline());

        /* Accept Offer */
        accept.setOnClickListener(v -> confirmAccept());

        /* Set Meet Up */
        meetUp.setOnClickListener(v -> {
            Intent intent = new Intent(OfferActivity.this, MeetUpActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("customer",offerBuyer.getText().toString());
            bundle.putString("item",offerName.getText().toString());
            bundle.putString("price", originalPrice.getText().toString());
            bundle.putString("offer",offerPrice.getText().toString());
            intent.putExtra("info",bundle);
            startActivity(intent);
        });

        /* Delete Offer */
        deleteOffer.setOnClickListener(v -> confirmDelete());
    }

    /* Display offer details */
    public void displayOffer(Offer offer) {
        offerImg.setImageResource(R.drawable.placeholder);
        getItem(itemId);
        String offer_price = String.format("%.2f",offer.getPrice());
        offer_price = "\u20B1" + offer_price;
        offerPrice.setText(offer_price);
        offerStatus.setText(offer.getStatus());
        offerMessage.setText(offer.getMessage());
    }

    private void getItem(int itemId) {
        UPLBTrade.retrofitClient.getItem(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull retrofit2.Response<Item> response) {
                Item item = response.body();
                assert item != null;
                offerName.setText(item.getItemName());
                price = String.format("%.2f",item.getPrice());
                price = "\u20B1" + price;
                originalPrice.setText(price);
            }

            @Override
            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, itemId);
    }

    private void getCustomers(int buyerId, int sellerId) {
        UPLBTrade.retrofitClient.getCustomer(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull retrofit2.Response<Customer> response) {
                Customer customer = response.body();
                assert customer != null;
                offerBuyer.setText(String.format("%s %s", customer.getfirstName(), customer.getlastName()));
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, buyerId);

        UPLBTrade.retrofitClient.getCustomer(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull retrofit2.Response<Customer> response) {
                Customer customer = response.body();
                assert customer != null;
                offerSeller.setText(String.format("%s %s", customer.getfirstName(), customer.getlastName()));
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, sellerId);
    }

    private void confirmDecline() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Decline Offer");
        builder.setMessage("Do you really want to decline the offer?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Toast.makeText(getApplicationContext(), "Declined offer", Toast.LENGTH_SHORT).show();
            declineOffer();
            dialog.dismiss();
            finish();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            Toast.makeText(getApplicationContext(), "Decline cancelled", Toast.LENGTH_SHORT).show();
            dialog.cancel();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void declineOffer() {
        UPLBTrade.retrofitClient.decline(new Callback<Offer>() {
            @Override
            public void onResponse(@NonNull Call<Offer> call, @NonNull retrofit2.Response<Offer> response) {
                System.out.println("Declined Offer");
            }

            @Override
            public void onFailure(@NonNull Call<Offer> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, offerId);
    }

    private void confirmAccept() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Accept Offer");
        builder.setMessage("Do you really want to accept the offer?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Toast.makeText(getApplicationContext(), "Accepted offer", Toast.LENGTH_SHORT).show();
            acceptOffer();
            finish();
        });

        builder.setNegativeButton("No", (dialog, which) -> Toast.makeText(getApplicationContext(), "Accept cancelled", Toast.LENGTH_SHORT).show());

        builder.show();
    }

    private void acceptOffer() {
        UPLBTrade.retrofitClient.accept(new Callback<Offer>() {
            @Override
            public void onResponse(@NonNull Call<Offer> call, @NonNull retrofit2.Response<Offer> response) {
                System.out.println("Accepted Offer");
            }

            @Override
            public void onFailure(@NonNull Call<Offer> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, offerId);
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Offer");
        builder.setMessage("Do you really want to delete the offer?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Toast.makeText(getApplicationContext(), "Deleted offer", Toast.LENGTH_SHORT).show();
            deleteOffer();
            finish();
        });

        builder.setNegativeButton("No", (dialog, which) -> Toast.makeText(getApplicationContext(), "Delete cancelled", Toast.LENGTH_SHORT).show());

        builder.show();
    }

    private void deleteOffer() {
        UPLBTrade.retrofitClient.deleteOffer(new Callback<Offer>() {
            @Override
            public void onResponse(@NonNull Call<Offer> call, @NonNull retrofit2.Response<Offer> response) {
                System.out.println("Deleted Offer");
            }

            @Override
            public void onFailure(@NonNull Call<Offer> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, offerId);
    }
}
