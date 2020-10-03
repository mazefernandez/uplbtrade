package com.mazefernandez.uplbtrade;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.mazefernandez.uplbtrade.activities.LoginActivity;
import com.mazefernandez.uplbtrade.retrofitAPI.RetrofitClient;

public class UPLBTrade extends Application {
    public Context context;
    public static RetrofitClient retrofitClient;

    @Override
    public void onCreate() {
        super.onCreate();
        retrofitClient = RetrofitClient.getRetrofitClient();

        context = getApplicationContext();

        // Initialize the Chat SDK
//        try {
//
//            // config for ChatSDK settings
//            Configuration.Builder config = new Configuration.Builder(context);
//            config.firebaseRootPath("prod");
//
//            // Start ChatSDK
//            ChatSDK.initialize(config.build(), new FirebaseNetworkAdapter(), new BaseInterfaceAdapter(context));
//        }
//        catch (ChatSDKException e) {
//            e.printStackTrace();
//        }

        // File storage for images
       // FirebaseFileStorageModule.activate();

         // Uncomment this to enable Firebase UI
//        FirebaseUIModule.activate(EmailAuthProvider.PROVIDER_ID, PhoneAuthProvider.PROVIDER_ID);
//        InterfaceManager.shared().a.startLoginActivity(context, true);

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
