package com.mazefernandez.uplbtrade.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.fragments.ConfirmMeetUpFragment;
import com.mazefernandez.uplbtrade.fragments.DatetimeFragment;
import com.mazefernandez.uplbtrade.fragments.MapFragment;

public class MeetUpActivity extends AppCompatActivity {
    private DatetimeFragment dtFragment = new DatetimeFragment();
    private MapFragment mapFragment = new MapFragment();
    private ConfirmMeetUpFragment confirmFragment = new ConfirmMeetUpFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetup);

        /* views */
        Button datetime = findViewById(R.id.date);
        Button venue = findViewById(R.id.venue);
        Button confirm = findViewById(R.id.confirm);

        /* Get offer data */
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("info");

        dtFragment.setArguments(bundle);

        showFragment(dtFragment, false, "datetime");

        datetime.setOnClickListener(v -> showFragment(dtFragment, false, "datetime"));

        venue.setOnClickListener(v -> showFragment(mapFragment, false, "map"));

        confirm.setOnClickListener(v -> {
            /* Get offer data */
            Intent intent1 = getIntent();
            Bundle bundle1 = intent1.getBundleExtra("info");

            String dt = dtFragment.getDateTime();
            String[] datetime1 = dt.split(" ");
            String date = datetime1[0];
            String time = datetime1[1];
            String venue1 = mapFragment.getVenue();
            Bundle confirmBundle = new Bundle();
            confirmBundle.putBundle("offer", bundle1);
            confirmBundle.putString("date", date);
            confirmBundle.putString("time", time);
            confirmBundle.putString("venue", venue1);
            confirmFragment.setArguments(confirmBundle);
            showFragment(confirmFragment, false, "confirm");
        });
    }
    public void showFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.replace(R.id.fragment_frame, fragment, tag);
        transaction.commit();
    }
}

