package com.sd.spartan.easyhealth.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.sd.spartan.easyhealth.MainActivity;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Objects;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class UserProfileActivity extends AppCompatActivity {
    private RelativeLayout mUserProRL ;
    private ConstraintLayout mSuperProCL, mUserProCL, mUserEditProCL, mUserChangePassCL, mEmailVerifyCL;
    private SwipeRefreshLayout mSwipeRL;
    private TextView mUpdateNameTV, mUserNameProTV, mUserContactProTV, mUserEmailProTV, mUserAddressProTV;
    private EditText mUserNameEt, mUserPhnNumET, mUserEmailET, mOldPassET, mNewPassET, mConfirmPassET;
    private ImageButton mEditImgBtn, mCancelImgBtn;
    private TextInputEditText mPhnNumVerifyTIE;
    private boolean mProfileLayout = true ;
    private String mUsername, mUserPhnNumVerify, mContact, mEmail, mOldPass, mNewPass, mConfirmPass;
    private ProgressDialog mProgressDialog;
    private CountDownTimer mCountDownTimer;
    long mRemainingRefreshTime = 2000 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = findViewById(R.id.appbar_user_profile) ;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        setTitle(MainActivity.mUserNameAdmin +_PROFILE) ;
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        Initialize() ;
        LoadUserProfile() ;

        mSwipeRL.setColorSchemeColors(Color.BLUE,Color.RED,Color.DKGRAY);
        mSwipeRL.setOnRefreshListener(this::SetAutoRefresh);

    }
    private void SetAutoRefresh(){
        if(mRemainingRefreshTime <=0){
            if(mCountDownTimer != null){
                mCountDownTimer.cancel();
            }
            return;
        }
        if(mCountDownTimer == null){
            mCountDownTimer = new CountDownTimer(mRemainingRefreshTime, 2500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mUserProRL.setVisibility(View.GONE);
                    LoadUserProfile() ;
                }
                @Override
                public void onFinish() {
                    mUserProRL.setVisibility(View.VISIBLE);
                    mSwipeRL.setRefreshing(false);
                    CancelAutoRefresh() ;
                }
            };

            mCountDownTimer.start() ;
        }
    }
    private void CancelAutoRefresh(){
        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
            mCountDownTimer =null;
        }
    }

    private void LoadUserProfile() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        hashMap.put(REG_USER_ID, MainActivity.mRegUserIdAdmin);
        hashMap.put(SUBDISTRICT_ID, MainActivity.mSubDistrictIdAdmin);

        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.RETRIVE_USER_PROFILE,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            mSuperProCL.setVisibility(View.VISIBLE);
                            mUserProCL.setVisibility(View.VISIBLE);
                            mUserEditProCL.setVisibility(View.GONE);

                            JSONObject diagJO = object.getJSONObject(USER_BIO);

                            String userName = diagJO.getString(USER_NAME) ;
                            String userPhnNum = diagJO.getString(USER_PHN_NUM) ;
                            String userEmail = diagJO.getString(USER_EMAIL) ;
                            String subDistrictBan = diagJO.getString(SUBDISTRICT_TITLE_BAN) ;
                            String subDistrictEng = diagJO.getString(SUBDISTRICT_TITLE_ENG) ;
                            String district_ban = diagJO.getString(DISTRICT_TITLE_BAN) ;
                            String districtEng = diagJO.getString(DISTRICT_TITLE_ENG) ;
                            String divisionBan = diagJO.getString(DIVISION_TITLE_BAN) ;
                            String divisionEng = diagJO.getString(DIVISION_TITLE_ENG) ;


                            setTitle(userName+_PROFILE) ;
                            mUpdateNameTV.setText(R.string.profile_details);
                            mUserNameProTV.setText(userName);
                            mUserContactProTV.setText(String.format("%s%s", BD_PHN_CODE, userPhnNum));
                            mUserEmailProTV.setText(userEmail);

                            String addBan = subDistrictBan+", "+district_ban+", "+divisionBan ;
                            String addEng = subDistrictEng+", "+districtEng+", "+divisionEng ;
                            if(AppAccess.languageEng){
                                mUserAddressProTV.setText(addEng);
                            }else{
                                mUserAddressProTV.setText(addBan);
                            }

                            mUserNameEt.setText(userName);
                            mUserPhnNumET.setText(String.format("0%s", userPhnNum));
                            mUserEmailET.setText(userEmail);


                        }
                    } catch (JSONException ignored) {
                    }
                }, error -> {
                    mSuperProCL.setVisibility(View.GONE);
                    ToastNotify.NetConnectionNotify(UserProfileActivity.this);
                }, hashMap) ;
    }


    public void onEditClick(View view) {
        mProfileLayout = false ;
        mUpdateNameTV.setText(R.string.edit_profile);
        mUserProCL.setVisibility(View.GONE);
        mUserEditProCL.setVisibility(View.VISIBLE);
        mEmailVerifyCL.setVisibility(View.GONE);
        mEditImgBtn.setVisibility(View.GONE);
        mCancelImgBtn.setVisibility(View.VISIBLE);
    }
    public void onCancelClick(View view) {
        CancelAllChange() ;
    }
    private void CancelAllChange() {
        mProfileLayout = true ;
        mUpdateNameTV.setText(R.string.profile_details);
        mUserProCL.setVisibility(View.VISIBLE);
        mUserEditProCL.setVisibility(View.GONE);
        mUserChangePassCL.setVisibility(View.GONE);
        mEmailVerifyCL.setVisibility(View.GONE);
        mCancelImgBtn.setVisibility(View.GONE);
        mEditImgBtn.setVisibility(View.VISIBLE);

        mPhnNumVerifyTIE.setText("");
        mOldPassET.setText("");
        mNewPassET.setText("");
        mConfirmPassET.setText("");


    }

    public void OnUpdateUserProfile(View view) {
        GetAllUserText() ;
        if((mContact.length() <10 || mContact.length() > 11) ){
            ToastNotify.ShowToast(UserProfileActivity.this, VALID_PHN_NUM);
        }else if(mUsername.length()<5){
            ToastNotify.ShowToast(UserProfileActivity.this, USER_NAME_LIMIT_MSG);
        }else if(mEmail.equalsIgnoreCase("")){
            ToastNotify.ShowToast(UserProfileActivity.this, ENTER_EMAIL);
        }else if(!mEmail.matches(EMAIL_PATTERN)){
            ToastNotify.ShowToast(UserProfileActivity.this, INVALID_EMAIL);
        }else{
            OnUpdateUserProfile() ;
        }


    }

    private void OnUpdateUserProfile() {
        mProgressDialog.setTitle(LOADING_UPDATE_PRO);
        mProgressDialog.setMessage(LOADING_PLZ_WAIT);
        mProgressDialog.show();

        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.NEW_UPDATE_USER_PROFILE,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            CancelAllChange();
                            LoadUserProfile() ;
                            changeSharedPreferenceData();
                        }
                        ToastNotify.ShowToast(UserProfileActivity.this, object.getString(MSG) );
                    } catch (JSONException ignored) {
                    }
                    mProgressDialog.dismiss();
                }, error -> {
                    ToastNotify.NetConnectionNotify(UserProfileActivity.this);
                    mProgressDialog.dismiss();
                }, hashmapUpdate(String.valueOf(NUM_ONE))) ;
    }

    private void changeSharedPreferenceData() {
        getSharedPreferences(USER, MODE_PRIVATE).edit().putString(USER_NAME, mUsername).apply();
        getSharedPreferences(USER, MODE_PRIVATE).edit().putString(USER_PASS, mNewPass).apply();
        getSharedPreferences(USER, MODE_PRIVATE).edit().putString(USER_PHN_NUM, mContact).apply();
    }






    public void onChangePassword(View view) {
        mProfileLayout = false ;
        mUpdateNameTV.setText(R.string.pass_change);
        mUserChangePassCL.setVisibility(View.VISIBLE);
        mUserProCL.setVisibility(View.GONE);
        mUserEditProCL.setVisibility(View.GONE);
        mEditImgBtn.setVisibility(View.GONE);
        mCancelImgBtn.setVisibility(View.VISIBLE);

    }

    public void onForgetPasswordClick(View view) {
        mProfileLayout = false ;
        mUpdateNameTV.setText(R.string.forget_pass);
        mEmailVerifyCL.setVisibility(View.VISIBLE);
        mUserProCL.setVisibility(View.GONE);
        mUserEditProCL.setVisibility(View.GONE);
        mUserChangePassCL.setVisibility(View.GONE);
        mEditImgBtn.setVisibility(View.GONE);
        mCancelImgBtn.setVisibility(View.VISIBLE);
    }

    public void onNewPasswordChange(View view) {
        GetAllUserText() ;

        if(mOldPass.equalsIgnoreCase("") && mNewPass.equalsIgnoreCase("") && mConfirmPass.equalsIgnoreCase("") ){
            ToastNotify.ShowToast(UserProfileActivity.this, TOAST_FILL_UP_MSG);
        } else if(mOldPass.length() < 6 || mNewPass.length() < 6 ){
            ToastNotify.ShowToast(UserProfileActivity.this, PASS_LIMIT_MSG);
        }else if( !mNewPass.equalsIgnoreCase(mConfirmPass) ){
            ToastNotify.ShowToast(UserProfileActivity.this, CONFIRM_PASS_NOT_MATCH);
        } else{
            OnPasswordChange() ;
        }

    }
    private void OnPasswordChange() {
        mProgressDialog.setTitle(LOADING_CHNG_PASS);
        mProgressDialog.setMessage(LOADING_PLZ_WAIT);
        mProgressDialog.show();

        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.NEW_UPDATE_USER_PROFILE,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            CancelAllChange();
                            LoadUserProfile() ;
                            changeSharedPreferenceData();
                        }
                        ToastNotify.ShowToast(UserProfileActivity.this, object.getString(MSG));
                    } catch (JSONException ignored) {
                    }
                    mProgressDialog.dismiss();
                }, error -> {
                    ToastNotify.NetConnectionNotify(UserProfileActivity.this);
                    mProgressDialog.dismiss();
                }, hashmapUpdate(String.valueOf(NUM_two)) ) ;
    }


    private HashMap<String, String> hashmapUpdate(String user_state_id) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(REG_USER_ID, MainActivity.mRegUserIdAdmin);
        dataMap.put(USER_NAME, mUsername);
        dataMap.put(CURRENT_PASS, mOldPass);
        dataMap.put(NEW_PASS, mNewPass);
        dataMap.put(USER_PHN_NUM, mContact);
        dataMap.put(USER_EMAIL, mEmail);
        dataMap.put(SUBDISTRICT_ID, MainActivity.mSubDistrictIdAdmin);
        dataMap.put(USER_STATE_ID, user_state_id);
        return  dataMap;
    }

    public void onEmailVerifyClick(View view) {
        GetAllUserText() ;
        if(mUserPhnNumVerify.equalsIgnoreCase("") || mUserPhnNumVerify.length() <10 || mUserPhnNumVerify.length() >12 ){
            ToastNotify.ShowToast(UserProfileActivity.this, VALID_PHN_NUM);
        } else{
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
            dataMap.put(USER_PHN_NUM, mUserPhnNumVerify);

            AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.VERIFY_USER_EMAIL_SENT,
                    response -> {
                        try {
                            JSONObject object = new JSONObject(response);
                            ToastNotify.ShowToast(UserProfileActivity.this, object.getString(MSG));
                            if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                                CancelAllChange();

                                String user_email = object.getString(USER_EMAIL) ;
                                String emailSlice = user_email.substring(0,3)+"***"+user_email.substring( user_email.length()-10) ;
                                ToastNotify.ShowToast(UserProfileActivity.this, PASS_SENT_EMAIL+ emailSlice);

                            }else if(object.getString(SHOW).equalsIgnoreCase(EDIT_PRO)){
                                onEditClick(view);
                            }
                        } catch (JSONException ignored) {
                        }
                        mProgressDialog.dismiss();
                    }, error -> {
                        ToastNotify.NetConnectionNotify(UserProfileActivity.this);
                        mProgressDialog.dismiss();
                    }, dataMap ) ;
        }



    }
    private void GetAllUserText() {
        mUsername = mUserNameEt.getText().toString().trim() ;
        mContact = mUserPhnNumET.getText().toString().trim() ;
        mEmail = mUserEmailET.getText().toString().trim() ;

        mOldPass = mOldPassET.getText().toString().trim() ;
        mNewPass = mNewPassET.getText().toString().trim() ;
        mConfirmPass = mConfirmPassET.getText().toString().trim() ;

        mUserPhnNumVerify = Objects.requireNonNull(mPhnNumVerifyTIE.getText()).toString().trim() ;
    }

    private void Initialize() {
        mUserProRL = findViewById(R.id.relative_user_fullview) ;
        mSuperProCL = findViewById(R.id.constraint_sup_pro) ;
        mUserProCL = findViewById(R.id.constraint_user_profile) ;
        mUserEditProCL = findViewById(R.id.constraint_user_profile_edit) ;
        mUserChangePassCL = findViewById(R.id.constraint_user_change_password) ;
        mEmailVerifyCL = findViewById(R.id.constraint_email_verify) ;
        mSwipeRL = findViewById(R.id.swipe_user_pro) ;
        mUpdateNameTV = findViewById(R.id.text_update_name) ;
        mUserNameProTV = findViewById(R.id.text_user_name_pro) ;
        mUserContactProTV = findViewById(R.id.text_user_contact_pro) ;
        mUserEmailProTV = findViewById(R.id.text_user_email_pro) ;
        mUserAddressProTV = findViewById(R.id.text_user_address_pro) ;
        mUserNameEt = findViewById(R.id.edit_user_name) ;
        mUserPhnNumET = findViewById(R.id.edit_user_phnnum) ;
        mUserEmailET = findViewById(R.id.edit_user_email) ;
        mOldPassET = findViewById(R.id.edit_old_password) ;
        mNewPassET = findViewById(R.id.edit_new_password) ;
        mConfirmPassET = findViewById(R.id.edit_confirm_password) ;
        mEditImgBtn = findViewById( R.id.img_edit_btn);
        mCancelImgBtn = findViewById( R.id.img_editbtn_cancel );
        mPhnNumVerifyTIE = findViewById( R.id.edit_phn_num_verify);

        mProgressDialog = new ProgressDialog(UserProfileActivity.this);

    }
    @Override
    public void onBackPressed() {
        if(mProfileLayout){
            startActivity(new Intent(UserProfileActivity.this, MainActivity.class));
            CancelAutoRefresh();
        }else{
            CancelAllChange();
        }

    }
}