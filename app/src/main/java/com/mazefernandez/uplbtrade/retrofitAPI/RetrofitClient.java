package com.mazefernandez.uplbtrade.retrofitAPI;

import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.Offer;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* Handles Retrofit Calls */

public class RetrofitClient {
    private static final String BASE_URL = "http://ec2-18-141-138-6.ap-southeast-1.compute.amazonaws.com:8000";
    private static UPLBTradeApi service;
    private static RetrofitClient retrofitClient;

    private RetrofitClient() {
        Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(UPLBTradeApi.class);
    }

    public static RetrofitClient getRetrofitClient() {
        if (retrofitClient == null) {
            retrofitClient = new RetrofitClient();
        }
        return retrofitClient;
    }

    /* Customer Calls */
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
    public void searchCustomerItems(Callback<List<Item>> callback, String email) {
        Call<List<Item>> searchCustomerItemsCall = service.searchCustomerItems(email);
        searchCustomerItemsCall.enqueue(callback);
    }

    /* Item Calls */
    public void getItems(Callback<List<Item>> callback) {
        Call<List<Item>> getItemsCall = service.getItems();
        getItemsCall.enqueue(callback);
    }
    public void getItem(Callback<Item> callback, int item_id) {
        Call<Item> getItemCall = service.getItem(item_id);
        getItemCall.enqueue(callback);
    }
    public void addItem(
            Callback<Item> callback,
            RequestBody name,
            RequestBody description,
            RequestBody price,
            MultipartBody.Part image,
            RequestBody condition,
            RequestBody customer_id) {
        Call<Item> addItemCall = service.addItem(name, description, price, image, condition, customer_id);
        addItemCall.enqueue(callback);
    }

    public void updateItem(Callback<Item> callback, Item item, int item_id) {
        Call<Item> updateItemCall = service.updateItem(item, item_id);
        updateItemCall.enqueue(callback);
    }

    public void deleteItem(Callback<Item> callback, int item_id) {
        Call<Item> deleteItemCall = service.deleteItem(item_id);
        deleteItemCall.enqueue(callback);
    }

    /* Offer Calls */
    public void getOffers(Callback<List<Offer>> callback) {
        Call<List<Offer>> getOffersCall = service.getOffers();
        getOffersCall.enqueue(callback);
    }
    public void getOffer(Callback<Offer> callback, int offer_id) {
        Call<Offer> getOfferCall = service.getOffer(offer_id);
        getOfferCall.enqueue(callback);
    }
    public void addOffer(Callback<Offer> callback, Offer offer) {
        Call<Offer> addOfferCall = service.addOffer(offer);
        addOfferCall.enqueue(callback);
    }
    public void deleteOffer(Callback<Offer> callback, int offer_id) {
        Call<Offer> deleteOfferCall = service.deleteOffer(offer_id);
        deleteOfferCall.enqueue(callback);
    }
    public void getOfferBuying(Callback<List<Offer>> callback, int buyer_id) {
        Call<List<Offer>> getOfferBuyingCall = service.getOfferBuying(buyer_id);
        getOfferBuyingCall.enqueue(callback);
    }
    public void getOfferSelling(Callback<List<Offer>> callback, int seller_id) {
        Call<List<Offer>> getOfferSellingCall = service.getOfferSelling(seller_id);
        getOfferSellingCall.enqueue(callback);
    }
    public void decline(Callback<Offer> callback, int offer_id) {
        Call<Offer> declineCall = service.decline(offer_id);
        declineCall.enqueue(callback);
    }
    public void accept(Callback<Offer> callback, int offer_id) {
        Call<Offer> acceptCall = service.accept(offer_id);
        acceptCall.enqueue(callback);
    }
}

