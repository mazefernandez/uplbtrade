package com.mazefernandez.uplbtrade.adapters;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        //convert blob to bitmap image
//        Blob itemBlob = dataList.get(position).getImage();
//        try {
//            int blobLength = (int) itemBlob.length();
//            byte[] itemByteArray = itemBlob.getBytes(1,blobLength);
//            Bitmap itemBitMap = BitmapFactory.decodeByteArray(itemByteArray, 0, itemByteArray.length);
//            holder.itemImg.setImageBitmap(itemBitMap);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        holder.offerImg.setImageResource(R.drawable.placeholder);
        /* Format price into decimal */
        holder.offerName.setText(offerList.get(position).getofferId());
        @SuppressLint("DefaultLocale") String price = String.format("%.2f",offerList.get(position).getPrice());
        holder.offerPrice.setText(price);
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView offerImg;
        TextView offerName, offerPrice;

        ItemViewHolder(View view) {
            super(view);
            offerImg = view.findViewById(R.id.item_img);
            offerName = view.findViewById(R.id.item_name);
            offerPrice = view.findViewById(R.id.item_price);
        }
    }
}