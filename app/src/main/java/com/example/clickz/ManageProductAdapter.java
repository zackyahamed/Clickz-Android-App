package com.example.clickz;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;

import model.Product;

public class ManageProductAdapter extends RecyclerView.Adapter<ManageProductAdapter.ManageProductViewHolder> {
    private List<Product> productList;
    private Context context;
    private Handler handler = new Handler();
    private Runnable runnable;

    public ManageProductAdapter(@NonNull Context context, List<Product> productList) {

        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ManageProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.manage_product_item,parent,false);
        return new ManageProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageProductViewHolder holder, int position) {
        Product product = productList.get(position);
        String ngrokUrl = context.getString(R.string.ngrok);
        Glide.with(context)
                .load(ngrokUrl + product.getImage1())
                .error(R.drawable.clickz_logo)
                .into(holder.productImage);
        holder.productTitle.setText(product.getTitle());
        holder.productQty.setText(String.valueOf(product.getQty()));
        holder.productPrice.setText(String.valueOf(product.getPrice()));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SingleProdutView.class);
            intent.putExtra("product", productList.get(position));
            context.startActivity(intent);
        });

        holder.productQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    int newQty = Integer.parseInt(editable.toString());

                    runnable = () -> updateProductQuantity(product.getProductId(), newQty);
                    handler.postDelayed(runnable, 3000);
                }
            }
        });
        holder.productPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {

                    Double newPrice = Double.parseDouble(editable.toString());


                    runnable = () -> updateProductPrice(product.getProductId(), newPrice);
                    handler.postDelayed(runnable, 3000);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ManageProductViewHolder extends RecyclerView.ViewHolder{
        ImageView productImage;
        TextView productTitle;
        TextView productQty;
        TextView productPrice;

        public ManageProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.imageView20);
            productTitle = itemView.findViewById(R.id.textView49);
            productQty = itemView.findViewById(R.id.editTextText16);
            productPrice = itemView.findViewById(R.id.editTextText15);

        }
    }
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public void updateProductQuantity(String productId, int newQty) {
        DocumentReference productRef = firestore.collection("products").document(productId);
Log.d("updateQty",productId);
        productRef.set(new HashMap<String, Object>() {{
                    put("qty", newQty);
                }}, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context,"Stock updated",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreUpdate", "Error updating stock", e));

    }

    public void updateProductPrice(String productId, Double newPrice) {
        DocumentReference productRef = firestore.collection("products").document(productId);

        productRef.set(new HashMap<String, Object>() {{
                    put("price", newPrice);
                }}, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context,"Price updated",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreUpdate", "Error updating Price", e));

    }


}
