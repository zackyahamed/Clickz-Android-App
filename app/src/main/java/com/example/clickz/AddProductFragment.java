package com.example.clickz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.clickz.databinding.FragmentAddProductBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddProductFragment extends Fragment {
    private FragmentAddProductBinding binding;
    private ImageView imageView1, imageView2, imageView3;
    private Uri imageUri1, imageUri2, imageUri3;
    private static final int PICK_IMAGE_1 = 1;
    private static final int PICK_IMAGE_2 = 2;
    private static final int PICK_IMAGE_3 = 3;
    private FirebaseFirestore firestore;
    private Spinner categorySpinner;
    private Spinner brandSpinner;
    private Spinner modelSpinner;

    private final ActivityResultLauncher<Intent> imagePicker1 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    imageUri1 = result.getData().getData();
                    imageView1.setImageURI(imageUri1);
                }
            });

    private final ActivityResultLauncher<Intent> imagePicker2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    imageUri2 = result.getData().getData();
                    imageView2.setImageURI(imageUri2);
                }
            });

    private final ActivityResultLauncher<Intent> imagePicker3 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    imageUri3 = result.getData().getData();
                    imageView3.setImageURI(imageUri3);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Spinner conditionSpinner = binding.spinner;
        categorySpinner = binding.spinner2;
        brandSpinner = binding.spinner3;
        modelSpinner = binding.spinner5;

        if (getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).setToolbarTitle("Add New Product");
        }
        firestore = FirebaseFirestore.getInstance();

        String[] items = {"Condition", "New", "Used"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionSpinner.setAdapter(adapter);

        imageView1 = binding.imageView7;
        imageView2 = binding.imageView9;
        imageView3 = binding.imageView10;
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        imageView1.setOnClickListener(view -> pickImage(imagePicker1));
        imageView2.setOnClickListener(view -> pickImage(imagePicker2));
        imageView3.setOnClickListener(view -> pickImage(imagePicker3));

        Button addProducts = binding.button8;
        addProducts.setOnClickListener(view -> {
            String title = binding.editTextText8.getText().toString().trim();
            String description = binding.editTextTextMultiLine.getText().toString().trim();
            String price = binding.editTextNumber.getText().toString().trim();
            String qty = binding.editTextNumber2.getText().toString().trim();
            String condition = conditionSpinner.getSelectedItem() != null ? conditionSpinner.getSelectedItem().toString() : "";
            String category = categorySpinner.getSelectedItem() != null ? categorySpinner.getSelectedItem().toString() : "";
            String brand = brandSpinner.getSelectedItem() != null ? brandSpinner.getSelectedItem().toString() : "";
            String model = modelSpinner.getSelectedItem() != null ? modelSpinner.getSelectedItem().toString() : "";

            if (title.isEmpty()) {
                WarinigAlert.showCustomAlert(getContext(), "Add a Title");
            } else if (description.isEmpty()) {
                WarinigAlert.showCustomAlert(getContext(), "Add a Description");
            } else if (price.isEmpty()) {
                WarinigAlert.showCustomAlert(getContext(), "Add a Price");
            } else if (qty.isEmpty()) {
                WarinigAlert.showCustomAlert(getContext(), "Add a Quantity");
            } else if ("Condition".equals(condition)) {
                WarinigAlert.showCustomAlert(getContext(), "Please Select Condition");
            } else if ("Category".equals(category)) {
                WarinigAlert.showCustomAlert(getContext(), "Please Select Category");
            } else if ("Brand".equals(brand)) {
                WarinigAlert.showCustomAlert(getContext(), "Please Select Brand");
            } else if ("Model".equals(model)) {
                WarinigAlert.showCustomAlert(getContext(), "Please Select Model");
            } else if (imageUri1 == null || imageUri2 == null || imageUri3 == null) {
                WarinigAlert.showCustomAlert(getContext(), "Please Add Product Images");
            } else {
                progressDialog.show();
                new Thread(() -> {
                    try {
                        String ngrokUrl = getContext().getString(R.string.ngrok);
                        String serverUrl = ngrokUrl + "/ClickzApp/UploadProductImageServlet";

                        OkHttpClient client = new OkHttpClient();

                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("image1", "image1.png", getRequestBody(imageUri1))
                                .addFormDataPart("image2", "image2.png", getRequestBody(imageUri2))
                                .addFormDataPart("image3", "image3.png", getRequestBody(imageUri3))
                                .build();

                        Request request = new Request.Builder()
                                .url(serverUrl)
                                .post(requestBody)
                                .build();

                        Response response = client.newCall(request).execute();
                        String responseBody = response.body().string();

                        JSONObject jsonObject = new JSONObject(responseBody);
                        String image1Url = jsonObject.getString("image1");
                        String image2Url = jsonObject.getString("image2");
                        String image3Url = jsonObject.getString("image3");

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String dateTime = sdf.format(new Date());
                        int validQty = (int) Math.round(Double.parseDouble(qty)); // Converts qty to integer

                        int validPrice = (int) Math.round(Double.parseDouble(price));
                        DocumentReference newProductRef = firestore.collection("products").document();
                        String productId = newProductRef.getId(); // Get the generated ID

                        // Create Firestore document
                        HashMap<String, Object> product = new HashMap<>();
                        product.put("productId", productId);
                        product.put("title", title);
                        product.put("description", description);
                        product.put("price", validPrice);
                        product.put("qty", validQty);
                        product.put("condition", condition);
                        product.put("category", category);
                        product.put("brand", brand);
                        product.put("model", model);
                        product.put("image1", image1Url);
                        product.put("image2", image2Url);
                        product.put("image3", image3Url);
                        product.put("datetime", dateTime);

                        newProductRef.set(product)
                                .addOnSuccessListener(aVoid -> {
                                    progressDialog.dismiss();
                                    SuccessAlert.showSuccessAlert(getContext(), "Product Uploaded");
                                    resetForm();
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    WarinigAlert.showCustomAlert(getContext(), "Error Try Again");
                                    resetForm();
                                });

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                        getActivity().runOnUiThread(() -> Log.e("UploadError", "Upload failed", e));
                    }
                }).start();
            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedCategory = parent.getItemAtPosition(position).toString();
                    loadBrands(selectedCategory);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedBrand = parent.getItemAtPosition(position).toString();
                    loadModels(selectedBrand);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        requestStoragePermission();

        return root;
    }

    private void resetForm() {
        binding.editTextText8.setText("");
        binding.editTextTextMultiLine.setText("");
        binding.editTextNumber.setText("");
        binding.editTextNumber2.setText("");
        categorySpinner.setSelection(0);
        brandSpinner.setSelection(0);
        modelSpinner.setSelection(0);
        imageView1.setImageURI(null);
        imageView2.setImageURI(null);
        imageView3.setImageURI(null);
    }

    private RequestBody getRequestBody(Uri imageUri) throws IOException {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = requireContext().getContentResolver().query(imageUri, projection, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
        cursor.close();
        File imageFile = new File(filePath);
        return RequestBody.create(MediaType.parse("image/*"), imageFile);
    }

    private void loadBrands(String category) {
        firestore.collection("brands").whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> brands = new ArrayList<>();
                    brands.add("Brand");
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        brands.add(documentSnapshot.getString("brandName"));
                    }
                    ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, brands);
                    brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    brandSpinner.setAdapter(brandAdapter);
                });
    }

    private void loadModels(String brand) {
        firestore.collection("models").whereEqualTo("brand", brand)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> models = new ArrayList<>();
                    models.add("Model");
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        models.add(documentSnapshot.getString("modelName"));
                    }
                    ArrayAdapter<String> modelAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, models);
                    modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    modelSpinner.setAdapter(modelAdapter);
                });
    }

    private void pickImage(ActivityResultLauncher<Intent> launcher) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Please grant storage permission first.", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Please grant storage permission first.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast.makeText(getContext(), "Storage permission is required to select images", Toast.LENGTH_SHORT).show();
                }
            });
}
