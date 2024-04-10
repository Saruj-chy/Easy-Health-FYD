package com.sd.spartan.easyhealth.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sd.spartan.easyhealth.R;

public class PopupAlert {
    public static MaterialAlertDialogBuilder materialAlertDialogBuilder ;
    public static AlertDialog dialog ;
    public static String t, d ;

    public static void AlertDialog(Activity context) {

        View view = context.getLayoutInflater().inflate(R.layout.popup_alert,  null);
        TextView alertTitle = view.findViewById(R.id.text_alert_title) ;
        TextView alertMsg = view.findViewById(R.id.text_alert_msg) ;

        alertTitle.setText(t);
        alertMsg.setText(d);

        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_rounded) ;
        materialAlertDialogBuilder.setView(view);
        dialog = materialAlertDialogBuilder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);



    }

    public static void setTitle(String title){
        t= title ;
    }
    public static void setDetail(String detail){
        d= detail ;
    }




}
