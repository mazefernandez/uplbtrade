package com.mazefernandez.uplbtrade.retrofitAPI;

import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.Item;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UplbTradeApi {
    @GET("/api/customers")
    Call<List<Customer>> getCustomers();

    @GET("/api/customers")
    Call<Customer> getCustomer(@Query("customer_id") int customerId);

    @POST("/api/customers")
    Call<Customer> addCustomer(@Body Customer customer);

    @PUT("/api/customers")
    Call<Customer> updateCustomer(@Body Customer customer);

    @GET("/api/customers/{customer_id}/items")
    Call<List<Item>> getItems(@Path("customer_id") int customerId);

}

