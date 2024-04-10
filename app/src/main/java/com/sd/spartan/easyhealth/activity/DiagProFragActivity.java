package com.sd.spartan.easyhealth.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.av.smoothviewpager.Smoolider.SmoothViewpager;
import com.google.android.material.tabs.TabLayout;
import com.sd.spartan.easyhealth.MainActivity;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.adapter.SmoothPagerAdapter;
import com.sd.spartan.easyhealth.adapter.VPagerDiagAdapter;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.fragment.DiagDocListFragment;
import com.sd.spartan.easyhealth.model.BuilderModel;
import com.sd.spartan.easyhealth.model.ClassBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class DiagProFragActivity extends AppCompatActivity {
    private TextView mDiagNameTV;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RelativeLayout mDiagRL;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private VPagerDiagAdapter mVPagerDiagAdapter;

    public static String mSubdistrictID, mDiagID, mDiagNameEng, mDiagNameBan;
    private CountDownTimer mCountDownTimer;
    long mRemainingRefreshTime = 2000 ;

    private SmoothViewpager mSmothPager;
    private int mCurrentPage = 0;
    private CountDownTimer mBannerCountDownTimer;
    long mBannerRefreshTime = 60*60*1000 ;
    private boolean mBannerAction = true ;
    private List<BuilderModel> mBannerList  ;
    private SmoothPagerAdapter mSmoothPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diag_pro_frag);

        Toolbar mToolbar = findViewById(R.id.appbar_my_diag_profile) ;
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        setTitle(R.string.diag_profile) ;
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());

        Initialize() ;
        mDiagID = getIntent().getStringExtra(DIAG_ID) ;
        mSubdistrictID = getIntent().getStringExtra(SUBDISTRICT_ID) ;
        mDiagNameEng = getIntent().getStringExtra(DIAG_NAME_ENG) ;
        mDiagNameBan = getIntent().getStringExtra(DIAG_NAME_BAN) ;

        if(AppAccess.languageEng){
            mDiagNameTV.setText(mDiagNameEng);
            mTabLayout.addTab(mTabLayout.newTab().setText(DOCTOR_ENG));
            mTabLayout.addTab(mTabLayout.newTab().setText(DETAILS_ENG));
        }else{
            mDiagNameTV.setText(mDiagNameBan);
            mTabLayout.addTab(mTabLayout.newTab().setText(DOCTOR_BAN));
            mTabLayout.addTab(mTabLayout.newTab().setText(DETAILS_BAN));
        }

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mVPagerDiagAdapter = new VPagerDiagAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(mVPagerDiagAdapter);
        mTabLayout.setScrollX(mTabLayout.getWidth());
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.RED,Color.DKGRAY);
        mSwipeRefreshLayout.setOnRefreshListener(this::SetAutoRefresh);

        mBannerList = new ArrayList<>() ;
        LoadBannerImage() ;
        mSmoothPagerAdapter = new SmoothPagerAdapter(DiagProFragActivity.this, mBannerList) ;
        mSmothPager.setAdapter(mSmoothPagerAdapter);
        mSmothPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {

            }
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            public void onPageSelected(final int position) {
                mCurrentPage =position ;
            }
        });
        SetBannerRefresh() ;

        if(!MainActivity.mDiagAdmin){
            AccessController.PushUserClickCount(DiagProFragActivity.this, "", mDiagID);
        }
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
                    mDiagRL.setVisibility(View.GONE);

                    RefreshPage() ;
                }
                @Override
                public void onFinish() {
                    mDiagRL.setVisibility(View.VISIBLE);
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
    private void RefreshPage() {
        int currentPosition = mViewPager.getCurrentItem();
        mVPagerDiagAdapter.notifyDataSetChanged();

        DiagDocListFragment docFrag = (DiagDocListFragment) mVPagerDiagAdapter.instantiateItem(mViewPager, currentPosition);
        docFrag.reload();
    }

    private void LoadBannerImage() {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DIAG_ID, mDiagID);
        dataMap.put(SUBDISTRICT_ID, mSubdistrictID);
        dataMap.put(BANNER_STATE_ID_TITLE, String.valueOf(NUM_FOUR));

        AppAccess.getAppController().getAppNetworkController().makeRequest( AppConfig.GET_ALL_BANNER,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            JSONArray jsonArray = object.getJSONArray(DATA) ;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                mBannerList.add(new ClassBuilder()
                                        .setBanner_id(jsonObject.getString(BANNER_ID))
                                        .setBanner_img(jsonObject.getString(BANNER_IMG))
                                        .setDiag_id(jsonObject.getString(DIAG_ID))
                                        .setBanner_num(jsonObject.getString(BANNER_NUM))
                                        .build());
                            }
                            if(mBannerList.size()>0){
                                mSmothPager.setVisibility(View.VISIBLE);
                            }else{
                                mSmothPager.setVisibility(View.GONE);
                            }
                            mSmoothPagerAdapter.notifyDataSetChanged();
                        }else{
                            mSmothPager.setVisibility(View.GONE) ;
                        }
                    } catch (JSONException e) {
                        mSmothPager.setVisibility(View.GONE);
                    }
                }, error -> mSmothPager.setVisibility(View.GONE), dataMap) ;

    }
    private void SetBannerRefresh(){
        if(mBannerRefreshTime <=0){
            if(mBannerCountDownTimer != null){
                mBannerCountDownTimer.cancel();
            }
            return;

        }
        if(mBannerCountDownTimer == null){
            mBannerCountDownTimer = new CountDownTimer(mBannerRefreshTime, 10*1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (mCurrentPage == mBannerList.size()-1) {
                        mBannerAction =false ;
                    }else if(mCurrentPage == 0){
                        mBannerAction =true ;
                    }
                    if(mBannerAction){
                        mSmothPager.setCurrentItem(mCurrentPage++, true);
                    }else{
                        mSmothPager.setCurrentItem(mCurrentPage--, true);
                    }

                }

                @Override
                public void onFinish() {
                }
            };
            mBannerCountDownTimer.start() ;
        }
    }


    private void Initialize() {
        mDiagNameTV = findViewById(R.id.text_diag_name) ;
        mSwipeRefreshLayout = findViewById(R.id.swipe_pro_frag) ;
        mDiagRL = findViewById(R.id.relative_doc_fullview) ;

        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mSmothPager = findViewById(R.id.smooth_banner_frag) ;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCountDownTimer =null ;
        mBannerCountDownTimer = null ;
    }

}