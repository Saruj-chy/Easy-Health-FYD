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
import com.sd.spartan.easyhealth.adapter.DivisionAdapter;
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

public class DivisionActivity extends AppCompatActivity {
    private ConstraintLayout divisionConstraint ;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mDivisionRV;
    private ProgressBar mProgressBar ;

    private DivisionAdapter mDivisionAdapter ;
    private List<BuilderModel> mDivisionList ;
    private CountDownTimer countDownTimer;
    long remainingRefreshTime = 2000 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_division);

        Toolbar toolbar = findViewById(R.id.app_bar_division) ;
        setSupportActionBar(toolbar) ;
        ActionBar actionBar = getSupportActionBar() ;
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true) ;
        actionBar.setDisplayShowCustomEnabled(true) ;
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        initialize() ;
        ChangeByLanguage();
        mDivisionList = new ArrayList<>() ;

        loadDivisionAdapter() ;
        loadDivisionList();

        swipeRefreshLayout.setColorSchemeColors(Color.GREEN,Color.RED,Color.BLUE);
        swipeRefreshLayout.setOnRefreshListener(this::setAutoRefresh);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadDivisionAdapter() {
        mDivisionAdapter = new DivisionAdapter(DivisionActivity.this, mDivisionList ) ;
        mDivisionRV.setLayoutManager(new LinearLayoutManager(DivisionActivity.this,LinearLayoutManager.VERTICAL,false));
        mDivisionRV.setAdapter(mDivisionAdapter);
        mDivisionAdapter.notifyDataSetChanged();
    }

    private void setAutoRefresh(){
        if(remainingRefreshTime<=0){
            if(countDownTimer!= null){
                countDownTimer.cancel();
            }
            return;
        }
        if(countDownTimer == null){
            countDownTimer = new CountDownTimer(remainingRefreshTime, 2500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    divisionConstraint.setVisibility(View.GONE);
                    loadDivisionList() ;
                }

                @Override
                public void onFinish() {
                    divisionConstraint.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                    cancelAutoRefresh() ;
                }
            };

            countDownTimer.start() ;
        }
    }
    private void cancelAutoRefresh(){
        if(countDownTimer!= null){
            countDownTimer.cancel();
            countDownTimer=null;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadDivisionList() {
        mDivisionList.clear();
        AppAccess.getAppController().getAppNetworkController().makeRequest( AppConfig.DIVISION_URL,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            mDivisionRV.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                            JSONArray jsonArray = object.getJSONArray(DATA) ;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                mDivisionList.add(new ClassBuilder()
                                .setDivision_id(jsonObject.getString(DIVISION_ID))
                                .setDivision_eng(jsonObject.getString(DIVISION_TITLE_ENG))
                                .setDivision_ban(jsonObject.getString(DIVISION_TITLE_BAN))
                                .build()) ;
                            }
                            mDivisionAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException ignored) {
                    }
                }, error -> ToastNotify.NetConnectionNotify(DivisionActivity.this), hashmap()) ;

    }
    private HashMap<String, String> hashmap() {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        return  dataMap;
    }



    private void initialize() {
        mProgressBar = findViewById(R.id.progress_division) ;
        mDivisionRV = findViewById(R.id.recycler_division) ;
        swipeRefreshLayout = findViewById(R.id.swipe_division_frag) ;
        divisionConstraint = findViewById(R.id.constraint_division_frag) ;
    }
    private void ChangeByLanguage() {
        if(AppAccess.languageEng){
            setTitle(DIVISION_ENG) ;
        }else{
            setTitle(DIVISION_BAN) ;
        }
    }
}