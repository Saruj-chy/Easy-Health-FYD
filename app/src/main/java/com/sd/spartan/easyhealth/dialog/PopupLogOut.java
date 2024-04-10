package com.sd.spartan.easyhealth.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sd.spartan.easyhealth.R;

public class PopupLogOut {
    public static MaterialAlertDialogBuilder materialAlertDialogBuilder ;
    public static AlertDialog backPressDialog ;

    public static void LogOut(Activity context, View.OnClickListener yesListener,
                                      View.OnClickListener rateListener,   View.OnClickListener noListener) {

        View view = context.getLayoutInflater().inflate(R.layout.popup_logout,  null);
        TextView textNo = view.findViewById(R.id.text_no) ;
        TextView textRateUs = view.findViewById(R.id.text_rate_us) ;
        TextView textYes = view.findViewById(R.id.text_yes) ;
        textRateUs.setVisibility(View.GONE);
        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_rounded) ;


        textNo.setOnClickListener(v -> {
            if(noListener != null){
                noListener.onClick(v);
            }
        });

        textYes.setOnClickListener(v -> {
            if(yesListener != null){
                yesListener.onClick(v);
            }
        });
        textRateUs.setOnClickListener(v -> {
            if(rateListener != null){
                rateListener.onClick(v);
            }
        });

        materialAlertDialogBuilder.setView(view);
        backPressDialog = materialAlertDialogBuilder.create();
        backPressDialog.show();
        backPressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

}
