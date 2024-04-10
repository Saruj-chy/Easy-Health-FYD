package com.sd.spartan.easyhealth.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class MainViewActivity extends AppCompatActivity {
    private TextView mTitleSplash, mLanguageTV;
    private ImageButton mBackImgBtn, mForwardImgbtn;
    private ImageView mLogoImgView;
    private EditText mUserPhnET;

    private final String[] mTitleList = {FIND_DOC, NEAR_MEDI_CEN, MANAGE_SERVE} ;
    private final int[] mImageList = {R.drawable.doctor_01, R.drawable.hospital_01, R.drawable.services_01} ;
    private int mClickCount = 0;

    private MaterialAlertDialogBuilder mMaterialAlertDialogBuilder;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        Initialize() ;
        LanguageChange() ;

        ShowTitleDetails(mClickCount) ;
    }

    private void ShowTitleDetails(int pos) {
        mTitleSplash.setText(mTitleList[pos]);
        mLogoImgView.setImageResource(mImageList[pos]);
        if(mClickCount % 3 == 0){
            mBackImgBtn.setVisibility(View.INVISIBLE);
            mForwardImgbtn.setVisibility(View.VISIBLE);
        }else if(mClickCount % 3 == 2){
            mBackImgBtn.setVisibility(View.VISIBLE);
            mForwardImgbtn.setVisibility(View.INVISIBLE);
        }else{
            mBackImgBtn.setVisibility(View.VISIBLE);
            mForwardImgbtn.setVisibility(View.VISIBLE);
        }
    }

    private void Initialize() {
        mTitleSplash = findViewById(R.id.text_title_splash) ;
        mLanguageTV = findViewById(R.id.text_language) ;
        mBackImgBtn = findViewById(R.id.imgbtn_arraw_back) ;
        mForwardImgbtn = findViewById(R.id.img_btn_arraw_forward) ;
        mLogoImgView = findViewById(R.id.img_details) ;
        mUserPhnET = findViewById(R.id.edit_user_phn) ;

        mMaterialAlertDialogBuilder = new MaterialAlertDialogBuilder(MainViewActivity.this, R.style.MaterialAlertDialog_rounded) ;

    }

    public void forwardImgBtn(View view) {
        mClickCount++;

        int pos = (mClickCount %3) ;
        ShowTitleDetails(pos);
    }

    public void backwardImgBtn(View view) {
        if(mClickCount >0){
            mClickCount--;
        }
        int pos = (mClickCount %3) ;
        ShowTitleDetails(pos);
    }



    public void OnLoginDiag(View view) {
        if(AppAccess.preCondiDiagShow ){
            CreatePreConditionDialog( );
        }else{
            startActivity(new Intent(getApplicationContext(), LoginDiagnosticActivity.class));
        }
    }

    private void CreatePreConditionDialog() {
        View view = getLayoutInflater().inflate(R.layout.popup_precondition,  null);
        TextView textDetails = view.findViewById(R.id.text_details_popup) ;
        Button okBtn = view.findViewById(R.id.btn_ok_popup) ;
        Button cancelBtn = view.findViewById(R.id.btn_cancel_popup) ;

        textDetails.setText(Html.fromHtml(terms_and_conditions));

        okBtn.setOnClickListener(v -> {
            AppAccess.preCondiDiagShow = false ;
            getSharedPreferences(SHARED_CONDITION, MODE_PRIVATE).edit().putBoolean(PRE_CONDITION_DIAG, AppAccess.preCondiDiagShow).apply();
            startActivity(new Intent(getApplicationContext(), LoginDiagnosticActivity.class));
            mAlertDialog.cancel();
        });
        cancelBtn.setOnClickListener(v -> {
            moveTaskToBack(true) ;
            mAlertDialog.cancel();
        });

        mMaterialAlertDialogBuilder.setView(view);
        mAlertDialog = mMaterialAlertDialogBuilder.create();
        mAlertDialog.show();

    }

    public void OnLanguageClick(View view) {
        AppAccess.languageEng = !AppAccess.languageEng ;
        getSharedPreferences(USER, MODE_PRIVATE).edit().putBoolean(LAN_ENG,
                AppAccess.languageEng).apply();
        LanguageChange() ;
    }

    private void LanguageChange() {
        if(AppAccess.languageEng){
            mLanguageTV.setText(BANGLA);
        }else{
            mLanguageTV.setText(ENGLISH);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        AppAccess.languageEng = getSharedPreferences(USER, MODE_PRIVATE).getBoolean(LAN_ENG, false) ;
        LanguageChange() ;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true) ;
    }


    public void OnLoginProcess(View view) {
        AppAccess.userPhnNumber = mUserPhnET.getText().toString().trim() ;
        AppAccess.createUser = true ;
        if(AppAccess.userPhnNumber.equalsIgnoreCase("") ||
                AppAccess.userPhnNumber.length() <10 || AppAccess.userPhnNumber.length() >11){
            ToastNotify.ShowToast(this, VALID_PHN_NUM);
        }else{
            startActivity(new Intent(MainViewActivity.this, DivisionActivity.class));
        }

    }
}