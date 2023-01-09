package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.models.Offer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* Create new offer */

public class MakeOfferActivity extends AppCompatActivity {
    private EditText offerPrice;
    private EditText offerMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_offer);

        /* Get offer info */
        Intent intent = getIntent();
        Bundle offerInfo = intent.getExtras();
        String owner = offerInfo.getString("OWNER");
        String name = offerInfo.getString("NAME");
        String image = offerInfo.getString("IMAGE");
        int itemId = offerInfo.getInt("ITEM");
        int sessionId = offerInfo.getInt("SESSION");
        int sellerId = offerInfo.getInt("SELLER");


        /*Make Offer views */
        ImageView offerImg = findViewById(R.id.offer_img);
        TextView offerName = findViewById(R.id.offer_name);
        offerPrice = findViewById(R.id.offer_price);
        offerMessage = findViewById(R.id.offer_message);
        TextView offerOwner = findViewById(R.id.offer_owner);

        Button makeOffer = findViewById(R.id.make_offer);
        Button cancel = findViewById(R.id.cancel);

        /* Firebase instances */
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        /* Initialize offer views */
        offerOwner.setText(owner);
        offerName.setText(name);

        /* retrieve image from firebase */
        StorageReference ref = storageReference.child("images/"+image);

        final long ONE_MEGABYTE = 1024 * 1024 * 5;
        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            System.out.println("Successfully read image");
            offerImg.setImageBitmap(bitmap);
        }).addOnFailureListener(fail -> System.out.println("Failed to read image" + fail));


        /* Cancel and return to item */
        cancel.setOnClickListener(v -> {
            Intent intent1 = new Intent();
            intent1.putExtra("CHECK", 0);
            setResult(RESULT_OK,intent1);
            finish();
        });

        /* Add Offer */
        makeOffer.setOnClickListener(v -> {
            /* Save Offer Info */

            String price = offerPrice.getText().toString();
            String message = offerMessage.getText().toString();
            double double_price = Double.parseDouble(price);
            Offer offer = new Offer(double_price, "Pending", message, itemId, sessionId, sellerId);


            UPLBTrade.retrofitClient.addOffer(new Callback<Offer>() {
                @Override
                public void onResponse(@NonNull Call<Offer> call, @NonNull Response<Offer> response) {
                    System.out.println("Created new offer");
                }

                @Override
                public void onFailure(@NonNull Call<Offer> call, @NonNull Throwable t) {
                    System.out.println(t.getMessage());
                }
            }, offer);

            Intent intent2 = new Intent();
            intent2.putExtra("CHECK", 1);

            setResult(RESULT_OK,intent2);
            finish();
        });
    }
}
