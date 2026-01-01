package com.example.clickz.ui.gallery;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickz.CartAdapter;
import com.example.clickz.CartItem;
import com.example.clickz.CheckoutActivity;
import com.example.clickz.HomeActivity;
import com.example.clickz.R;
import com.example.clickz.SignInActivity;
import com.example.clickz.databinding.FragmentCartBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private double[] grandTotal = {0.0};
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView=binding.cartItemContainer;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(requireContext(),cartItemList);
        recyclerView.setAdapter(cartAdapter);
        if (getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).setToolbarTitle("Cart");
        }


        cartAdapter.setOnQuantityChangeListener(new CartAdapter.OnQuantityChangeListener() {
            @Override
            public void onQuantityChanged() {
                calculateGrandtotal();
            }
        });


        SharedPreferences preferences = requireContext().getSharedPreferences("user_data", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("is_logged_in", false);

        if (isLoggedIn) {
            String userId = preferences.getString("user_id", null);
            Log.i("ClickLog","userId "+userId);
            if (userId != null) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("cart")
                        .whereEqualTo("userId", userId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    String cartDocId = task.getResult().getDocuments().get(0).getId();
                                    Log.d("FirestoreData", "Cart Document ID: " + cartDocId);

                                    firestore.collection("cart")
                                            .document(cartDocId)
                                            .collection("items")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> itemTask) {
                                                    if (itemTask.isSuccessful()) {
                                                        Log.d("FirestoreData", "task success");

                                                        cartItemList.clear();
                                                        for (QueryDocumentSnapshot document : itemTask.getResult()) {
                                                            Log.d("FirestoreData", document.getData().toString());

                                                            CartItem cartItem = document.toObject(CartItem.class);
                                                            cartItemList.add(cartItem);
                                                        }
                                                        cartAdapter.notifyDataSetChanged();
                                                    } else {
                                                        Log.e("FirestoreData", "Failed to retrieve items: ", itemTask.getException());
                                                    }
                                                }
                                            });
                                } else {
                                    Log.d("FirestoreData", "No cart found for this user.");
                                    Toast.makeText(getActivity(), "No items in cart.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            } else {
                Toast.makeText(getActivity(), "Please sign in to view cart.", Toast.LENGTH_SHORT).show();
            }

        } else {
           Toast.makeText(requireContext(),"Need To Sign In",Toast.LENGTH_LONG).show();
                        Intent signInIntent = new Intent(requireContext(), SignInActivity.class);
                        startActivity(signInIntent);


        }


        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                CartItem cartItem = cartItemList.get(position);

                deleteItemFromFirestore(cartItem, position);
            }
        };

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Saving Address...");
        progressDialog.setCancelable(false);
       // progressDialog.show();



        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        Button checkoutButton  = root.findViewById(R.id.button15);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CheckoutActivity.class);
                intent.putExtra("cartTotal", grandTotal[0]);
                startActivity(intent);
            }
        });

        calculateGrandtotal();

        return root;
    }
    private void calculateGrandtotal() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        SharedPreferences preferences = requireContext().getSharedPreferences("user_data", MODE_PRIVATE);
        String userId = preferences.getString("user_id", null);

        if (userId != null) {
            firestore.collection("cart")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                grandTotal[0] = 0.0;

                                for (DocumentSnapshot cartDoc : task.getResult()) {
                                    String cartDocId = cartDoc.getId();
                                    firestore.collection("cart")
                                            .document(cartDocId)
                                            .collection("items")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> itemTask) {
                                                    if (itemTask.isSuccessful()) {
                                                        double totalPrice = 0.0;

                                                        for (DocumentSnapshot document : itemTask.getResult()) {
                                                            double price = document.getDouble("price");
                                                            int qty = document.getLong("qty").intValue();

                                                            totalPrice += price * qty;
                                                        }

                                                        grandTotal[0] += totalPrice;

                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                TextView grandTotalText = binding.textView34;
                                                                grandTotalText.setText("Cart Total : LKR " + grandTotal[0]);
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Sign in First", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteItemFromFirestore(CartItem cartItem, int position) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        SharedPreferences preferences = requireContext().getSharedPreferences("user_data", MODE_PRIVATE);
        String userId = preferences.getString("user_id", null);

        if (userId != null) {
            firestore.collection("cart")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            String cartDocId = task.getResult().getDocuments().get(0).getId();

                            // Delete the item from the cart's items subcollection
                            firestore.collection("cart")
                                    .document(cartDocId)
                                    .collection("items")
                                    .document(cartItem.getProductId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        cartItemList.remove(position);
                                        cartAdapter.notifyItemRemoved(position);
                                        Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                                        calculateGrandtotal();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Failed to delete item", Toast.LENGTH_SHORT).show();
                                        cartAdapter.notifyItemChanged(position);
                                    });
                        }
                    });
        }
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}