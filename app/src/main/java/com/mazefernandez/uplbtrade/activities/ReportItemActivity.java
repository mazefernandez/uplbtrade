package com.mazefernandez.uplbtrade.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.ItemReport;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportItemActivity extends AppCompatActivity {

    private ImageView itemImage;
    private TextView itemName, itemOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_item);

        /* SharedPref to save customer_id */
        final SharedPreferences pref = this.getSharedPreferences("uplbtrade", MODE_PRIVATE);
        int sessionId = pref.getInt("customer_id",-1);

        /* Report Item Views */

        itemImage = findViewById(R.id.item_image);
        itemName = findViewById(R.id.item_name);
        itemOwner = findViewById(R.id.item_owner);
        EditText report = findViewById(R.id.report);
        Button send = findViewById(R.id.send);
        Button cancel = findViewById(R.id.cancel);

        /* Get Item Data */
        Intent itemIntent = getIntent();
        Bundle itemInfo = itemIntent.getExtras();
        assert itemInfo != null;
        Item item = (Item) getIntent().getSerializableExtra("ITEM");
        getCustomer(item.getCustomerId());
        displayItem(item);

        /* Send report to database */
        send.setOnClickListener(v -> {
            String message = report.getText().toString().trim();
            ItemReport itemReport = new ItemReport(message, sessionId, item.getItemId());

            UPLBTrade.retrofitClient.addItemReport(new Callback<ItemReport>() {
                @Override
                public void onResponse(@NonNull Call<ItemReport> call, @NonNull Response<ItemReport> response) {
                    System.out.println("Added Item Report");
                    System.out.println(response.body());
                }

                @Override
                public void onFailure(@NonNull Call<ItemReport> call, @NonNull Throwable t) {
                    System.out.println("Failed to add Customer Report");
                    System.out.println(t.getMessage());
                }
            }, itemReport);
            Toast.makeText(this, "Thank you for your report!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ReportItemActivity.this,ItemActivity.class);
            intent.putExtra("ITEM", item);
            startActivity(intent);
        });
        /* Cancel and return to item */
        cancel.setOnClickListener(v -> finish());

    }
    public void getCustomer(int customerId){
        UPLBTrade.retrofitClient.getCustomer(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                Customer customer = response.body();
                assert customer != null;
                String owner = customer.getFirstName() + " " + customer.getLastName();
                itemOwner.setText(owner);
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {

            }
        }, customerId);
    }
    public void displayItem(Item item){
        String imgString = item.getImage();
        String[] split = imgString.split("-");
        String rotate = split[split.length-1];
        int rotation = Integer.parseInt(rotate);
        /* Firebase instances */
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        /* retrieve image from firebase */
        StorageReference ref = storageReference.child("images/"+imgString+"/0");

        final long ONE_MEGABYTE = 1024 * 1024 * 5;
        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            System.out.println("Successfully read image");
            itemImage.setImageBitmap(bitmap);
            itemImage.setRotation(rotation);
        }).addOnFailureListener(fail -> System.out.println("Failed to read image" + fail));
        String name = item.getItemName();
        itemName.setText(name);
    }
}