package com.example.clickz;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.clickz.databinding.FragmentContactBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ContactFragment extends Fragment {

    private FragmentContactBinding binding;
    private final String phoneNumber = "+94758626310";

    private final ActivityResultLauncher<String> requestCallPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    makeCall();
                } else {
                    Toast.makeText(getContext(), "Call permission is required!", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentContactBinding.inflate(inflater, container, false);

        if (getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).setToolbarTitle("Contact Us");
        }

        Button smsButton = binding.button17;
        smsButton.setOnClickListener(v -> sendSMS());

        Button callButton = binding.button18;
        callButton.setOnClickListener(v -> checkAndMakeCall());

        setupGoogleMap();

        return binding.getRoot();
    }

    private void sendSMS() {
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
        smsIntent.putExtra("sms_body", "Hello! ");
        startActivity(smsIntent);
    }

    private void checkAndMakeCall() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            makeCall();
        } else {
            requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE);
        }
    }

    private void makeCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    private void setupGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapLayout);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.mapLayout, mapFragment).commit();
        }

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                LatLng colomboBranch = new LatLng(6.907257, 79.852962);
                googleMap.addMarker(new MarkerOptions().position(colomboBranch).title("Clickz - Colombo Branch")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.sightseeing)));

                LatLng kandyBranch = new LatLng(7.290571, 80.633728);
                googleMap.addMarker(new MarkerOptions().position(kandyBranch).title("Clickz - Kandy Branch")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.sightseeing)));

                LatLng kurunegalaBranch = new LatLng(7.486046, 80.365865);
                googleMap.addMarker(new MarkerOptions().position(kurunegalaBranch).title("Clickz - Kurunegala Branch")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.sightseeing)));

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder().target(colomboBranch).zoom(15).build()));

                googleMap.setOnMarkerClickListener(marker -> {
                    marker.showInfoWindow();
                    return false;
                });

                googleMap.setOnMapClickListener(latLng -> {
                    googleMap.addMarker(new MarkerOptions().position(latLng)
                                    .title("Custom Location: " + latLng.latitude + ", " + latLng.longitude)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.locationpin)))
                            .showInfoWindow();
                });
            }
        });
    }
}
