package com.example.clickz;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.util.List;

import model.WishList;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.wishListViewHolder> {

    private List<WishList> wishListItems;

    private Context context;





    public WishListAdapter(@NonNull Context context, List<WishList> wishListItems) {
        this.wishListItems = wishListItems;
        this.context = context;

    }

    @NonNull
    @Override
    public WishListAdapter.wishListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wish_list_item,parent,false);
        return new WishListAdapter.wishListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishListAdapter.wishListViewHolder holder, int position) {
        String ngrokUrl = context.getString(R.string.ngrok);

        WishList wishList = wishListItems.get(position);

        holder.productTitle.setText(wishList.getTitle());
        holder.productQty.setText("In Stock "+wishList.getQty());

        holder.productPrice.setText(String.valueOf(wishList.getPrice()));
        Glide.with(holder.itemView.getContext())
                .load(ngrokUrl+wishList.getImage1())
                .into(holder.productImage);




    }

    @Override
    public int getItemCount() {
        return wishListItems.size();
    }

    public void removeItem(int position) {
        wishListItems.remove(position);
        notifyItemRemoved(position);
    }
    public static class wishListViewHolder extends RecyclerView.ViewHolder{
        ImageView productImage;
        TextView productTitle;

        TextView productQty;
        TextView productPrice;



        public wishListViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage=itemView.findViewById(R.id.imageView23);
            productTitle = itemView.findViewById(R.id.textView63);
            productQty = itemView.findViewById(R.id.textView64);
            productPrice =itemView.findViewById(R.id.textView65);

        }
    }
}
