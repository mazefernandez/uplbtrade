package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.mazefernandez.uplbtrade.R;

/* Upload Item for selling */

public class AddItemActivity extends AppCompatActivity{
    private ImageView itemImg;
    private FloatingActionButton addItemImg;
    private EditText itemName;
    private EditText itemDesc;
    private EditText itemPrice;
    private EditText itemCondition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        /* Upload Item Views */
        itemImg = findViewById(R.id.item_img);
        addItemImg = findViewById(R.id.add_item_img);
        itemName = findViewById(R.id.item_name);
        itemDesc = findViewById(R.id.item_desc);
        itemPrice = findViewById(R.id.item_price);
        itemCondition = findViewById(R.id.item_condition);

        Button addItem = findViewById(R.id.add_item);
        Button cancel = findViewById(R.id.cancel);

        /* New Item */
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String name = itemName.getText().toString();
            String desc = itemDesc.getText().toString();
            String price = itemPrice.getText().toString();
            String condition = itemCondition.getText().toString();

            Intent intent = new Intent();
            Bundle itemInfo = new Bundle();
            itemInfo.putString("NAME",name);
            itemInfo.putString("DESC",desc);
            itemInfo.putString("PRICE",price);
            itemInfo.putString("CONDITION",condition);
            intent.putExtras(itemInfo);
            setResult(RESULT_OK,intent);
            finish();
            }
        });

        /* Cancel and return to profile */
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
