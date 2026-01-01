package com.example.clickz;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SuccessAlert {
    public static void showSuccessAlert(Context context, String message) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.suucess_layout, null, false);


        TextView textView3 = view.findViewById(R.id.textView9);
        textView3.setText(message);

        Button b3 = view.findViewById(R.id.button5);


        AlertDialog alertDialog = new AlertDialog.Builder(context).setView(view).show();
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }
}
