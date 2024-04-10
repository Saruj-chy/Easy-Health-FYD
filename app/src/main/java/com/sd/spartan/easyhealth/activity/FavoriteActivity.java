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
import com.sd.spartan.easyhealth.adapter.FavouriteDiagAdapter;
import com.sd.spartan.easyhealth.adapter.FavouriteDocAdapter;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;
import com.sd.spartan.easyhealth.database.DatabaseHelperTest;
import com.sd.spartan.easyhealth.interfaces.OnDeleteInterface;
import com.sd.spartan.easyhealth.model.BuilderModel;
import com.sd.spartan.easyhealth.model.ClassBuilder;
import com.sd.spartan.easyhealth.model.SerialNumModel;
import com.sd.spartan.easyhealth.model.SpecialistModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class FavoriteActivity extends AppCompatActivity implements OnDeleteInterface {
    private ConstraintLayout mFavouriteCL, mDocCL, mDiagCL;
    private SwipeRefreshLayout mSwipeRL;
    private RecyclerView mDocRV, mDiagRV;

    private List<BuilderModel> mDocSpeList;
    private List<BuilderModel> mDiagSerList;
    private FavouriteDiagAdapter mFavouriteDiagAdapter;
    private FavouriteDocAdapter mFavouriteDocAdapter;

    private DatabaseHelperTest mDatabaseHelperTest;
    private final ArrayList<String> mDocIdDbList = new ArrayList<>();
    private final ArrayList<String> mDiagIdDbList = new ArrayList<>();

    private MaterialAlertDialogBuilder mMaterialAlertDialogBuilder;
    private AlertDialog mAlertDialog;
    private TextView mYesBtnTV, mNoBtnTV;
    private TextView mNameItemTV;

    private CountDownTimer mCountDownTimer;
    private final long mRemainingRefreshTime = 2000 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);


        Toolbar toolbar = findViewById(R.id.appbar_favorite) ;
        setSupportActionBar(toolbar) ;
        ActionBar actionBar = getSupportActionBar() ;
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true) ;
        actionBar.setDisplayShowCustomEnabled(true) ;
        setTitle(R.string.favourite) ;
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Initialize() ;

        mMaterialAlertDialogBuilder = new MaterialAlertDialogBuilder(FavoriteActivity.this, R.style.MaterialAlertDialog_rounded) ;
        mDocSpeList = new ArrayList<>() ;
        mDiagSerList = new ArrayList<>() ;

        DocDiagAdapter();
        GetDocFavList();
        GetDiagFavList();

        if(mDocIdDbList.size() == 0){
            mDocCL.setVisibility(View.GONE);
        }else{
            LoadFavDocList(mDocIdDbList) ;
        }

        if(mDiagIdDbList.size() == 0){
            mDiagCL.setVisibility(View.GONE);
        }else{
            LoadFavDiagList(mDiagIdDbList) ;
        }

        mSwipeRL.setColorSchemeColors(Color.GREEN,Color.RED,Color.BLUE);
        mSwipeRL.setOnRefreshListener(this::SetAutoRefresh);


    }

    private void SetAutoRefresh(){
        if(mCountDownTimer == null){
            mCountDownTimer = new CountDownTimer(mRemainingRefreshTime, 2500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mFavouriteCL.setVisibility(View.GONE);
                }

                @Override
                public void onFinish() {
                    mFavouriteCL.setVisibility(View.VISIBLE);
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
    private void DocDiagAdapter() {
        mFavouriteDocAdapter = new FavouriteDocAdapter(FavoriteActivity.this, mDocSpeList, this);
        mDocRV.setLayoutManager(new LinearLayoutManager(FavoriteActivity.this,LinearLayoutManager.VERTICAL,false));
        mDocRV.setAdapter(mFavouriteDocAdapter);
        mFavouriteDocAdapter.notifyDataSetChanged();

        mFavouriteDiagAdapter = new FavouriteDiagAdapter(FavoriteActivity.this, mDiagSerList, "", this);
        mDiagRV.setLayoutManager(new LinearLayoutManager(FavoriteActivity.this, LinearLayoutManager.VERTICAL,false));
        mDiagRV.setAdapter(mFavouriteDiagAdapter);
        mFavouriteDiagAdapter.notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    private void LoadFavDiagList(ArrayList<String> diagList) {
        mDiagSerList.clear();
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DIAG_ID_LIST, diagList.toString());

        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.FAVOURITE_DIAG_SER,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            JSONArray jsonArray = object.getJSONArray(DATA) ;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                JSONArray ja = jsonObject.getJSONArray(SER_LIST) ;
                                List<SerialNumModel> serialNumModelList = new ArrayList<>() ;
                                for(int j=0; j<ja.length(); j++){
                                    JSONObject JO = ja.getJSONObject(j);
                                    serialNumModelList.add(new SerialNumModel(
                                            JO.getString(SER_NUM_BAN),
                                            JO.getString(SER_NUM_ENG),
                                            JO.getString(DIAG_ID)
                                    ));
                                }


                                mDiagSerList.add(new ClassBuilder()
                                        .setDiag_id(jsonObject.getString(DIAG_ID))
                                        .setDiag_name_eng(jsonObject.getString(DIAG_NAME_ENG))
                                        .setDiag_name_ban(jsonObject.getString(DIAG_NAME_BAN))
                                        .setDiag_address_eng(jsonObject.getString(DIAG_ADDRESS_ENG))
                                        .setDiag_address_ban(jsonObject.getString(DIAG_ADDRESS_BAN))
                                        .setSubdistrict_id(jsonObject.getString(SUBDISTRICT_ID))
                                        .setSubdistrict_ban(jsonObject.getString(SUBDISTRICT_TITLE_BAN))
                                        .setSubdistrict_eng(jsonObject.getString(SUBDISTRICT_TITLE_ENG))
                                        .setDistrict_ban(jsonObject.getString(DISTRICT_TITLE_BAN))
                                        .setDistrict_eng(jsonObject.getString(DISTRICT_TITLE_ENG))
                                        .setSer_list(serialNumModelList)
                                        .build());

                            }
                            mFavouriteDiagAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException ignored) {
                    }
                }, error -> ToastNotify.NetConnectionNotify(FavoriteActivity.this), dataMap) ;
    }
    @SuppressLint("NotifyDataSetChanged")
    private void LoadFavDocList(ArrayList<String> doc_id_dbList) {
        mDocSpeList.clear();

        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DOC_ID_LIST, doc_id_dbList.toString());

        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.FAVOURITE_DOC_SPE,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            JSONArray jsonArray = object.getJSONArray(DATA) ;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                List<SpecialistModel> specialistModelList = new ArrayList<>() ;

                                JSONArray ja = jsonObject.getJSONArray(SPE_LIST);
                                for(int j=0; j<ja.length(); j++){
                                    JSONObject JO = ja.getJSONObject(j);
                                    specialistModelList.add(new SpecialistModel(
                                            JO.getString(SPE_ID),
                                            JO.getString(SPE_NAME_BAN),
                                            JO.getString(SPE_NAME_ENG),
                                            JO.getString(DOC_ID)
                                    ));
                                }

                                mDocSpeList.add(new ClassBuilder()
                                        .setDoc_id(jsonObject.getString(DOC_ID))
                                        .setDoc_code(jsonObject.getString(DOC_CODE))
                                        .setDoc_name_eng(jsonObject.getString(DOC_NAME_ENG))
                                        .setDoc_name_ban(jsonObject.getString(DOC_NAME_BAN))
                                        .setDegree_eng(jsonObject.getString(DEGREE_ENG))
                                        .setDegree_ban(jsonObject.getString(DEGREE_BAN))
                                        .setSpe_list(specialistModelList)
                                        .build());

                            }
                            mFavouriteDocAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException ignored) {
                    }
                }, error -> ToastNotify.NetConnectionNotify(FavoriteActivity.this), dataMap) ;
    }



    private void GetDiagFavList() {
        mDiagIdDbList.clear();
        Cursor y = mDatabaseHelperTest.checkTable(DIAG, AppAccess.clientID) ;
        if (y.moveToFirst()) {
            while (true) {
                mDiagIdDbList.add(y.getString(1));
                if (y.isLast())
                    break;
                y.moveToNext();
            }
        }
    }
    private void GetDocFavList() {
        mDocIdDbList.clear();
        Cursor y = mDatabaseHelperTest.checkTable(DOC, AppAccess.clientID) ;
        if (y.moveToFirst()) {
            while (true) {
                mDocIdDbList.add(y.getString(1));
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

    private void Initialize() {
        mFavouriteCL = findViewById(R.id.constraint_favourite) ;
        mDocCL = findViewById(R.id.constraint_doc_fav) ;
        mDiagCL = findViewById(R.id.constraint_diag_fav) ;
        mSwipeRL = findViewById(R.id.swipe_favourite) ;
        mDocRV = findViewById(R.id.recycler_doc_fav) ;
        mDiagRV = findViewById(R.id.recycler_diag_fav) ;

        mDatabaseHelperTest = new DatabaseHelperTest(this) ;
    }


    private void CreatePopupDialog(String docId, String diagId, String name) {
        View view = getLayoutInflater().inflate(R.layout.popup_delete,  null);
        PopupLayoutInitialize(view);

        mNameItemTV.setText(String.format("%s?", name));

        mYesBtnTV.setOnClickListener(v -> DeleteFavourite(docId, diagId));

        mNoBtnTV.setOnClickListener(v -> mAlertDialog.cancel());


        mMaterialAlertDialogBuilder.setView(view);
        mAlertDialog = mMaterialAlertDialogBuilder.create();
        mAlertDialog.show();
        mAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }
    private void PopupLayoutInitialize(View view) {
        mNameItemTV = view.findViewById(R.id.text_del_name) ;
        mYesBtnTV = view.findViewById(R.id.text_yes) ;
        mNoBtnTV = view.findViewById(R.id.text_no) ;
    }
    @SuppressLint("NotifyDataSetChanged")
    private void DeleteFavourite(String docId, String diagId) {
        boolean l ;
        if(docId==null){
            l  = mDatabaseHelperTest.DeleteDiagnosticTbl(diagId) ;
            if(l){
                GetDiagFavList();
                LoadFavDiagList(mDiagIdDbList) ;
                mFavouriteDiagAdapter.notifyDataSetChanged();
            }
        }else{
            l  = mDatabaseHelperTest.DeleteDoctorTbl(docId) ;
            if(l){
                GetDocFavList();
                LoadFavDocList(mDocIdDbList) ;
                mFavouriteDocAdapter.notifyDataSetChanged();
            }
        }
        mAlertDialog.cancel();

    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        mDatabaseHelperTest.close();
    }
}