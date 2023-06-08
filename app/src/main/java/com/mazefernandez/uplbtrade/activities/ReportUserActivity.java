package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.CustomerReport;
import com.mazefernandez.uplbtrade.picasso.CircleTransformation;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* Files a report regarding a user in the application */

public class ReportUserActivity extends AppCompatActivity {
    private ImageView profileImage;
    private TextView profileName, contactNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_user);

        /* SharedPref to save customer_id */
        final SharedPreferences pref = this.getSharedPreferences("uplbtrade", MODE_PRIVATE);
        int sessionId = pref.getInt("customer_id",-1);

        /* Report User Views */
        profileImage = findViewById(R.id.profile_img);
        profileName = findViewById(R.id.profile_name);
        contactNo = findViewById(R.id.contactNo);

        EditText report = findViewById(R.id.report);
        Button send = findViewById(R.id.send);
        Button cancel = findViewById(R.id.cancel);

        Intent profileIntent = getIntent();

        /* Display Customer Details */
        Bundle userInfo = profileIntent.getExtras();
        assert userInfo != null;
        Customer customer = (Customer) getIntent().getSerializableExtra("CUSTOMER");
        displayUser(customer);

        /* Report Customer */
        send.setOnClickListener(v -> {
            String message = report.getText().toString().trim();
            int customerId = customer.getCustomerId();

            CustomerReport customerReport = new CustomerReport(message, sessionId, customerId);

            UPLBTrade.retrofitClient.addCustomerReport(new Callback<CustomerReport>() {
                @Override
                public void onResponse(@NonNull Call<CustomerReport> call, @NonNull Response<CustomerReport> response) {
                    System.out.println("Added Customer Report");
                    System.out.println(response.body());
                }

                @Override
                public void onFailure(@NonNull Call<CustomerReport> call, @NonNull Throwable t) {
                    System.out.println("Failed to add Customer Report");
                    System.out.println(t.getMessage());
                }
            }, customerReport);

            Toast.makeText(this, "Your report has been sent.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ReportUserActivity.this,HomeActivity.class);
            startActivity(intent);
        });

        /* Cancel and return to profile */
        cancel.setOnClickListener(v -> finish());
    }
    /* display user account data */
    private void displayUser(Customer customer) {
        if (customer.getImage().equals("placeholder")) {
            Picasso.get().load(R.drawable.placeholder).centerInside().fit().transform(new CircleTransformation()).into(profileImage);
        }
        else {
            Picasso.get().load(customer.getImage()).centerInside().fit().transform(new CircleTransformation()).into(profileImage);
        }
        String name = customer.getFirstName() + " " + customer.getLastName();
        profileName.setText(name);
        contactNo.setText(customer.getContactNo());
    }
}
