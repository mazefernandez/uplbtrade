package com.mazefernandez.uplbtrade.retrofitAPI;

import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.Offer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UPLBTradeApi {
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

    /* Offer Calls */
    @GET("/api/offers")
    Call<List<Offer>> getOffers();

    @GET("/api/offers/{offer_id}")
    Call<Offer> getOffer(@Path("offer_id") int offerId);

    @POST("/api/offers")
    Call<Offer> addOffer(@Body Offer offer);

    @DELETE("/api/offers/{offer_id}")
    Call<Offer> deleteOffer(@Path("offer_id") int offerId);

    @GET("/api/offers/buyer/{buyer_id}")
    Call<List<Offer>> getOfferBuying(@Path("buyer_id") int buyerId);

    @GET("/api/offers/seller/{seller_id}")
    Call<List<Offer>> getOfferSelling(@Path("seller_id") int sellerId);

    @PUT("/api/offers/decline/{offer_id}")
    Call<Offer> decline(@Path("offer_id") int offerId);

    @PUT("/api/offers/accept/{offer_id}")
    Call<Offer> accept(@Path("offer_id") int offerId);
}

