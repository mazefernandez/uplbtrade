package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.picasso.CircleTransformation;
import com.squareup.picasso.Picasso;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.GOOGLE_ACCOUNT;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* Edit Customer Information */

public class EditProfileActivity extends AppCompatActivity {
    private TextView editName;
    private EditText editAddress, editContactNo;
    private ImageView editImg;
    private Bundle userInfo;
    private GoogleSignInAccount account;
    private int customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        /* Edit Customer Views */
        editName = findViewById(R.id.edit_text);
        editAddress = findViewById(R.id.edit_address);
        editContactNo = findViewById(R.id.edit_contactNo);
        editImg = findViewById(R.id.edit_img);

        Button saveCustomer = findViewById(R.id.save_customer);
        Intent profileIntent = getIntent();

        /* Display Customer Details */
        userInfo = profileIntent.getExtras();
        assert userInfo != null;
        account = userInfo.getParcelable(GOOGLE_ACCOUNT);
        customerId = userInfo.getInt("ID");
        displayUser();

        /* save new info to Customer */
        saveCustomer.setOnClickListener(view -> {
            String address = editAddress.getText().toString().trim();
            String contactNo = editContactNo.getText().toString().trim();

            Customer customer = new Customer(address, contactNo);

            /* UPDATE customer info in database */
            UPLBTrade.retrofitClient.updateCustomer(new Callback<Customer>() {
                @Override
                public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                    System.out.println("Updated Customer");
                }

                @Override
                public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                    System.out.println("Failed to update customer");
                    System.out.println(t.getMessage());
                }
            }, customer, customerId);

            /* Return to profile */
            Intent intent = new Intent();
            intent.putExtra("CHECK", 1);
            setResult(RESULT_OK,intent);
            finish();
        });
    }

    /* display user account data */
    private void displayUser() {
        Picasso.get().load(account.getPhotoUrl()).centerInside().fit().transform(new CircleTransformation()).into(editImg);
        editName.setText(account.getDisplayName());
        editAddress.setText(userInfo.getString("ADDRESS"));
        editContactNo.setText(userInfo.getString("CONTACT"));
    }

}
