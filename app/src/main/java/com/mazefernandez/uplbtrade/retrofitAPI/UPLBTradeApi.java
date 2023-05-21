package com.mazefernandez.uplbtrade.retrofitAPI;

import com.mazefernandez.uplbtrade.models.ApplicationReview;
import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.CustomerReport;
import com.mazefernandez.uplbtrade.models.CustomerReview;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.ItemReport;
import com.mazefernandez.uplbtrade.models.Offer;
import com.mazefernandez.uplbtrade.models.Tag;
import com.mazefernandez.uplbtrade.models.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UPLBTradeApi {
    /* AppReview calls */
    @GET("/api/application-reviews")
    Call<List<ApplicationReview>> getAppReviews();

    @GET("/api/application-reviews/{customer_id}")
    Call<ApplicationReview> getAppReview();

    @POST("/api/application-reviews/")
    Call<ApplicationReview> addAppReview(@Body ApplicationReview appReview);

    @DELETE("/api/application-reviews/{appReview_id}")
    Call<ApplicationReview> deleteAppReview(@Path("appReview_id") int appReview_id);

    /* CustomerReview calls */

    @GET("/api/customer-reviews")
    Call<List<CustomerReview>> getCustomerReviews();

    @GET("/api/customer-reviews/{id}")
    Call<CustomerReview> getCustomerReview();

    @POST("/api/customer-reviews/")
    Call<CustomerReview> addCustomerReview(@Body CustomerReview customerReview);

    @GET("/api/customer-reviews/customer/{id}")
    Call<List<CustomerReview>> getSpecificCustomerReviews();

    @GET("/api/customer-reviews/rating/{id}")
    Call<Double> getCustomerRating();

    @DELETE("/api/customer-reviews/{id}")
    Call<CustomerReview> deleteCustomerReview(@Path("customerReview_id") int customerReview_id);

    /* Customer Report calls */

    @POST("/api/customer-reports")
    Call<CustomerReport> addCustomerReport(@Body CustomerReport customerReport);

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

    @GET("/api/items/id/{img}")
    Call<Item> getItemByImg(@Path("img") String image);

    @POST("/api/items")
    Call<Item> addItem(@Body Item item);

    @PUT("/api/items/{item_id}")
    Call<Item> updateItem(@Body Item item, @Path("item_id") int itemId);

    @DELETE("/api/items/{item_id}")
    Call<Item> deleteItem(@Path("item_id") int itemId);

    /* Item Report Calls */
    @POST("/api/item-reports")
    Call<ItemReport> addItemReport(@Body ItemReport itemReport);

    /* Tag Calls */
    @POST("/api/tags")
    Call<List<Tag>> addTags(@Body List<Tag> tags);

    @GET("/api/tags/item/{item_id}")
    Call<List<Tag>> getTagsFromItem(@Path("item_id") int itemId);

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

    /* Transaction Calls */
    @GET("api/transactions/")
    Call<List<Transaction>> getTransactions();

    @GET("api/transactions/{transaction_id}")
    Call<Transaction> getTransaction(@Path("transaction_id") int transactionId);

    @GET("api/transactions/buyer/{buyer_id}")
    Call<List<Transaction>> getBuyerTransactions(@Path("buyer_id") int buyerId);

    @GET("api/transactions/seller/{seller_id}")
    Call<List<Transaction>> getSellerTransactions(@Path("seller_id") int sellerId);

    @POST("/api/transactions")
    Call<Transaction> addTransaction(@Body Transaction transaction);

}

