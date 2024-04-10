package com.sd.spartan.easyhealth.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;

import com.sd.spartan.easyhealth.MainActivity;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.adapter.SubDistrictAdapter;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;
import com.sd.spartan.easyhealth.interfaces.CreateUserInterface;
import com.sd.spartan.easyhealth.model.BuilderModel;
import com.sd.spartan.easyhealth.model.ClassBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class SubDistrictActivity extends AppCompatActivity implements CreateUserInterface {
    private ConstraintLayout mSubDistrictCL;
    private SwipeRefreshLayout mSwipeRL;
    private RecyclerView mSubDistrictRV;
    private ProgressBar mProgressBar ;
    private List<BuilderModel> mSubDistrictList ;
    private SubDistrictAdapter mSubDistrictAdapter ;

    private String mDistrictId;
    private CountDownTimer mCountDownTimer;
    private final long mRemainingRefreshTime = 2000 ;
    private final long mCountIntervalTime = 2500 ;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_district);

        Toolbar toolbar = findViewById(R.id.app_bar_subdistrict) ;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ChangeByLanguage() ;
        Initialize() ;
        mSubDistrictList = new ArrayList<>() ;

        if(getIntent().getStringExtra(DISTRICT_ID) == null){
            mDistrictId = getSharedPreferences(USER, MODE_PRIVATE).getString(DISTRICT_ID, "") ;
        }else{
            mDistrictId = getIntent().getStringExtra(DISTRICT_ID).trim();
            getSharedPreferences(USER, MODE_PRIVATE).edit().putString(DISTRICT_ID, mDistrictId).apply();
        }

        LoadAdapter() ;
        LoadSubDistrictList(mDistrictId);

        mSwipeRL.setColorSchemeColors(Color.GREEN,Color.RED,Color.BLUE);
        mSwipeRL.setOnRefreshListener(this::SetAutoRefresh);
    }

    private void ChangeByLanguage() {
        if(AppAccess.languageEng){
            setTitle(SUBDISTRICT_ENG) ;
        }else{
            setTitle(SUBDISTRICT_BAN) ;
        }
    }
    private void SetAutoRefresh(){
        if(mCountDownTimer == null){
            mCountDownTimer = new CountDownTimer(mRemainingRefreshTime, mCountIntervalTime) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mSubDistrictCL.setVisibility(View.GONE);
                    LoadSubDistrictList(mDistrictId);
                }

                @Override
                public void onFinish() {
                    mSubDistrictCL.setVisibility(View.VISIBLE);
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

    @SuppressLint("NotifyDataSetChanged")
    private void LoadSubDistrictList(String district_id) {
        mSubDistrictList.clear();

        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DISTRICT_ID, district_id);
        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.SUBDISTRICT_URL,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            mSubDistrictRV.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                            JSONArray jsonArray = object.getJSONArray(DATA) ;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                mSubDistrictList.add(new ClassBuilder()
                                        .setSubdistrict_id(jsonObject.getString(SUBDISTRICT_ID))
                                        .setDistrict_id(jsonObject.getString(DISTRICT_ID))
                                        .setSubdistrict_eng(jsonObject.getString(SUBDISTRICT_TITLE_ENG))
                                        .setSubdistrict_ban(jsonObject.getString(SUBDISTRICT_TITLE_BAN))
                                        .build());
                            }
                            mSubDistrictAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException ignored) {
                    }
                }, error -> ToastNotify.NetConnectionNotify(SubDistrictActivity.this), dataMap) ;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadAdapter() {
        mSubDistrictAdapter = new SubDistrictAdapter(SubDistrictActivity.this, mSubDistrictList, this ) ;
        mSubDistrictRV.setLayoutManager(new LinearLayoutManager(SubDistrictActivity.this,LinearLayoutManager.VERTICAL,false));
        mSubDistrictRV.setAdapter(mSubDistrictAdapter);
        mSubDistrictAdapter.notifyDataSetChanged();
    }
    private void Initialize() {
        mProgressBar = findViewById(R.id.progress_subdistrict) ;
        mSubDistrictRV = findViewById(R.id.recycler_subdistrict) ;
        mSwipeRL = findViewById(R.id.swipe_subdistrict) ;
        mSubDistrictCL = findViewById(R.id.constraint_subdistrict) ;

        mProgressDialog = new ProgressDialog(this);

    }

    @Override
    public void CreateUser(String phnNumber, String subDistrictId) {
        CreateNewUser(phnNumber, subDistrictId) ;
    }
    private void CreateNewUser(String phnNumber, String subDistrictId) {
        mProgressDialog.setTitle(LOADING_NEW_ACC);
        mProgressDialog.setMessage(LOADING_PLZ_WAIT);
        mProgressDialog.show();

        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.REGISTRATION_NEW_USER,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            getSharedPreferences(USER, MODE_PRIVATE).edit().putString(REG_USER_ID, object.getString(REG_USER_ID) ).apply();
                            getSharedPreferences(USER, MODE_PRIVATE).edit().putString(USER_PHN_NUM, phnNumber).apply();
                            getSharedPreferences(USER, MODE_PRIVATE).edit().putString(SUBDISTRICT_ID, subDistrictId).apply();
                            onIntentMainActivity();

                            AccessController.PushVersion(SubDistrictActivity.this, USER_CLIENT_ID+object.getString(REG_USER_ID), NUM_ONE );

                        }
                        ToastNotify.ShowToast(SubDistrictActivity.this,object.getString(MSG));
                    } catch (JSONException ignored) {

                    }
                    mProgressDialog.dismiss();
                }, error -> {
                    mProgressDialog.dismiss();
                    ToastNotify.NetConnectionNotify(SubDistrictActivity.this);
                }, hashmap(phnNumber, subDistrictId)) ;
    }
    private HashMap<String, String> hashmap(String phnNumber, String subDistrictId){
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(REG_USER_ID, String.valueOf(NUM_ZERO));
        dataMap.put(USER_PHN_NUM, phnNumber);
        dataMap.put(SUBDISTRICT_ID, subDistrictId);
        return dataMap;
    }
    private void onIntentMainActivity() {
        startActivity(new Intent(SubDistrictActivity.this, MainActivity.class));
    }
}