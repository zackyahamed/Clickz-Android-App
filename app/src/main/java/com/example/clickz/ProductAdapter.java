package com.example.clickz;

import static android.content.Context.MODE_PRIVATE;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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

import model.Product;
import model.SQLiteHelper;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;


    public ProductAdapter(@NonNull Context context, List<Product> productList) {

        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_product,parent,false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        String ngrokUrl = context.getString(R.string.ngrok);
        Glide.with(context)
                .load(ngrokUrl+product.getImage1())
                .error(R.drawable.clickz_logo)
                .into(holder.productImage);
        holder.productTitle.setText(product.getTitle());
        holder.productQty.setText(String.valueOf(product.getQty()));
        holder.productPrice.setText(String.valueOf(product.getPrice()));
        holder.addtowishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductToWishlist(product.getProductId(),product.getTitle(),product.getPrice(),product.getQty(),product.getImage1());
                Toast.makeText(context, "Added to Wishlist", Toast.LENGTH_SHORT).show();

            }
        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SingleProdutView.class);
            intent.putExtra("product", productList.get(position));
            context.startActivity(intent);        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        ImageView productImage;
        TextView productTitle;
        TextView productQty;
        TextView productPrice;
        Button addtowishList;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.imageView8);
            productTitle = itemView.findViewById(R.id.textView16);
            productQty = itemView.findViewById(R.id.textView17);
            productPrice = itemView.findViewById(R.id.textView18);
            addtowishList=itemView.findViewById(R.id.button6);

        }
    }
    public void addProductToWishlist(String productId, String productName, double productPrice, int productQty,String productImageUrl) {


        SQLiteHelper dbHelper = new SQLiteHelper(context, "Clickz.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_id", productId);
        values.put("product_name", productName);
        values.put("product_price", productPrice);
        values.put("product_qty", productQty);
        values.put("product_image_url", productImageUrl);

        db.insert("wishlist", null, values);
        db.close();
    }

}
