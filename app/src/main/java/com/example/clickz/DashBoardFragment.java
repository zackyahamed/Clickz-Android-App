package com.example.clickz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashBoardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView totalUsersTextView;
    private TextView todayOrdersTextView;
    private TextView todaySalesTextView;

    public DashBoardFragment() {
    }
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public static DashBoardFragment newInstance(String param1, String param2) {
        DashBoardFragment fragment = new DashBoardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dash_board, container, false);


        todaySalesTextView = view.findViewById(R.id.textView51);
         todayOrdersTextView = view.findViewById(R.id.textView53);
         totalUsersTextView = view.findViewById(R.id.textView55);


        fetchTodaySalesAndOrders();
        fetchTotalUsers();

        return view;

    }
    private void fetchTodaySalesAndOrders() {
        CollectionReference ordersRef = firestore.collection("orders");


        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.HOUR, -24);
        Date yesterday = calendar.getTime();

        ordersRef.whereGreaterThanOrEqualTo("order_date", new Timestamp(yesterday))
                .whereLessThanOrEqualTo("order_date", new Timestamp(now))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalSales = 0;
                    int orderCount = queryDocumentSnapshots.size();

                    for (var doc : queryDocumentSnapshots) {
                        Double orderTotal = doc.getDouble("grand_total");
                        if (orderTotal != null) {
                            totalSales += orderTotal;
                        }
                    }

                    todaySalesTextView.setText("LKR " + totalSales);
                    todayOrdersTextView.setText(String.valueOf(orderCount));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching sales/orders", e));
    }

    private void fetchTotalUsers() {
        CollectionReference usersRef = firestore.collection("user");

        usersRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int userCount = queryDocumentSnapshots.size();
                    totalUsersTextView.setText(String.valueOf(userCount));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching users count", e));
    }
}