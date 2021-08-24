package com.mazefernandez.uplbtrade.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.activities.TransactionActivity;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.Transaction;

import java.util.ArrayList;

/* Binds values of transaction information to views */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>{

    private final ArrayList<Transaction> transactionList;

    public TransactionAdapter(ArrayList<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder transactionViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    /* Holds the values for individual views on the recycler */
    public static class TransactionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final Context context;
        private Item item;

        /* View attributes */
        TransactionViewHolder(View view) {
            super(view);
            ImageView itemImg = view.findViewById(R.id.item_img);
            TextView itemName = view.findViewById(R.id.item_name);
            TextView itemPrice = view.findViewById(R.id.offer);
            LinearLayout card = view.findViewById(R.id.card);
            context = view.getContext();
            card.setOnClickListener(this);
        }

        /* Redirects to a transaction page with the details */
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, TransactionActivity.class);
            intent.putExtra("TRANSACTION", item);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }
}
