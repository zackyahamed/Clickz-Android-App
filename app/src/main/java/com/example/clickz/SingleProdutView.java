package com.example.clickz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Product;

public class SingleProdutView extends AppCompatActivity {
    private ViewPager2 viewPager;
    private ImageSliderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_produt_view);

        Product product = (Product) getIntent().getSerializableExtra("product");

        if (product == null) {
            Log.e("SingleProdutView", "Product data is missing!");
            return;
        }
        Button webR = findViewById(R.id.button19);
        webR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleProdutView.this,WebReviewActivity.class);
                intent.putExtra("productTitle",product.getTitle());
                startActivity(intent);
            }
        });

        viewPager = findViewById(R.id.viewPager);
        String ngrok = getString(R.string.ngrok);

        // Add image URLs from the product
        List<String> imageUrls = new ArrayList<>();
        if (product.getImage1() != null) imageUrls.add(ngrok + product.getImage1());
        if (product.getImage2() != null) imageUrls.add(ngrok + product.getImage2());
        if (product.getImage3() != null) imageUrls.add(ngrok + product.getImage3());

        adapter = new ImageSliderAdapter(this, imageUrls);
        viewPager.setAdapter(adapter);

        TextView title = findViewById(R.id.textView19);
        TextView qty = findViewById(R.id.textView20);
        TextView condition = findViewById(R.id.textView33);
        TextView price = findViewById(R.id.textView21);
        TextView description = findViewById(R.id.textView23);

        title.setText(product.getTitle());
        condition.setText(product.getCondition());
        if (product.getQty() <= 0) {
            qty.setText("Out of Stock!");
        } else {
            qty.setText(product.getQty() + " Available");
        }
        price.setText("Rs. " + product.getPrice());
        description.setText(product.getDescription());

        TextView cartQuantityTextView = findViewById(R.id.textView32);

        Button addButton = findViewById(R.id.button13);
        Button minusButton = findViewById(R.id.button14);

        addButton.setOnClickListener(view -> {
            int cartQty = Integer.parseInt(cartQuantityTextView.getText().toString().trim());
            if (product.getQty() > cartQty) {
                cartQty++;
                cartQuantityTextView.setText(String.valueOf(cartQty));
            } else {
                Snackbar.make(view, "You Reached the Max Quantity", Snackbar.LENGTH_LONG)
                        .setAction("Ok", v -> cartQuantityTextView.setText(String.valueOf(product.getQty()))).show();
            }
        });

        minusButton.setOnClickListener(view -> {
            int cartQty = Integer.parseInt(cartQuantityTextView.getText().toString().trim());
            if (cartQty > 1) {
                cartQty--;
                cartQuantityTextView.setText(String.valueOf(cartQty));
            }
        });

        Button addtocart = findViewById(R.id.button10);
        addtocart.setOnClickListener(view -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if (product.getProductId() == null) {
                Log.e("FirestoreError", "Product ID is null!");
                return;
            }

            SharedPreferences preferences = SingleProdutView.this.getSharedPreferences("user_data", Context.MODE_PRIVATE);
            String userId = preferences.getString("user_id", null);

            if (userId != null) {
                int cartQty = Integer.parseInt(cartQuantityTextView.getText().toString().trim());

                db.collection("cart")
                        .whereEqualTo("userId", userId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                String cartDocId = task.getResult().getDocuments().get(0).getId();
                                Log.d("FirestoreData", "Cart Document ID: " + cartDocId);

                                db.collection("cart")
                                        .document(cartDocId)
                                        .collection("items")
                                        .whereEqualTo("productId", product.getProductId())
                                        .get()
                                        .addOnCompleteListener(productTask -> {
                                            if (productTask.isSuccessful()) {
                                                if (!productTask.getResult().isEmpty()) {
                                                    QueryDocumentSnapshot document = (QueryDocumentSnapshot) productTask.getResult().getDocuments().get(0);
                                                    String productDocId = document.getId();
                                                    int currentQty = document.getLong("qty").intValue();
                                                    int newQty = currentQty + cartQty;

                                                    db.collection("cart")
                                                            .document(cartDocId)
                                                            .collection("items")
                                                            .document(productDocId)
                                                            .update("qty", newQty)
                                                            .addOnSuccessListener(aVoid -> {
                                                                Toast.makeText(SingleProdutView.this, "Cart updated!", Toast.LENGTH_SHORT).show();
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Toast.makeText(SingleProdutView.this, "Failed to update cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            });
                                                } else {
                                                    Map<String, Object> cartItem = new HashMap<>();
                                                    cartItem.put("productId", product.getProductId());
                                                    cartItem.put("title", product.getTitle());
                                                    cartItem.put("image", product.getImage1());
                                                    cartItem.put("price", product.getPrice());
                                                    cartItem.put("qty", cartQty);
                                                    cartItem.put("availableqty", product.getQty());

                                                    db.collection("cart")
                                                            .document(cartDocId)
                                                            .collection("items")
                                                            .document(product.getProductId())
                                                            .set(cartItem)
                                                            .addOnSuccessListener(aVoid -> {
                                                                Toast.makeText(SingleProdutView.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Toast.makeText(SingleProdutView.this, "Failed to add to Cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            });
                                                }
                                            } else {
                                                Toast.makeText(SingleProdutView.this, "Failed to check cart: " + productTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                DocumentReference newCartRef = db.collection("cart").document();
                                String newCartDocId = newCartRef.getId();

                                Map<String, Object> newCart = new HashMap<>();
                                newCart.put("userId", userId);

                                newCartRef.set(newCart)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("FirestoreData", "New cart created for user: " + userId);

                                            Map<String, Object> cartItem = new HashMap<>();
                                            cartItem.put("productId", product.getProductId());
                                            cartItem.put("title", product.getTitle());
                                            cartItem.put("image", product.getImage1());
                                            cartItem.put("price", product.getPrice());
                                            cartItem.put("qty", cartQty);
                                            cartItem.put("availableqty", product.getQty());

                                            db.collection("cart")
                                                    .document(newCartDocId)
                                                    .collection("items")
                                                    .document(product.getProductId())
                                                    .set(cartItem)
                                                    .addOnSuccessListener(aVoid2 -> {
                                                        Toast.makeText(SingleProdutView.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                                                    });
                                        });
                            }
                        });
            } else {
                Toast.makeText(SingleProdutView.this, "Please sign in to add items to cart.", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
