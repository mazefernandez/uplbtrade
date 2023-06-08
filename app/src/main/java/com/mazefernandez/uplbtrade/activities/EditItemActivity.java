package com.mazefernandez.uplbtrade.activities;

import static java.lang.System.out;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.Spinner;
import android.widget.TextView;
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

/* Edit Item */

public class EditItemActivity extends AppCompatActivity {
    private TextView itemOwner;
    private EditText itemName;
    private EditText itemDesc;
    private EditText itemPrice;
    private ImageSwitcher itemImg;
    private Spinner itemCondition;
    private Bundle itemInfo;
    private String imgString;
    private int itemId;
    private int position = 0;
    private boolean duplicate;
    private EditText addTags;
    private ChipGroup chipGroup;
    private final ArrayList<Uri> uriArrayList = new ArrayList<>();

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
                }
                /* display first image */
            }
            else {
                Uri url = intent.getData();
                uriArrayList.add(url);
            }
            itemImg.setImageURI(uriArrayList.get(0));
            position = 0;
        }
        else {
            Toast.makeText(this, "Please pick an image.", Toast.LENGTH_LONG).show();
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
        chipGroup = findViewById(R.id.chip_group);

        Button saveItem = findViewById(R.id.save_item);
        Button cancel = findViewById(R.id.cancel);
        Button next = findViewById(R.id.next);
        Button previous = findViewById(R.id.previous);

        ImageButton addTag = findViewById(R.id.add_tag);
        ImageButton rotate = findViewById(R.id.rotate);

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
            String name = itemName.getText().toString().trim();
            String desc = itemDesc.getText().toString().trim();
            String price = itemPrice.getText().toString().trim();
            String condition = itemCondition.getSelectedItem().toString().trim();
            Double double_price = Double.parseDouble(price);

            /* Upload image to firebase storage */
            imgString = UUID.randomUUID().toString();
            int size = uriArrayList.size();
            imgString = imgString + "-" + size;
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

            if (uriArrayList.isEmpty()) {
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
            deleteTags(itemId);
            addTags(itemId);

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
            String newTag = addTags.getText().toString().trim();
            if(!newTag.isEmpty()) {
                /* check for duplicate tags */
                duplicate = checkDuplicate(newTag);
                if (!duplicate) {
                    addChip(newTag);
                }
                else {
                    Toast.makeText(EditItemActivity.this, newTag + " is already added", Toast.LENGTH_SHORT).show();
                }
                addTags.setText("");
            }
            else {
                Toast.makeText(EditItemActivity.this, "Enter a tag", Toast.LENGTH_SHORT).show();
            }
        });

        /* Rotate Image */
        rotate.setOnClickListener(v -> itemImg.setRotation(90));

        /* Select next image */
        next.setOnClickListener(view -> {
            if (position < uriArrayList.size() - 1) {
                position = position + 1;
                itemImg.setImageURI(uriArrayList.get(position));
            } else {
                Toast.makeText(EditItemActivity.this, "This is the last image.", Toast.LENGTH_SHORT).show();
            }
        });

        /* Select previous image */
        previous.setOnClickListener(view -> {
            if (position > 0) {
                position = position - 1;
                itemImg.setImageURI(uriArrayList.get(position));
            }
        });

        /* Upload image to item */
        addItemImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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
    public void displayItem(int spinnerPosition) {
        itemOwner.setText(itemInfo.getString("OWNER"));
        itemName.setText(itemInfo.getString("NAME"));
        itemDesc.setText(itemInfo.getString("DESC"));
        itemPrice.setText(itemInfo.getString("PRICE"));
        itemCondition.setSelection(spinnerPosition);

        /* Firebase instances */
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        /* retrieve size of folder of images from firebase */
        if (itemInfo.getString("IMAGE") == null) {
            itemImg.setImageResource(R.drawable.placeholder);
        }
        else {
            /* retrieve image from firebase */
            String[] split = itemInfo.getString("IMAGE").split("-");
            String image = split[split.length - 1];
            int size = Integer.parseInt(image);

            for (int i = 0; i < size; i++) {
                StorageReference ref = storageReference.child("images/" + itemInfo.getString("IMAGE") + "/" + i);
                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    uriArrayList.add(uri);
                    System.out.println("Successfully read image");

                }).addOnFailureListener(fail -> out.println("Failed to read image" + fail));
            }
        }

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

    private void addTags(int itemId){
        /* Send tags to database */
        ArrayList<Tag> tags = new ArrayList<>();

        for (int i=0;i<this.chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) this.chipGroup.getChildAt(i);

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
    private void deleteTags(int itemId){
        UPLBTrade.retrofitClient.deleteTag(new Callback<Tag>() {
            @Override
            public void onResponse(@NonNull Call<Tag> call, @NonNull Response<Tag> response) {
                System.out.println("Deleted Tags");
                System.out.println(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Tag> call, @NonNull Throwable t) {
                System.out.println("Failed to delete tags");
                System.out.println(t.getMessage());
            }
        }, itemId);
    }
}