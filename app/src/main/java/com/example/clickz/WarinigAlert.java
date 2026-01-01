package com.example.clickz;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WarinigAlert {
    public static void showCustomAlert(Context context, String message) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.alert_layout, null, false);


        TextView textView3 = view.findViewById(R.id.textView7);
        textView3.setText(message);

        Button b3 = view.findViewById(R.id.button4);


        AlertDialog alertDialog = new AlertDialog.Builder(context).setView(view).show();
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

}
