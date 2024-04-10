package com.sd.spartan.easyhealth.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.adapter.NotifyAdapter;
import com.sd.spartan.easyhealth.AccessControl.AppConstants;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;
import com.sd.spartan.easyhealth.database.DatabaseHelperTest;
import com.sd.spartan.easyhealth.interfaces.OnDeleteInterface;
import com.sd.spartan.easyhealth.model.NotifyModel;

import java.util.ArrayList;

public class NotifyActivity extends AppCompatActivity implements OnDeleteInterface {
    private SwipeRefreshLayout mSwipeRL;
    private ConstraintLayout mNotifyCL;
    private TextView mNotifyTV ;
    private RecyclerView mNotifyRV;
    private DatabaseHelperTest mDatabaseHelperTest;

    private ArrayList<NotifyModel> mNotifyList;
    private NotifyAdapter mNotifyAdapter;
    private CountDownTimer mCountDownTimer;
    private final long mRemainingRefreshTime = 2000 ;

    private MaterialAlertDialogBuilder mMaterialAlertDialogBuilder;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        Toolbar toolbar = findViewById(R.id.appbar_notify_diag) ;
        setSupportActionBar(toolbar) ;
        ActionBar actionBar = getSupportActionBar() ;
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true) ;
        actionBar.setDisplayShowCustomEnabled(true) ;
        actionBar.setTitle(R.string.notification) ;
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Initialize() ;

        mNotifyList = new ArrayList<>();
        GetNotificationList();
        LoadAdapter() ;

        if(mNotifyList.size()<=0){
            mNotifyRV.setVisibility(View.GONE);
            mNotifyTV.setVisibility(View.VISIBLE);
        }else{
            mNotifyRV.setVisibility(View.VISIBLE);
            mNotifyTV.setVisibility(View.GONE);
        }

        mSwipeRL.setColorSchemeColors(Color.BLUE,Color.RED,Color.DKGRAY);
        mSwipeRL.setOnRefreshListener(this::SetAutoRefresh);

    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadAdapter() {
        mNotifyAdapter = new NotifyAdapter(getApplicationContext(), mNotifyList, this);
        LinearLayoutManager mNotifyLLM = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mNotifyRV.setLayoutManager(mNotifyLLM);
        mNotifyRV.setAdapter(mNotifyAdapter);
        mNotifyAdapter.notifyDataSetChanged();
    }

    private void SetAutoRefresh(){
        if(mCountDownTimer == null){
            mCountDownTimer = new CountDownTimer(mRemainingRefreshTime, 2500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mNotifyCL.setVisibility(View.GONE);
                }

                @Override
                public void onFinish() {
                    mNotifyCL.setVisibility(View.VISIBLE);
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

    private void GetNotificationList() {
        mNotifyList.clear();
        Cursor y = mDatabaseHelperTest.checkTable(AppConstants.NOTIFY_MAIN, AppAccess.clientID) ;
        if (y.moveToFirst()) {
            while (true) {
                mNotifyList.add(new NotifyModel(
                        y.getString(1),
                        y.getString(2),
                        y.getString(3),
                        y.getString(4),
                        y.getString(5)
                ));
                if (y.isLast())
                    break;
                y.moveToNext();
            }
        }
    }

    @Override
    public void onDelete(String docId, String diagId, String name) {
        CreatePopupDialog(docId, diagId, name);
    }
    @SuppressLint("NotifyDataSetChanged")
    private void CreatePopupDialog(String notify_id, String notify_item_id, String name) {
        View view = getLayoutInflater().inflate(R.layout.popup_delete,  null);
        TextView mNameItemTV = view.findViewById(R.id.text_del_name);
        TextView mYesBtnTV = view.findViewById(R.id.text_yes);
        TextView mNoBtnTV = view.findViewById(R.id.text_no);

        mNameItemTV.setText(String.format("%s?", name));

        mYesBtnTV.setOnClickListener(v -> {
            boolean l  = mDatabaseHelperTest.DeleteNotificationTbl(notify_id,2+"", notify_item_id) ;
            if(l){
                ToastNotify.ShowToast(NotifyActivity.this, AppConstants.TOAST_ONE_ITEM_DEL);

                GetNotificationList();
                mNotifyAdapter.notifyDataSetChanged();
            }
            mAlertDialog.cancel();
        });

        mNoBtnTV.setOnClickListener(v -> mAlertDialog.cancel());


        mMaterialAlertDialogBuilder.setView(view);
        mAlertDialog = mMaterialAlertDialogBuilder.create();
        mAlertDialog.show();
        mAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }


    private void Initialize() {
        mNotifyRV = findViewById(R.id.recycler_notify) ;
        mNotifyTV = findViewById(R.id.text_notify_data) ;
        mSwipeRL = findViewById(R.id.swipe_notify) ;
        mNotifyCL = findViewById(R.id.constraint_notify) ;

        mDatabaseHelperTest = new DatabaseHelperTest(this) ;
        mMaterialAlertDialogBuilder = new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_rounded) ;
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        mDatabaseHelperTest.close();
    }
}