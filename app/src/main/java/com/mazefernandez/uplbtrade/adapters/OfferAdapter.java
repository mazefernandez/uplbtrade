package com.mazefernandez.uplbtrade.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
    private String imgString;
    private final ArrayList<Offer> offerList;
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
        /* retrieve offer data */
        getItem(offerList.get(position).getItemId(), holder);
        holder.offer = offerList.get(position);
        @SuppressLint("DefaultLocale") String price = String.format("%.2f",offerList.get(position).getPrice());
        price = "\u20B1" + price;
        holder.offerPrice.setText(price);
        holder.offerStatus.setText(offerList.get(position).getStatus());
    }
    /* retrieve item data from the offer */
    private void getItem(int itemId, final ItemViewHolder holder){
        UPLBTrade.retrofitClient.getItem(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                Item item = response.body();
                assert item != null;
                holder.offerName.setText(item.getItemName());
                imgString = item.getImage();

                /* Firebase instances */
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference();

                /* retrieve image from firebase */
                StorageReference ref = storageReference.child("images/"+imgString);
                final long ONE_MEGABYTE = 1024 * 1024 * 5;
                ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    System.out.println("Successfully read image");
                    holder.offerImg.setImageBitmap(bitmap);
                }).addOnFailureListener(fail -> System.out.println("Failed to read image" + fail));

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

    /* Holds the values for individual views on the recycler */
    static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView offerImg;
        TextView offerName, offerPrice, offerStatus, originalPrice;
        LinearLayout card;
        private final Context context;
        Offer offer;

        /* View attributes */
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

        /* Redirects to an offer page with the details */
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, OfferActivity.class);
            intent.putExtra("OFFER", offer);
            context.startActivity(intent);
        }
    }
}