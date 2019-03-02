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

import java.util.ArrayList;

// binds values of item information to views
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private ArrayList<Item> dataList;

    public ItemAdapter(ArrayList<Item> dataList) {
        this.dataList = dataList;
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
        holder.itemImg.setImageResource(R.drawable.placeholder);
        // format price into decimal
        holder.itemName.setText(dataList.get(position).getItemName());
        @SuppressLint("DefaultLocale") String price = String.format("%.2f",dataList.get(position).getPrice());
        holder.itemPrice.setText(price);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImg;
        TextView itemName, itemPrice;

        ItemViewHolder(View view) {
            super(view);
            itemImg = view.findViewById(R.id.item_img);
            itemName = view.findViewById(R.id.item_name);
            itemPrice = view.findViewById(R.id.item_price);
        }
    }
}