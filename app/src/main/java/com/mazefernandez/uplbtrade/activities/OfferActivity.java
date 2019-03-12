package com.mazefernandez.uplbtrade.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.Response;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.Offer;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;

public class OfferActivity extends AppCompatActivity {
    private ImageView offerImg;
    private TextView offerName;
    private TextView offerPrice;
    private TextView offerStatus;
    private TextView offerMessage;
    private TextView offerSeller;
    private TextView offerBuyer;
    private Button decline;
    private Button accept;
    private ImageButton deleteOffer;
    private int itemId;
    private int buyerId;
    private int sellerId;
    private int sessionId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        /*SharedPref to save customer_id*/
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        sessionId = pref.getInt("customer_id",-1);

        /* Offer Views */
        offerImg = findViewById(R.id.offer_img);
        offerName = findViewById(R.id.offer_name);
        offerPrice = findViewById(R.id.offer_price);
        offerStatus = findViewById(R.id.offer_status);
        offerMessage = findViewById(R.id.offer_message);
        offerSeller = findViewById(R.id.offer_seller);
        offerBuyer = findViewById(R.id.offer_buyer);
        decline = findViewById(R.id.decline);
        accept = findViewById(R.id.accept);
        deleteOffer = findViewById(R.id.offer_delete);

        /* Retrieve offer data */
        Offer offer = (Offer) getIntent().getSerializableExtra("OFFER");
        itemId = offer.getitemId();
        buyerId = offer.getBuyerId();
        sellerId = offer.getSellerId();

        /* Initialize values */
        restrictView(sessionId, buyerId);
        getCustomers(buyerId, sellerId);
        getItemName(itemId);
        displayOffer(offer);

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        deleteOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void displayOffer(Offer offer) {
        offerImg.setImageResource(R.drawable.placeholder);
        getItemName(itemId);
        offerPrice.setText(offer.getPrice().toString());
        offerStatus.setText(offer.getStatus());
        offerMessage.setText(offer.getMessage());
    }

    private void getItemName(int itemId) {
        UPLBTrade.retrofitClient.getItem(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, retrofit2.Response<Item> response) {
                offerName.setText(response.body().getItemName());
            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        }, itemId);
    }

    private void getCustomers(int buyerId, int sellerId) {
        UPLBTrade.retrofitClient.getCustomer(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, retrofit2.Response<Customer> response) {
                offerBuyer.setText(String.format("%s %s", response.body().getfirstName(), response.body().getlastName()));
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        }, buyerId);

        UPLBTrade.retrofitClient.getCustomer(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, retrofit2.Response<Customer> response) {
                offerSeller.setText(String.format("%s %s", response.body().getfirstName(), response.body().getlastName()));
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        }, sellerId);
    }

    /* Loads appropriate view for buyer or seller */
    public void restrictView(int sessionId, int buyerId) {
        if (sessionId == buyerId) {
            decline.setVisibility(View.GONE);
            accept.setVisibility(View.GONE);
        }
        else {
            deleteOffer.setVisibility(View.GONE);
        }
    }
}
