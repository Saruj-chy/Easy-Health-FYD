package com.sd.spartan.easyhealth.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sd.spartan.easyhealth.MainActivity;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.activity.MyDiagProfileFragActivity;
import com.sd.spartan.easyhealth.adapter.SerialNumberAdapter;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;
import com.sd.spartan.easyhealth.model.SerialNumModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class MyProfileFragment extends Fragment {
    private ConstraintLayout superiorConstraint, diagProConstraint, diagProEditConstraint, diagPassChangeConstraint, emailVerifyConstraint ;
    private LinearLayout websiteLL, serialEngLL, serialBanLL ;

    private TextView addEngTV, addBanTV, contactTV, emailTV, websiteTV, changePassTV, diagUserNameTV;
    private EditText addEngET, addBanET, contactET, emailET, websiteET, usernameET,  serialEngET, serialBanET, mCurrentPassET, mNewPassET, mConfirmPassET;
    private TextInputLayout text_user_name_verifyTIL;
    private TextInputEditText mVerifyUserNameET;
    private String contactETStr, emailETStr,websiteETStr="", usernameETStr, mCurrentPass, mNewPass, mConfirmPass;
    private TextView textBtnSaved, forgetPassBtnTV, changePassBtnTV, emailSentBtnTV;
    private ImageButton mEditBtn, mCancelBtn ;
    private RecyclerView diagSerialEngRV, diagSerialBanRV ;

    private List<SerialNumModel> mSerialList ;
    private SerialNumberAdapter serialNumberAdapter1, serialNumberAdapter2 ;
    private ProgressDialog loading ;

    private String diagId, regDiagId ;
    private List<String> mSerialEngList, mSerialBanList;
    private boolean dataInsert = true ;
    private final CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder() ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_my_profile, container, false);
        initialize(view);
        mSerialList = new ArrayList<>() ;
        loadAdapter();
        loadDiagnosticProfile(MyDiagProfileFragActivity.mDiagID, MyDiagProfileFragActivity.mSubDistrictID);


        mEditBtn.setOnClickListener(v -> {
            MyDiagProfileFragActivity.mProfileLayout = false;
            diagProConstraint.setVisibility(View.GONE);
            diagProEditConstraint.setVisibility(View.VISIBLE);
            mEditBtn.setVisibility(View.GONE);
            mCancelBtn.setVisibility(View.VISIBLE);

        });
        mCancelBtn.setOnClickListener(v -> CancelClickShow());
        changePassTV.setOnClickListener(v -> {
            MyDiagProfileFragActivity.mProfileLayout = false ;
            diagPassChangeConstraint.setVisibility(View.VISIBLE);
            diagProConstraint.setVisibility(View.GONE);
            diagProEditConstraint.setVisibility(View.GONE);
            emailVerifyConstraint.setVisibility(View.GONE);
            mEditBtn.setVisibility(View.GONE) ;
            mCancelBtn.setVisibility(View.VISIBLE) ;
        });
        addEngET.setOnClickListener(v -> ToastNotify.ToastLanguage(getActivity(), TOAST_CONTACT_AUTHOURITY_ENG, TOAST_CONTACT_AUTHOURITY_BAN ));
        addBanET.setOnClickListener(v -> ToastNotify.ToastLanguage(getActivity(), TOAST_CONTACT_AUTHOURITY_ENG, TOAST_CONTACT_AUTHOURITY_BAN ));
        textBtnSaved.setOnClickListener(v -> OnDiagProfileUpdateCheck());
        forgetPassBtnTV.setOnClickListener(v -> {
            MyDiagProfileFragActivity.mProfileLayout = false;
            emailVerifyConstraint.setVisibility(View.VISIBLE);
            diagProConstraint.setVisibility(View.GONE);
            diagProEditConstraint.setVisibility(View.GONE);
            diagPassChangeConstraint.setVisibility(View.GONE);
            mEditBtn.setVisibility(View.GONE);
            mCancelBtn.setVisibility(View.VISIBLE);
            mVerifyUserNameET.setText("");

        });
        changePassBtnTV.setOnClickListener(v -> onNewPasswordChange());

        builder.setToolbarColor(getResources().getColor(R.color.color_primary_1));
        builder.setShowTitle(true);
        websiteTV.setOnClickListener(v -> openCustomTabs(getActivity(), builder.build(), Uri.parse(websiteTV.getText().toString().trim()) ));
        websiteET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                websiteETStr = s.toString() ;
                if(!s.toString().startsWith(HTTP_URL)){
                    websiteET.setText(HTTP_URL);
                    Selection.setSelection(websiteET.getText(), websiteET.getText().length());
                }
            }
        });

        emailSentBtnTV.setOnClickListener(v -> OnEmailPassVerify());

        return view;
    }

    private void OnEmailPassVerify() {
        String verifyUsername = mVerifyUserNameET.getText().toString().trim() ;
        if(verifyUsername.equalsIgnoreCase("") ){
            text_user_name_verifyTIL.setError(ENTER_USER_NAME);
        }
        else if( verifyUsername.length() < 6 ){
            text_user_name_verifyTIL.setError(USER_NAME_LIMIT_MSG);
        }
        else{
            loading.setMessage(LOADING_PLZ_WAIT);
            loading.show();

            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
            dataMap.put(DIAG_USERNAME, verifyUsername);

            AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.VERIFY_DIAG_EMAIL_SENT, response -> {
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                        CancelClickShow();

                        String diag_email = object.getString(DIAG_EMAIL) ;
                        String emailSlice = diag_email.substring(0,3)+"***"+diag_email.substring( diag_email.length()-10) ;
                        ToastNotify.ShowToast(getContext(), PASS_SENT_EMAIL+ emailSlice);
                    }
                    ToastNotify.ShowToast(getContext(), object.getString(MSG));
                } catch (JSONException ignored) {
                }
                loading.dismiss();
            }, error -> ToastNotify.NetConnectionNotify(getContext()), dataMap ) ;
        }
    }

    private void onNewPasswordChange() {
        getAllTextStr() ;
        if(mCurrentPass.equalsIgnoreCase("") && mNewPass.equalsIgnoreCase("") && mConfirmPass.equalsIgnoreCase("") ){
            ToastNotify.ShowToast(getContext(), TOAST_FILL_UP_MSG );
        } else if(mCurrentPass.length() < 6 || mNewPass.length() < 6 ){
            ToastNotify.ShowToast(getContext(), PASS_LIMIT_MSG);
        }else if( !(mNewPass.equalsIgnoreCase(mConfirmPass)) ){
            ToastNotify.ShowToast(getContext(), CONFIRM_PASS_NOT_MATCH );
        }else{
            OnPasswordChange() ;
        }
    }
    private void OnPasswordChange() {
        loading.setTitle(LOADING_CHNG_PASS);
        loading.setMessage(LOADING_PLZ_WAIT);
        loading.show();

        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.NEW_UPDATE_DIAG_PROFILE,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            loadDiagnosticProfile(MyDiagProfileFragActivity.mDiagID, MyDiagProfileFragActivity.mSubDistrictID);
                            CancelClickShow();
                        }
                        ToastNotify.ShowToast(getContext(), object.getString(MSG));
                    } catch (JSONException ignored) {
                    }
                    loading.dismiss();
                }, error -> {
                    ToastNotify.NetConnectionNotify(getContext());
                    loading.dismiss();
                }, hashmapUpdate(String.valueOf(NUM_two)) ) ;
    }

    private void OnDiagProfileUpdateCheck() {
        dataInsert = true ;
        getAllTextStr() ;
        produceSerialNum() ;

        if((contactETStr.length() <10 || contactETStr.length() > 11) ){
            dataInsert = false ;
            ToastNotify.ShowToast(getContext(), VALID_PHN_NUM);
        }else if(usernameETStr.length()<6){
            dataInsert = false ;
            ToastNotify.ShowToast(getContext(), USER_NAME_LIMIT_MSG);
        }else if(emailETStr.equalsIgnoreCase("") || !emailETStr.matches(EMAIL_PATTERN)){
            dataInsert = false ;
            ToastNotify.ShowToast(getContext(), ENTER_VALID_EMAIL);
        }

        if(dataInsert){
            OnDiagProfileUpdate() ;
        }
    }
    private void OnDiagProfileUpdate() {
        loading.setTitle(LOADING_UPDATE_DATA);
        loading.setMessage(LOADING_PLZ_WAIT);
        loading.show();

        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.NEW_UPDATE_DIAG_PROFILE,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            loadDiagnosticProfile(MyDiagProfileFragActivity.mDiagID, MyDiagProfileFragActivity.mSubDistrictID);
                            CancelClickShow();
                        }else{
                            if(object.getString(USERNAME_SML).equalsIgnoreCase(ERROR)){
                                ToastNotify.ShowToast(getContext(), object.getString(MSG));
                            }
                        }
                    } catch (JSONException ignored) {
                    }
                    loading.dismiss();
                }, error -> {
                    ToastNotify.NetConnectionNotify(getContext());
                    loading.dismiss();
                }, hashmapUpdate(String.valueOf(NUM_ONE))) ;
    }
    private HashMap<String, String> hashmapUpdate(String diag_state_id) {
        if(websiteETStr.length()>7){
            websiteETStr = websiteETStr.substring(8) ;
        }

        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DIAG_ID, diagId);
        dataMap.put(REG_DIAG_ID, regDiagId);
        dataMap.put(DIAG_USERNAME, usernameETStr);
        dataMap.put(CURRENT_PASS, mCurrentPass);
        dataMap.put(NEW_PASS, mNewPass);
        dataMap.put(DIAG_CONTACT, contactETStr);
        dataMap.put(DIAG_WEB, websiteETStr);
        dataMap.put(DIAG_EMAIL, emailETStr);
        dataMap.put(DIAG_STATE_ID, diag_state_id);
        dataMap.put(SUBDISTRICT_ID, MainActivity.mSubDistrictIdAdmin);

        if(diag_state_id.equalsIgnoreCase(String.valueOf(NUM_ONE))){
            dataMap.put(DIAG_SER_NUM_ENG, mSerialEngList.toString());
            dataMap.put(DIAG_SER_NUM_BAN, mSerialBanList.toString());
        }
        return  dataMap;
    }

    public void openCustomTabs(FragmentActivity activity, CustomTabsIntent customTabsIntent, Uri uri){
        customTabsIntent.intent.setPackage(CHROME_PACKAGE);
        customTabsIntent.launchUrl(activity,uri);

    }

    private void CancelClickShow() {
        MyDiagProfileFragActivity.mProfileLayout = true ;
        diagProEditConstraint.setVisibility(View.GONE);
        diagProConstraint.setVisibility(View.VISIBLE);
        diagPassChangeConstraint.setVisibility(View.GONE);
        emailVerifyConstraint.setVisibility(View.GONE);
        mCancelBtn.setVisibility(View.GONE);
        mEditBtn.setVisibility(View.VISIBLE);

        mCurrentPassET.setText("");
        mNewPassET.setText("");
        mConfirmPassET.setText("");
        mVerifyUserNameET.setText("");
    }



    private void produceSerialNum() {
       String diagSerialEng = Objects.requireNonNull(serialEngET.getText()).toString().trim() ;
       String diagSerialBan = Objects.requireNonNull(serialBanET.getText()).toString().trim() ;

        mSerialEngList = new ArrayList<>();
        mSerialBanList = new ArrayList<>();
        if(diagSerialEng.length() > 1){
            if(diagSerialEng.length() > 9){
                List<String> ser_eng_items = Arrays.asList(diagSerialEng.split("\\s*,\\s*"));
                List<String> ser_ban_items = Arrays.asList(diagSerialBan.split("\\s*,\\s*"));
                if(ser_eng_items.size() == ser_ban_items.size() ){
                    for(int j=0; j<ser_eng_items.size(); j++){
                        if(ser_eng_items.get(j).length()>9 && ser_ban_items.get(j).length() >9){
                            mSerialEngList.add("\""+ser_eng_items.get(j)+"\"");
                            mSerialBanList.add("\""+ser_ban_items.get(j)+"\"");
                        }
                    }
                }else{
                    dataInsert = false ;
                    ToastNotify.ShowToast(getContext(), TOAST_SER_BAN_ENG_NOT_MATCH);
                }
            }else{
                dataInsert = false ;
                ToastNotify.ShowToast(getContext(), TOAST_PROVIDE_NUM);
            }
        }

    }

    private void getAllTextStr() {
        contactETStr = contactET.getText().toString().trim();
        emailETStr = emailET.getText().toString().trim();
        usernameETStr = usernameET.getText().toString().trim();

        mCurrentPass = mCurrentPassET.getText().toString().trim();
        mNewPass = mNewPassET.getText().toString().trim();
        mConfirmPass = mConfirmPassET.getText().toString().trim();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadAdapter() {
        serialNumberAdapter1 = new SerialNumberAdapter(getContext(), mSerialList);
        LinearLayoutManager serialNumLLM1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        diagSerialEngRV.setLayoutManager(serialNumLLM1);
        diagSerialEngRV.setAdapter(serialNumberAdapter1);
        serialNumberAdapter1.notifyDataSetChanged();

        serialNumberAdapter2 = new SerialNumberAdapter(getContext(), mSerialList);
        LinearLayoutManager serialNumLLM2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        diagSerialBanRV.setLayoutManager(serialNumLLM2);
        diagSerialBanRV.setAdapter(serialNumberAdapter2);
        serialNumberAdapter2.notifyDataSetChanged();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadDiagnosticProfile(String diagId, String subDistrictId) {
        mSerialList.clear();
        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.RETRIVE_DIAG_MY_PROFILE,
                response -> {
                    superiorConstraint.setVisibility(View.VISIBLE);
                    diagProConstraint.setVisibility(View.VISIBLE);
                    diagProEditConstraint.setVisibility(View.GONE);
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            JSONObject diag_object = object.getJSONObject(DIAG_BIO) ;
                            this.diagId = diag_object.getString(DIAG_ID) ;
                            regDiagId = diag_object.getString(REG_DIAG_ID) ;
                            String diag_address_eng = diag_object.getString(DIAG_ADDRESS_ENG)+", "+ diag_object.getString(SUBDISTRICT_TITLE_ENG)+", "+diag_object.getString(DISTRICT_TITLE_ENG)+", "+diag_object.getString(DIVISION_TITLE_ENG);
                            String diag_address_ban = diag_object.getString(DIAG_ADDRESS_BAN)+", "+ diag_object.getString(SUBDISTRICT_TITLE_BAN)+", "+diag_object.getString(DISTRICT_TITLE_BAN)+", "+diag_object.getString(DIVISION_TITLE_BAN);
                            String diagWebsite = diag_object.getString(DIAG_WEB) ;
                            String diag_contact = diag_object.getString(DIAG_CONTACT) ;
                            String diag_email = diag_object.getString(DIAG_EMAIL) ;
                            String diag_username = diag_object.getString(DIAG_USERNAME) ;

                            addBanTV.setText(diag_address_ban);
                            addBanET.setText(diag_address_ban);
                            addEngTV.setText(diag_address_eng);
                            addEngET.setText(diag_address_eng);
                            contactTV.setText(String.format("0%s", diag_contact));
                            contactET.setText(String.format("0%s", diag_contact));
                            emailTV.setText(diag_email);
                            emailET.setText(diag_email);
                            diagUserNameTV.setText(diag_username);
                            usernameET.setText(diag_username);

                            if(!diagWebsite.equalsIgnoreCase("")){
                                websiteLL.setVisibility(View.VISIBLE);
                                websiteTV.setText(String.format("%s%s", HTTP_URL, diagWebsite));
                                websiteET.setText(String.format("%s%s", HTTP_URL, diagWebsite));
                            }else{
                                websiteLL.setVisibility(View.GONE);
                            }

                            JSONArray ja_ser = object.getJSONArray(SER_LIST) ;
                            if(ja_ser.length()>0){
                                serialEngLL.setVisibility(View.VISIBLE);
                                serialBanLL.setVisibility(View.VISIBLE);
                            }else{
                                serialEngLL.setVisibility(View.GONE);
                                serialBanLL.setVisibility(View.GONE);
                            }
                            for(int j=0; j<ja_ser.length(); j++){
                                JSONObject JO = ja_ser.getJSONObject(j);
                                mSerialList.add(new SerialNumModel(
                                        JO.getString(DIAG_SER_ID),
                                        JO.getString(SER_NUM_BAN),
                                        JO.getString(SER_NUM_ENG),
                                        JO.getString(DIAG_ID)
                                ));
                            }
                            serialNumberAdapter1.notifyDataSetChanged();
                            serialNumberAdapter2.notifyDataSetChanged();
                            setDiagUpdateSerialNumber();

                        }
                    } catch (JSONException ignored) {
                    }
                }, error -> {
                    superiorConstraint.setVisibility(View.GONE);
                    ToastNotify.NetConnectionNotify(getContext());
                }, hashmap(diagId, subDistrictId)) ;
    }
    private HashMap<String, String> hashmap(String diagId, String subDistrictId) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DIAG_ID, diagId+"");
        dataMap.put(SUBDISTRICT_ID, subDistrictId+"");
        return  dataMap;
    }
    private void setDiagUpdateSerialNumber() {
        StringBuilder ser_eng = new StringBuilder();
        StringBuilder ser_ban = new StringBuilder();
        for (int i=0; i<mSerialList.size();i++){
            if(i==(mSerialList.size()-1)){
                ser_eng.append(mSerialList.get(i).getSerial_num_eng());
                ser_ban.append(mSerialList.get(i).getSerial_num_ban());
            }else{
                ser_eng.append(mSerialList.get(i).getSerial_num_eng()).append(",");
                ser_ban.append(mSerialList.get(i).getSerial_num_ban()).append(",");
            }
        }
        serialEngET.setText(ser_eng.toString());
        serialBanET.setText(ser_ban.toString());
    }

    private void initialize(View root) {
        superiorConstraint = root.findViewById( R.id.constraint_sup_pro );
        diagProConstraint = root.findViewById( R.id.constraint_diag_profile );
        diagProEditConstraint = root.findViewById( R.id.constraint_diag_profile_edit );
        diagPassChangeConstraint = root.findViewById( R.id.constraint_diag_change_password );
        emailVerifyConstraint = root.findViewById( R.id.constraint_email_verify );
        websiteLL = root.findViewById( R.id.linear_diag_website );
        serialEngLL = root.findViewById( R.id.linear_serial_num_eng );
        serialBanLL = root.findViewById( R.id.linear_serial_num_ban );

        addEngTV = root.findViewById( R.id.text_diag_address_eng );
        addBanTV = root.findViewById( R.id.text_diag_address_ban );
        contactTV = root.findViewById( R.id.text_diag_contact );
        emailTV = root.findViewById( R.id.text_diag_email );
        websiteTV = root.findViewById( R.id.text_diag_website );
        changePassTV = root.findViewById( R.id.text_change_password );
        diagUserNameTV = root.findViewById( R.id.text_diag_user_name_pro );

        addEngET = root.findViewById( R.id.edit_diag_add_eng );
        addBanET = root.findViewById( R.id.edit_diag_add_ban);
        contactET = root.findViewById( R.id.edit_diag_contact );
        emailET = root.findViewById( R.id.edit_diag_email );
        websiteET = root.findViewById( R.id.edit_diag_website );
        usernameET = root.findViewById( R.id.edit_diag_username );
        serialEngET = root.findViewById( R.id.edit_diag_serial_eng );
        serialBanET = root.findViewById( R.id.edit_diag_serial_ban );
        mEditBtn = root.findViewById( R.id.img_edit_btn);
        mCancelBtn = root.findViewById( R.id.img_editbtn_cancel );
        mCurrentPassET = root.findViewById(R.id.edit_old_password) ;
        mNewPassET = root.findViewById(R.id.edit_new_password) ;
        mConfirmPassET = root.findViewById(R.id.edit_confirm_password) ;
        mVerifyUserNameET = root.findViewById(R.id.edit_user_name_verify) ;
        text_user_name_verifyTIL = root.findViewById(R.id.text_user_name_verify) ;
        textBtnSaved = root.findViewById( R.id.text_btn_saved );
        forgetPassBtnTV = root.findViewById( R.id.text_forget_password );
        changePassBtnTV = root.findViewById( R.id.text_btn_change_password );
        emailSentBtnTV = root.findViewById( R.id.text_email_sent_btn );

        diagSerialEngRV = root.findViewById( R.id.recycler_diag_serial_num_eng );
        diagSerialBanRV = root.findViewById( R.id.recycler_diag_serial_num_ban );

        loading = new ProgressDialog(getContext());
    }


}