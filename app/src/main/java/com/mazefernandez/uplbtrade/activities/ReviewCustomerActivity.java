package com.mazefernandez.uplbtrade.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.CustomerReview;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewCustomerActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText review;
    private int raterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_customer);

        /*SharedPref to save customer_id*/
        SharedPreferences pref = this.getSharedPreferences("uplbtrade", MODE_PRIVATE);
        raterId = pref.getInt("customer_id", -1);

        /* Get info from transaction page */
        Intent transactionIntent = getIntent();
        Bundle transactionInfo = transactionIntent.getExtras();

        int transactionId = transactionInfo.getInt("TRANSACTION_ID");
        int customerId = transactionInfo.getInt("CUSTOMER_ID");

        TextView ratePrompt = findViewById(R.id.rate_prompt);
        ratePrompt.setVisibility(View.VISIBLE);
        ratingBar = findViewById(R.id.rating_bar);
        TextView feedbackPrompt = findViewById(R.id.feedback_prompt);
        feedbackPrompt.setVisibility(View.VISIBLE);
        review = findViewById(R.id.review);
        Button submit = findViewById(R.id.submit_button);

        submit.setOnClickListener(v -> {
            /* Add Customer Review to database*/
            float float_rating;
            float_rating = ratingBar.getRating();
            Double rating = (double) float_rating;
            String string_review = review.getText().toString().trim();

            /* Add Customer review to database */
            CustomerReview customerReview = new CustomerReview(rating, string_review, raterId, customerId, transactionId);
            UPLBTrade.retrofitClient.addCustomerReview(new Callback<CustomerReview>() {
                @Override
                public void onResponse(@NonNull Call<CustomerReview> call, @NonNull Response<CustomerReview> response) {
                    System.out.println("Added Customer Review");
                    System.out.println(response.body());
                    updateRating(customerId);
                }
                @Override
                public void onFailure(@NonNull Call<CustomerReview> call, @NonNull Throwable t) {
                    System.out.println("Failed to add Customer Review");
                    System.out.println(t.getMessage());
                }
            }, customerReview);

            Toast.makeText(this, "Your review has been sent!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ReviewCustomerActivity.this, TransactionsActivity.class);
            startActivity(intent);
        });

    }
    private void updateRating(int customerId) {
        UPLBTrade.retrofitClient.updateRating(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                System.out.println("Updated customer Rating");
                assert response.body() != null;
                System.out.println(response.body().getOverallRating());
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                System.out.println("Failed to update customer Rating");
                System.out.println(t.getMessage());
            }
        }, customerId);
    }
}