package com.sd.spartan.easyhealth.dialog;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sd.spartan.easyhealth.R;

public class PopupPreCondition {
    public static MaterialAlertDialogBuilder materialAlertDialogBuilder ;
    public static AlertDialog dialog;
    public static String t, d ;

    public static void PreConditionDialog(Activity context) {

        View view = context.getLayoutInflater().inflate(R.layout.popup_precondition,  null);
        TextView textTitle = view.findViewById(R.id.text_title_popup) ;
        TextView textDetails = view.findViewById(R.id.text_details_popup) ;
        RelativeLayout bottomViewRelative = view.findViewById(R.id.relative_bottom_view) ;

        textTitle.setText(String.format("%s", t ));
        textDetails.setText(Html.fromHtml(d));
        bottomViewRelative.setVisibility(View.GONE);

        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_rounded) ;
        materialAlertDialogBuilder.setView(view);
        dialog = materialAlertDialogBuilder.create();
        dialog.show();

    }

    public static void setTitle(String title){
        t= title ;
    }
    public static void setDetail(String detail){
        d= detail ;
    }
}
