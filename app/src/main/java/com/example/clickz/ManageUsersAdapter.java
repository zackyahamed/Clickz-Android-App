package com.example.clickz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import model.User;

public class ManageUsersAdapter extends RecyclerView.Adapter<ManageUsersAdapter.ManageUsersViewHolder> {
    private List<User> userList;
    private Context context;
    private FirebaseFirestore firestore;

    public ManageUsersAdapter(@NonNull Context context, List<User> userList) {
        this.userList = userList;
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ManageUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.manage_user_item, parent, false);
        return new ManageUsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageUsersViewHolder holder, int position) {
        User user = userList.get(position);

        holder.userName.setText(user.getFname() + " " + user.getLname());
        holder.mobile.setText(user.getMobile());
        holder.email.setText(user.getEmail());

        // Set button text based on user status
        if (user.getStatus() == 1) {
            holder.status.setText("Active");
            holder.status.setTextColor(Color.WHITE);
        } else {
            holder.status.setText("Blocked");
            holder.status.setTextColor(Color.RED);

        }

        holder.status.setOnClickListener(view -> {
            int newStatus = (user.getStatus() == 1) ? 0 : 1;


            firestore.collection("user").document(user.getUserId())
                    .update("status", newStatus)
                    .addOnSuccessListener(aVoid -> {
                        user.setStatus(newStatus);
                        notifyItemChanged(position);
                        Toast.makeText(context, "User status updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()
                    );
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ManageUsersViewHolder extends RecyclerView.ViewHolder {
        TextView userName, mobile, email;
        Button status;

        public ManageUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.textView48);
            mobile = itemView.findViewById(R.id.textView58);
            email = itemView.findViewById(R.id.textView61);
            status = itemView.findViewById(R.id.button20);
        }
    }
}
