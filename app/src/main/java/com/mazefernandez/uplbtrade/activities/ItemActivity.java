package com.mazefernandez.uplbtrade.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.Item;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.mazefernandez.uplbtrade.models.RequestCode.EDIT_ITEM;

public class ItemActivity extends AppCompatActivity {
    private TextView itemOwner;
    private TextView itemName;
    private TextView itemDesc;
    private TextView itemPrice;
    private ImageView itemImg;
    private TextView itemCondition;
    private ImageButton itemEdit;
    private ImageButton itemDelete;
    private Button makeOffer;
    private int itemId;
    private int sessionId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        /*SharedPref to save customer_id*/
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        sessionId = pref.getInt("customer_id",-1);

        /*Item activity views */
        itemOwner = findViewById(R.id.item_owner);
        itemName = findViewById(R.id.item_name);
        itemDesc = findViewById(R.id.item_desc);
        itemPrice = findViewById(R.id.item_price);
        itemImg = findViewById(R.id.item_img);
        itemCondition = findViewById(R.id.item_condition);
        itemEdit = findViewById(R.id.item_edit);
        itemDelete = findViewById(R.id.item_delete);
        makeOffer = findViewById(R.id.make_offer);

        /* Retrieve item data */
        Item item = (Item) getIntent().getSerializableExtra("ITEM");
        itemId = item.getitemId();
        getOwner(item);

        /* Edit item */
        itemEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemActivity.this, EditItemActivity.class);
                Bundle itemInfo = new Bundle();
                String owner = itemOwner.getText().toString();
                String name = itemName.getText().toString();
                String desc = itemDesc.getText().toString();
                String price = itemPrice.getText().toString();
                /* TODO FIX ITEM IMG */
                String condition = itemCondition.getText().toString();

                itemInfo.putString("OWNER",owner);
                itemInfo.putString("NAME",name);
                itemInfo.putString("DESC",desc);
                itemInfo.putString("PRICE",price);
                itemInfo.putString("CONDITION",condition);

                intent.putExtras(itemInfo);
                startActivityForResult(intent,EDIT_ITEM);
            }
        });

        /* Delete item */
        itemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete();
            }
        });
    }

    private void checkCustomer(int sessionId, int customerId) {
        if (sessionId == customerId) {
            makeOffer.setVisibility(View.GONE);
        }
        else {
            itemEdit.setVisibility(View.GONE);
            itemDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case EDIT_ITEM:
                    Bundle editInfo = data.getExtras();
                    assert editInfo != null;
                    String name = editInfo.getString("NAME");
                    String desc = editInfo.getString("DESC");
                    String price = editInfo.getString("PRICE");
                    String condition = editInfo.getString("CONDITION");

                    itemName.setText(name);
                    itemDesc.setText(desc);
                    itemPrice.setText(price);
                    itemCondition.setText(condition);

                    Double dprice = Double.parseDouble(price);

                    Item item = new Item(name, desc, dprice, null, condition);
                    UPLBTrade.retrofitClient.updateItem(new Callback<Item>() {
                        @Override
                        public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                            System.out.println("Updated Item");
                        }

                        @Override
                        public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                            System.out.println("Failed to update Item");
                            System.out.println(t.getMessage());
                        }
                    }, item, itemId);
                    break;
            }
        }
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
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                Customer customer = response.body();
                int customerId = response.body().getcustomerId();
                displayItem(customer,item);
                checkCustomer(sessionId, customerId);
                System.out.println("Retrieved owner of item");
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, item.getcustomerId());
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Item");
        builder.setMessage("Do you really want to delete your item?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem();
                Toast.makeText(getApplicationContext(), "Item has been deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Delete cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }
    private void deleteItem(){
        UPLBTrade.retrofitClient.deleteItem(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                System.out.println("Deleted Item");
            }

            @Override
            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                System.out.println("Failed to delete item");
            }
        }, itemId);
    }
}
