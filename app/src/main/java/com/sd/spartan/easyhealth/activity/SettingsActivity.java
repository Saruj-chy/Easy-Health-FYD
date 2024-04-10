package com.sd.spartan.easyhealth.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.sd.spartan.easyhealth.MainActivity;
import com.sd.spartan.easyhealth.R;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class SettingsActivity extends AppCompatActivity {
    private SwipeRefreshLayout mSwipeRL;
    private RelativeLayout mRelativeLayout;
    private RadioButton mEnglishRB, mBanglaRB, mLightRB, mDarkRB;

    private CountDownTimer mCountDownTimer;
    long mRemainingRefreshTime = 2000 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.appbar_settings) ;
        setSupportActionBar(toolbar) ;
        ActionBar actionBar = getSupportActionBar() ;
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true) ;
            actionBar.setDisplayShowCustomEnabled(true) ;
        }
        setTitle(R.string.settings) ;
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Initialize() ;
        LoadSettingsData() ;

        mSwipeRL.setColorSchemeColors(Color.GREEN,Color.RED,Color.BLUE);
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
            mCountDownTimer = new CountDownTimer(mRemainingRefreshTime, 2500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mRelativeLayout.setVisibility(View.GONE);
                    LoadSettingsData() ;
                }

                @Override
                public void onFinish() {
                    mRelativeLayout.setVisibility(View.VISIBLE);
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

    private void LoadSettingsData() {
        boolean darkMode = getSharedPreferences(USER, MODE_PRIVATE).getBoolean(DARK_MODE, false) ;
        if(darkMode){
            mDarkRB.setChecked(true);
        }else{
            mLightRB.setChecked(true);
        }
        AppAccess.languageEng = getSharedPreferences(USER, MODE_PRIVATE).getBoolean(LAN_ENG, false) ;
        if(AppAccess.languageEng){
            mEnglishRB.setChecked(true);
        }else{
            mBanglaRB.setChecked(true);
        }

        int nightModeFlags = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                mDarkRB.setChecked(true);
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                mLightRB.setChecked(true);
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onRadioButtonClicked(View v) {
        boolean checked = ((RadioButton) v).isChecked();
        switch (v.getId()) {
            case R.id.radio_btn_light:
                if (checked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    getSharedPreferences(USER, MODE_PRIVATE).edit().putBoolean(DARK_MODE, false).apply();
                }

                break;

            case R.id.radio_btn_dark:
                if (checked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    getSharedPreferences(USER, MODE_PRIVATE).edit().putBoolean(DARK_MODE, true).apply();
                }
                break;

            case R.id.radio_btn_english:
                if (checked) {
                    getSharedPreferences(USER, MODE_PRIVATE).edit().putBoolean(LAN_ENG,
                            true).apply();
                }

                break;

            case R.id.radio_btn_bangla:
                if (checked) {
                    getSharedPreferences(USER, MODE_PRIVATE).edit().putBoolean(LAN_ENG, false).apply();
                }
                break;

            default:
                break;
        }
    }

    private void Initialize() {
        mSwipeRL = findViewById(R.id.swipe_settings) ;
        mRelativeLayout = findViewById(R.id.relative_settings) ;
        mEnglishRB = findViewById(R.id.radio_btn_english) ;
        mBanglaRB = findViewById(R.id.radio_btn_bangla) ;
        mLightRB = findViewById(R.id.radio_btn_light) ;
        mDarkRB = findViewById(R.id.radio_btn_dark) ;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SettingsActivity.this, MainActivity.class ));
    }
}