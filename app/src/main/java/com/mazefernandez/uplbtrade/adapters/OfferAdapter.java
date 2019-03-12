package com.mazefernandez.uplbtrade.adapters;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.Offer;

import java.util.ArrayList;

/* Binds values of item information to views */
public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ItemViewHolder> {

    private ArrayList<Offer> offerList;

    public OfferAdapter(ArrayList<Offer> offerList) {
        this.offerList = offerList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.offer_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.offerImg.setImageResource(R.drawable.placeholder);
        /* get item name from REST API */
        holder.offerName.setText("Math 17 book");
        //holder.offerName.setText(offerList.get(position).getOfferId());
        @SuppressLint("DefaultLocale") String price = String.format("%.2f",offerList.get(position).getPrice());
        holder.offerPrice.setText(price);
        holder.offer = offerList.get(position);
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView offerImg;
        TextView offerName, offerPrice;
        LinearLayout card;
        Offer offer;

        ItemViewHolder(View view) {
            super(view);
            offerImg = view.findViewById(R.id.offer_img);
            offerName = view.findViewById(R.id.offer_name);
            offerPrice = view.findViewById(R.id.offer_price);
            card = view.findViewById(R.id.card);
        }

        @Override
        public void onClick(View v) {

        }
    }
}