package com.sd.spartan.easyhealth.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import com.sd.spartan.easyhealth.MainActivity;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.adapter.TutorialsAdapter;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;
import com.sd.spartan.easyhealth.interfaces.OnGoogleInterface;
import com.sd.spartan.easyhealth.model.BuilderModel;
import com.sd.spartan.easyhealth.model.ClassBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class TutorialsListActivity extends AppCompatActivity implements OnGoogleInterface {

    private RecyclerView mTutorialsRV;
    private SwipeRefreshLayout mSwipeRL;

    private TutorialsAdapter mTutorialsAdapter;
    private List<BuilderModel> mTutorialsList;
    private CustomTabsIntent.Builder mBuilder ;
    private CountDownTimer mCountDownTimer;
    long mRemainingRefreshTime = 2000, mCountIntervalTime = 2500 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorials_list);

        Toolbar toolbar = findViewById(R.id.appbar_tutorials_list) ;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(R.string.tutorials_list) ;
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        mTutorialsRV = findViewById(R.id.recycler_tutorials) ;
        mSwipeRL = findViewById(R.id.swipe_tutorials) ;

        mTutorialsList = new ArrayList<>() ;
        LoadAdapter();
        LoadGuideTutorialsList();

        mBuilder = new CustomTabsIntent.Builder() ;
        mBuilder.setToolbarColor(getResources().getColor(R.color.color_primary_1));
        mBuilder.setShowTitle(true);

        mSwipeRL.setColorSchemeColors(Color.BLUE, Color.RED,Color.DKGRAY);
        mSwipeRL.setOnRefreshListener(this::SetAutoRefresh);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadAdapter() {
        mTutorialsAdapter = new TutorialsAdapter(TutorialsListActivity.this, mTutorialsList, this ) ;
        mTutorialsRV.setLayoutManager(new LinearLayoutManager(TutorialsListActivity.this,LinearLayoutManager.VERTICAL,false));
        mTutorialsRV.setAdapter(mTutorialsAdapter);
        mTutorialsAdapter.notifyDataSetChanged();
    }

    private void SetAutoRefresh(){
        if(mRemainingRefreshTime <=0){
            if(mCountDownTimer != null){
                mCountDownTimer.cancel();
            }
            return;
        }
        if(mCountDownTimer == null){
            mCountDownTimer = new CountDownTimer(mRemainingRefreshTime, mCountIntervalTime) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTutorialsRV.setVisibility(View.GONE);
                    LoadGuideTutorialsList() ;
                }

                @Override
                public void onFinish() {
                    mTutorialsRV.setVisibility(View.VISIBLE);
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
    private void LoadGuideTutorialsList() {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code) );
        dataMap.put(REG_DIAG_ID, MainActivity.mRegDiagIdAdmin);
        dataMap.put(REG_USER_ID, MainActivity.mRegUserIdAdmin);

        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.GET_GUIDE_TUTORIALS,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString(ERROR).equalsIgnoreCase(FALSE)) {
                            JSONArray jsonArray = object.getJSONArray(DATA);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                mTutorialsList.add(new ClassBuilder()
                                        .setTutorial_id(jsonObject.getString(TUTORIAL_ID))
                                        .setTutorial_title(jsonObject.getString(TUTORIAL_TITLE))
                                        .setTutorial_details(jsonObject.getString(TUTORIAL_DETAIL))
                                        .setTutorial_video_id(jsonObject.getString(TUTORIAL_VIDEO_ID))
                                .build()) ;
                            }
                            mTutorialsAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException ignored) {
                    }
                }, error -> ToastNotify.NetConnectionNotify(TutorialsListActivity.this), dataMap );

    }

    @Override
    public void OnGoogle(String link) {
        CustomTabsIntent customTabsIntent = mBuilder.build();
        customTabsIntent.intent.setPackage(CHROME_PACKAGE);
        customTabsIntent.launchUrl(TutorialsListActivity.this, Uri.parse(link));
    }
}