package com.mazefernandez.uplbtrade.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    private final ArrayList<Uri> uriArrayList = new ArrayList<>();
    private final ArrayList<String> tagList = new ArrayList<>();

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
        ListView tagsList = findViewById(R.id.tag_list);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Button previous = findViewById(R.id.previous);
        Button next = findViewById(R.id.next);

        Button addTag = findViewById(R.id.add_tag);
        Button deleteTag = findViewById(R.id.delete_tag);

        Button addItem = findViewById(R.id.add_item);
        Button cancel = findViewById(R.id.cancel);

        /* Show all images in ImageSwitcher */
        itemImg.setFactory(() -> new ImageView(getApplicationContext()));

        /* Initialize ImageSwitcher with animations */
        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right);
        itemImg.setInAnimation(in);
        itemImg.setOutAnimation(out);

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

        /* Get current user */
        int customerId = getIntent().getIntExtra("CUSTOMER_ID", -1);

        /* Upload image to firebase storage */
        imgString = UUID.randomUUID().toString();
        int size = uriArrayList.size();
        imgString = imgString + "-" + size;
        int i;
        for (i = 0; i<uriArrayList.size(); i++) {
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

        /* Select next image */
        next.setOnClickListener(view -> {
            if (position < uriArrayList.size() - 1) {
                position = position + 1;
                itemImg.setImageURI(uriArrayList.get(position));
            }
            else {
                Toast.makeText(AddItemActivity.this, "This is the last image.", Toast.LENGTH_SHORT).show();
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
                System.out.println(tags);
            }
        }, tags);
    }
}
