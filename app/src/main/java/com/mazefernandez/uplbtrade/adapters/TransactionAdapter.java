package com.mazefernandez.uplbtrade.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.activities.TransactionActivity;
import com.mazefernandez.uplbtrade.models.Customer;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.Offer;
import com.mazefernandez.uplbtrade.models.Transaction;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* Binds values of transaction information to views */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>{

    private final ArrayList<Transaction> transactionList;

    public TransactionAdapter(ArrayList<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.transaction_row, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        /* Insert transaction details */
        String date = transactionList.get(position).getDate().toString();
        holder.transaction = transactionList.get(position);
        holder.date.setText(date);
        holder.time.setText(transactionList.get(position).getTime());
        holder.venue.setText(transactionList.get(position).getVenue());
        getItem(transactionList.get(position).getItemId(), holder);
        getOffer(transactionList.get(position).getOfferId(), holder);
        getCustomer(transactionList.get(position).getBuyerId(), holder, true);
        getCustomer(transactionList.get(position).getSellerId(), holder, false);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    /* retrieve item data */
    private void getItem(int itemId, final TransactionViewHolder holder){
        UPLBTrade.retrofitClient.getItem(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                Item item = response.body();
                assert item != null;
                holder.itemName.setText(item.getItemName());
                System.out.println("Retrieved item");
            }
            @Override
            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, itemId);
    }

    /* retrieve offer data */
    private void getOffer(int offerId, final TransactionViewHolder holder){
        UPLBTrade.retrofitClient.getOffer(new Callback<Offer>() {
            @Override
            public void onResponse(@NonNull Call<Offer> call, @NonNull Response<Offer> response) {
                Offer offer = response.body();
                assert offer != null;
                String price = "\u20B1" + offer.getPrice();
                holder.offer.setText(price);
                System.out.println("Retrieved offer");
            }
            @Override
            public void onFailure(@NonNull Call<Offer> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, offerId);
    }

    /* retrieve buyer data */
    private void getCustomer(int customerId, final TransactionViewHolder holder, Boolean buyer){
        UPLBTrade.retrofitClient.getCustomer(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                Customer customer = response.body();
                assert customer != null;
                String customerName = customer.getFirstName() + " " + customer.getLastName();

                if (buyer) { holder.buyer.setText(customerName); }
                else { holder.seller.setText(customerName); }

                System.out.println("Retrieved offer");
            }
            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, customerId);
    }

    /* Holds the values for individual views on the recycler */
    public static class TransactionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final Context context;
        private final TextView date;
        private final TextView time;
        private final TextView venue;
        private final TextView itemName;
        private final TextView offer;
        private final TextView buyer;
        private final TextView seller;
        private Transaction transaction;

        /* View attributes */
        TransactionViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            time = view.findViewById(R.id.time);
            venue = view.findViewById(R.id.venue);
            itemName = view.findViewById(R.id.item_name);
            offer = view.findViewById(R.id.offer);
            buyer = view.findViewById(R.id.buyer);
            seller = view.findViewById(R.id.seller);
            LinearLayout card = view.findViewById(R.id.card);
            context = view.getContext();
            card.setOnClickListener(this);
        }

        /* Redirects to a transaction page with the details */
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, TransactionActivity.class);
            intent.putExtra("TRANSACTION", transaction);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }
}
