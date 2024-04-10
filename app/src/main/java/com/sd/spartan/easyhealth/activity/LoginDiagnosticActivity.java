package com.sd.spartan.easyhealth.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.sd.spartan.easyhealth.MainActivity;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;
import com.sd.spartan.easyhealth.database.DatabaseHelperTest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class LoginDiagnosticActivity extends AppCompatActivity {
    private TextInputEditText mUserNameTET, mPassTET;
    private ConstraintLayout mLoginCL;
    private TextView mForgetPassMsgTV;
    private String mUserName, mPass;
    private ProgressDialog mProgressDialog;

    private ConstraintLayout mEmailVerifyCL;
    private TextInputEditText mVerifyUserNameTET;

    private boolean mLoginPageShow = true, mEmailSent = false ;
    private DatabaseHelperTest mDatabaseHelperTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_diagonostic);

        Initialize() ;
        mEmailVerifyCL.setVisibility(View.GONE);
        mLoginCL.setVisibility(View.VISIBLE);
    }

    private void Initialize() {
        mLoginCL = findViewById(R.id.constraint_login);
        mUserNameTET = findViewById(R.id.edit_diag_username);
        mPassTET = findViewById(R.id.edit_diag_pass);
        mForgetPassMsgTV = findViewById(R.id.text_forget_pass_msg) ;
        mEmailVerifyCL = findViewById(R.id.constraint_email_verify);
        mVerifyUserNameTET = findViewById(R.id.edit_user_name_verify);

        mProgressDialog = new ProgressDialog(this);
        mDatabaseHelperTest = new DatabaseHelperTest(this) ;
    }

    public void onRegDiagClick(View view) {
        startActivity(new Intent(getApplicationContext(), RegistrationDiagActivity.class));
    }

    public void onLoginDiagClick(View view) {
        mUserName = Objects.requireNonNull(mUserNameTET.getText()).toString().trim() ;
        mPass = Objects.requireNonNull(mPassTET.getText()).toString().trim() ;

        if(mUserName.equalsIgnoreCase("") || mPass.equalsIgnoreCase("") ) {
            ToastNotify.ShowToast(LoginDiagnosticActivity.this, TOAST_FILL_UP_MSG );
        }else if(mUserName.length()<6 || mPass.length()<6){
            ToastNotify.ShowToast(LoginDiagnosticActivity.this, TOAST_PROVIDE_DATA_MSG );
        } else{
            mProgressDialog.setMessage(LOADING_PLZ_WAIT);
            mProgressDialog.show();
            AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.LOGIN_DIAG,
                    response -> {
                        Log.e("Res", "res:--"+ response) ;
                        try {
                            JSONObject object = new JSONObject(response);
                            if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                                JSONObject jsonObject = object.getJSONObject(DATA);

                                getSharedPreferences(USER, MODE_PRIVATE).edit().putString(REG_DIAG_ID, jsonObject.getString(REG_DIAG_ID)).apply();
                                getSharedPreferences(USER, MODE_PRIVATE).edit().putString(DIAG_USERNAME, jsonObject.getString(DIAG_USERNAME)).apply();
                                getSharedPreferences(USER, MODE_PRIVATE).edit().putString(DIAG_PASS, jsonObject.getString(DIAG_PASS)).apply();
                                getSharedPreferences(USER, MODE_PRIVATE).edit().putString(DIAG_NAME_ENG, jsonObject.getString(DIAG_NAME_ENG)).apply();
                                getSharedPreferences(USER, MODE_PRIVATE).edit().putString(DIAG_NAME_BAN, jsonObject.getString(DIAG_NAME_BAN)).apply();
                                getSharedPreferences(USER, MODE_PRIVATE).edit().putString(ADMIN_STATUS, jsonObject.getString(ADMIN_STATUS)).apply();
                                getSharedPreferences(USER, MODE_PRIVATE).edit().putString(DIAG_ID, jsonObject.getString(DIAG_ID)).apply();
                                getSharedPreferences(USER, MODE_PRIVATE).edit().putString(SUBDISTRICT_ID, jsonObject.getString(SUBDISTRICT_ID)).apply();

                                String clientID = DIAG_CLIENT_ID+jsonObject.getString(REG_DIAG_ID) ;
                                AccessController.RetriveFavouriteSelect(LoginDiagnosticActivity.this, mDatabaseHelperTest, clientID);
                                onIntentMain();
                            }
                            ToastNotify.ShowToast(LoginDiagnosticActivity.this, object.getString("msg"));

                        } catch (JSONException ignored) {
                        }
                        mProgressDialog.dismiss();
                    }, error -> {
                        mProgressDialog.dismiss();
                        ToastNotify.NetConnectionNotify(LoginDiagnosticActivity.this);
                    }, hashmap() ) ;
        }
    }
    private HashMap<String, String> hashmap(){
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DIAG_USERNAME, mUserName);
        dataMap.put(DIAG_PASS, mPass);
        return  dataMap;
    }
    private void onIntentMain() {
        startActivity(new Intent(LoginDiagnosticActivity.this, MainActivity.class));
    }


    public void onEmailVerifyClick(View view) {
        String mVerifyUserName = Objects.requireNonNull(mVerifyUserNameTET.getText()).toString().trim();
        if(mVerifyUserName.equalsIgnoreCase("") ){
            mVerifyUserNameTET.setError(ENTER_USER_NAME);
        }
        else if( mVerifyUserName.length() < 6 ){
            mVerifyUserNameTET.setError(USER_NAME_LIMIT_MSG);
        }
        else{
            mProgressDialog.setMessage(LOADING_PLZ_WAIT);
            mProgressDialog.show();

            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
            dataMap.put(DIAG_USERNAME, mVerifyUserName);

            AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.VERIFY_DIAG_EMAIL_SENT,
                    response -> {
                        try {
                            JSONObject object = new JSONObject(response);
                            if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                                mEmailVerifyCL.setVisibility(View.GONE);
                                mLoginCL.setVisibility(View.VISIBLE);
                                mEmailSent = true ;
                                mLoginPageShow = true;

                                String diagEmail = object.getString(DIAG_EMAIL) ;
                                String emailSlice = diagEmail.substring(0,3)+"***"+diagEmail.substring( diagEmail.length()-10) ;
                                mForgetPassMsgTV.setText(String.format("%s%s", PASS_SENT_EMAIL, emailSlice));
                                mForgetPassMsgTV.setVisibility(View.VISIBLE);
                            }else{
                                mLoginPageShow = false;
                            }
                            ToastNotify.ShowToast(LoginDiagnosticActivity.this, object.getString(MSG));
                            if(mEmailSent){
                                mForgetPassMsgTV.setVisibility(View.VISIBLE);
                            }else{
                                mForgetPassMsgTV.setVisibility(View.GONE);
                            }

                        } catch (JSONException ignored) {
                        }
                        mProgressDialog.dismiss();
                    }, error -> {
                        mLoginPageShow = false;
                        mProgressDialog.dismiss();
                        ToastNotify.NetConnectionNotify(LoginDiagnosticActivity.this);
                    }, dataMap ) ;
        }

    }

    public void OnForgetPassClick(View view) {
        mLoginPageShow = false ;
        mEmailSent = false ;
        mVerifyUserNameTET.setText("");
        mEmailVerifyCL.setVisibility(View.VISIBLE);
        mLoginCL.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        if(mLoginPageShow){
            super.onBackPressed();
        }else{
            mLoginPageShow = true ;
            mEmailVerifyCL.setVisibility(View.GONE);
            mLoginCL.setVisibility(View.VISIBLE);
        }
        if(mEmailSent){
            mForgetPassMsgTV.setVisibility(View.VISIBLE);
        }else{
            mForgetPassMsgTV.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        mDatabaseHelperTest.close();
    }
}