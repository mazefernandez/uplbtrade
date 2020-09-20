package com.mazefernandez.uplbtrade.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.activities.OfferActivity;
import com.mazefernandez.uplbtrade.models.Item;
import com.mazefernandez.uplbtrade.models.Offer;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.BitmapFactory.decodeByteArray;

/* Binds values of offer information to views */
public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ItemViewHolder> {

    private ArrayList<Offer> offerList;

    public OfferAdapter(ArrayList<Offer> offerList) {
        this.offerList = offerList;
    }

    private Bitmap stringToBitMap(String encodedString){
        try{
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            return decodeByteArray(encodeByte, 0, encodeByte.length);
        }catch(Exception e){
            e.getMessage();
            return null;
        }
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
        getItem(offerList.get(position).getitemId(), holder);
        holder.offer = offerList.get(position);
        @SuppressLint("DefaultLocale") String price = String.format("%.2f",offerList.get(position).getPrice());
        price = "\u20B1" + price;
        holder.offerPrice.setText(price);
        holder.offerStatus.setText(offerList.get(position).getStatus());
    }
    private void getItem(int itemId, final ItemViewHolder holder){
        UPLBTrade.retrofitClient.getItem(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                Item item = response.body();
                assert item != null;
                holder.offerName.setText(item.getItemName());
                if (item.getImage() == null) {
                    holder.offerImg.setImageResource(R.drawable.placeholder);
                }
                else {
                    holder.offerImg.setImageBitmap(stringToBitMap(item.getImage()));
                }
                System.out.println("Retrieved item from offer");
            }
            @Override
            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, itemId);
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView offerImg;
        TextView offerName, offerPrice, offerStatus, originalPrice;
        LinearLayout card;
        private final Context context;
        Offer offer;

        ItemViewHolder(View view) {
            super(view);
            offerImg = view.findViewById(R.id.offer_img);
            offerName = view.findViewById(R.id.offer_name);
            offerPrice = view.findViewById(R.id.offer_price);
            offerStatus = view.findViewById(R.id.offer_status);
            context = view.getContext();
            card = view.findViewById(R.id.card);
            card.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, OfferActivity.class);
            intent.putExtra("OFFER", offer);
            context.startActivity(intent);
        }
    }
}