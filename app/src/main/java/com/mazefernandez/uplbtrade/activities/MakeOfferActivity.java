package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mazefernandez.uplbtrade.R;

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
        String owner = intent.getStringExtra("OWNER");
        String name = intent.getStringExtra("NAME");

        /*Make Offer views */
        ImageView offerImg = findViewById(R.id.offer_img);
        TextView offerName = findViewById(R.id.offer_name);
        offerPrice = findViewById(R.id.offer_price);
        offerMessage = findViewById(R.id.offer_message);
        TextView offerOwner = findViewById(R.id.offer_owner);

        Button makeOffer = findViewById(R.id.make_offer);
        Button cancel = findViewById(R.id.cancel);

        /* Initialize offer views */
        offerOwner.setText(owner);
        offerName.setText(name);
        offerImg.setImageResource(R.drawable.placeholder);

        /* Cancel and return to item */
        cancel.setOnClickListener(v -> {
            Intent intent1 = new Intent();
            setResult(RESULT_OK, intent1);
            finish();
        });

        /* Add Offer */
        makeOffer.setOnClickListener(v -> {
            Intent intent12 = new Intent();
            /* Save Offer Info */
            String price = offerPrice.getText().toString();
            String message = offerMessage.getText().toString();
            double double_price = Double.parseDouble(price);
            //TODO add image
            Bundle offerInfo = new Bundle();
            offerInfo.putDouble("PRICE", double_price);
            offerInfo.putString("MESSAGE",message);
            intent12.putExtras(offerInfo);
            setResult(RESULT_OK, intent12);
            finish();
        });
    }
}
