package com.mazefernandez.uplbtrade.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* Upload Item for selling */

public class AddItemActivity extends AppCompatActivity {
    private EditText itemName;
    private EditText itemDesc;
    private EditText itemPrice;
    private Spinner itemCondition;
    private EditText itemTags;
    private String imgString;
    private Integer itemId;
    private ImageSwitcher itemImg;
    private int position = 0;
    private int rotation = 0;
    private boolean duplicate;
    private ChipGroup chipGroup;
    private final ArrayList<Uri> uriArrayList = new ArrayList<>();
//    private final ArrayList<Integer> rotationList = new ArrayList<>();

    /* AR Launchers to replace OnActivityResult */
    ActivityResultLauncher<Intent> selectImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = result.getData();
            /* get images */
            assert intent != null;
            if (intent.getClipData() != null) {
                ClipData clipData = intent.getClipData();
                int count = clipData.getItemCount();
                /* add all images to array */
                for (int i=0; i<count; i++) {
                    Uri url = clipData.getItemAt(i).getUri();
                    uriArrayList.add(url);
//                    try{
//                        ContentResolver contentResolver = getContentResolver();
//                        InputStream inputStream = contentResolver.openInputStream(url);
//                        ExifInterface ei = new ExifInterface(inputStream);
//                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//                        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
//                            rotationList.add(90);
//                        }
//                        else if (orientation == ExifInterface.ORIENTATION_ROTATE_180){
//                            rotationList.add(180);
//                        }
//                        else if (orientation == ExifInterface.ORIENTATION_ROTATE_270){
//                            rotationList.add(270);
//                        }
//                        else {
//                            rotationList.add(0);
//                        }
//                    } catch(Exception e) {
//                        System.out.println("Error getting rotation: "+e.getMessage());
//                    }
                }
            }
            else {
                Uri url = intent.getData();
                uriArrayList.add(url);
            }
            /* display first image */
            itemImg.setImageURI(uriArrayList.get(0));
            itemImg.setRotation(rotation);
//            itemImg.setRotation(rotationList.get(0));
            position = 0;
        }
        else {
            Toast.makeText(this, "Please pick an image.", Toast.LENGTH_LONG).show();
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
        FloatingActionButton addItemImg = findViewById(R.id.add_item_img);
        itemImg = findViewById(R.id.item_img);
        itemName = findViewById(R.id.item_name);
        itemDesc = findViewById(R.id.item_desc);
        itemPrice = findViewById(R.id.offer);
        itemCondition = findViewById(R.id.item_condition);
        itemTags = findViewById(R.id.tags);
        chipGroup = findViewById(R.id.chip_group);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Button previous = findViewById(R.id.previous);
        Button next = findViewById(R.id.next);

        ImageButton addTag = findViewById(R.id.add_tag);
        ImageButton rotate = findViewById(R.id.rotate);
        ImageButton deleteImages = findViewById(R.id.delete_images);
        ImageButton info = findViewById(R.id.info);

        Button addItem = findViewById(R.id.add_item);
        Button cancel = findViewById(R.id.cancel);

        /* Show all images in ImageSwitcher */
        itemImg.setFactory(() -> new ImageView(this));

        /* Initialize ImageSwitcher with animations */
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        itemImg.setInAnimation(in);
        itemImg.setOutAnimation(out);

        /* ArrayAdapter for condition spinner */
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.conditions, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemCondition.setAdapter(spinnerAdapter);

        deleteImages.setOnClickListener(v -> confirmDelete());
        info.setOnClickListener(v -> provideInfo());

        /* New Item */
        addItem.setOnClickListener(v -> {
            String string_name = itemName.getText().toString().trim();
            String string_desc = itemDesc.getText().toString().trim();
            String string_price = itemPrice.getText().toString().trim();
            String string_condition = itemCondition.getSelectedItem().toString().trim();
            Double double_price = Double.parseDouble(string_price);

            /* Get current user */
            int customerId = getIntent().getIntExtra("CUSTOMER_ID", -1);

            /* Upload image to firebase storage */
            imgString = UUID.randomUUID().toString();
            int size = uriArrayList.size();
            imgString = imgString + "-" + size + "-" + rotation;
            int i;
            for (i = 0; i < uriArrayList.size(); i++) {
                Uri file = uriArrayList.get(i);
                StorageReference ref = storageReference.child("images/" + imgString + "/" + i);
                UploadTask uploadTask = ref.putFile(file);

                uploadTask.addOnSuccessListener(t -> {
                    System.out.println("Uploaded image");
                    System.out.println(t.getMetadata());
                }).addOnFailureListener(t -> {
                    System.out.println("Failed to upload image");
                    System.out.println(t.getMessage());
                });
            }

            /* Add item to database */
            Item item = new Item(string_name, string_desc, double_price, imgString, string_condition, customerId);

            UPLBTrade.retrofitClient.addItem(new Callback<Item>() {
                @Override
                public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                    System.out.println("Added Item");
                    System.out.println(response.body());
                    getItemFromImage(imgString);
                }

                @Override
                public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                    System.out.println("Failed to add item");
                    System.out.println(t.getMessage());
                }
            }, item);

            /* Return to profile */
            Intent intent = new Intent();
            intent.putExtra("CHECK", 1);

            setResult(RESULT_OK, intent);
            finish();
        });

        /* Add Tag to tags list for new item */
        addTag.setOnClickListener(v -> {
            String newTag = itemTags.getText().toString().trim();
            if (!newTag.isEmpty()) {
                duplicate = checkDuplicate(newTag);
                if (!duplicate) addChip(newTag);
                else {
                    Toast.makeText(AddItemActivity.this, newTag + " is already added", Toast.LENGTH_SHORT).show();
                }
                itemTags.setText("");
            }
            else {
                Toast.makeText(AddItemActivity.this, "Enter a tag", Toast.LENGTH_SHORT).show();
            }
        });

        /* Select next image */
        next.setOnClickListener(view -> {
            if (position < uriArrayList.size() - 1) {
                position = position + 1;
                itemImg.setImageURI(uriArrayList.get(position));
                itemImg.setRotation(rotation);
            } else {
                Toast.makeText(AddItemActivity.this, "This is the last image.", Toast.LENGTH_SHORT).show();
            }
        });

        /* Select previous image */
        previous.setOnClickListener(view -> {
            if (position > 0) {
                position = position - 1;
                itemImg.setImageURI(uriArrayList.get(position));
                itemImg.setRotation(rotation);
            }
        });

        /* Rotate Image */
        rotate.setOnClickListener(view -> {
            rotation = (rotation + 90) % 360;
            itemImg.setRotation(rotation);
//            rotationList.set(position,rotation);
        });

        /* Upload image to item */
        addItemImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            selectImage.launch(Intent.createChooser(intent, "Select Picture"));
        });

        /* Cancel and return to profile */
        cancel.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("CHECK", 0);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
    /* Get item_id of newly inserted item from the database */
    private void getItemFromImage(String imgString) {
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
    }
    /* Send tags to database */
    private void updateTags(int itemId) {
        /* Send tags to database */
        ArrayList<Tag> tags = new ArrayList<>();

        for (int i=0;i<chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);

            Tag tag = new Tag((String) chip.getText(), itemId);
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
                System.out.println(tags);
            }
        }, tags);
    }
    /* Add chips to chip group when user adds tag */
    private void addChip(String tag) {
        try {
            LayoutInflater inflater = LayoutInflater.from(this);
            Chip chip = (Chip) inflater.inflate(R.layout.chip, chipGroup, false);
            chip.setId(ViewCompat.generateViewId());
            chip.setText(tag);
            chip.setCloseIconVisible(true);
            chip.setClickable(true);
            chip.setCheckable(false);
            chip.setOnCloseIconClickListener(v -> chipGroup.removeView(chip));
            chipGroup.addView(chip);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in adding chip " + e.getMessage());
        }

    }
    /* Check if there's a duplicate tag */
    private boolean checkDuplicate(String tag) {
        for (int i=0;i<chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.getText().equals(tag)) return true;
        }
        return false;
    }
    private void confirmDelete(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Images");
        builder.setMessage("Do you really want to delete your images?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteImages();
            Toast.makeText(getApplicationContext(), "Images deleted", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
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
    private void deleteImages() {
        itemImg.setImageDrawable(null);
    }
    private void provideInfo() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How to Add Items");
        builder.setMessage("Make your title simple! e.g (TC7)\nAdd more details in the description.\n* Year purchased, subjects, marks, damage, etc.\n(Max 250 characters)");

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
