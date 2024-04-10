package com.sd.spartan.easyhealth.activity;

import androidx.appcompat.app.ActionBar ;
import androidx.appcompat.app.AppCompatActivity ;
import androidx.appcompat.widget.Toolbar ;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout ;
import androidx.viewpager.widget.ViewPager ;
import android.content.Intent ;
import android.graphics.Color ;
import android.os.Bundle ;
import android.os.CountDownTimer ;
import android.view.View ;
import android.view.WindowManager ;
import android.widget.RelativeLayout ;
import android.widget.TextView ;
import com.google.android.material.tabs.TabLayout ;
import com.sd.spartan.easyhealth.MainActivity ;
import com.sd.spartan.easyhealth.R ;
import com.sd.spartan.easyhealth.adapter.VPagerMyDiagAdapter ;
import com.sd.spartan.easyhealth.fragment.MyDoctorsFragment;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.* ;

public class MyDiagProfileFragActivity extends AppCompatActivity {
    private TextView mDiagNameTV;
    private SwipeRefreshLayout mSwipeRL;
    private RelativeLayout mFullViewRL;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private VPagerMyDiagAdapter mVPagerMyDiagAdapter;

    public static String mSubDistrictID, mDiagID;
    public static boolean mProfileLayout = true ;
    private CountDownTimer mCountDownTimer;
    long mRemainingRefreshTime = 2000 ;
    long mCountDownInterval = 2500 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_my_diag_profile_frag);

        Toolbar toolbar = findViewById(R.id.appbar_my_diag_profile) ;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle(R.string.my_profile) ;
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Initialize() ;
        if(AppAccess.languageEng){
            mDiagNameTV.setText(MainActivity.mAdminDiagNameEng);
            mTabLayout.addTab(mTabLayout.newTab().setText(DOCTOR_ENG));
            mTabLayout.addTab(mTabLayout.newTab().setText(DETAILS_ENG));
        }else{
            mDiagNameTV.setText(MainActivity.mAdminDiagNameBan);
            mTabLayout.addTab(mTabLayout.newTab().setText(DOCTOR_BAN));
            mTabLayout.addTab(mTabLayout.newTab().setText(DETAILS_BAN));
        }


        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mVPagerMyDiagAdapter = new VPagerMyDiagAdapter(getSupportFragmentManager(),
                mTabLayout.getTabCount());
        mViewPager.setAdapter(mVPagerMyDiagAdapter);
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


        if(getIntent().getStringExtra(DIAG_ID) != null){
            mDiagID = getIntent().getStringExtra(DIAG_ID) ;
            mSubDistrictID = getIntent().getStringExtra(SUBDISTRICT_ID) ;
        }else{
            mDiagID = MainActivity.mDiagIdAdmin;
            mSubDistrictID = MainActivity.mSubDistrictIdAdmin;
        }


        mSwipeRL.setColorSchemeColors(Color.BLUE,Color.RED,Color.DKGRAY);
        mSwipeRL.setOnRefreshListener(this::SetAutoRefresh);
    }

    private void SetAutoRefresh(){
        if(mRemainingRefreshTime <=0){
            if(mCountDownTimer != null){
                mCountDownTimer.cancel();
            }
            return;
        }
        if(mCountDownTimer == null){
            mCountDownTimer = new CountDownTimer(mRemainingRefreshTime, mCountDownInterval) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mFullViewRL.setVisibility(View.GONE);
                    RefreshPage() ;
                }

                @Override
                public void onFinish() {
                    mFullViewRL.setVisibility(View.VISIBLE);
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


    private void RefreshPage() {
        int currentPosition = mViewPager.getCurrentItem() ;
        mVPagerMyDiagAdapter.notifyDataSetChanged() ;

        MyDoctorsFragment docFrag = (MyDoctorsFragment) mVPagerMyDiagAdapter.instantiateItem(mViewPager, currentPosition);
        docFrag.reload();
    }
    private void Initialize() {
        mDiagNameTV = findViewById(R.id.text_diag_name) ;
        mSwipeRL = findViewById(R.id.swipe_pro_frag) ;
        mFullViewRL = findViewById(R.id.relative_diag_fullview) ;
        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCountDownTimer =null ;
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        RefreshPage() ;
    }
    @Override
    public void onBackPressed() {
        if(!mProfileLayout){
            RefreshPage();
            mProfileLayout = true ;
        }else{
            startActivity(new Intent(MyDiagProfileFragActivity.this, MainActivity.class));
        }
    }
}