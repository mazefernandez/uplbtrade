package com.mazefernandez.uplbtrade.retrofitAPI;

import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.Item;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UplbTradeApi {
    /* Customer calls */
    @GET("/api/customers")
    Call<List<Customer>> getCustomers();
    @GET("/api/customers")
    Call<Customer> getCustomer(@Query("customer_id") int customerId);
    @GET("/api/customers/search")
    Call<Customer> getCustomerByEmail(@Query("email") String email);
    @POST("/api/customers")
    Call<Customer> addCustomer(@Body Customer customer);
    @PUT("/api/customers")
    Call<Customer> updateCustomer(@Body Customer customer);

    @GET("/api/customers/{customer_id}/items")
    Call<List<Item>> getCustomerItems(@Path("customer_id") int customerId);

    /* Item calls */
    @GET("/api/items")
    Call<List<Item>> getItems();
    @GET("/api/items")
    Call<Item> getItem(@Query("item_id") int itemId);
    @POST("/api/items")
    Call<Item> addItem(@Body Item item);
    @PUT("/api/items")
    Call<Item> updateItem(@Body Item item);
    @DELETE("/api/items")
    Call<Item> deleteItem(@Query("item_id") int itemId);
}

