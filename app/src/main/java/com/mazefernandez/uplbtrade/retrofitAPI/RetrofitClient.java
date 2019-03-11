package com.mazefernandez.uplbtrade.retrofitAPI;

import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.Item;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;

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
    }

    public void getCustomer(Callback<Customer> callback, int customer_id) {
        Call<Customer> getCustomerCall = service.getCustomer(customer_id);
        getCustomerCall.enqueue(callback);
    }

    public void getCustomerByEmail(Callback<Customer> callback, String email) {
        Call<Customer> getCustomerByEmailCall = service.getCustomerByEmail(email);
        getCustomerByEmailCall.enqueue(callback);
    }

    public void addCustomer(Callback<Customer> callback, Customer customer) {
        Call<Customer> addCustomerCall = service.addCustomer(customer);
        addCustomerCall.enqueue(callback);
    }

    public void updateCustomer(Callback<Customer> callback, Customer customer, int customer_id) {
        Call<Customer> updateCustomerCall = service.updateCustomer(customer,customer_id);
        updateCustomerCall.enqueue(callback);
    }

    public void getCustomerItems(Callback<List<Item>> callback, int customer_id) {
        Call<List<Item>> getCustomerItemsCall = service.getCustomerItems(customer_id);
        getCustomerItemsCall.enqueue(callback);
    }

    public void getItems(Callback<List<Item>> callback) {
        Call<List<Item>> getItemsCall = service.getItems();
        getItemsCall.enqueue(callback);
    }

    public void getItem(Callback<Item> callback, int item_id) {
        Call<Item> getItemCall = service.getItem(item_id);
        getItemCall.enqueue(callback);
    }

    public void addItem(Callback<Item> callback, Item item) {
        Call<Item> addItemCall = service.addItem(item);
        addItemCall.enqueue(callback);
    }

    public void updateItem(Callback<Item> callback, Item item) {
        Call<Item> updateItemCall = service.updateItem(item);
        updateItemCall.enqueue(callback);
    }

    public void deleteItem(Callback<Item> callback, int item_id) {
        Call<Item> deleteItemCall = service.deleteItem(item_id);
        deleteItemCall.enqueue(callback);
    }
}
