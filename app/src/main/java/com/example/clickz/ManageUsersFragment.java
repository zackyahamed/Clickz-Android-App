package com.example.clickz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.clickz.databinding.FragmentManageUsersBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import model.User;


public class ManageUsersFragment extends Fragment {
    private FragmentManageUsersBinding binding;
    private RecyclerView recyclerView;
    private ManageUsersAdapter manageUsersAdapter;
    private List<User> userList;


    public ManageUsersFragment() {
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentManageUsersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView=binding.manageUserRecycleView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        manageUsersAdapter = new ManageUsersAdapter(requireContext(),userList);
        recyclerView.setAdapter(manageUsersAdapter);

        loadUsers();
        return root;

    }
    private void loadUsers(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();



        firestore.collection("user")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        userList.add(user);
                    }
                    manageUsersAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getActivity(), "Failed to load Users: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );


    }

}