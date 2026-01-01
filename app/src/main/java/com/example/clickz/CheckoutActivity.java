package com.example.clickz;

import static android.app.PendingIntent.getActivity;
import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;

import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;

public class CheckoutActivity extends AppCompatActivity {

    private Spinner provinceSpinner;
    private static final String TAG = "payhereDemo";
    private  Spinner citySpinner;
    private List<String> provinces;
    private List<City> cities;
    private String userId;
    private String orderId;
    private double cartTotal;
    private int shippingCost;

    private double grandTotal;

    private TextView grandTotaltext;
    private TextView cartTotaltext;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private ActivityResultLauncher<Intent> payHereLauncher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);

        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        userId = preferences.getString("user_id", null);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cartTotaltext  = findViewById(R.id.textView37);

            String timestamp = String.valueOf(System.currentTimeMillis());
            orderId= "ORD-" + userId.substring(0, 6) + "-" + timestamp;


        provinceSpinner = findViewById(R.id.spinner4);
        citySpinner = findViewById(R.id.spinner6);





        provinces = Arrays.asList(
                "Western", "Central", "Southern", "Northern",
                "Eastern", "North Western", "North Central",
                "Uva", "Sabaragamuwa"
        );

        cities = new ArrayList<>();
        cities.add(new City("Colombo", "Western"));
        cities.add(new City("Gampaha", "Western"));
        cities.add(new City("Kalutara", "Western"));
        cities.add(new City("Kandy", "Central"));
        cities.add(new City("Matale", "Central"));
        cities.add(new City("Nuwara Eliya", "Central"));
        cities.add(new City("Galle", "Southern"));
        cities.add(new City("Matara", "Southern"));
        cities.add(new City("Hambantota", "Southern"));

        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(
                CheckoutActivity.this,
                android.R.layout.simple_spinner_item,
                provinces
        );
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(provinceAdapter);

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedProvince = provinces.get(position);
                loadCitiesForProvince(selectedProvince);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        calculateCarttotal();
        citySpinner = findViewById(R.id.spinner6);
        TextView shippingCostText = findViewById(R.id.textView38);
        grandTotaltext = findViewById(R.id.textView40);

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = citySpinner.getSelectedItem().toString();

                // Set shipping cost based on city

                if (selectedCity.equalsIgnoreCase("Colombo")) {
                    shippingCost = 500;
                } else {
                    shippingCost = 1000;
                }

                // Display the shipping cost
                shippingCostText.setText("Shipping Cost: LKR " + shippingCost);
                loadCheckoutTotal();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        Switch addresSwith = findViewById(R.id.switch1);
        EditText line1 = findViewById(R.id.editTextText13);
        EditText line2 = findViewById(R.id.editTextText14);
        EditText firstName = findViewById(R.id.editTextText10);
        EditText lastName = findViewById(R.id.editTextText11);
        EditText mobile = findViewById(R.id.editTextText12);
        Spinner citySpinner = findViewById(R.id.spinner6);
        Spinner provinceSpinner = findViewById(R.id.spinner4);



        addresSwith.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    firestore.collection("address")
                            .document(userId)
                            .collection("address_details")
                            .limit(1)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                                        for( DocumentSnapshot document : task.getResult()) {
                                            line1.setText(document.getString("line1"));
                                            line2.setText(document.getString("line2"));
                                            firstName.setText(document.getString("fname"));
                                            lastName.setText(document.getString("lname"));
                                            mobile.setText(document.getString("mobile"));

                                            // Set city and province spinner selections
                                            String city = document.getString("city");
                                            String province = document.getString("province");

                                            setSpinnerSelection(provinceSpinner, province);
                                            setSpinnerSelection(citySpinner, city);

                                            line1.setEnabled(false);
                                            firstName.setEnabled(false);
                                            lastName.setEnabled(false);
                                            line2.setEnabled(false);
                                            mobile.setEnabled(false);
                                            provinceSpinner.setEnabled(false);
                                            citySpinner.setEnabled(false);

                                        }
                                    }else{
                                        Snackbar.make(compoundButton, "No Available Address.", Snackbar.LENGTH_LONG).show();
                                        addresSwith.setChecked(false);                                }
                                }
                            }).addOnFailureListener(e -> {
                                Snackbar.make(compoundButton, "Address Load Faild.", Snackbar.LENGTH_LONG).show();

                                addresSwith.setChecked(false);
                            });;
                }else{
                    line1.setText("");
                    line2.setText("");
                    firstName.setText("");
                    lastName.setText("");
                    mobile.setText("");
                    citySpinner.setSelection(0);
                    provinceSpinner.setSelection(0);
                    line1.setEnabled(true);
                    firstName.setEnabled(true);
                    lastName.setEnabled(true);
                    line2.setEnabled(true);
                    mobile.setEnabled(true);
                    provinceSpinner.setEnabled(true);
                    citySpinner.setEnabled(true);
                }

            }
        });




        Button checkoutbtn = findViewById(R.id.button16);
        checkoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addresSwith.isChecked()){

                    initiatePayment();
                }else{
                    saveAddressToFirestore();

                }
            }
        });
        payHereLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) result.getData().getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
                if (response != null && response.isSuccess()) {
                    Log.d("TAG", "Payment successful: " + response.getData());
                    saveOrderToFirestore();
                } else {
                    Log.d("TAG", "Payment failed or no response");
                }
            } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                Log.d("TAG", "Payment canceled");
            }
        });



    }

    private void loadCheckoutTotal() {
        grandTotal = cartTotal + shippingCost;
        grandTotaltext.setText("LKR " + grandTotal);
    }



    private void saveAddressToFirestore() {
        Switch addresSwith = findViewById(R.id.switch1);
        EditText line1 = findViewById(R.id.editTextText13);
        EditText line2 = findViewById(R.id.editTextText14);
        EditText firstName = findViewById(R.id.editTextText10);
        EditText lastName = findViewById(R.id.editTextText11);
        EditText mobile = findViewById(R.id.editTextText12);
        Spinner citySpinner = findViewById(R.id.spinner6);
        Spinner provinceSpinner = findViewById(R.id.spinner4);

        String line1Text = line1.getText().toString().trim();
        String line2Text = line2.getText().toString().trim();
        String firstNameText = firstName.getText().toString().trim();
        String lastNameText = lastName.getText().toString().trim();
        String mobileText = mobile.getText().toString().trim();
        String cityText = citySpinner.getSelectedItem().toString();
        String provinceText = provinceSpinner.getSelectedItem().toString();

        // Check if required fields are not empty
        if (line1Text.isEmpty() || firstNameText.isEmpty() || lastNameText.isEmpty() || mobileText.isEmpty()) {
            Snackbar.make(findViewById(R.id.main), "Please fill all required fields.", Snackbar.LENGTH_LONG).show();
            return;
        }

        // Create a HashMap for the address
        HashMap<String, Object> addressMap = new HashMap<>();
        addressMap.put("line1", line1Text);
        addressMap.put("line2", line2Text);
        addressMap.put("fname", firstNameText);
        addressMap.put("lname", lastNameText);
        addressMap.put("mobile", mobileText);
        addressMap.put("city", cityText);
        addressMap.put("province", provinceText);

        // Save to Firestore
        firestore.collection("address")
                .document(userId)
                .collection("address_details")
                .add(addressMap)
                .addOnSuccessListener(documentReference -> {
                    Snackbar.make(findViewById(R.id.main), "Address saved successfully.", Snackbar.LENGTH_LONG).show();
                    initiatePayment();
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(R.id.main), "Failed to save address.", Snackbar.LENGTH_LONG).show();
                });
    }






    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(value);
            spinner.setSelection(position);
        }
    }


    private void loadCitiesForProvince(String selectedProvince) {
        List<String> filteredCities = new ArrayList<>();

        for (City city : cities) {
            if (city.getProvince().equals(selectedProvince)) {
                filteredCities.add(city.getName());
            }
        }

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                filteredCities
        );
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
    }
private int PAYHERE_REQUEST = 1221;


    private void calculateCarttotal() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Get the cart document ID from the user
        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String userId = preferences.getString("user_id", null);

        if (userId != null) {
            firestore.collection("cart")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                cartTotal = 0.0; // Reset the grandTotal before recalculating

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

                                                        cartTotal += totalPrice;
                                                        cartTotaltext.setText("LKR "+String.valueOf(cartTotal));
                                                        loadCheckoutTotal();


                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });
        } else {
            Toast.makeText(CheckoutActivity.this, "Sign in First", Toast.LENGTH_SHORT).show();
        }
    }





    private void initiatePayment() {

        int intGrandTotal = (int) grandTotal;
        Log.d("payherebug", String.valueOf(intGrandTotal));

        InitRequest req = new InitRequest();
        req.setMerchantId("1226424");
        req.setCurrency("LKR");
        req.setAmount(intGrandTotal);
        req.setOrderId(orderId);
        req.setItemsDescription("Door bell wireless");
        req.setCustom1("This is the custom message 1");
        req.setCustom2("This is the custom message 2");
        req.getCustomer().setFirstName("Sahan");
        req.getCustomer().setLastName("Khan");
        req.getCustomer().setEmail("samanp@gmail.com");
        req.getCustomer().setPhone("+94771234567");
        req.getCustomer().getAddress().setAddress("No.1, Kandy Road");
        req.getCustomer().getAddress().setCity("Matale");
        req.getCustomer().getAddress().setCountry("Sri Lanka");

//Optional Params
        req.getCustomer().getDeliveryAddress().setAddress("No.2, Kandy Road");
        req.getCustomer().getDeliveryAddress().setCity("Kadawatha");
        req.getCustomer().getDeliveryAddress().setCountry("Sri Lanka");
        req.getItems().add(new Item(null, "Door bell wireless", 1, 1000.0));


        Intent intent = new Intent(CheckoutActivity.this, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
        payHereLauncher.launch(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
            if (resultCode == Activity.RESULT_OK) {
                String msg;
                if (response != null){
                    if (response.isSuccess()) {
                        msg = "Activity result:" + response.getData().toString();
//                        saveOrderToFirestore();
                    }else{
                        msg = "Result:" + response.toString();}
            } else{
                    msg = "Result: no response";
                Log.d(TAG, msg);
            }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (response != null) {
                    Log.d(TAG, response.toString());

                } else{
                    Log.d(TAG, "request Canceld");
                }
            }
        }
    }
    private void saveOrderToFirestore() {
        CollectionReference ordersRef = firestore.collection("orders");
        WriteBatch batch = firestore.batch();

        // Create order object
        Map<String, Object> order = new HashMap<>();
        order.put("orderId", orderId);
        order.put("userId", userId);
        order.put("order_date", Timestamp.now());
        order.put("grand_total", grandTotal);

        // Save order in Firestore
        ordersRef.document(orderId).set(order)
                .addOnSuccessListener(aVoid -> {
                    updateProductStock();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save order!", Toast.LENGTH_SHORT).show());
    }

    private void updateProductStock() {
        firestore.collection("cart").document(userId).collection("items").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = firestore.batch();
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

                    for (QueryDocumentSnapshot cartItem : queryDocumentSnapshots) {
                        String productId = cartItem.getString("productId");
                        long purchasedQty = cartItem.getLong("qty");

                        Task<DocumentSnapshot> productTask = firestore.collection("products").document(productId).get();
                        tasks.add(productTask);

                        productTask.addOnSuccessListener(productDoc -> {
                            if (productDoc.exists()) {
                                long currentStock = productDoc.getLong("qty");
                                long newStock = Math.max(currentStock - purchasedQty, 0);

                                batch.update(firestore.collection("products").document(productId), "qty", newStock);
                            }
                        });
                    }

                    Tasks.whenAllComplete(tasks).addOnSuccessListener(taskList -> {
                        batch.commit()
                                .addOnSuccessListener(aVoid -> {
                                    clearCart();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update stock!", Toast.LENGTH_SHORT).show());
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to get cart items!", Toast.LENGTH_SHORT).show());
    }




    private void clearCart() {
        firestore.collection("cart").whereEqualTo("userId", userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = firestore.batch();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        batch.delete(document.getReference());
                    }

                    batch.commit();

                    Snackbar.make(findViewById(R.id.main), "Order Placed", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(CheckoutActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    }).show();
                });
    }


}