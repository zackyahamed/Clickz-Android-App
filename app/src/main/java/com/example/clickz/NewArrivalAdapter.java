package com.example.clickz;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import model.Product;

public class NewArrivalAdapter extends RecyclerView.Adapter<NewArrivalAdapter.NewArrivalViewHolder> {

    private List<Product> newArrivalList ;
    private Context context;


    public NewArrivalAdapter(@NonNull Context context, List<Product> newArrivalList) {

        this.newArrivalList = newArrivalList;
        this.context = context;
    }

    @NonNull
    @Override
    public NewArrivalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.new_arrival_card, parent, false);
        return new NewArrivalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewArrivalViewHolder holder, int position) {
        String ngrokUrl = context.getString(R.string.ngrok);
        Product product = newArrivalList.get(position);
//        holder.productImage.setImageResource(product.getImageres());
        holder.productTitle.setText(product.getBrand()+" "+product.getModel());
        holder.productPrice.setText(String.valueOf(product.getPrice()));

        Glide.with(context)
                .load(ngrokUrl+product.getImage1())
                .error(R.drawable.clickz_logo)
                .into(holder.productImage);


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SingleProdutView.class);
            intent.putExtra("product", newArrivalList.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return newArrivalList.size();
    }

    public static class NewArrivalViewHolder extends RecyclerView.ViewHolder{

        ImageView productImage;
        TextView productTitle;
        TextView productPrice;

        public NewArrivalViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.imageView5);
            productTitle = itemView.findViewById(R.id.textView11);
            productPrice = itemView.findViewById(R.id.textView12);

        }
    }
}
