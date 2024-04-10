package com.sd.spartan.easyhealth.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sd.spartan.easyhealth.MainActivity;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class RegistrationDiagActivity extends AppCompatActivity {
    private TextInputEditText mUserNameTET, mPassTET, mConfirmPassTET, mDiagNameBanTET, mDiagNameEngTET, mDiagAddBanTET, mDiagAddEngTET, mContactTET, mEmailTET;
    private TextView mDivisionTV, mDistrictTV, mSubDistrictTV ;
    private ProgressDialog mProgressDialog;

    private String mUserName, mPass, mConfirmPass, mDiagNameBan, mDiagNameEng, mDiagAddBan, mDiagAddEng, mContact, mEmail, mSubDistrictID;

    private ListView mSearchLV;
    private AlertDialog.Builder mDialogBuilder;
    private AlertDialog mAlertDialog;
    private ArrayAdapter mArrayAdapter;
    private ArrayList<String> mDivisionList  ;
    private ArrayList<String> mDistrictList ;
    private ArrayList<String> mDistrictIDList ;
    private ArrayList<String> mSubDistrictList  ;
    private ArrayList<String> mSubDistrictIDList ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_diag);

        Initialize() ;
        mDivisionList = new ArrayList<>() ;
        mDistrictList = new ArrayList<>() ;
        mDistrictIDList = new ArrayList<>() ;
        mSubDistrictList = new ArrayList<>() ;
        mSubDistrictIDList = new ArrayList<>() ;


        addTextChanger(mUserNameTET, USERNAME);
        addTextChanger(mPassTET, PASS);
        addTextChanger(mConfirmPassTET, Confirm_PASS);


    }

    private void addTextChanger(TextInputEditText textInputEditText, String name) {
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().length() < 6){
                    textInputEditText.setError(name+LIMIT_MSG);
                }
            }
        });
    }

    public void onLoginDiagClick(View view) {
        startActivity(new Intent(RegistrationDiagActivity.this, LoginDiagnosticActivity.class));
    }

    public void onDivisionClick(View view) {
        CreatePopupDialog(DIVISION) ;
    }
    public void onDistrictClick(View view) {
        String tagID = mDivisionTV.getTag()+"" ;
        if(tagID.equalsIgnoreCase(NULL)){
            ToastNotify.ShowToast(RegistrationDiagActivity.this, TOAST_SEL_DIV);
        }else{
            CreatePopupDialog(DISTRICT) ;
        }


    }
    public void onSubDistrictClick(View view) {
        String tagID = mDistrictTV.getTag()+"" ;
        if(tagID.equalsIgnoreCase(NULL)){
            ToastNotify.ShowToast(RegistrationDiagActivity.this, TOAST_SEL_DIS);
        }else{
            CreatePopupDialog(SUBDISTRICT) ;
        }

    }

    private void CreatePopupDialog(String name) {
        mDivisionList.clear();
        mDistrictList.clear();
        mSubDistrictList.clear();
        mSubDistrictIDList.clear();
        View view = getLayoutInflater().inflate(R.layout.layout_name, null);
        TextInputLayout mSearchTIL = view.findViewById(R.id.textI_search_Layout);
        TextInputEditText mSearchTIE = view.findViewById(R.id.edit_search);
        mSearchLV = view.findViewById(R.id.listview_popup);

        switch (name){
            case DIVISION:
                mSearchTIL.setVisibility(View.GONE);
                LoadDivisionList();
                mSearchLV.setOnItemClickListener((parent, view1, position, id) -> {
                    mDivisionTV.setText((String) parent.getItemAtPosition(position));
                    mDivisionTV.setTag(position+1);
                    mAlertDialog.cancel();

                    mDistrictTV.setText(TOAST_SEL_DIS);
                    mSubDistrictTV.setText(TOAST_SEL_SUB_DIS);
                    mSubDistrictTV.setTag(0);
                });

                break;
            case DISTRICT:
                mSearchTIL.setVisibility(View.GONE);
                LoadDistrictList();
                mSearchLV.setOnItemClickListener((parent, view12, position, id) -> {
                    mDistrictTV.setText((String) parent.getItemAtPosition(position));
                    mDistrictTV.setTag(Integer.valueOf(mDistrictIDList.get(position)));
                    mAlertDialog.cancel();

                    mSubDistrictTV.setText(TOAST_SEL_SUB_DIS);
                    mSubDistrictTV.setTag(0);
                });
                break;
            case SUBDISTRICT:
                LoadSUbDistrictList();
                mSearchLV.setOnItemClickListener((parent, view13, position, id) -> {
                    mSubDistrictTV.setText((String) parent.getItemAtPosition(position));
                    mSubDistrictTV.setTag(Integer.valueOf(mSubDistrictIDList.get(position)));
                    mAlertDialog.cancel();
                });

                mSearchTIE.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        mArrayAdapter.getFilter().filter(s.toString());
                    }
                });
                break;
            default:
                break;
        }



        mDialogBuilder.setView(view);
        mAlertDialog = mDialogBuilder.create();
        mAlertDialog.show();
    }

    private void LoadDivisionList() {
        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.DIVISION_URL,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            JSONArray grpArray = object.getJSONArray(DATA) ;
                            for(int i=0; i<grpArray.length(); i++){
                                JSONObject mGrpObject;
                                try {
                                    mGrpObject = grpArray.getJSONObject(i);
                                    if(AppAccess.languageEng){
                                        mDivisionList.add(mGrpObject.getString(DIVISION_TITLE_ENG)) ;
                                    }else{
                                        mDivisionList.add(mGrpObject.getString(DIVISION_TITLE_BAN)) ;
                                    }
                                } catch (JSONException ignored) {
                                }
                            }
                            mArrayAdapter = new ArrayAdapter<>(RegistrationDiagActivity.this,
                                    R.layout.layout_single_text, R.id.list_content, mDivisionList);
                            mSearchLV.setAdapter(mArrayAdapter);
                        }

                    } catch (JSONException ignored) {
                    }
                }, error -> {
                    mAlertDialog.cancel();
                    ToastNotify.NetConnectionNotify(RegistrationDiagActivity.this);
                },hashMap("", "") );
    }
    private void LoadDistrictList() {
        String division_id = mDivisionTV.getTag().toString() ;
        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.DISTRICT_URL, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                    mDistrictIDList.clear();
                    JSONArray grpArray = object.getJSONArray(DATA) ;
                    for(int i=0; i<grpArray.length(); i++){
                        JSONObject mGrpObject;
                        try {
                            mGrpObject = grpArray.getJSONObject(i);
                            if(AppAccess.languageEng){
                                mDistrictList.add(mGrpObject.getString(DISTRICT_TITLE_ENG)) ;
                            }else{
                                mDistrictList.add(mGrpObject.getString(DISTRICT_TITLE_BAN)) ;
                            }
                            mDistrictIDList.add(mGrpObject.getString(DISTRICT_ID)) ;
                        } catch (JSONException ignored) {
                        }
                    }
                    mArrayAdapter = new ArrayAdapter<>(RegistrationDiagActivity.this,
                            R.layout.layout_single_text, R.id.list_content, mDistrictList);
                    mSearchLV.setAdapter(mArrayAdapter);
                }

            } catch (JSONException ignored) {
            }
        }, error -> {
            mAlertDialog.cancel();
            ToastNotify.NetConnectionNotify(RegistrationDiagActivity.this);
        },hashMap(DIVISION_ID, division_id ) );

    }
    private void LoadSUbDistrictList() {
        String district_id = mDistrictTV.getTag().toString() ;
        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.SUBDISTRICT_URL, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                    JSONArray grpArray = object.getJSONArray(DATA) ;
                    for(int i=0; i<grpArray.length(); i++){
                        JSONObject mGrpObject;
                        try {
                            mGrpObject = grpArray.getJSONObject(i);
                            if(AppAccess.languageEng){
                                mSubDistrictList.add(mGrpObject.getString(SUBDISTRICT_TITLE_ENG)) ;
                            }else{
                                mSubDistrictList.add(mGrpObject.getString(SUBDISTRICT_TITLE_BAN)) ;
                            }
                            mSubDistrictIDList.add(mGrpObject.getString(SUBDISTRICT_ID)) ;
                        } catch (JSONException ignored) {
                        }
                    }
                    mArrayAdapter = new ArrayAdapter<>(RegistrationDiagActivity.this,
                            R.layout.layout_single_text, R.id.list_content, mSubDistrictList);
                    mSearchLV.setAdapter(mArrayAdapter);
                }

            } catch (JSONException ignored) {
            }
        }, error -> {
            mAlertDialog.cancel();
            ToastNotify.NetConnectionNotify(RegistrationDiagActivity.this);
        },hashMap(DISTRICT_ID, district_id ) );



    }
    private HashMap<String, String> hashMap(String name, String value) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(name, value);

        return  dataMap;
    }


    private void getAllData() {
        mUserName = Objects.requireNonNull(mUserNameTET.getText()).toString().trim();
        mPass = Objects.requireNonNull(mPassTET.getText()).toString().trim();
        mConfirmPass = Objects.requireNonNull(mConfirmPassTET.getText()).toString().trim();
        mDiagNameEng = Objects.requireNonNull(mDiagNameEngTET.getText()).toString().trim();
        mDiagNameBan = Objects.requireNonNull(mDiagNameBanTET.getText()).toString().trim();
        mDiagAddEng = Objects.requireNonNull(mDiagAddEngTET.getText()).toString().trim();
        mDiagAddBan = Objects.requireNonNull(mDiagAddBanTET.getText()).toString().trim();
        mContact = Objects.requireNonNull(mContactTET.getText()).toString().trim();
        mEmail = Objects.requireNonNull(mEmailTET.getText()).toString().trim();

        if(mSubDistrictTV.getTag() == null){
            ToastNotify.ShowToast(RegistrationDiagActivity.this, TOAST_SEL_ADDr);
        }else{
            mSubDistrictID = mSubDistrictTV.getTag().toString();
        }
    }
    public void onRegDiagClick(View view) {
        getAllData() ;
        if( mUserName.equalsIgnoreCase("") || mPass.equalsIgnoreCase("") || mConfirmPass.equalsIgnoreCase("") ||
                mDiagNameEng.equalsIgnoreCase("") || mDiagNameBan.equalsIgnoreCase("") || mDiagAddEng.equalsIgnoreCase("") ||
                mDiagAddBan.equalsIgnoreCase("") || mSubDistrictTV.getTag() == null || mSubDistrictTV.getTag().equals(0)  ){
            ToastNotify.ShowToast(RegistrationDiagActivity.this, TOAST_FILL_UP_MSG);
        }else if(mUserName.length()<6){
            mUserNameTET.setError(USER_NAME_LIMIT_MSG);
        }else if(mPass.length()<6){
            mPassTET.setError(PASS_LIMIT_MSG);
        } else if( !mPass.equalsIgnoreCase(mConfirmPass) ){
            mConfirmPassTET.setError(CONFIRM_PASS_NOT_MATCH);
        }else if(mContact.equalsIgnoreCase("") || mContact.length() <10 || mContact.length() >12){
            mContactTET.setError(VALID_PHN_NUM);
        }else if(!mEmail.matches(EMAIL_PATTERN)){
            mEmailTET.setError(INVALID_EMAIL);
        }
        else{
            OnInsertDiagData() ;
        }
    }

    private void OnInsertDiagData() {
        mProgressDialog.setTitle(LOADING_INSERT_DATA);
        mProgressDialog.setMessage(LOADING_PLZ_WAIT);
        mProgressDialog.show();


        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.REGISTRATION_DIAG,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equals(FALSE)){
                            getSharedPreferences(USER, MODE_PRIVATE).edit().putString(DIAG_ID, object.getString(DIAG_ID)).apply();
                            getSharedPreferences(USER, MODE_PRIVATE).edit().putString(DIAG_USERNAME, mUserName).apply();
                            getSharedPreferences(USER, MODE_PRIVATE).edit().putString(DIAG_PASS, mPass).apply();
                            getSharedPreferences(USER, MODE_PRIVATE).edit().putString(SUBDISTRICT_ID, mSubDistrictID).apply();
                            getSharedPreferences(USER, MODE_PRIVATE).edit().putString(DIAG_NAME_ENG, mDiagNameEng).apply();
                            getSharedPreferences(USER, MODE_PRIVATE).edit().putString(DIAG_NAME_BAN, mDiagNameBan).apply();
                            getSharedPreferences(USER, MODE_PRIVATE).edit().putString(ADMIN_STATUS, NO).apply();

                            onIntentMainActivity() ;
                            AccessController.PushVersion(RegistrationDiagActivity.this, DIAG_CLIENT_ID+object.getString(DIAG_ID), NUM_ONE );

                        }
                        ToastNotify.ShowToast(RegistrationDiagActivity.this,  object.getString(MSG));

                    } catch (JSONException ignored) {
                    }
                    mProgressDialog.dismiss();
                }, error -> {
                    mProgressDialog.dismiss();
                    ToastNotify.NetConnectionNotify(RegistrationDiagActivity.this);
                }, hashmap() ) ;
    }
    private HashMap<String, String> hashmap(){
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code) );
        dataMap.put(DIAG_USERNAME, mUserName);
        dataMap.put(DIAG_PASS, mPass);
        dataMap.put(DIAG_NAME_ENG, mDiagNameEng);
        dataMap.put(DIAG_NAME_BAN, mDiagNameBan);
        dataMap.put(DIAG_ADDRESS_ENG, mDiagAddEng);
        dataMap.put(DIAG_ADDRESS_BAN, mDiagAddBan);
        dataMap.put(DIAG_CONTACT, mContact);
        dataMap.put(DIAG_EMAIL, mEmail);
        dataMap.put(SUBDISTRICT_ID, mSubDistrictID);

        return  dataMap;
    }

    private void onIntentMainActivity() {
        startActivity(new Intent(RegistrationDiagActivity.this, MainActivity.class));
    }

    private void Initialize() {
        mUserNameTET = findViewById(R.id.edit_diag_username) ;
        mPassTET = findViewById(R.id.edit_diag_pass) ;
        mConfirmPassTET = findViewById(R.id.edit_diag_confirm_pass) ;
        mDiagNameBanTET = findViewById(R.id.edit_diag_nameban) ;
        mDiagNameEngTET = findViewById(R.id.edit_diag_nameeng) ;
        mDiagAddBanTET = findViewById(R.id.edit_diag_addressban) ;
        mDiagAddEngTET = findViewById(R.id.edit_diag_addresseng) ;
        mContactTET = findViewById(R.id.edit_diag_contact) ;
        mEmailTET = findViewById(R.id.edit_diag_email) ;

        mDivisionTV = findViewById(R.id.text_division);
        mDistrictTV = findViewById(R.id.text_district);
        mSubDistrictTV = findViewById(R.id.text_subdistrict);

        mProgressDialog = new ProgressDialog(this);
        mDialogBuilder = new AlertDialog.Builder(this);
    }
}