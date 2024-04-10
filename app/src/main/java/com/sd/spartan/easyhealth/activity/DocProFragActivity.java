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

import com.google.android.material.tabs.TabLayout;
import com.sd.spartan.easyhealth.MainActivity;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.adapter.VPagerDocAdapter;
import com.sd.spartan.easyhealth.AccessControl.AppConstants;
import com.sd.spartan.easyhealth.fragment.DocDiagListFragment;

public class DocProFragActivity extends AppCompatActivity {
    private TextView mDocNameTV;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RelativeLayout mFullViewRL;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private VPagerDocAdapter mVPagerDocAdapter;

    public static String mDocID, mDocNameEng, mDocNameBan;
    private CountDownTimer mCountDownTimer;
    private final long mRemainingRefreshTime = 2000 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_pro_frag);

        Toolbar toolbar = findViewById(R.id.appbar_my_doc_profile) ;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(R.string.doc_profile) ;
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Initialize() ;
        mDocID = getIntent().getStringExtra(AppConstants.DOC_ID) ;
        mDocNameEng = getIntent().getStringExtra(AppConstants.DOC_NAME_ENG) ;
        mDocNameBan = getIntent().getStringExtra(AppConstants.DOC_NAME_BAN) ;

        if(AppAccess.languageEng){
            mDocNameTV.setText(mDocNameEng);
            mTabLayout.addTab(mTabLayout.newTab().setText(AppConstants.DIAGNOSTIC_ENG));
            mTabLayout.addTab(mTabLayout.newTab().setText(AppConstants.DETAILS_ENG));
        }else{
            mDocNameTV.setText(mDocNameBan);
            mTabLayout.addTab(mTabLayout.newTab().setText(AppConstants.DIAGNOSTIC_BAN));
            mTabLayout.addTab(mTabLayout.newTab().setText(AppConstants.DETAILS_BAN));
        }

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mVPagerDocAdapter = new VPagerDocAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(mVPagerDocAdapter);
        mTabLayout.setScrollX(mTabLayout.getWidth());
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        mSwipeRefreshLayout.setOnRefreshListener(this::setAutoRefresh);

        if(!MainActivity.mDiagAdmin){
            AccessController.PushUserClickCount(DocProFragActivity.this, mDocID, "");
        }
    }

    private void setAutoRefresh(){
        if(mCountDownTimer == null){
            mCountDownTimer = new CountDownTimer(mRemainingRefreshTime, 2500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mFullViewRL.setVisibility(View.GONE);
                    RefreshPage() ;
                }

                @Override
                public void onFinish() {
                    mFullViewRL.setVisibility(View.VISIBLE);
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
        mVPagerDocAdapter.notifyDataSetChanged();

        DocDiagListFragment docFrag = (DocDiagListFragment) mVPagerDocAdapter.instantiateItem(mViewPager, currentPosition);
        docFrag.reload();
    }

    private void Initialize() {
        mDocNameTV = findViewById(R.id.text_input_doc_name) ;
        mSwipeRefreshLayout = findViewById(R.id.swipe_pro_frag) ;
        mFullViewRL = findViewById(R.id.relative_doc_fullview) ;

        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCountDownTimer =null ;
    }
}