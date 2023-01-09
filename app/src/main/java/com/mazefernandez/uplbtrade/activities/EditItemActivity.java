package com.mazefernandez.uplbtrade.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

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

/* Edit Item */

public class EditItemActivity extends AppCompatActivity {
    private TextView itemOwner;
    private EditText itemName;
    private EditText itemDesc;
    private EditText itemPrice;
    private ImageView itemImg;
    private Spinner itemCondition;
    private Bundle itemInfo;
    private String imgString;
    private Bitmap bitmap;
    private Uri filepath;
    private int itemId;
    private EditText addTags;
    private ListView tagsList;
    private final ArrayList<Tag> tags = new ArrayList<>();
    private final ArrayList<Tag> newTags = new ArrayList<>();
    private final ArrayList<Tag> deleteTags = new ArrayList<>();
    private final ArrayList<String> tagStrings = new ArrayList<>();


    /* AR Launchers to replace OnActivityResult */
    ActivityResultLauncher<Intent> selectImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = result.getData();
            /* get image */
            if (intent != null) {
                Uri uri = intent.getData();
                filepath = uri;
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        /* Firebase instances */
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();



        /* Edit Item Views */
        FloatingActionButton addItemImg = findViewById(R.id.add_item_img);
        itemOwner = findViewById(R.id.item_owner);
        itemName = findViewById(R.id.item_name);
        itemDesc = findViewById(R.id.item_desc);
        itemPrice = findViewById(R.id.offer);
        itemImg = findViewById(R.id.item_img);
        itemCondition = findViewById(R.id.item_condition);
        addTags = findViewById(R.id.add_tags);
        tagsList = findViewById(R.id.tags);

        Button saveItem = findViewById(R.id.save_item);
        Button addTag = findViewById(R.id.add_tag);
        Button deleteTag = findViewById(R.id.delete_tag);
        Button cancel = findViewById(R.id.cancel);

        /* listview for tags */
        ArrayAdapter<String> stringAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, tagStrings);
        tagsList.setAdapter(stringAdapter);

        /* ArrayAdapter for condition spinner */
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.conditions, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemCondition.setAdapter(spinnerAdapter);

        /* Display Item Details */
        Intent itemIntent = getIntent();
        itemInfo = itemIntent.getExtras();

        String conditionValue = itemInfo.getString("CONDITION");
        itemId = itemInfo.getInt("ID");
        int spinnerPosition = spinnerAdapter.getPosition(conditionValue);

        displayItem(spinnerPosition);
        getTags(itemId);

        /* Save info to item */
        saveItem.setOnClickListener(view -> {
            String name = itemName.getText().toString();
            String desc = itemDesc.getText().toString();
            String price = itemPrice.getText().toString();
            String condition = itemCondition.getSelectedItem().toString();
            Double double_price = Double.parseDouble(price);

            /* save image to file */
            if (bitmap != null) {
                File fileDirectory = getApplicationContext().getFilesDir();
                File file = new File(fileDirectory,"image.jpeg");

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,0, bos);
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

                /* Upload image to firebase storage */
                imgString = UUID.randomUUID().toString();
                StorageReference ref = storageReference.child("images/" + imgString);

                ref.putFile(filepath).addOnSuccessListener(success -> {
                    // Image uploaded successfully
                    System.out.println("image uploaded successfully");
                }).addOnFailureListener(failure -> {
                    // Image upload fail
                    System.out.println("image failed to upload");
                });
            }

            if (imgString == null) {
                imgString = itemInfo.getString("IMAGE");
            }

            /* Save to database */
            Item item = new Item(name, desc, double_price, imgString, condition);

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

            /* Add and delete tags from database */

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
            }, newTags);



            /* Return to item */
            Intent intent = new Intent();
            intent.putExtra("CHECK", 1);
            setResult(RESULT_OK,intent);
            finish();
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



        /* Add Tag to tags list for new item */
        addTag.setOnClickListener(v -> {
            String newTag = addTags.getText().toString();
            if(newTag.length() > 0)
            {
                Tag tag = new Tag(newTag, itemId);
                tags.add(tag);
                stringAdapter.add(newTag);

                newTags.add(tag);
            }
        });

        /* Delete Tag to tags list for new item */
        deleteTag.setOnClickListener(v -> {
            String newTag = addTags.getText().toString();
            if(newTag.length() > 0)
            {
                Tag tag = new Tag(newTag, itemId);
                tags.remove(tag);
                stringAdapter.remove(newTag);

                deleteTags.add(tag);
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

        /* Cancel and return to item */
        cancel.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("CHECK", 0);
            setResult(RESULT_OK,intent);
            finish();
        });
    }

    /* Retrieve the tags of the item */
    private void getTags(int itemId) {
        UPLBTrade.retrofitClient.getTagsFromItem(new Callback<List<Tag>>() {
            @Override
            public void onResponse(@NonNull Call<List<Tag>> call, @NonNull Response<List<Tag>> response) {
                ArrayList<Tag> tags = (ArrayList<Tag>) response.body();
                assert tags != null;
                ArrayList<Tag> tagList = new ArrayList<>(tags);
                for (Tag tag: tagList) {
                    tagStrings.add(tag.getTagName());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Tag>> call, @NonNull Throwable t) {

            }
        }, itemId);
    }
    public void displayItem(int spinnerPosition) {
        itemOwner.setText(itemInfo.getString("OWNER"));
        itemName.setText(itemInfo.getString("NAME"));
        itemDesc.setText(itemInfo.getString("DESC"));
        itemPrice.setText(itemInfo.getString("PRICE"));
        itemCondition.setSelection(spinnerPosition);

        /* Firebase instances */
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        /* retrieve image from firebase */
        StorageReference ref = storageReference.child("images/" + itemInfo.getString("IMAGE"));

        final long ONE_MEGABYTE = 1024 * 1024 * 5;
        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            System.out.println("Successfully read image");
            itemImg.setImageBitmap(bitmap);
        }).addOnFailureListener(fail -> System.out.println("Failed to read image" + fail));

    }
}
