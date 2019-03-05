package com.mazefernandez.uplbtrade.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mazefernandez.uplbtrade.R;

public class ItemActivity extends AppCompatActivity {
    private TextView itemOwner;
    private TextView itemName;
    private TextView itemDesc;
    private TextView itemPrice;
    private ImageView itemImg;
    private TextView itemCondition;
    private Button itemEdit;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itemOwner = findViewById(R.id.item_owner);
        itemName = findViewById(R.id.item_name);
        itemDesc = findViewById(R.id.item_desc);
        itemPrice = findViewById(R.id.item_price);
        itemImg = findViewById(R.id.item_img);
        itemCondition = findViewById(R.id.item_condition);
        itemEdit = findViewById(R.id.edit_item);


    }
}
