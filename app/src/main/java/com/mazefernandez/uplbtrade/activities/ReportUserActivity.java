package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.picasso.CircleTransformation;
import com.squareup.picasso.Picasso;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.GOOGLE_ACCOUNT;

/* Files a report regarding a user in the application */

public class ReportUserActivity extends AppCompatActivity {
    private ImageView profileImage;
    private TextView name, contactNo;
    private Bundle userInfo;
    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_user);

        /* Report User Views */
        profileImage = findViewById(R.id.profile_img);
        name = findViewById(R.id.profile_name);
        contactNo = findViewById(R.id.contactNo);

        EditText report = findViewById(R.id.report);
        Button send = findViewById(R.id.send);
        Button cancel = findViewById(R.id.cancel);

        Intent profileIntent = getIntent();

        /* Display Customer Details */
        userInfo = profileIntent.getExtras();
        assert userInfo != null;
        account = userInfo.getParcelable(GOOGLE_ACCOUNT);
        displayUser();

        /* Report Customer */

        /* Cancel and return to profile */
        cancel.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("CHECK", 0);
            setResult(RESULT_OK,intent);
            finish();
        });
    }
    /* display user account data */
    private void displayUser() {
        Picasso.get().load(account.getPhotoUrl()).centerInside().fit().transform(new CircleTransformation()).into(profileImage);
        name.setText(account.getDisplayName());
        contactNo.setText(userInfo.getString("CONTACT"));
    }
}
