package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.models.Item;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

public class AddItemActivity extends AppCompatActivity {
    private ImageView itemImg;
    private EditText itemName;
    private EditText itemDesc;
    private EditText itemPrice;
    private EditText itemCondition;
    private String imgString;
    private Bitmap bitmap;

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

        Button addItem = findViewById(R.id.add_item);
        Button cancel = findViewById(R.id.cancel);

        /* New Item */
        addItem.setOnClickListener(v -> {
        String string_name = itemName.getText().toString();
        String string_desc = itemDesc.getText().toString();
        String string_price = itemPrice.getText().toString();
        String string_condition = itemCondition.getText().toString();
        Double double_price = Double.parseDouble(string_price);

        /* save image to file */
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

            int customerId = getIntent().getIntExtra("CUSTOMER_ID", -1);

        /* Convert item data to RequestBody for database */
        RequestBody name = RequestBody.create(MultipartBody.FORM, string_name);
        RequestBody description = RequestBody.create(MultipartBody.FORM, string_desc);
        RequestBody price = RequestBody.create(MultipartBody.FORM, String.valueOf(double_price));
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part image = MultipartBody.Part.createFormData("image",file.getName(), fileReqBody);
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
            }
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
        });

        /* Cancel and return to profile */
        cancel.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("CHECK", 0);
            setResult(RESULT_OK,intent);
            finish();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_IMAGE:
                    if (data != null) {
                        Uri uri = data.getData();
                        String[] filePathColumn = { MediaStore.Images.Media._ID };
                        assert uri != null;
                        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                        assert cursor != null;
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imgString = cursor.getString(columnIndex);
                        cursor.close();
                        try {
                            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                            itemImg.setImageBitmap(bitmap);
                        }
                        catch(IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
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
