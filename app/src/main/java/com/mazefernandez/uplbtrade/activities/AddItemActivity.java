package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.models.Item;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mazefernandez.uplbtrade.models.RequestCode.SELECT_IMAGE;

/* Upload Item for selling */

public class AddItemActivity extends AppCompatActivity{
    private ImageView itemImg;
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
        FloatingActionButton addItemImg = findViewById(R.id.add_item_img);
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
            String string_name = itemName.getText().toString();
            String string_desc = itemDesc.getText().toString();
            String string_price = itemPrice.getText().toString();
            String string_condition = itemCondition.getText().toString();
            Bitmap bitmap_image = ((BitmapDrawable)itemImg.getDrawable()).getBitmap();
            Double double_price = Double.parseDouble(string_price);

            /* save image to file */
            try {
                File f = new File(AddItemActivity.this.getCacheDir(), "image.jpg");
                f.createNewFile();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap_image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(imageBytes);
                fos.flush();
                fos.close();

                int customerId = getIntent().getIntExtra("CUSTOMER_ID", -1);

                /* Convert item data to RequestBody for database */
                RequestBody name = RequestBody.create(MultipartBody.FORM, string_name);
                RequestBody description = RequestBody.create(MultipartBody.FORM, string_desc);
                RequestBody price = RequestBody.create(MultipartBody.FORM, String.valueOf(double_price));
                RequestBody fileReqBody = RequestBody.create(MultipartBody.FORM, f);
                MultipartBody.Part image = MultipartBody.Part.createFormData(bitmapToString(bitmap_image),f.getName(), fileReqBody);
                RequestBody condition = RequestBody.create(MultipartBody.FORM, string_condition);
                RequestBody customer_id = RequestBody.create(MultipartBody.FORM, String.valueOf(customerId));

                System.out.println(image);

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
                }, name, description, price, image, condition, customer_id);

            } catch (IOException ie) {
                ie.printStackTrace();
            }

            Intent intent = new Intent();
            intent.putExtra("CHECK", 1);
            setResult(RESULT_OK,intent);
            finish();
            }
        });

        addItemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
            }
        });

        /* Cancel and return to profile */
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("CHECK", 0);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_IMAGE:
                    if (data != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                            itemImg.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    public String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArrayOutputStream);
        byte[] b=byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }
}
