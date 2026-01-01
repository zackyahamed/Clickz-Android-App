package com.example.clickz;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clickz.ui.gallery.CartFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;

    private Context context;

    private OnQuantityChangeListener quantityChangeListener;

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.quantityChangeListener = listener;
    }


    public CartAdapter(@NonNull Context context, List<CartItem> cartItems) {
        this.cartItems = cartItems;
        this.context = context;

    }

    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item,parent,false);
        return new CartAdapter.CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        String ngrokUrl = context.getString(R.string.ngrok);

        CartItem cartItem = cartItems.get(position);

        holder.productTitle.setText(cartItem.getTitle());
        holder.availableQty.setText("In Stock "+String.valueOf(cartItem.getAvailableqty()));
        holder.productQty.setText(String.valueOf(cartItem.getQty()));
        holder.productPrice.setText(String.valueOf(cartItem.getPrice()));
        Glide.with(holder.itemView.getContext())
                .load(ngrokUrl+cartItem.getImage())
                .into(holder.productImage);
        double totalPrice = cartItem.getQty() * cartItem.getPrice();
        holder.totalPrice.setText(String.valueOf(totalPrice));

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        SharedPreferences preferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);

        String userId = preferences.getString("user_id", null);
        holder.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newQty = Integer.parseInt(holder.productQty.getText().toString().trim());
                   int avaqty = Integer.parseInt(String.valueOf(cartItem.getAvailableqty()));
                   if(avaqty>newQty){
                       newQty++;
                       holder.productQty.setText(String.valueOf(newQty));
                       double totalPrice = newQty * cartItem.getPrice();
                       holder.totalPrice.setText(String.valueOf(totalPrice));

                       firestore.collection("cart")
                               .whereEqualTo("userId", userId)
                               .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                   @Override
                                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                       if(task.isSuccessful()){
                                           String cartDocId = task.getResult().getDocuments().get(0).getId();
                                           Log.d("FirestoreData", "Cart Document ID: " + cartDocId);

                                           // Step 2: Check if product is already in the cart
                                           DocumentReference productRef = firestore.collection("cart")
                                                   .document(cartDocId)
                                                   .collection("items")
                                                   .document(cartItem.getProductId());

                                           productRef.get().addOnSuccessListener(documentSnapshot -> {
                                               if (documentSnapshot.exists()) {

                                                   Log.d("FirestoreData", "Cartdocumentexixt");

                                                   productRef.update("qty",Integer.parseInt(holder.productQty.getText().toString()))
                                                           .addOnSuccessListener(aVoid -> {
                                                               Toast.makeText(context, "Cart updated!", Toast.LENGTH_SHORT).show();
                                                               if (quantityChangeListener != null) {
                                                                   quantityChangeListener.onQuantityChanged();
                                                               }
                                                           })
                                                           .addOnFailureListener(e -> {
                                                               Toast.makeText(context, "Failed to update cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                           });
                                               }
                                           });
                                           }
                                   }
                               });

                   }else{
                       Snackbar.make(view, "You Reached the Max Quantity", Snackbar.LENGTH_LONG)
                               .setAction("Ok", new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       holder.productQty.setText(String.valueOf(avaqty));
                                   }
                               }).show();
                   }
            }
        });

//
        holder.minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newQty = Integer.parseInt(holder.productQty.getText().toString().trim());

                if(newQty>1){
                    newQty--;
                    holder.productQty.setText(String.valueOf(newQty));
                    double totalPrice = newQty * cartItem.getPrice();
                    holder.totalPrice.setText(String.valueOf(totalPrice));
                    //update firestore

                    firestore.collection("cart")
                            .whereEqualTo("userId", userId)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        String cartDocId = task.getResult().getDocuments().get(0).getId();
                                        Log.d("FirestoreData", "Cart Document ID: " + cartDocId);

                                        // Step 2: Check if product is already in the cart
                                        DocumentReference productRef = firestore.collection("cart")
                                                .document(cartDocId)
                                                .collection("items")
                                                .document(cartItem.getProductId());

                                        productRef.get().addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {

                                                Log.d("FirestoreData", "Cartdocumentexixt");

                                                productRef.update("qty",Integer.parseInt(holder.productQty.getText().toString()))
                                                        .addOnSuccessListener(aVoid -> {
                                                            Toast.makeText(context, "Cart updated!", Toast.LENGTH_SHORT).show();
                                                            if (quantityChangeListener != null) {
                                                                quantityChangeListener.onQuantityChanged();
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(context, "Failed to update cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        });
                                            }
                                        });
                                    }
                                }
                            });


                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void removeItem(int position) {
        cartItems.remove(position);
        notifyItemRemoved(position);
    }
    public static class CartViewHolder extends RecyclerView.ViewHolder{
        ImageView productImage;
        TextView productTitle;
        TextView availableQty;
        TextView productQty;
        TextView productPrice;
        TextView totalPrice;
        Button minusButton;
        Button plusButton;
        Button checkout;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
productImage=itemView.findViewById(R.id.cartItemimageView);
productTitle = itemView.findViewById(R.id.textView25);
productQty = itemView.findViewById(R.id.textView32);
availableQty = itemView.findViewById(R.id.textView26);
productPrice =itemView.findViewById(R.id.textView27);
totalPrice=itemView.findViewById(R.id.textView30);
        minusButton     =itemView.findViewById(R.id.button14);
             plusButton = itemView.findViewById(R.id.button13);



        }
    }
}
