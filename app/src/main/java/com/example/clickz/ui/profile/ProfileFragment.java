package com.example.clickz.ui.profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.clickz.SignInActivity;
import com.example.clickz.databinding.FragmentProfileBinding;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_data", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        updateButtonText(sharedPreferences);
        EditText fname = binding.editTextText19;
        EditText lname = binding.editTextText20;
        EditText email = binding.editTextText21;
        EditText mobile = binding.editTextText22;

        if(isLoggedIn){

             fname.setText(sharedPreferences.getString("user_fname",null));
            lname.setText(sharedPreferences.getString("user_lname",null));
            email.setText(sharedPreferences.getString("email",null));
            mobile.setText(sharedPreferences.getString("mobile",null));

        }else{
            fname.setEnabled(false);
            lname.setEnabled(false);
            email.setEnabled(false);
            mobile.setEnabled(false);
        }

        Button singButton = binding.button12;
        singButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the latest login status


                if (isLoggedIn) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("is_logged_in", false); // Update login status
                    editor.apply();

                    updateButtonText(sharedPreferences);

                    Intent intent = new Intent(requireContext(), SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();

                } else {
                    Intent intent = new Intent(requireContext(), SignInActivity.class);
                    startActivity(intent);
                }
            }
        });

        return root;
    }

    private void updateButtonText(SharedPreferences sharedPreferences) {
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        if (isLoggedIn) {
            binding.button12.setText("Sign Out");
        } else {
            binding.button12.setText("Sign In");
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}