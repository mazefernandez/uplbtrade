package com.mazefernandez.uplbtrade.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.Offer;
import com.mazefernandez.uplbtrade.models.Transaction;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* Shows the user the progress of their transaction */

public class TransactionActivity extends AppCompatActivity {

    TextView date;
    TextView time;
    TextView venue;
    TextView itemName;
    TextView buyer;
    TextView seller;
    TextView price;
    Button cancel;

    private int itemId;
    private int offerId;
    private int sellerId;
    private int buyerId;
    private int sessionId;
    private int transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        /* SharedPref to save customer_id */
        SharedPreferences pref = this.getSharedPreferences("uplbtrade", MODE_PRIVATE);
        sessionId = pref.getInt("customer_id",-1);

        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        venue = findViewById(R.id.venue);
        itemName = findViewById(R.id.item);
        buyer = findViewById(R.id.buyer);
        seller = findViewById(R.id.seller);
        price = findViewById(R.id.price);
        Button rate = findViewById(R.id.rate);
        cancel = findViewById(R.id.cancel_order);

        Transaction transaction = (Transaction) getIntent().getSerializableExtra("TRANSACTION");
        assert transaction != null;
        transactionId = transaction.getTransactionId();

        itemId = transaction.getItemId();
        offerId = transaction.getOfferId();
        sellerId = transaction.getSellerId();
        buyerId = transaction.getBuyerId();

        getTransaction();
        /* adjust views for buyer and seller */
        if (sellerId == sessionId) {
            cancel.setVisibility(View.GONE);
        }

        // go to rating page for transaction
        rate.setOnClickListener(v -> {
            Intent review = new Intent(TransactionActivity.this, ReviewCustomerActivity.class);
            Bundle reviewInfo = new Bundle();
            reviewInfo.putInt("TRANSACTION_ID", transactionId);

            if (buyerId == sessionId) {
                reviewInfo.putInt("CUSTOMER_ID", sellerId);
            }
            else {
                reviewInfo.putInt("CUSTOMER_ID", buyerId);
            }
            review.putExtras(reviewInfo);
            startActivity(review);
        });
        cancel.setOnClickListener(v -> confirmCancel(transactionId));

    }

    /* retrieve transaction information */
    private void getTransactionInfo(Transaction transaction) {
        String dateStr = transaction.getDate();
        String timeStr = transaction.getTime();
        date.setText(dateStr);
        time.setText(timeStr);
        venue.setText(transaction.getVenue());

        getItemName();
        getPrice();
        getSellerAndBuyer();

    }

    /* Retrieve transaction */
    private void getTransaction() {
        UPLBTrade.retrofitClient.getTransaction(new Callback<Transaction>() {
            @Override
            public void onResponse(@NonNull Call<Transaction> call, @NonNull Response<Transaction> response) {
                Transaction transaction = response.body();
                assert transaction != null;
                getTransactionInfo(transaction);
                System.out.println("Retrieved transaction");
            }

            @Override
            public void onFailure(@NonNull Call<Transaction> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, transactionId);
    }

    /* get item name */
    private void getItemName() {
        UPLBTrade.retrofitClient.getItem(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                Item item = response.body();
                assert item != null;
                // retrieve item name from database
                itemName.setText(item.getItemName());
                System.out.println("retrieved item");
            }

            @Override
            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, itemId);
    }

    /* get offer price */
    private void getPrice(){
        UPLBTrade.retrofitClient.getOffer(new Callback<Offer>() {
            @Override
            public void onResponse(@NonNull Call<Offer> call, @NonNull Response<Offer> response) {
                Offer offer = response.body();
                assert offer != null;
                // retrieve offer price from database
                String string_price = offer.getPrice().toString();
                price.setText(string_price);
                System.out.println("retrieved offer");
            }

            @Override
            public void onFailure(@NonNull Call<Offer> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, offerId);
    }

    /* get seller and buyer */
    private void getSellerAndBuyer(){
        UPLBTrade.retrofitClient.getCustomer(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                Customer customer = response.body();
                assert customer != null;
                //retrieve seller
                seller.setText(String.format("%s %s", customer.getFirstName(), customer.getLastName()));
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, sellerId);

        UPLBTrade.retrofitClient.getCustomer(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                Customer customer = response.body();
                assert customer != null;
                //retrieve buyer
                buyer.setText(String.format("%s %s", customer.getFirstName(), customer.getLastName()));
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, buyerId);
    }
    private void cancelOrder(int transaction_id) {
        UPLBTrade.retrofitClient.cancelTransaction(new Callback<Transaction>() {
            @Override
            public void onResponse(@NonNull Call<Transaction> call, @NonNull Response<Transaction> response) {
                System.out.println("Cancelled Transaction");
            }

            @Override
            public void onFailure(@NonNull Call<Transaction> call, @NonNull Throwable t) {
                System.out.println("Failed to cancel Transaction");
                System.out.println(t.getMessage());
            }
        }, transaction_id);
    }
    private void confirmCancel(int transaction_id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Item");
        builder.setMessage("Do you really want to cancel?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", (dialog, which) -> {
            cancelOrder(transaction_id);
            Toast.makeText(getApplicationContext(), "Order canceled", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            cancel.setVisibility(View.GONE);
            finish();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            dialog.cancel();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
    }
}
