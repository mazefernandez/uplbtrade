package com.mazefernandez.uplbtrade.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private int offerId;

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
        offerId = offer.getofferId();

        /* Initialize values */
        restrictView(sessionId, buyerId);
        getCustomers(buyerId, sellerId);
        getItemName(itemId);
        displayOffer(offer);

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDecline();
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAccept();
            }
        });

        deleteOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete();
            }
        });
    }
    private void confirmDecline() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Decline Offer");
        builder.setMessage("Do you really want to decline the offer?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Declined offer", Toast.LENGTH_SHORT).show();
                declineOffer();
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Decline cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    private void confirmAccept() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Accept Offer");
        builder.setMessage("Do you really want to accept the offer?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Accepted offer", Toast.LENGTH_SHORT).show();
                acceptOffer();
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Accept cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Offer");
        builder.setMessage("Do you really want to delete the offer?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Deleted offer", Toast.LENGTH_SHORT).show();
                deleteOffer();
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Delete cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    private void deleteOffer() {
        UPLBTrade.retrofitClient.deleteOffer(new Callback<Offer>() {
            @Override
            public void onResponse(Call<Offer> call, retrofit2.Response<Offer> response) {
                System.out.println("Deleted Offer");
            }

            @Override
            public void onFailure(Call<Offer> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        }, offerId);
    }

    private void declineOffer() {
        UPLBTrade.retrofitClient.decline(new Callback<Offer>() {
            @Override
            public void onResponse(Call<Offer> call, retrofit2.Response<Offer> response) {
                System.out.println("Declined Offer");
            }

            @Override
            public void onFailure(Call<Offer> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        }, offerId);
    }

    private void acceptOffer() {
        UPLBTrade.retrofitClient.accept(new Callback<Offer>() {
            @Override
            public void onResponse(Call<Offer> call, retrofit2.Response<Offer> response) {
                System.out.println("Accepted Offer");
            }

            @Override
            public void onFailure(Call<Offer> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        }, offerId);
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
