package com.mazefernandez.uplbtrade.retrofitAPI;

import com.mazefernandez.uplbtrade.models.Customer;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://ec2-54-169-159-167.ap-southeast-1.compute.amazonaws.com:8000";
    private static UplbTradeApi service;
    private static RetrofitClient retrofitClient;

    private RetrofitClient() {
        Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(UplbTradeApi.class);
    }

    public static RetrofitClient getRetrofitClient() {
        if (retrofitClient == null) {
            retrofitClient = new RetrofitClient();
        }

        return retrofitClient;
    }

    public void getCustomers(Callback<List<Customer>> callback) {
        Call<List<Customer>> getCustomersCall = service.getCustomers();
        getCustomersCall.enqueue(callback);
    };
}
