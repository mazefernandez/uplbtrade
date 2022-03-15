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
import com.mazefernandez.uplbtrade.models.ApplicationReview;
import com.mazefernandez.uplbtrade.models.Item;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewAppActivity extends AppCompatActivity {

    private TextView ratePrompt;
    private RatingBar ratingBar;
    private TextView feedbackPrompt;
    private EditText review;
    private Button submit;
    private int customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_app);

        /*SharedPref to save customer_id*/
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        customerId = pref.getInt("customer_id", -1);

        /* Review App Views */
        ratePrompt = findViewById(R.id.rate_prompt);
        ratingBar = findViewById(R.id.rating_bar);
        feedbackPrompt = findViewById(R.id.feedback_prompt);
        review = findViewById(R.id.review);
        submit = findViewById(R.id.submit_button);

        submit.setOnClickListener(v -> {
            Float float_rating = ratingBar.getRating();
            Double rating = float_rating.doubleValue();
            String string_review = review.getText().toString();

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

            Intent intent = new Intent(ReviewAppActivity.this,HomeActivity.class);
            startActivity(intent);
        });
    }


}