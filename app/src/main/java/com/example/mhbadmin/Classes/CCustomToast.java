package com.example.mhbadmin.Classes;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mhbadmin.R;

public class CCustomToast {

    public void makeText(Context context, String sMessage) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;

        View layout = inflater.inflate(R.layout.custom_toast, null);
        LinearLayout linearLayout = layout.findViewById(R.id.layout_custom_toast);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(sMessage);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0,50);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
