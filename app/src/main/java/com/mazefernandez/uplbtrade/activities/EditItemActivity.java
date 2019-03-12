package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mazefernandez.uplbtrade.R;

public class EditItemActivity extends AppCompatActivity {
    private TextView itemOwner;
    private EditText itemName;
    private EditText itemDesc;
    private EditText itemPrice;
    private ImageView itemImg;
    private EditText itemCondition;
    private Button saveItem;
    private Intent itemIntent;
    private Bundle itemInfo;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        itemOwner = findViewById(R.id.item_owner);
        itemName = findViewById(R.id.item_name);
        itemDesc = findViewById(R.id.item_desc);
        itemPrice = findViewById(R.id.item_price);
        itemImg = findViewById(R.id.item_img);
        itemCondition = findViewById(R.id.item_condition);
        saveItem = findViewById(R.id.save_item);

        itemIntent = getIntent();
        itemInfo = itemIntent.getExtras();


        displayItem();

        /* Save info to item */
        saveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = itemName.getText().toString();
                String desc = itemDesc.getText().toString();
                String price = itemPrice.getText().toString();
                String condition = itemCondition.getText().toString();

                Intent intent = new Intent();
                Bundle editInfo = new Bundle();
                editInfo.putString("NAME",name);
                editInfo.putString("DESC",desc);
                editInfo.putString("PRICE",price);
                editInfo.putString("CONDITION",condition);
                intent.putExtras(editInfo);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
    public void displayItem() {
        itemOwner.setText(itemInfo.getString("OWNER"));
        itemName.setText(itemInfo.getString("NAME"));
        itemDesc.setText(itemInfo.getString("DESC"));
        itemPrice.setText(itemInfo.getString("PRICE"));
        itemCondition.setText(itemInfo.getString("CONDITION"));
        itemImg.setImageResource(R.drawable.placeholder);
    }
}
