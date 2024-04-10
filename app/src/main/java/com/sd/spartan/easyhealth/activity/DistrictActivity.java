package com.sd.spartan.easyhealth.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.adapter.DistrictAdapter;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;
import com.sd.spartan.easyhealth.model.BuilderModel;
import com.sd.spartan.easyhealth.model.ClassBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class DistrictActivity extends AppCompatActivity {
    private ConstraintLayout mDistrictCL;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mDistrictRV;
    private ProgressBar mProgressBar ;
    private List<BuilderModel> mDistrictList ;
    private DistrictAdapter mDistrictAdapter ;

    private String mDivisionId;
    private CountDownTimer mCountDownTimer;
    private final long mRemainingRefreshTime = 2000 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_district);

        Toolbar toolbar = findViewById(R.id.app_bar_district) ;
        setSupportActionBar(toolbar) ;
        ActionBar actionBar = getSupportActionBar() ;
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true) ;
        actionBar.setDisplayShowCustomEnabled(true) ;
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Initialize() ;
        mDistrictList = new ArrayList<>() ;
        ChangeByLanguage() ;



        if(getIntent().getStringExtra(DIVISION_ID) == null){
            mDivisionId = getSharedPreferences(USER, MODE_PRIVATE).getString(DIVISION_ID, "") ;
        }else{
            mDivisionId = getIntent().getStringExtra(DIVISION_ID).trim();
            getSharedPreferences(USER, MODE_PRIVATE).edit().putString(DIVISION_ID, mDivisionId).apply();
        }


        LoadAdapter() ;
        LoadDistrictList(mDivisionId);

        mSwipeRefreshLayout.setColorSchemeColors(Color.GREEN,Color.RED,Color.BLUE);
        mSwipeRefreshLayout.setOnRefreshListener(this::SetAutoRefresh);

    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadAdapter() {
        mDistrictAdapter = new DistrictAdapter(DistrictActivity.this, mDistrictList ) ;
        mDistrictRV.setLayoutManager(new LinearLayoutManager(DistrictActivity.this,LinearLayoutManager.VERTICAL,false));
        mDistrictRV.setAdapter(mDistrictAdapter);
        mDistrictAdapter.notifyDataSetChanged();
    }

    private void SetAutoRefresh(){
        if(mCountDownTimer == null){
            mCountDownTimer = new CountDownTimer(mRemainingRefreshTime, 2500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mDistrictCL.setVisibility(View.GONE);
                    LoadDistrictList(mDivisionId);
                }

                @Override
                public void onFinish() {
                    mDistrictCL.setVisibility(View.VISIBLE);
                    mSwipeRefreshLayout.setRefreshing(false);
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

    private void ChangeByLanguage() {
        if(AppAccess.languageEng){
            setTitle(DISTRICT_ENG) ;
        }else{
            setTitle(DISTRICT_BAN) ;
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private void LoadDistrictList(String division_id) {
        mDistrictList.clear();

        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DIVISION_ID, division_id);
        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.DISTRICT_URL,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            mDistrictRV.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);

                            JSONArray jsonArray = object.getJSONArray(DATA) ;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                mDistrictList.add(new ClassBuilder()
                                        .setDistrict_id(jsonObject.getString(DISTRICT_ID))
                                        .setDivision_id(jsonObject.getString(DIVISION_ID))
                                        .setDistrict_eng(jsonObject.getString(DISTRICT_TITLE_ENG))
                                        .setDistrict_ban(jsonObject.getString(DISTRICT_TITLE_BAN))
                                        .build());
                            }
                            mDistrictAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException ignored) {
                    }
                }, error -> ToastNotify.NetConnectionNotify(DistrictActivity.this), dataMap) ;

    }

    private void Initialize() {
        mDistrictRV = findViewById(R.id.recycler_district) ;
        mProgressBar = findViewById(R.id.progress_district) ;
        mSwipeRefreshLayout = findViewById(R.id.swipe_district) ;
        mDistrictCL = findViewById(R.id.constraint_district) ;
    }
}