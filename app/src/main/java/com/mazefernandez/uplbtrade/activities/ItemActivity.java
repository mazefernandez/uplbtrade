package com.mazefernandez.uplbtrade.activities;

import static java.lang.System.out;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.Offer;
import com.mazefernandez.uplbtrade.models.Tag;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* Item details */

public class ItemActivity extends AppCompatActivity {
    private TextView itemOwner;
    private TextView itemName;
    private TextView itemDesc;
    private TextView itemPrice;
    private ImageSwitcher itemImg;
    private TextView itemCondition;
    private Button makeOffer;
    private Button seeOffer;
    private int itemId;
    private int sellerId;
    private int position = 0;
    private int rotation = 0;
    private Offer offer;
    private Customer seller;
    private ChipGroup chipGroup;
    private final ArrayList<String> addresses = new ArrayList<>();
    /* Firebase instances */
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageReference = storage.getReference();

    /* Edit item details */
    ActivityResultLauncher<Intent> editItem = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
       if (result.getResultCode() == Activity.RESULT_OK) {
           Intent intent = result.getData();

           assert intent != null;
           if (intent.getIntExtra("CHECK",-1) == 1) {
               Toast.makeText(this, "Edited item", Toast.LENGTH_SHORT).show();
           }
           finish();
           startActivity(getIntent());
       }
    });

    /* Create a new offer for item */
    ActivityResultLauncher<Intent> addOffer = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = result.getData();
            assert intent != null;
            if (intent.getIntExtra("CHECK",-1) == 1) {
                Toast.makeText(this, "made offer", Toast.LENGTH_SHORT).show();
            }
            finish();
            startActivity(getIntent());
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        /* SharedPref to save customer_id */
        final SharedPreferences pref = this.getSharedPreferences("uplbtrade", MODE_PRIVATE);
        int sessionId = pref.getInt("customer_id",-1);

        /* Item activity views */
        itemOwner = findViewById(R.id.item_owner);
        itemName = findViewById(R.id.item_name);
        itemDesc = findViewById(R.id.item_desc);
        itemPrice = findViewById(R.id.offer);
        itemImg = findViewById(R.id.item_img);
        itemCondition = findViewById(R.id.item_condition);
        ImageButton itemEdit = findViewById(R.id.item_edit);
        ImageButton itemDelete = findViewById(R.id.item_delete);
        ImageButton flag = findViewById(R.id.flag);
        makeOffer = findViewById(R.id.make_offer);
        seeOffer = findViewById(R.id.see_offer);
        chipGroup = findViewById(R.id.tags);
        Button previous = findViewById(R.id.previous);
        Button next = findViewById(R.id.next);

        /* Show all images in ImageSwitcher */
        itemImg.setFactory(() -> new ImageView(getApplicationContext()));

        /* Initialize ImageSwitcher with animations */
        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this,android.R.anim.fade_out);
        itemImg.setInAnimation(in);
        itemImg.setOutAnimation(out);

        /* Default visibility for offer */
        seeOffer.setVisibility(View.GONE);
        /* Retrieve item data */
        Item item = (Item) getIntent().getSerializableExtra("ITEM");
        assert item != null;
        itemId = item.getItemId();
        sellerId = item.getCustomerId();
        getOwner(item);
        getTags(itemId);

        /* Set View Visibilities */
        UPLBTrade.retrofitClient.getOfferBuying(new Callback<List<Offer>>() {
            @Override
            public void onResponse(@NonNull Call<List<Offer>> call, @NonNull Response<List<Offer>> response) {
                ArrayList<Offer> offers = (ArrayList<Offer>) response.body();
                assert offers != null;
                for (int i = 0; i<offers.size(); i++) {
                    if (offers.get(i).getItemId() == itemId) {
                        offer = new Offer(offers.get(i),offers.get(i).getOfferId());
                        seeOffer.setVisibility(View.VISIBLE);
                        seeOffer.isClickable();
                        makeOffer.setVisibility(View.GONE);
                        break;
                    }
                    else {
                        seeOffer.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Offer>> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, sessionId);

        /* Check if customer owns this item */
        if (sessionId == sellerId) {
            makeOffer.setVisibility(View.GONE);
            seeOffer.setVisibility(View.GONE);
            flag.setVisibility(View.GONE);
        }
        else {
            makeOffer.setVisibility(View.VISIBLE);
            flag.setVisibility(View.VISIBLE);
            flag.isClickable();
            itemEdit.setVisibility(View.GONE);
            itemDelete.setVisibility(View.GONE);
        }

        /* Edit item */
        itemEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ItemActivity.this, EditItemActivity.class);
            Bundle itemInfo = new Bundle();
            String owner = itemOwner.getText().toString().trim();
            String name = itemName.getText().toString().trim();
            String desc = itemDesc.getText().toString().trim();
            String price = itemPrice.getText().toString().trim();
            String image = item.getImage();
            String condition = itemCondition.getText().toString().trim();
            int itemId = item.getItemId();

            itemInfo.putString("OWNER",owner);
            itemInfo.putString("NAME",name);
            itemInfo.putString("DESC",desc);
            itemInfo.putString("PRICE",price);
            itemInfo.putString("IMAGE", image);
            itemInfo.putString("CONDITION",condition);
            itemInfo.putInt("ID", itemId);

            intent.putExtras(itemInfo);
            editItem.launch(intent);
            seeOffer.setVisibility(View.GONE);
        });

        /* Delete item */
        itemDelete.setOnClickListener(v -> confirmDelete());

        /* Make offer */
        makeOffer.setOnClickListener(v -> {
            Intent intent = new Intent(ItemActivity.this, MakeOfferActivity.class);
            Bundle offerInfo = new Bundle();
            String owner = itemOwner.getText().toString().trim();
            String name = itemName.getText().toString().trim();
            String image = item.getImage();

            offerInfo.putString("OWNER", owner);
            offerInfo.putString("NAME", name);
            offerInfo.putString("IMAGE", image);
            offerInfo.putInt("ITEM", itemId);
            offerInfo.putInt("SESSION", sessionId);
            offerInfo.putInt("SELLER", sellerId);
            intent.putExtras(offerInfo);
            addOffer.launch(intent);
        });

        /* View current offer */
        seeOffer.setOnClickListener(v -> {
            Intent intent = new Intent(ItemActivity.this, OfferActivity.class);
            intent.putExtra("OFFER",offer);
            startActivity(intent);
        });

        if (sessionId != sellerId) {
            itemOwner.setOnClickListener(v -> {
                Intent intent = new Intent(ItemActivity.this, CustomerProfileActivity.class);
                intent.putExtra("CUSTOMER",seller);
                startActivity(intent);
            });
            itemOwner.setTextColor(Color.BLUE);
        }

        /* Select next image */
        next.setOnClickListener(view -> {
            if (position < addresses.size() - 1) {
                position = position + 1;
                StorageReference ref = storageReference.child(addresses.get(position));
                final long ONE_MEGABYTE = 1024 * 1024 * 5;
                ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Drawable drawable = new BitmapDrawable(this.getResources(), bitmap);
                    itemImg.setImageDrawable(drawable);
                    System.out.println("Successfully read image");
                }).addOnFailureListener(fail -> System.out.println("Failed to read image" + fail));
            }
            else {
                Toast.makeText(ItemActivity.this, "This is the last image.", Toast.LENGTH_SHORT).show();
            }
        });

        /* Select previous image */
        previous.setOnClickListener(view -> {
            if (position > 0) {
                position = position - 1;
                StorageReference ref = storageReference.child(addresses.get(position));
                final long ONE_MEGABYTE = 1024 * 1024 * 5;
                ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Drawable drawable = new BitmapDrawable(this.getResources(), bitmap);
                    itemImg.setImageDrawable(drawable);
                    System.out.println("Successfully read image");
                }).addOnFailureListener(fail -> System.out.println("Failed to read image" + fail));
            }
        });

        flag.setOnClickListener(v -> {
            Intent intent = new Intent(ItemActivity.this, ReportItemActivity.class);
            intent.putExtra("ITEM", item);
            startActivity(intent);
        });
    }
    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    /* Display Item Details */
    private void displayItem(Customer customer, Item item) {
        itemOwner.setText(String.format("%s %s", customer.getFirstName(), customer.getLastName()));
        itemName.setText(item.getItemName());
        itemDesc.setText(item.getDescription());
        String price = "\u20B1" + item.getPrice().toString();
        itemPrice.setText(price);
        if (item.getImage() == null) {
            itemImg.setImageResource(R.drawable.placeholder);
        }
        else {
            /* retrieve image from firebase */
            String[] split = item.getImage().split("-");
            String image = split[split.length-2];
            String rotate = split[split.length-1];
            int size = Integer.parseInt(image);
            rotation = Integer.parseInt(rotate);
            /* Store all addresses in an arraylist */
            for (int i = 0; i<size; i++){
                String address = "images/" + item.getImage() + "/" + i;
                addresses.add(address);
            }
            StorageReference ref = storageReference.child(addresses.get(0));
            final long ONE_MEGABYTE = 1024 * 1024 * 5;
            ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Drawable drawable = new BitmapDrawable(this.getResources(), bitmap);
                itemImg.setImageDrawable(drawable);
                itemImg.setRotation(rotation);
                System.out.println("Successfully read image");
            }).addOnFailureListener(fail -> System.out.println("Failed to read image" + fail));
        }
        itemCondition.setText(item.getCondition());

        seller = new Customer(customer.getCustomerId(), customer.getImage(),customer.getFirstName(),customer.getLastName(),
                customer.getEmail(),customer.getAddress(),customer.getContactNo(),customer.getOverallRating());
    }

    /* Retrieve owner of item */
    private void getOwner(final Item item) {
        UPLBTrade.retrofitClient.getCustomer(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                Customer customer = response.body();
                assert customer != null;
                displayItem(customer,item);
                out.println("Retrieved owner of item");
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                out.println(t.getMessage());
            }
        }, item.getCustomerId());
    }

    /* Retrieve the tags of the item */
    private void getTags(int itemId) {
        UPLBTrade.retrofitClient.getTagsFromItem(new Callback<List<Tag>>() {
            @Override
            public void onResponse(@NonNull Call<List<Tag>> call, @NonNull Response<List<Tag>> response) {
                ArrayList<Tag> tags = (ArrayList<Tag>) response.body();
                assert tags != null;
                /* Add tags to chip group */
                for (int i=0; i<tags.size();i++) {
                    addChip(tags.get(i).getTagName());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Tag>> call, @NonNull Throwable t) {

            }
        }, itemId);
    }

    /* Delete item confirmation */
    private void confirmDelete() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Item");
        builder.setMessage("Do you really want to delete your item?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteItem();
            Toast.makeText(getApplicationContext(), "Item has been deleted", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            finish();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            Toast.makeText(getApplicationContext(), "Delete cancelled", Toast.LENGTH_SHORT).show();
            dialog.cancel();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
    }

    /* Delete user's item */
    private void deleteItem(){
        UPLBTrade.retrofitClient.deleteItem(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                out.println("Deleted Item");
            }

            @Override
            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                out.println("Failed to delete item");
                out.println(t.getMessage());

            }
        }, itemId);
    }
    /* Add tag to chip group */
    private void addChip(String tag) {
        try {
            LayoutInflater inflater = LayoutInflater.from(this);
            Chip chip = (Chip) inflater.inflate(R.layout.chip, chipGroup, false);
            chip.setId(ViewCompat.generateViewId());
            chip.setText(tag);
            chip.setCloseIconVisible(false);
            chip.setClickable(true);
            chip.setCheckable(false);
            chipGroup.addView(chip);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in adding chip " + e.getMessage());
        }

    }
}
