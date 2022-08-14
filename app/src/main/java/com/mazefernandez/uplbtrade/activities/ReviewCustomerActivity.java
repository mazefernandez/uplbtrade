package com.mazefernandez.uplbtrade.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.models.CustomerReview;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewCustomerActivity extends AppCompatActivity {

    private TextView ratePrompt;
    private RatingBar ratingBar;
    private TextView feedbackPrompt;
    private EditText review;
    private Button submit;
    private int raterId;
    private Intent transactionIntent;
    private Bundle transactionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_customer);

        /*SharedPref to save customer_id*/
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        raterId = pref.getInt("customer_id", -1);

        /* Get info from transaction page */
        transactionIntent = getIntent();
        transactionInfo = transactionIntent.getExtras();

        int transactionId = transactionInfo.getInt("TRANSACTION_ID");
        int customerId = transactionInfo.getInt("CUSTOMER_ID");

        ratePrompt = findViewById(R.id.rate_prompt);
        ratingBar = findViewById(R.id.rating_bar);
        feedbackPrompt = findViewById(R.id.feedback_prompt);
        review = findViewById(R.id.review);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(v -> {
            /* Add Customer Review to database*/
            Float float_rating = ratingBar.getRating();
            Double rating = float_rating.doubleValue();
            String string_review = review.getText().toString();

            /* Add Customer review to database */
            CustomerReview customerReview = new CustomerReview(rating, string_review, raterId, customerId, transactionId);

            UPLBTrade.retrofitClient.addCustomerReview(new Callback<CustomerReview>() {
                @Override
                public void onResponse(@NonNull Call<CustomerReview> call, @NonNull Response<CustomerReview> response) {
                    System.out.println("Added Customer Review");
                    System.out.println(response.body());
                }
                @Override
                public void onFailure(@NonNull Call<CustomerReview> call, @NonNull Throwable t) {
                    System.out.println("Failed to add Customer Review");
                    System.out.println(t.getMessage());
                }
            }, customerReview);
            Intent intent = new Intent(ReviewCustomerActivity.this, TransactionsActivity.class);
            startActivity(intent);
        });

    }
}