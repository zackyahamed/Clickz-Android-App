package com.example.clickz.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickz.Category;
import com.example.clickz.CategoryAdapter;
import com.example.clickz.HomeActivity;
import com.example.clickz.NewArrivalAdapter;
import model.Product;
import com.example.clickz.ProductAdapter;
import com.example.clickz.R;
import com.example.clickz.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        loadNewArrival();
        loadCategories();
        loadProduct();
        if (getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).setToolbarTitle("Home");
        }

        return root;
    }

    private void loadCategories() {
        RecyclerView categoryRecyclerView = binding.categoryContainerLayout;
        // Sample categories
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Digital Cam", R.drawable.digitalcamcat));
        categoryList.add(new Category("Action Cam", R.drawable.actioncamcat));
        categoryList.add(new Category("Accessories", R.drawable.accessoriescat));
        categoryList.add(new Category("Drone", R.drawable.dronecat));





        CategoryAdapter adapter = new CategoryAdapter(getContext(), categoryList);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(adapter);
    }
    private void loadProduct(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            RecyclerView productRecyclerView = binding.homeProductContainerLayout;
            List<Product> productList = new ArrayList<>();
            ProductAdapter productAdapter = new ProductAdapter(getContext(), productList);

            productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            productRecyclerView.setAdapter(productAdapter);

            firestore.collection("products")
                    .orderBy("datetime", Query.Direction.ASCENDING) // Order by latest first
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
//
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Product product = document.toObject(Product.class);
                            productList.add(product);
                        }
                        productAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getActivity(), "Failed to load products: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );


    }

    private void loadNewArrival(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            RecyclerView newArrivalProductRecyclerView = binding.newArrivalcontainer;
            List<Product> productList = new ArrayList<>();
            NewArrivalAdapter newArrivalAdapter = new NewArrivalAdapter(getContext(), productList);

            newArrivalProductRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            newArrivalProductRecyclerView.setAdapter(newArrivalAdapter);

            firestore.collection("products")
                    .orderBy("datetime", Query.Direction.DESCENDING)
                    .limit(10)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        productList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Product product = document.toObject(Product.class);
                            productList.add(product);
                        }
                        newArrivalAdapter.notifyDataSetChanged(); // Refresh UI
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getActivity(), "Failed to load new arrivals: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );



    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}