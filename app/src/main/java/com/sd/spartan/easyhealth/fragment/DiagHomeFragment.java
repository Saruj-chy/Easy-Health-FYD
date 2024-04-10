package com.sd.spartan.easyhealth.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sd.spartan.easyhealth.MainActivity;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.activity.FavoriteActivity;
import com.sd.spartan.easyhealth.activity.MyDiagProfileFragActivity;
import com.sd.spartan.easyhealth.activity.SettingsActivity;
import com.sd.spartan.easyhealth.activity.SubDistrictDocDiagActivity;

import static android.content.Context.MODE_PRIVATE;
import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class DiagHomeFragment extends Fragment{
    private RelativeLayout fragHomeRL ;
    private LinearLayout profileLL, searchLL, savedLL, settingsLL ;
    private TextView mProfileTV, mSearchTV, mSavedTV, mSettingsTV ;
    private TextView diagNameTV ;
    private SwipeRefreshLayout swipeRefreshLayout ;

    private String mSubDistrictID;
    private CountDownTimer countDownTimer ;
    long remainingRefreshTime = 2000, mCountIntervalTime = 2500 ;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_diag_home, container, false);
        mSubDistrictID = getContext().getSharedPreferences(USER, MODE_PRIVATE).getString(SUBDISTRICT_ID, "") ;

        Initialize(root) ;

        profileLL.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MyDiagProfileFragActivity.class) ;
            intent.putExtra(SUBDISTRICT_ID, mSubDistrictID) ;
            getContext().startActivity(intent);

        });
        searchLL.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SubDistrictDocDiagActivity.class) ;
            intent.putExtra(SUBDISTRICT_ID, mSubDistrictID) ;
            getContext().startActivity(intent);
        });
        savedLL.setOnClickListener(v -> startActivity(new Intent(getContext(), FavoriteActivity.class)));

        settingsLL.setOnClickListener(v -> startActivity(new Intent(getActivity(), SettingsActivity.class)));


        swipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.RED,Color.DKGRAY);
        swipeRefreshLayout.setOnRefreshListener(this::setAutoRefresh);

        return root;
    }

    private void DaigHomeItemName() {
        AppAccess.languageEng = getContext().getSharedPreferences(USER, MODE_PRIVATE).getBoolean(LAN_ENG, false) ;
        if(AppAccess.languageEng){
            diagNameTV.setText(MainActivity.mAdminDiagNameEng);
            mProfileTV.setText(PROFILE_ENG);
            mSearchTV.setText(SEARCH_ENG);
            mSavedTV.setText(SAVED_ENG);
            mSettingsTV.setText(SETTINGS_ENG);
        }else{
            diagNameTV.setText(MainActivity.mAdminDiagNameBan);
            mProfileTV.setText(PROFILE_BAN);
            mSearchTV.setText(SEARCH_BAN);
            mSavedTV.setText(SAVED_BAN);
            mSettingsTV.setText(SETTINGS_BAN);
        }
    }

    private void setAutoRefresh(){
        if(remainingRefreshTime<=0){
            if(countDownTimer!= null){
                countDownTimer.cancel();
            }
            return;
        }
        if(countDownTimer == null){
            countDownTimer = new CountDownTimer(remainingRefreshTime, mCountIntervalTime) {
                @Override
                public void onTick(long millisUntilFinished) {
                    fragHomeRL.setVisibility(View.GONE);
                }

                @Override
                public void onFinish() {
                    fragHomeRL.setVisibility(View.VISIBLE);
                    diagNameTV.setText(MainActivity.mAdminDiagNameBan);
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


    private void Initialize(View root) {
        fragHomeRL = root.findViewById( R.id.relative_frag_home );
        profileLL = root.findViewById( R.id.linear_profile );
        searchLL = root.findViewById( R.id.linear_search );
        savedLL = root.findViewById( R.id.linear_saved );
        settingsLL = root.findViewById( R.id.linear_settings );
        diagNameTV = root.findViewById( R.id.text_diag_name);
        mProfileTV = root.findViewById( R.id.text_profile);
        mSearchTV = root.findViewById( R.id.text_search);
        mSavedTV = root.findViewById( R.id.text_saved);
        mSettingsTV = root.findViewById( R.id.text_settings);
        swipeRefreshLayout = root.findViewById( R.id.swipe_diag_home );
    }

    @Override
    public void onStart() {
        super.onStart();

        DaigHomeItemName() ;

    }
}