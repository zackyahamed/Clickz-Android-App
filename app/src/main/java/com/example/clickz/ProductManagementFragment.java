package com.example.clickz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.clickz.databinding.FragmentProductManagementBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import model.Product;


public class ProductManagementFragment extends Fragment {



    private FragmentProductManagementBinding binding;
    private RecyclerView recyclerView;
    private ManageProductAdapter manageProductAdapter;
    private List<Product> productList;
    public ProductManagementFragment() {
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductManagementBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView=binding.manageProductRecycleView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        productList = new ArrayList<>();
        manageProductAdapter = new ManageProductAdapter(requireContext(),productList);
        recyclerView.setAdapter(manageProductAdapter);


        loadProduct();


        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (position >= 0 && position < productList.size()) {
                    Product product = productList.get(position);
                    deleteItemFromFirestore(product, position);
                } else {
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);



        return root;
    }
    private void deleteItemFromFirestore(Product product, int position) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("products").document(product.getProductId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    productList.remove(position); // Remove item from the list
                    recyclerView.getAdapter().notifyItemRemoved(position); // Notify adapter
                    Toast.makeText(getContext(), "Product deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    recyclerView.getAdapter().notifyDataSetChanged(); // Restore item if deletion fails
                });
    }


    private void loadProduct(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();



        firestore.collection("products")
                .orderBy("datetime", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Product product = document.toObject(Product.class);
                        productList.add(product);
                    }
                    manageProductAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getActivity(), "Failed to load products: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );


    }
}