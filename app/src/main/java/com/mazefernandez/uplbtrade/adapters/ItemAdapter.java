package com.mazefernandez.uplbtrade.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.activities.ItemActivity;
import com.mazefernandez.uplbtrade.models.Item;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.BitmapFactory.decodeByteArray;

/* Binds values of item information to views */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> implements Filterable {
    private List<Item> itemList;
    private List<Item> itemListFiltered;

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
        this.itemListFiltered = itemList;
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
        View view = layoutInflater.inflate(R.layout.item_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        if(itemListFiltered.get(position).getImage() == null) {
            holder.itemImg.setImageResource(R.drawable.placeholder);
        }
        else {
            holder.itemImg.setImageBitmap(stringToBitMap(itemListFiltered.get(position).getImage()));
//            System.out.println(itemListFiltered.get(position).getImage());
        }
        holder.itemName.setText(itemListFiltered.get(position).getItemName());
        /* Format price into decimal */
        @SuppressLint("DefaultLocale") String price = String.format("%.2f",itemListFiltered.get(position).getPrice());
        price = "\u20B1" + price;
        holder.itemPrice.setText(price);
        holder.item = itemListFiltered.get(position);
    }

    @Override
    public int getItemCount() {
        return itemListFiltered.size();
    }

    /* Search function for item list */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    itemListFiltered = itemList;
                } else {
                    List<Item> filteredList = new ArrayList<>();
                    for (Item item : itemList) {
                        if (item.getItemName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(item);
                        }
                    }
                    itemListFiltered = filteredList;
                }
                FilterResults results = new FilterResults();
                results.values = itemListFiltered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                itemListFiltered = (List<Item>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView itemImg;
        private TextView itemName, itemPrice;
        private final Context context;
        private Item item;

        ItemViewHolder(View view) {
            super(view);
            itemImg = view.findViewById(R.id.item_img);
            itemName = view.findViewById(R.id.item_name);
            itemPrice = view.findViewById(R.id.offer);
            LinearLayout card = view.findViewById(R.id.card);
            context = view.getContext();
            card.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ItemActivity.class);
            intent.putExtra("ITEM", item);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }
}