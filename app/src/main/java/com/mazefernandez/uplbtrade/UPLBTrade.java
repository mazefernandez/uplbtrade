package com.mazefernandez.uplbtrade;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.mazefernandez.uplbtrade.activities.LoginActivity;
import com.mazefernandez.uplbtrade.retrofitAPI.RetrofitClient;

public class UPLBTrade extends Application {
    public static Context context;
    public static RetrofitClient retrofitClient;

    @Override
    public void onCreate() {
        super.onCreate();
        retrofitClient = RetrofitClient.getRetrofitClient();

        context = getApplicationContext();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
