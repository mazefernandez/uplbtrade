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
import com.mazefernandez.uplbtrade.models.ApplicationReview;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewAppActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText review;
    private int customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_app);

        /*SharedPref to save customer_id*/
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        customerId = pref.getInt("customer_id", -1);

        /* Review App Views */
        TextView ratePrompt = findViewById(R.id.rate_prompt);
        ratePrompt.setVisibility(View.VISIBLE);
        ratingBar = findViewById(R.id.rating_bar);
        TextView feedbackPrompt = findViewById(R.id.feedback_prompt);
        feedbackPrompt.setVisibility(View.VISIBLE);
        review = findViewById(R.id.review);
        Button submit = findViewById(R.id.submit_button);

        submit.setOnClickListener(v -> {
            float float_rating;
            float_rating = ratingBar.getRating();
            Double rating = (double) float_rating;
            String string_review = review.getText().toString().trim();

            /* Add application review to database */
            ApplicationReview applicationReview = new ApplicationReview(rating, string_review, customerId);

            UPLBTrade.retrofitClient.addAppReview(new Callback<ApplicationReview>() {
                @Override
                public void onResponse(@NonNull Call<ApplicationReview> call, @NonNull Response<ApplicationReview> response) {
                    System.out.println("Added App Review");
                    System.out.println(response.body());
                }
                @Override
                public void onFailure(@NonNull Call<ApplicationReview> call, @NonNull Throwable t) {
                    System.out.println("Failed to add App Review");
                    System.out.println(t.getMessage());
                }
            }, applicationReview);

            Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ReviewAppActivity.this,HomeActivity.class);
            startActivity(intent);
        });
    }


}