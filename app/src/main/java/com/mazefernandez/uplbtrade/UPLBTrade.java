package com.mazefernandez.uplbtrade;

import static com.mazefernandez.uplbtrade.adapters.GoogleAccountAdapter.TAG;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mazefernandez.uplbtrade.activities.LoginActivity;
import com.mazefernandez.uplbtrade.retrofitAPI.RetrofitClient;

public class UPLBTrade extends Application {
    public Context context;
    public static RetrofitClient retrofitClient;

    @Override
    public void onCreate() {
        super.onCreate();

        /* initialize retrofit client */
        retrofitClient = RetrofitClient.getRetrofitClient();
        context = getApplicationContext();

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        /* start the application and login */
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
