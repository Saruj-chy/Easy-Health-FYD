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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sd.spartan.easyhealth.MainActivity;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.adapter.MessageAdapter;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;
import com.sd.spartan.easyhealth.model.BuilderModel;
import com.sd.spartan.easyhealth.model.ClassBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class MessageActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout ;
    private ConstraintLayout mMsgCL, mMsgListCL;
    private TextInputLayout mBodyTIL;
    private TextInputEditText  mBodyTET ;
    private RecyclerView mMsgListRV ;
    private FloatingActionButton mAddMsgFAB;
    private String mBody ;
    private ProgressBar progressBar ;

    private final ArrayList<BuilderModel> mMsgList = new ArrayList<>();
    private MessageAdapter mMessageAdapter;
    private boolean mMsgLayout = false ;

    private CountDownTimer mCountDownTimer;
    long mRemainingRefreshTime = 2000, refreshTimeInterval = 2500 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.appbar_message) ;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        setTitle(R.string.message) ;
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Initialize() ;
        ChangeByLanguage() ;
        LoadAdapter() ;

        swipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.RED,Color.DKGRAY);
        swipeRefreshLayout.setOnRefreshListener(this::SetAutoRefresh);
    }
    private void SetAutoRefresh(){
        if(mRemainingRefreshTime <=0){
            if(mCountDownTimer != null){
                mCountDownTimer.cancel();
            }
            return;
        }
        if(mCountDownTimer == null){
            mCountDownTimer = new CountDownTimer(mRemainingRefreshTime, refreshTimeInterval) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mMsgListRV.setVisibility(View.GONE);

                }
                @Override
                public void onFinish() {
                    mMsgListRV.setVisibility(View.VISIBLE);
                    refreshPage();
                    swipeRefreshLayout.setRefreshing(false);
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
    private void LoadAllMsgReply() {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getString(R.string.prj_code));
        dataMap.put(CLIENT_ID, AppAccess.clientID);
        dataMap.put(INFO_TYPE, NUM_two+"" );
        AppAccess.getAppController().getAppNetworkController().makeRequest( AppConfig.LIVE_EMAIL_MSG,
                response -> {
            progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            mMsgListRV.setVisibility(View.VISIBLE);
                            mMsgList.clear();
                            JSONArray jsonArray = object.getJSONArray(DATA) ;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                mMsgList.add(new ClassBuilder()
                                        .setE_id(jsonObject.getString(E_ID))
                                        .setE_msg(jsonObject.getString(E_MSG))
                                        .setE_reply(jsonObject.getString(E_REPLY))
                                        .build()) ;
                            }
                            mMessageAdapter.notifyDataSetChanged();

                        }
                    } catch (JSONException ignored) {
                    }
                }, error ->
                        ToastNotify.NetConnectionNotify(MessageActivity.this), dataMap) ;

    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadAdapter() {
        mMessageAdapter = new MessageAdapter(this, mMsgList);
        LinearLayoutManager mLinearLM = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mMsgListRV.setLayoutManager(mLinearLM);
        mMsgListRV.setAdapter(mMessageAdapter);
        mMessageAdapter.notifyDataSetChanged();
    }

    private void ChangeByLanguage() {
        if(AppAccess.languageEng){
            mBodyTIL.setHint(SUB_MSG_ENG);
        }else{
            mBodyTIL.setHint(SUB_MSG_BAN);
        }
    }

    public void onMessageSent(View view) {
        mBody = Objects.requireNonNull(mBodyTET.getText()).toString().trim() ;

        if(mBody.equalsIgnoreCase("")){
            ToastNotify.ShowToast(MessageActivity.this, TOAST_FILL_UP_MSG);
        }else{
            emailSent() ;
        }

    }

    private void emailSent() {
        HashMap<String, String> map = new HashMap<>() ;
        map.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code)) ;
        map.put(MSG, mBody) ;
        map.put(DIAG_ID, MainActivity.mDiagIdAdmin) ;
        map.put(REG_USER_ID, MainActivity.mRegUserIdAdmin) ;
        map.put(CLIENT_ID, AppAccess.clientID);
        map.put(INFO_TYPE, NUM_ONE+"" );


        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.LIVE_EMAIL_MSG,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            refreshPage() ;
                        }
                        ToastNotify.ShowToast(MessageActivity.this, object.getString("msg"));
                    } catch (JSONException ignored) {
                    }
                }, error -> ToastNotify.NetConnectionNotify(MessageActivity.this), map );

    }
    @SuppressLint("NotifyDataSetChanged")
    private void refreshPage() {
        mMsgLayout = false ;
        mBodyTET.setText("");
        mBodyTET.clearFocus();

        mMsgCL.setVisibility(View.GONE);
        mMsgListCL.setVisibility(View.VISIBLE);
        mAddMsgFAB.setVisibility(View.VISIBLE);

        LoadAllMsgReply() ;
        mMessageAdapter.notifyDataSetChanged();
    }

    private void Initialize() {
        mMsgCL = findViewById(R.id.constraint_message) ;
        mMsgListCL = findViewById(R.id.constraint_msg_list) ;
        mBodyTIL = findViewById(R.id.textinput_body) ;
        mBodyTET = findViewById(R.id.edit_body) ;
        mMsgListRV = findViewById(R.id.recycler_message) ;
        mAddMsgFAB = findViewById(R.id.fab_message);
        progressBar = findViewById(R.id.progress);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);

    }


    @Override
    protected void onStart() {
        super.onStart();

        refreshPage();
    }
    @Override
    public void onBackPressed() {
        if(mMsgLayout){
            mMsgLayout = false ;
            refreshPage();
        }else{
            super.onBackPressed();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        CancelAutoRefresh();
    }

    public void OnEmailSent(View view) {
        mMsgLayout = true ;
        mMsgCL.setVisibility(View.VISIBLE);
        mMsgListCL.setVisibility(View.GONE);
        mAddMsgFAB.setVisibility(View.GONE);
    }
}