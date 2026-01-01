package com.example.clickz;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickz.databinding.FragmentCartBinding;
import com.example.clickz.databinding.FragmentWishListBinding;
import com.example.clickz.ui.gallery.GalleryViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import model.SQLiteHelper;
import model.WishList;

public class WishListFragment extends Fragment {
private FragmentWishListBinding binding;
    private RecyclerView recyclerView;
    private WishListAdapter wishListAdapter;
    private List<WishList> wishListItems;
    private SQLiteHelper sqLiteHelper;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        sqLiteHelper = new SQLiteHelper(requireContext(),"Clickz.db",null,1);

        wishListItems = new ArrayList<>();


        loadWishlistFromDatabase();
        binding = FragmentWishListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView=binding.wishlistRecycleView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        wishListItems = new ArrayList<>();
        wishListAdapter = new WishListAdapter(requireContext(),wishListItems);
        recyclerView.setAdapter(wishListAdapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                WishList wishItem = wishListItems.get(position);

                // Call the method to delete from Firestore
                deleteItemFromSQLite(wishItem, position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return root;
    }
    private void deleteItemFromSQLite(WishList wishItem, int position) {
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

        // Define the WHERE clause to find the item
        String whereClause = "product_name = ? AND product_price = ?";
        String[] whereArgs = {wishItem.getTitle(), String.valueOf(wishItem.getPrice())};

        // Delete the item
        int deletedRows = db.delete("wishlist", whereClause, whereArgs);
        db.close();

        if (deletedRows > 0) {
            // Remove from list and update RecyclerView
            wishListItems.remove(position);
            wishListAdapter.notifyItemRemoved(position);
            Toast.makeText(getContext(), "Item removed from wishlist", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Error deleting item", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadWishlistFromDatabase() {
        // Fetch the wishlist items from SQLite
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<WishList> data = sqLiteHelper.getWishlistItems();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (data != null && !data.isEmpty()) {
                            wishListItems.clear();
                            wishListItems.addAll(data);
                            wishListAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }).start();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }




}


