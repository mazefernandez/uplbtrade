package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.Item;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemActivity extends AppCompatActivity {
    private TextView itemOwner;
    private TextView itemName;
    private TextView itemDesc;
    private TextView itemPrice;
    private ImageView itemImg;
    private TextView itemCondition;
    private ImageButton itemEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        itemOwner = findViewById(R.id.item_owner);
        itemName = findViewById(R.id.item_name);
        itemDesc = findViewById(R.id.item_desc);
        itemPrice = findViewById(R.id.item_price);
        itemImg = findViewById(R.id.item_img);
        itemCondition = findViewById(R.id.item_condition);
        itemEdit = findViewById(R.id.item_edit);

        Item item = (Item) getIntent().getSerializableExtra("ITEM");
        getOwner(item);
    }
    private void displayItem(Customer customer, Item item) {
        itemOwner.setText(String.format("%s %s", customer.getfirstName(), customer.getlastName()));
        itemName.setText(item.getItemName());
        itemDesc.setText(item.getDescription());
        itemPrice.setText(item.getPrice().toString());
        /*Fix add item picture */
        itemImg.setImageResource(R.drawable.placeholder);
        //itemImg.setImageResource(item.getImage());
        itemCondition.setText(item.getCondition());
    }

    private void getOwner(final Item item) {
        UPLBTrade.retrofitClient.getCustomer(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                Customer customer = response.body();
                displayItem(customer,item);
                System.out.println("Retrieved owner of item");
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        }, item.getcustomerId());
    }
}
