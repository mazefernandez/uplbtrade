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

    @GET("/api/customers/{customer_id}")
    Call<Customer> getCustomer(@Path("customer_id") int customerId);

    @GET("/api/customers/email/{email}")
    Call<Customer> getCustomerByEmail(@Path("email") String email);

    @POST("/api/customers")
    Call<Customer> addCustomer(@Body Customer customer);

    @PUT("/api/customers/{customer_id}")
    Call<Customer> updateCustomer(@Body Customer customer, @Path("customer_id") int customerId);

    @GET("/api/customers/{customer_id}/items")
    Call<List<Item>> getCustomerItems(@Path("customer_id") int customerId);

    @GET("/api/customers/search/{email}")
    Call<List<Item>> searchCustomerItems(@Path("email") String email);

    /* Item calls */
    @GET("/api/items")
    Call<List<Item>> getItems();

    @GET("/api/items/{item_id}")
    Call<Item> getItem(@Path("item_id") int itemId);

    @POST("/api/items")
    Call<Item> addItem(@Body Item item);

    @PUT("/api/items/{item_id}")
    Call<Item> updateItem(@Body Item item, @Path("item_id") int itemId);

    @DELETE("/api/items/{item_id}")
    Call<Item> deleteItem(@Path("item_id") int itemId);
}

