package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.mazefernandez.uplbtrade.picasso.CircleTransformation;
import com.mazefernandez.uplbtrade.R;
import com.squareup.picasso.Picasso;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.GOOGLE_ACCOUNT;
/* Edit Customer Information */

public class EditProfileActivity extends AppCompatActivity {
    private TextView editName;
    private EditText editAddress, editContactNo;
    private ImageView editImg;
    private Button saveCustomer;
    private Intent profileIntent;
    private Bundle userInfo;
    private GoogleSignInAccount account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editName = findViewById(R.id.edit_text);
        editAddress = findViewById(R.id.edit_address);
        editContactNo = findViewById(R.id.edit_contactNo);
        editImg = findViewById(R.id.edit_img);
        saveCustomer = findViewById(R.id.save_customer);
        profileIntent = getIntent();
        userInfo = profileIntent.getExtras();
        assert userInfo != null;
        account = userInfo.getParcelable(GOOGLE_ACCOUNT);

        displayUser();

        // save new info to Customer
        saveCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editAddress = findViewById(R.id.edit_address);
                EditText editContactNo = findViewById(R.id.edit_contactNo);
                String address = editAddress.getText().toString();
                String contactNo = editContactNo.getText().toString();

                Intent intent = new Intent();
                Bundle editInfo = new Bundle();
                editInfo.putString("NEW_ADDRESS",address);
                editInfo.putString("NEW_CONTACT",contactNo);
                intent.putExtras(editInfo);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    // display user account data
    private void displayUser() {
        Picasso.get().load(account.getPhotoUrl()).centerInside().fit().transform(new CircleTransformation()).into(editImg);
        editName.setText(account.getDisplayName());
        editAddress.setText(userInfo.getString("ADDRESS"));
        editContactNo.setText(userInfo.getString("CONTACT"));
    }
}
