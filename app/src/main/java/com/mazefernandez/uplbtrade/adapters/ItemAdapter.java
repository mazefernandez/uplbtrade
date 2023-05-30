package com.mazefernandez.uplbtrade.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.activities.ItemActivity;
import com.mazefernandez.uplbtrade.models.Item;

import java.util.ArrayList;

/* Binds values of item information to views */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> implements Filterable {
    private final ArrayList<Item> itemList;
    private ArrayList<Item> itemListFiltered;


    public ItemAdapter(ArrayList<Item> itemList) {
        this.itemList = itemList;
        this.itemListFiltered = itemList;
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
        /* Firebase instances */
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        /* puts default image if no image */
        if(itemListFiltered.get(position).getImage() == null) {
            holder.itemImg.setImageResource(R.drawable.placeholder);
        }
        else {
            /* retrieve image from firebase */
            StorageReference ref = storageReference.child("images/"+itemListFiltered.get(position).getImage()+"/0");

            final long ONE_MEGABYTE = 1024 * 1024 * 5;
            ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                System.out.println("Successfully read image");
                holder.itemImg.setImageBitmap(bitmap);
            }).addOnFailureListener(fail -> System.out.println("Failed to read image" + fail));
        }
        /* retrieve item data */
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
                    ArrayList<Item> filteredList = new ArrayList<>();
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

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                itemListFiltered = (ArrayList<Item>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    /* Holds the values for individual views on the recycler */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ImageView itemImg;
        private final TextView itemName;
        private final TextView itemPrice;
        private final Context context;
        private Item item;

        /* View attributes */
        ItemViewHolder(View view) {
            super(view);
            itemImg = view.findViewById(R.id.item_img);
            itemName = view.findViewById(R.id.item_name);
            itemPrice = view.findViewById(R.id.offer);
            LinearLayout card = view.findViewById(R.id.card);
            context = view.getContext();
            card.setOnClickListener(this);
        }

        /* Redirects to an item page with the details */
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ItemActivity.class);
            intent.putExtra("ITEM", item);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }
}