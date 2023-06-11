package com.mazefernandez.uplbtrade.retrofitAPI;

import com.mazefernandez.uplbtrade.models.ApplicationReview;
import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.CustomerReport;
import com.mazefernandez.uplbtrade.models.CustomerReview;
import com.mazefernandez.uplbtrade.models.Id;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.ItemReport;
import com.mazefernandez.uplbtrade.models.Offer;
import com.mazefernandez.uplbtrade.models.Tag;
import com.mazefernandez.uplbtrade.models.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* Handles Retrofit Calls */

public class RetrofitClient {
    private static final String BASE_URL = "https://uplbtrade.com/";
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

    /* Application Review Calls */
    public void getAppReviews(Callback<List<ApplicationReview>> callback) {
        Call<List<ApplicationReview>> getAppReviewsCall = service.getAppReviews();
        getAppReviewsCall.enqueue(callback);
    }

    public void getAppReview(Callback<ApplicationReview> callback) {
        Call<ApplicationReview> getAppReviewCall = service.getAppReview();
        getAppReviewCall.enqueue(callback);
    }

    public void addAppReview(Callback<ApplicationReview> callback, ApplicationReview appReview) {
        Call<ApplicationReview> addAppReviewCall = service.addAppReview(appReview);
        addAppReviewCall.enqueue(callback);
    }

    public void deleteAppReview(Callback<ApplicationReview> callback, int appReview_id) {
        Call<ApplicationReview> deleteAppReviewCall = service.deleteAppReview(appReview_id);
        deleteAppReviewCall.enqueue(callback);
    }

    /* Customer Review Calls */
    public void getCustomerReviews(Callback<List<CustomerReview>> callback) {
        Call<List<CustomerReview>> getCustomerReviewsCall = service.getCustomerReviews();
        getCustomerReviewsCall.enqueue(callback);
    }

    public void getCustomerReview(Callback<CustomerReview> callback) {
        Call<CustomerReview> getCustomerReviewCall = service.getCustomerReview();
        getCustomerReviewCall.enqueue(callback);
    }

    public void addCustomerReview(Callback<CustomerReview> callback, CustomerReview customerReview) {
        Call<CustomerReview> addCustomerReviewCall = service.addCustomerReview(customerReview);
        addCustomerReviewCall.enqueue(callback);
    }

    public void getSpecificCustomerReviews(Callback<List<CustomerReview>> callback) {
        Call<List<CustomerReview>> getSpecificCustomerReviewsCall = service.getSpecificCustomerReviews();
        getSpecificCustomerReviewsCall.enqueue(callback);
    }

    public void getCustomerRating(Callback<Double> callback) {
        Call<Double> getCustomerRatingCall = service.getCustomerRating();
        getCustomerRatingCall.enqueue(callback);
    }

    public void deleteCustomerReview(Callback<CustomerReview> callback, int customerReview_id) {
        Call<CustomerReview> deleteCustomerReviewCall = service.deleteCustomerReview(customerReview_id);
        deleteCustomerReviewCall.enqueue(callback);
    }

    /* Customer Report Calls */

    public void addCustomerReport(Callback<CustomerReport> callback, CustomerReport customerReport) {
        Call<CustomerReport> addCustomerReportCall = service.addCustomerReport(customerReport);
        addCustomerReportCall.enqueue(callback);
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
    public void searchTagItems(Callback<List<Id>> callback, List<String> tags) {
        Call<List<Id>> getTagItemsCall = service.searchTagItems(tags);
        getTagItemsCall.enqueue(callback);
    }
    public void getItemsByIds(Callback<List<Item>> callback, List<Integer> ids) {
        Call<List<Item>> getItemsByIdsCall = service.getItemsByIds(ids);
        getItemsByIdsCall.enqueue(callback);
    }
    public void getItem(Callback<Item> callback, int item_id) {
        Call<Item> getItemCall = service.getItem(item_id);
        getItemCall.enqueue(callback);
    }

    public void getItemByImg(Callback<Item> callback, String image) {
        Call<Item> getItemByImgCall = service.getItemByImg(image);
        getItemByImgCall.enqueue(callback);
    }

    public void addItem(Callback<Item> callback, Item item) {
        Call<Item> addItemCall = service.addItem(item);
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

    /* Item Report Calls */
    public void addItemReport(Callback<ItemReport> callback, ItemReport itemReport) {
        Call<ItemReport> addItemReportCall = service.addItemReport(itemReport);
        addItemReportCall.enqueue(callback);
    }

    /* Tag Calls */
    public void addTags(Callback<List<Tag>> callback, List<Tag> tags) {
        Call<List<Tag>> addTagsCall = service.addTags(tags);
        addTagsCall.enqueue(callback);
    }

    public void getTagsFromItem(Callback<List<Tag>> callback, int item_id) {
        Call<List<Tag>> getTagsFromItemCall = service.getTagsFromItem(item_id);
        getTagsFromItemCall.enqueue(callback);
    }

    public void deleteTag(Callback<Tag> callback, int tag_id) {
        Call<Tag> deleteTagCall = service.deleteTag(tag_id);
        deleteTagCall.enqueue(callback);
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

    /* Transaction Calls */

    public void getTransactions(Callback<List<Transaction>> callback) {
        Call<List<Transaction>> getTransactionsCall = service.getTransactions();
        getTransactionsCall.enqueue(callback);
    }
    public void getTransaction(Callback<Transaction> callback, int transaction_id) {
        Call<Transaction> getTransactionCall = service.getTransaction(transaction_id);
        getTransactionCall.enqueue(callback);
    }
    public void getBuyerTransactions(Callback<List<Transaction>> callback, int buyer_id) {
        Call<List<Transaction>> getBuyerTransactionsCall = service.getBuyerTransactions(buyer_id);
        getBuyerTransactionsCall.enqueue(callback);
    }
    public void getSellerTransactions(Callback<List<Transaction>> callback, int seller_id) {
        Call<List<Transaction>> getSellerTransactionsCall = service.getSellerTransactions(seller_id);
        getSellerTransactionsCall.enqueue(callback);
    }
    public void addTransaction(Callback<Transaction> callback, Transaction transaction) {
        Call<Transaction> addTransactionCall = service.addTransaction(transaction);
        addTransactionCall.enqueue(callback);
    }
}

