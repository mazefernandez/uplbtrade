package com.mazefernandez.uplbtrade.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.Tag;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* Upload Item for selling */

public class AddItemActivity extends AppCompatActivity {
    private ImageView itemImg;
    private EditText itemName;
    private EditText itemDesc;
    private EditText itemPrice;
    private Spinner itemCondition;
    private EditText itemTags;
    private String imgString;
    private Integer itemId;
    private Bitmap bitmap;
    private final ArrayList<String> tagList = new ArrayList<>();

    /* AR Launchers to replace OnActivityResult */
    ActivityResultLauncher<Intent> selectImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = result.getData();
            /* get image */
            if (intent != null) {
                Uri uri = intent.getData();
                String[] filePathColumn = {MediaStore.Images.Media._ID};
                assert uri != null;
                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                cursor.close();
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    itemImg.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    // Firebase instances
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        /* Upload Item Views */
        itemImg = findViewById(R.id.item_img);
        FloatingActionButton addItemImg = findViewById(R.id.add_item_img);
        itemName = findViewById(R.id.item_name);
        itemDesc = findViewById(R.id.item_desc);
        itemPrice = findViewById(R.id.offer);
        itemCondition = findViewById(R.id.item_condition);
        itemTags = findViewById(R.id.tags);
        ListView tagsList = findViewById(R.id.tag_list);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Button addTag = findViewById(R.id.add_tag);
        Button deleteTag = findViewById(R.id.delete_tag);

        Button addItem = findViewById(R.id.add_item);
        Button cancel = findViewById(R.id.cancel);

        /* ArrayAdapter for condition spinner */
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.conditions, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemCondition.setAdapter(spinnerAdapter);

        /* listview for tags */
        ArrayAdapter<String> stringAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, tagList);
        tagsList.setAdapter(stringAdapter);

        /* New Item */
        addItem.setOnClickListener(v -> {
        String string_name = itemName.getText().toString();
        String string_desc = itemDesc.getText().toString();
        String string_price = itemPrice.getText().toString();
        String string_condition = itemCondition.getSelectedItem().toString();
        Double double_price = Double.parseDouble(string_price);

        /* save image to file */
        File fileDirectory = getApplicationContext().getFilesDir();
        File file = new File(fileDirectory,"image.jpeg");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,30, bos);
        byte[] bitmapData = bos.toByteArray();

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        /* Get current user */
        int customerId = getIntent().getIntExtra("CUSTOMER_ID", -1);

        /* Upload image to firebase storage */
        imgString = UUID.randomUUID().toString();
        StorageReference ref = storageReference.child("images/" + imgString);

        ref.putBytes(bitmapData).addOnSuccessListener(success -> {
            // Image uploaded successfully
            System.out.println("image uploaded successfully");
        }).addOnFailureListener(failure -> {
            // Image upload fail
            System.out.println("image failed to upload");
        });

        /* Add item to database */
        Item item = new Item(string_name, string_desc, double_price, imgString, string_condition, customerId);

        UPLBTrade.retrofitClient.addItem(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                System.out.println("Added Item");
                System.out.println(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                System.out.println("Failed to add item");
                System.out.println(t.getMessage());
            }
        }, item);

        UPLBTrade.retrofitClient.getItemByImg(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                Item item = response.body();
                assert item != null;
                itemId = item.getItemId();
                System.out.println("Received item_id");
                System.out.println(response.body());
                updateTags(itemId);
            }

            @Override
            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                System.out.println("Failed to receive item_id");
                System.out.println(t.getMessage());
            }
        }, imgString);

        /* Return to profile */
        Intent intent = new Intent();
        intent.putExtra("CHECK", 1);

        setResult(RESULT_OK,intent);
        finish();
        });

        /* Add Tag to tags list for new item */
        addTag.setOnClickListener(v -> {
            String newTag = itemTags.getText().toString();
            if(newTag.length() > 0)
            {
                stringAdapter.add(newTag);
            }
        });

        /* Delete Tag to tags list for new item */
        deleteTag.setOnClickListener(v -> {
            String newTag = itemTags.getText().toString();
            if(newTag.length() > 0)
            {
                stringAdapter.remove(newTag);
            }
        });

        /* Upload image to item */
        addItemImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            selectImage.launch(Intent.createChooser(intent, "Select Picture"));
        });

        /* Cancel and return to profile */
        cancel.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("CHECK", 0);
            setResult(RESULT_OK,intent);
            finish();
        });
    }
    public void updateTags(Integer itemId) {
        /* Send tags to database */
        ArrayList<Tag> tags = new ArrayList<>();

        for (int i=0;i<tagList.size(); i++) {
            Tag tag = new Tag(tagList.get(i), itemId);
            tags.add(tag);
        }

        UPLBTrade.retrofitClient.addTags(new Callback<List<Tag>>() {
            @Override
            public void onResponse(@NonNull Call<List<Tag>> call, @NonNull Response<List<Tag>> response) {
                System.out.println("Added Tags");
                System.out.println(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<Tag>> call, @NonNull Throwable t) {
                System.out.println("Failed to add tags");
                System.out.println(t.getMessage());
            }
        }, tags);
    }
}
