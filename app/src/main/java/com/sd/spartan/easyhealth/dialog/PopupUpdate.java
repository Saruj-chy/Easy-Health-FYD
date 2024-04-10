package com.sd.spartan.easyhealth.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sd.spartan.easyhealth.R;
@SuppressLint("StaticFieldLeak")
public class PopupUpdate {
    public static MaterialAlertDialogBuilder materialAlertDialogBuilder ;
    public static AlertDialog dialog ;
    private static String t, d ;
    public static TextView updateTitle, updateMsg, textUpdateNow, textRemindLater ;

    public static void UpdateDialog( Activity context, View.OnClickListener updateListener,
                                  View.OnClickListener remindListener ) {

        View view = context.getLayoutInflater().inflate(R.layout.popup_update,  null);
        updateTitle = view.findViewById(R.id.text_update_title) ;
        updateMsg = view.findViewById(R.id.text_update_msg) ;
        textUpdateNow = view.findViewById(R.id.text_update_now) ;
        textRemindLater = view.findViewById(R.id.text_remind_later) ;

        updateTitle.setText(t);
        updateMsg.setText(d);
        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_rounded) ;
        materialAlertDialogBuilder.setView(view);
        dialog = materialAlertDialogBuilder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        textUpdateNow.setOnClickListener(v -> {
            if(updateListener != null){
                updateListener.onClick(v);
            }
        });
        textRemindLater.setOnClickListener(v -> {
            if(remindListener != null){
                remindListener.onClick(v);
            }
        });




    }

    public static void setTitle(String title){
        t= title ;
    }
    public static void setDetail(String detail){
        d= detail ;
    }


}
