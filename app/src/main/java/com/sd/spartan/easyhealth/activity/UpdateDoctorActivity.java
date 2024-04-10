package com.sd.spartan.easyhealth.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class UpdateDoctorActivity extends AppCompatActivity {
    private ConstraintLayout mConstUpdate ;
    private TextInputEditText mDocCodeTIET, mDocNameEngTIET, mDocNameBanTIET, mDegEngTIET, mDegBanTIET, mSpeNameEngTIET, mSpeNameBanTIET,
            mWorkPlaceEngTIET, mWorkPlaceBanTIET, mDocSerialNumEngTIET, mDocSerialNumBanTIET, mDocWebTIET, mVisitFeeTIET, mDocCmntBanTIET, mDocCmntEngTIET;
    private TextInputEditText docTimeDetailEngTIET, docTimeDetailBanTIET ;
    private RadioGroup mRadioGroup;
    private ProgressBar mUpdateProgress ;

    private String mDocCode;
    private String mDocNameEng;
    private String mDocNameBan;
    private String mDegEng;
    private String mDegBan;
    private String mWorkPlaceEng;
    private String mWorkPlaceBan;
    private String mDocWebsite;
    private String mVisitFee;
    private String mDocCmntBan;
    private String mDocCmntEng;
    private String mRadioStr, docTimeDetailEng, docTimeDetailBan;


    private List<String> mSerEngList , mSerBanList,  mSpeEngList , mSpeBanList ;


    private String mDocID, mDoctorCode, mDiagID, mSubDistrictID;
    private boolean mSerEqual = true ;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_doctor);

        Toolbar toolbar = findViewById(R.id.app_bar_doc_update) ;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(R.string.doc_pro_update) ;
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Initialize() ;

        mDocID = getIntent().getStringExtra(DOC_ID).trim() ;
        mDoctorCode = getIntent().getStringExtra(DOC_CODE).trim() ;
        mDiagID = getIntent().getStringExtra(DIAG_ID).trim() ;
        mSubDistrictID = getIntent().getStringExtra(SUBDISTRICT_ID).trim() ;

        LoadDocSpeSerList() ;

        mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = group.findViewById(checkedId);
            mRadioStr = (String) rb.getText() ;
        });


    }



    private void LoadDocSpeSerList() {
        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.RETRIVE_DOC_SPE_SER,
                response -> {
                    mUpdateProgress.setVisibility(View.GONE);
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            mConstUpdate.setVisibility(View.VISIBLE);

                            JSONObject data_JO = object.getJSONObject(DATA);
                            mDocCodeTIET.setText(data_JO.getString(DOC_CODE));

                            String objDocEng = data_JO.getString(DOC_NAME_ENG) ;
                            String objDocBan = data_JO.getString(DOC_NAME_BAN) ;
                            String subDocNameEng = objDocEng.substring(7) ;
                            String subDocNameBan = objDocBan.substring(8) ;

                            mDocNameEngTIET.setText(subDocNameEng);
                            mDocNameBanTIET.setText(subDocNameBan);
                            mDegEngTIET.setText(data_JO.getString(DEGREE_ENG));
                            mDegBanTIET.setText(data_JO.getString(DEGREE_BAN));
                            mWorkPlaceEngTIET.setText(data_JO.getString(WORK_PLACE_ENG));
                            mWorkPlaceBanTIET.setText(data_JO.getString(WORK_PLACE_BAN));

                            mDocWebTIET.setText(data_JO.getString(DOC_WEB));
                            mVisitFeeTIET.setText(data_JO.getString(DOC_VISIT_FEE));
                            mDocCmntEngTIET.setText(data_JO.getString(DOC_CMNT_ENG));
                            mDocCmntBanTIET.setText(data_JO.getString(DOC_CMNT_BAN));

                            String EXTRA_WEEK_TIME_eng = data_JO.getString(EXTRA_WEEK_TIME_ENG) ;
                            String EXTRA_WEEK_TIME_ban = data_JO.getString(EXTRA_WEEK_TIME_BAN) ;

                            String TIME_FROM_eng = data_JO.getString(TIME_FROM_ENG);
                            String TIME_to_eng = data_JO.getString(TIME_TO_ENG);
                            String TIME_FROM_ban = data_JO.getString(TIME_FROM_BAN);
                            String TIME_to_ban = data_JO.getString(TIME_TO_BAN);

                            String TIME_NAME_FROM_eng = data_JO.getString(TIME_NAME_FROM_ENG);
                            String TIME_NAME_FROM_ban = data_JO.getString(TIME_NAME_FROM_BAN);
                            String TIME_NAME_TO_eng = data_JO.getString(TIME_NAME_TO_ENG);
                            String TIME_NAME_TO_ban = data_JO.getString(TIME_NAME_TO_BAN);

                            String WEEK_NAME_eng = data_JO.getString(WEEK_NAME_ENG);
                            String WEEK_NAME_ban = data_JO.getString(WEEK_NAME_BAN);
                            String MONTH_NAME_eng = data_JO.getString(MONTH_NAME_ENG);
                            String MONTH_NAME_ban = data_JO.getString(MONTH_NAME_BAN);


                            String docTimeDetailBan = data_JO.getString(DOC_TIME_DETAIL_BAN);
                            String docTimeDetailEng = data_JO.getString(DOC_TIME_DETAIL_ENG);
                            if(docTimeDetailEng.equalsIgnoreCase("") && !TIME_FROM_eng.equalsIgnoreCase("") ){
                                StringBuilder scheduleEng = new StringBuilder() ;
                                if(!MONTH_NAME_eng.equalsIgnoreCase("")  ){
                                    scheduleEng.append("Every month ").append(MONTH_NAME_eng).append(" week ") ;
                                }
                                if(!WEEK_NAME_eng.equalsIgnoreCase("")){
                                    scheduleEng.append("every ").append(WEEK_NAME_eng) ;
                                }

                                StringBuilder timeString1 = new StringBuilder().append(scheduleEng.toString())
                                        .append(" From ").append(TIME_FROM_eng)
                                        .append(" ").append(TIME_NAME_FROM_eng).append(" to ")
                                        .append(TIME_to_eng).append(" ")
                                        .append(TIME_NAME_TO_eng).append(" ")
                                        ;
                                if( !EXTRA_WEEK_TIME_eng.equalsIgnoreCase("") ){
                                    timeString1.append("  and ").append(EXTRA_WEEK_TIME_eng) ;
                                }
                                docTimeDetailEngTIET.setText(timeString1.toString() );
                            }
                            else{
                                docTimeDetailEngTIET.setText(docTimeDetailEng);
                            }
                            if(docTimeDetailBan.equalsIgnoreCase("") && !TIME_FROM_ban.equalsIgnoreCase("")){
                                StringBuilder scheduleBan = new StringBuilder() ;
                                if(!MONTH_NAME_ban.equalsIgnoreCase("")  ){
                                    scheduleBan.append("প্রতি মাসের ").append(MONTH_NAME_ban).append(" সপ্তাহ ") ;
                                }
                                if(!WEEK_NAME_ban.equalsIgnoreCase("")){
                                    scheduleBan.append("প্রতি ").append(WEEK_NAME_ban) ;
                                }else{
                                    scheduleBan.append("প্রতিদিন ") ;
                                }

                                StringBuilder timeString2 = new StringBuilder().append(scheduleBan.toString()).append(" ").append(TIME_NAME_FROM_ban).append(" ").append(TIME_FROM_ban).append(" ঘটিকা হতে ")
                                        .append(TIME_NAME_TO_ban).append(" ").append(TIME_to_ban).append(" ঘটিকা");
                                if( !EXTRA_WEEK_TIME_ban.equalsIgnoreCase("") ){
                                    timeString2.append(" এবং ").append(EXTRA_WEEK_TIME_ban) ;
                                }
                                docTimeDetailBanTIET.setText(timeString2.toString() );
                            }
                            else{
                                docTimeDetailBanTIET.setText(docTimeDetailBan);
                            }


                            if(data_JO.getString(DOC_PRESENT).equalsIgnoreCase(NO)){
                                mRadioGroup.check(R.id.radio_btn_02);
                            }else{
                                mRadioGroup.check(R.id.radio_btn_01);
                            }

                            JSONArray ja_spe = data_JO.getJSONArray(SPE_LIST);
                            if(ja_spe.length()>0){
                                StringBuilder spe_ban = new StringBuilder();
                                StringBuilder spe_eng = new StringBuilder();
                                for (int i = 0; i < ja_spe.length(); i++) {
                                    JSONObject jsonObject = ja_spe.getJSONObject(i);
                                    String speBanName =  jsonObject.getString(SPE_NAME_BAN) ;
                                    String speEngName =  jsonObject.getString(SPE_NAME_ENG) ;
                                    if(i==0 ){
                                        if(!speBanName.equalsIgnoreCase("")){
                                            spe_ban.append(speBanName);
                                        }
                                        if(!speEngName.equalsIgnoreCase("")){
                                            spe_eng.append(speEngName);
                                        }
                                    }else{
                                        if(!speBanName.equalsIgnoreCase("")){
                                            spe_ban.append(", ").append(speBanName);
                                        }
                                        if(!speEngName.equalsIgnoreCase("")){
                                            spe_eng.append(", ").append(speEngName);
                                        }
                                    }
                                }
                                mSpeNameBanTIET.setText(spe_ban.toString());
                                mSpeNameEngTIET.setText(spe_eng.toString());
                            }

                            JSONArray ja_ser = data_JO.getJSONArray(SER_LIST);
                            if(ja_ser.length()>0){
                                StringBuilder ser_ban = new StringBuilder();
                                StringBuilder ser_eng = new StringBuilder();
                                for (int i = 0; i < ja_ser.length(); i++) {
                                    JSONObject jsonObject = ja_ser.getJSONObject(i);
                                    if(i== ja_ser.length()-1){
                                        ser_ban.append(jsonObject.getString(SER_NUM_BAN)).append("");
                                        ser_eng.append(jsonObject.getString(SER_NUM_ENG)).append("");
                                    }else{
                                        ser_ban.append(jsonObject.getString(SER_NUM_BAN)).append(", ");
                                        ser_eng.append(jsonObject.getString(SER_NUM_ENG)).append(", ");
                                    }
                                }
                                mDocSerialNumBanTIET.setText(ser_ban.toString());
                                mDocSerialNumEngTIET.setText(ser_eng.toString());
                            }


                            String docDiagTotalCount = data_JO.getString(DOC_DIAG_TOTAL_COUNT) ;
                            if(Integer.parseInt(docDiagTotalCount)>1){
                                mDocCodeTIET.setFocusable(false);
                            }


                        }
                    } catch (JSONException ignored) {
                    }
                }, error -> ToastNotify.NetConnectionNotify(UpdateDoctorActivity.this), hashmap()) ;
    }

    private HashMap<String, String> hashmap() {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DOC_CODE, mDoctorCode);
        dataMap.put(DIAG_ID, mDiagID);
        return dataMap;
    }

    private void Initialize() {
        mDocCodeTIET = findViewById(R.id.edit_doc_code);
        mDocNameEngTIET = findViewById(R.id.edit_doc_name_eng) ;
        mDocNameBanTIET = findViewById(R.id.edit_doc_name_ban) ;
        mDegEngTIET = findViewById(R.id.edit_degree_eng) ;
        mDegBanTIET = findViewById(R.id.edit_degree_ban) ;
        mSpeNameEngTIET = findViewById(R.id.edit_specialist_name_eng) ;
        mSpeNameBanTIET = findViewById(R.id.edit_specialist_name_ban) ;
        mWorkPlaceEngTIET = findViewById(R.id.edit_work_place_eng) ;
        mWorkPlaceBanTIET = findViewById(R.id.edit_work_place_ban) ;
        docTimeDetailEngTIET = findViewById(R.id.edit_doc_time_details_eng) ;
        docTimeDetailBanTIET = findViewById(R.id.edit_doc_time_details_ban) ;
        mDocSerialNumEngTIET = findViewById(R.id.edit_doc_serial_num_eng) ;
        mDocSerialNumBanTIET = findViewById(R.id.edit_doc_serial_num_ban) ;
        mDocWebTIET = findViewById(R.id.edit_doc_website) ;
        mVisitFeeTIET = findViewById(R.id.edit_visit_fee) ;
        mDocCmntBanTIET = findViewById(R.id.edit_doc_cmnt_ban) ;
        mDocCmntEngTIET = findViewById(R.id.edit_doc_cmnt_eng) ;
        mRadioGroup = findViewById(R.id.radio_grp_doc_present) ;
        mConstUpdate = findViewById(R.id.constraint_update_main) ;
        mUpdateProgress = findViewById(R.id.progress_update) ;



        mSerEngList = new ArrayList<>() ;
        mSerBanList = new ArrayList<>() ;
        mSpeEngList = new ArrayList<>() ;
        mSpeBanList = new ArrayList<>() ;
        mProgressDialog = new ProgressDialog(this);
    }

    public void onInsertClick(View view) {
        mProgressDialog.setTitle(LOADING_UPDATE_DATA);
        mProgressDialog.setMessage(LOADING_PLZ_WAIT);
        mProgressDialog.show();

        GetAllText() ;
        if(docTimeDetailBan.equalsIgnoreCase("") || docTimeDetailEng.equalsIgnoreCase("")){
            mProgressDialog.dismiss();
            ToastNotify.ShowToast(this, "Fill up both doctor schedule");
        } else if(mSerEqual ){
            AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.UPDATE_DOCTOR_SINGLE_DATA,
                    response -> {
                        try {
                            JSONObject object = new JSONObject(response);
                            ToastNotify.ShowToast(UpdateDoctorActivity.this, object.getString(STATE));
                            if (object.getString(ERROR).equalsIgnoreCase(FALSE)) {
                                onBackPressed();
                            }

                        } catch (JSONException ignored) {}
                        mProgressDialog.dismiss();
                    }, error -> {
                ToastNotify.NetConnectionNotify(UpdateDoctorActivity.this);
                mProgressDialog.dismiss();
            }, hashmapUpdate() ) ;
        }else{
            ToastNotify.ToastLanguage(UpdateDoctorActivity.this, CHECK_Empty_ENG, CHECK_Empty_BAN);
            mProgressDialog.dismiss();
        }

    }
    private HashMap<String, String> hashmapUpdate(){
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code) );
        dataMap.put(DOC_ID, mDocID);
        dataMap.put(DIAG_ID, mDiagID);
        dataMap.put(DOC_CODE, mDocCode);
        dataMap.put(DOC_TIME_DETAIL_ENG, docTimeDetailEng);
        dataMap.put(DOC_TIME_DETAIL_BAN, docTimeDetailBan);

        dataMap.put(DOC_NAME_ENG, mDocNameEng);
        dataMap.put(DOC_NAME_BAN, mDocNameBan);
        dataMap.put(DEGREE_ENG, mDegEng);
        dataMap.put(DEGREE_BAN, mDegBan);
        dataMap.put(WORK_PLACE_ENG, mWorkPlaceEng);
        dataMap.put(WORK_PLACE_BAN, mWorkPlaceBan);
        dataMap.put(SPE_NAME_ENG, mSpeEngList.toString() );
        dataMap.put(SPE_NAME_BAN, mSpeBanList.toString() );
        dataMap.put(DOC_SER_NUM_ENG, mSerEngList.toString() );
        dataMap.put(DOC_SER_NUM_BAN, mSerBanList.toString() );
        dataMap.put(DOC_WEB, mDocWebsite);
        dataMap.put(DOC_VISIT_FEE, mVisitFee);
        dataMap.put(DOC_CMNT_ENG, mDocCmntEng);
        dataMap.put(DOC_CMNT_BAN, mDocCmntBan);
        dataMap.put(DOC_PRESENT, mRadioStr);
        dataMap.put(SUBDISTRICT_ID, mSubDistrictID);

        return  dataMap;
    }



    private void GetAllText() {
        mDocCode = Objects.requireNonNull(mDocCodeTIET.getText()).toString().trim() ;
        mDocNameEng = DOCTOR_ENG+ Objects.requireNonNull(mDocNameEngTIET.getText()).toString().trim() ;
        mDocNameBan = DOCTOR_BAN+ Objects.requireNonNull(mDocNameBanTIET.getText()).toString().trim() ;
        mDegEng = Objects.requireNonNull(mDegEngTIET.getText()).toString().trim() ;
        mDegBan = Objects.requireNonNull(mDegBanTIET.getText()).toString().trim() ;
        mWorkPlaceEng = Objects.requireNonNull(mWorkPlaceEngTIET.getText()).toString().trim() ;
        mWorkPlaceBan = Objects.requireNonNull(mWorkPlaceBanTIET.getText()).toString().trim() ;
        docTimeDetailEng = Objects.requireNonNull(docTimeDetailEngTIET.getText()).toString().trim() ;
        docTimeDetailBan = Objects.requireNonNull(docTimeDetailBanTIET.getText()).toString().trim() ;
        String mDocSerNumEng = Objects.requireNonNull(mDocSerialNumEngTIET.getText()).toString().trim();
        String mDocSerNumBan = Objects.requireNonNull(mDocSerialNumBanTIET.getText()).toString().trim();
        String mSpeNameEng = Objects.requireNonNull(mSpeNameEngTIET.getText()).toString().trim();
        String mSpeNameBan = Objects.requireNonNull(mSpeNameBanTIET.getText()).toString().trim();
        mDocWebsite = Objects.requireNonNull(mDocWebTIET.getText()).toString().trim() ;
        mVisitFee = Objects.requireNonNull(mVisitFeeTIET.getText()).toString().trim() ;
        mDocCmntBan = Objects.requireNonNull(mDocCmntBanTIET.getText()).toString().trim() ;
        mDocCmntEng = Objects.requireNonNull(mDocCmntEngTIET.getText()).toString().trim() ;


        if(mDocSerNumEng.length() > 9){
            List<String> ser_eng_items = Arrays.asList(mDocSerNumEng.split(REGEX));
            List<String> ser_ban_items = Arrays.asList(mDocSerNumBan.split(REGEX));
            if(ser_eng_items.size() == ser_ban_items.size() ){
                mSerEqual = true ;
                for(int j=0; j<ser_eng_items.size(); j++){
                    if(ser_eng_items.get(j).length()>9 && ser_ban_items.get(j).length() >9){
                        mSerEngList.add("\""+ser_eng_items.get(j)+"\"");
                        mSerBanList.add("\""+ser_ban_items.get(j)+"\"");
                    }
                }
            }else{
                ToastNotify.ShowToast(UpdateDoctorActivity.this, TOAST_SER_BAN_ENG_NOT_MATCH);
                mSerEqual = false ;
            }
        }


        if(mSpeNameEng.length() > 2){
            List<String> spe_eng_items = Arrays.asList(mSpeNameEng.split(REGEX));
            List<String> spe_ban_items = Arrays.asList(mSpeNameBan.split(REGEX));

            List<String> spe_eng_itemsList = new ArrayList<>() ;
            List<String> spe_ban_itemsList = new ArrayList<>() ;
            for(int j=0; j<spe_eng_items.size(); j++){
                if(!spe_eng_items.get(j).equalsIgnoreCase("")){
                    spe_eng_itemsList.add(spe_eng_items.get(j).trim()) ;
                }
            }
            for(int j=0; j<spe_ban_items.size(); j++){
                if(!spe_ban_items.get(j).equalsIgnoreCase("")){
                    spe_ban_itemsList.add(spe_ban_items.get(j).trim()) ;
                }
            }

            int totalSize = Math.max(spe_eng_itemsList.size(), spe_ban_itemsList.size()) ;
            for(int j=0; j<totalSize; j++){
                String speEng;
                String speBan;
                if(spe_eng_itemsList.size()<= j ){
                    speEng = "" ;
                }else{
                    speEng = spe_eng_itemsList.get(j) ;
                }
                if(spe_ban_itemsList.size() <= j){
                    speBan = "" ;
                }else{
                    speBan = spe_ban_itemsList.get(j) ;
                }
                mSpeEngList.add("\""+speEng+"\"");
                mSpeBanList.add("\""+speBan+"\"");
            }

        }
    }
}