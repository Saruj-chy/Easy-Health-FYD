package com.sd.spartan.easyhealth.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.av.smoothviewpager.Smoolider.SmoothViewpager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.adapter.HomeFragDiagAdapter;
import com.sd.spartan.easyhealth.adapter.HomeFragDocAdapter;
import com.sd.spartan.easyhealth.adapter.SmoothPagerAdapter;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;
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

public class SubDistrictDocDiagActivity extends AppCompatActivity {
    private TextInputLayout mDocNameTIL, mDiagNameTIL, mSpeNameTIL;
    private TextInputEditText mDocNameTIE, mDiagNameTIE, mSpeNameTIE;
    private TextView mToastMsgTV ;
    private RecyclerView mHomeDiagRV, mHomeDocRV;
    private RelativeLayout mDistrictRL;
    private SwipeRefreshLayout mSwipeRL;
    private NestedScrollView mNestedScroll ;
    private ProgressBar mProgressLoading;

    private List<BuilderModel> mDocSpeList;
    private List<BuilderModel> mDiagSerList;
    private HomeFragDiagAdapter mHomeFragDiagAdapter;
    private HomeFragDocAdapter mHomeFragDocAdapter;

    private String mSubDistrictID;
    private boolean mChangeDoc = true, diagFocus = false, docFocus = false, speFocus= false ;

    private CountDownTimer mCountDownTimer, mRefTimer ;
    long mRemainingRefreshTime = 1000, mCountIntervalTime = 1500, mSwipeRefTime = 2000 ;

    private int mPageDiagNumb = 1, mPageDocNumb = 1 ;
    boolean mDocNameListView = false, mSpeListView = false ;
    private String mDiagBanText ="", mDiagEngText ="", mDocBanText = "", mDocEngText = "", mSpeBanText = "", mSpeEngText = "" ;

    private SmoothViewpager mSmoothPager;
    private int mCurrentPage = 0;
    private CountDownTimer mBannerCountDownTimer;
    private boolean mBannerAction = true ;
    long mBannerRefreshTime = 60*60*1000 ;
    private List<BuilderModel> mBannerList;
    private SmoothPagerAdapter smoothPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_district_doc_diag);

        Toolbar toolbar = findViewById(R.id.appbar_main_doc_diag) ;
        setSupportActionBar(toolbar) ;
        ActionBar actionBar = getSupportActionBar() ;
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true) ;
        actionBar.setDisplayShowCustomEnabled(true) ;
        setTitle(APP_NAME) ;
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Initialize() ;
        mSubDistrictID = getIntent().getStringExtra(SUBDISTRICT_ID).trim();
        mDocSpeList = new ArrayList<>() ;
        mDiagSerList = new ArrayList<>() ;

        mHomeDiagRV.setVisibility(View.VISIBLE);
        mHomeDocRV.setVisibility(View.GONE);
        LoadDiagSerialList(1, mSubDistrictID,"","", false);
        LoadAllAdapter() ;
        LoadNextPageList() ;


        mDiagNameTIE.setOnFocusChangeListener((v, hasFocus) -> {
            diagFocus= hasFocus ;
            if(hasFocus){
                TextInputClear();
                mChangeDoc =true ;
                mDiagSerList.clear();
                mDocNameListView = false ;
                LoadDiagSerialList(1, mSubDistrictID,"","", false);
            }

        });
        mDiagNameTIE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CancelAutoRefresh();
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(diagFocus && s.toString().length()>0){
                    VisibilitySmooth(s.toString().length()) ;
                    if(AppAccess.languageEng){
                        mDiagEngText = s.toString() ;
                    }else{
                        mDiagBanText = s.toString() ;
                    }

                    mCountDownTimer = new CountDownTimer(mRemainingRefreshTime, mCountIntervalTime) {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onTick(long millisUntilFinished) {
                            mProgressLoading.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onFinish() {
                            mProgressLoading.setVisibility(View.GONE);
                            LoadDiagSerialList(1, mSubDistrictID, mDiagEngText, mDiagBanText, true );
                            CancelAutoRefresh();
                        }
                    };
                    mCountDownTimer.start() ;
                }

            }
        });

        mDocNameTIE.setOnFocusChangeListener((v, hasFocus) -> {
            docFocus = hasFocus ;
            if(hasFocus){
                TextInputClear();
                mDocNameListView = true ;
                mSpeListView = false ;
                DoctorListFocusClick() ;
            }

        });
        mDocNameTIE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CancelAutoRefresh();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(docFocus && s.toString().length()>0){
                    VisibilitySmooth(s.toString().length()) ;
                    if(AppAccess.languageEng){
                        mDocEngText = s.toString() ;
                    }else{
                        mDocBanText = s.toString() ;
                    }

                    mCountDownTimer = new CountDownTimer(mRemainingRefreshTime, mCountIntervalTime) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            mProgressLoading.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onFinish() {
                            mProgressLoading.setVisibility(View.GONE);
                            LoadDocSpeList(1, AppConfig.SEARCH_DOC_NAME_SUBDISTRICT, mSubDistrictID, mDocEngText, mDocBanText,"","", true);
                            CancelAutoRefresh();
                        }
                    };
                    mCountDownTimer.start() ;
                }

            }
        });

        mSpeNameTIE.setOnFocusChangeListener((v, hasFocus) -> {
            speFocus = hasFocus ;
            if(hasFocus){
                TextInputClear();
                mDocNameListView = false ;
                mSpeListView = true ;
                DoctorListFocusClick() ;
            }

        });
        mSpeNameTIE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CancelAutoRefresh();
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(speFocus && s.toString().length()>0 ){
                    VisibilitySmooth(s.toString().length()) ;
                    if(AppAccess.languageEng){
                        mSpeEngText = s.toString() ;
                    }else{
                        mSpeBanText = s.toString() ;
                    }

                    mCountDownTimer = new CountDownTimer(mRemainingRefreshTime, mCountIntervalTime) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            mProgressLoading.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onFinish() {
                            mProgressLoading.setVisibility(View.GONE);
                            LoadDocSpeList(1, AppConfig.SEARCH_DOC_SPE_SUBDISTRICT, mSubDistrictID,"","", mSpeEngText, mSpeBanText, true);
                            CancelAutoRefresh();
                        }
                    };
                    mCountDownTimer.start() ;
                }

            }
        });

        AccessController.SetOnEditorActionListener(mDiagNameTIE, this);
        AccessController.SetOnEditorActionListener(mDocNameTIE, this);
        AccessController.SetOnEditorActionListener(mSpeNameTIE, this);

        mSwipeRL.setColorSchemeColors(Color.BLUE,Color.RED,Color.GREEN);
        mSwipeRL.setOnRefreshListener(this::SetAutoRefresh);

        mBannerList = new ArrayList<>() ;
        LoadBannerImage() ;
        smoothPagerAdapter = new SmoothPagerAdapter(this, mBannerList) ;
        mSmoothPager.setAdapter(smoothPagerAdapter);
        mSmoothPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {

            }
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            public void onPageSelected(final int position) {
                mCurrentPage =position ;
            }
        });
        SetBannerRefresh() ;

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
                        mSmoothPager.setCurrentItem(mCurrentPage++, true);
                    }else{
                        mSmoothPager.setCurrentItem(mCurrentPage--, true);
                    }

                }

                @Override
                public void onFinish() {
                }
            };
            mBannerCountDownTimer.start() ;
        }
    }

    private void DoctorListFocusClick() {
        if(mChangeDoc){
            mDocSpeList.clear();
            mChangeDoc = false ;
            LoadDocSpeList(1, AppConfig.SEARCH_DOC_NAME_SUBDISTRICT, mSubDistrictID,"","","","", false);
        }
    }
    private void LoadNextPageList() {
        if (mNestedScroll != null ) {
            mNestedScroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    mCountDownTimer = new CountDownTimer(1000, 500) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            mProgressLoading.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onFinish() {
                            mProgressLoading.setVisibility(View.GONE);

                            if(mDocNameListView && !mSpeListView){
                                LoadDocSpeList(mPageDocNumb, AppConfig.SEARCH_DOC_NAME_SUBDISTRICT, mSubDistrictID, mDocEngText, mDocBanText, mSpeEngText, mSpeBanText, false);
                            } else if(!mDocNameListView && mSpeListView){
                                LoadDocSpeList(mPageDocNumb, AppConfig.SEARCH_DOC_SPE_SUBDISTRICT, mSubDistrictID, mDocEngText, mDocBanText, mSpeEngText, mSpeBanText, false);
                            }else{
                                LoadDiagSerialList(mPageDiagNumb, mSubDistrictID, mDiagEngText, mDiagBanText, false);
                            }
                            CancelAutoRefresh();
                        }
                    };
                    mCountDownTimer.start() ;

                }
            });

        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private void LoadAllAdapter() {
        mHomeFragDiagAdapter = new HomeFragDiagAdapter(SubDistrictDocDiagActivity.this, mDiagSerList, "" ){
            @Override
            public void loadNextPage(int pageNumber) {
                pageNumber = mPageDiagNumb++ ;
                mHomeFragDiagAdapter.setPageNumber(pageNumber);
            }
        } ;
        mHomeDiagRV.setLayoutManager(new LinearLayoutManager(SubDistrictDocDiagActivity.this,LinearLayoutManager.VERTICAL,false));
        mHomeDiagRV.setAdapter(mHomeFragDiagAdapter);


        mHomeFragDocAdapter = new HomeFragDocAdapter(SubDistrictDocDiagActivity.this, mDocSpeList) {
            @Override
            public void loadNextPage(int pageNumber) {
                pageNumber = mPageDocNumb++ ;
                mHomeFragDocAdapter.setPageNumber(pageNumber);
            }
        };
        mHomeDocRV.setLayoutManager(new LinearLayoutManager(SubDistrictDocDiagActivity.this,LinearLayoutManager.VERTICAL,false));
        mHomeDocRV.setAdapter(mHomeFragDocAdapter);

    }

    private void SetAutoRefresh(){
        if(mSwipeRefTime <=0){
            if(mRefTimer != null){
                mRefTimer.cancel();
            }
            return;

        }
        if(mRefTimer == null){

            mRefTimer = new CountDownTimer(mSwipeRefTime, 2500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mDistrictRL.setVisibility(View.GONE);

                }

                @Override
                public void onFinish() {
                    mDocSpeList.clear();
                    mDiagSerList.clear();
                    mChangeDoc =true ;
                    mSwipeRL.setRefreshing(false);
                    mDistrictRL.setVisibility(View.VISIBLE);
                    mHomeDiagRV.setVisibility(View.VISIBLE);
                    mHomeDocRV.setVisibility(View.GONE);
                    LoadDiagSerialList(1, mSubDistrictID,"","", false);
                    CancelRef() ;
                }
            };

            mRefTimer.start() ;
        }
    }
    private void CancelRef() {
        if(mRefTimer != null){
            mRefTimer.cancel();
            mRefTimer =null;
        }
    }

    private void CancelAutoRefresh(){
        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
            mCountDownTimer =null;
        }
        if(mRefTimer != null){
            mRefTimer.cancel();
            mRefTimer =null;
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private void LoadDiagSerialList(int number, String subDistrictId, String diagNameEng, String diagNameBan, boolean search) {
        mPageDiagNumb =Math.max(number, 1) ;

        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code) );
        dataMap.put(SUBDISTRICT_ID, subDistrictId);
        dataMap.put(DIAG_NAME_ENG, diagNameEng);
        dataMap.put(DIAG_NAME_BAN, diagNameBan);
        dataMap.put(PAGE_NUM, mPageDiagNumb+"");
        dataMap.put(CLIENT_ID, AppAccess.clientID);

        mProgressLoading.setVisibility(View.VISIBLE);
        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.SEARCH_DIAG_SUBDISTRICT,
                response -> {
                    mProgressLoading.setVisibility(View.GONE);
                    mToastMsgTV.setVisibility(View.GONE);

                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            if(search){
                                mDiagSerList.clear();
                            }

                            mHomeDiagRV.setVisibility(View.VISIBLE);
                            mHomeDocRV.setVisibility(View.GONE);

                            JSONArray jsonArray = object.getJSONArray(DATA) ;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                List<SerialNumModel> serialNumModelList = new ArrayList<>() ;
                                JSONArray ja = jsonObject.getJSONArray(SER_LIST);
                                for(int j=0; j<ja.length(); j++){
                                    JSONObject JO = ja.getJSONObject(j);
                                    serialNumModelList.add(new SerialNumModel(
                                            JO.getString(DIAG_SER_ID),
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
                            mHomeFragDiagAdapter.notifyDataSetChanged();
                        }else{
                            mToastMsgTV.setVisibility(View.VISIBLE);
                            AccessController.DataNullMsgShow(mToastMsgTV,TEXT_DIAG_ENG, TEXT_DIAG_BAN );
                            ToastNotify.ShowToast(SubDistrictDocDiagActivity.this,object.getString(MSG));
                        }

                    } catch (JSONException ignored) {
                    }
                }, error -> ToastNotify.NetConnectionNotify(SubDistrictDocDiagActivity.this), dataMap) ;
    }
    @SuppressLint("NotifyDataSetChanged")
    private void LoadDocSpeList(int number, String url, String subDistrictId, String docNameEng, String docNameBan, String speNameEng, String speNameBan, boolean search) {
        mProgressLoading.setVisibility(View.VISIBLE);
        mPageDocNumb =Math.max(number, 1) ;
        AppAccess.getAppController().getAppNetworkController().makeRequest(url,
                response -> {
                    mToastMsgTV.setVisibility(View.GONE);
                    mProgressLoading.setVisibility(View.GONE);
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            mHomeDiagRV.setVisibility(View.GONE);
                            mHomeDocRV.setVisibility(View.VISIBLE);
                            if(search){
                                mDocSpeList.clear();
                                mHomeFragDocAdapter.notifyDataSetChanged();
                            }

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
                                        .setSubdistrict_id(jsonObject.getString(SUBDISTRICT_ID))
                                        .setSpe_list(specialistModelList)
                                        .build());
                            }
                            mHomeFragDocAdapter.notifyDataSetChanged();
                        }else{
                            mToastMsgTV.setVisibility(View.VISIBLE);
                            if(mSpeListView){
                                AccessController.DataNullMsgShow(mToastMsgTV,TEXT_DOC_SPE_ENG, TEXT_DOC_SPE_BAN );
                            }else{
                                AccessController.DataNullMsgShow(mToastMsgTV,TEXT_DOC_NAME_ENG, TEXT_DOC_NAME_BAN );
                            }
                        }
                    } catch (JSONException e) {
                        mProgressLoading.setVisibility(View.GONE);
                    }
                }, error -> {
                    mProgressLoading.setVisibility(View.GONE);
                    ToastNotify.NetConnectionNotify(SubDistrictDocDiagActivity.this);
                }, hashmapDoc(mPageDocNumb, subDistrictId, docNameEng, docNameBan, speNameEng, speNameBan) ) ;
    }
    private HashMap<String, String> hashmapDoc(int number, String subDistrictId, String docNameEng, String docNameBan, String speNameEng, String speNameBan) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DOC_NAME_ENG, docNameEng );
        dataMap.put(DOC_NAME_BAN, docNameBan );
        dataMap.put(SPE_NAME_ENG, speNameEng );
        dataMap.put(SPE_NAME_BAN, speNameBan );
        dataMap.put(SUBDISTRICT_ID, subDistrictId);
        dataMap.put(PAGE_NUM, number+"");
        dataMap.put(CLIENT_ID, AppAccess.clientID);
        return  dataMap;
    }


    private void LoadBannerImage() {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DIAG_ID, "");
        dataMap.put(SUBDISTRICT_ID, mSubDistrictID);
        dataMap.put(BANNER_STATE_ID_TITLE, String.valueOf(NUM_THREE));

        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.GET_ALL_BANNER,
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
                                mSmoothPager.setVisibility(View.VISIBLE);
                            }else{
                                mSmoothPager.setVisibility(View.GONE);
                            }
                            smoothPagerAdapter.notifyDataSetChanged();
                        }else{
                            mSmoothPager.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        mSmoothPager.setVisibility(View.GONE);
                    }
                }, error -> {
                    mSmoothPager.setVisibility(View.GONE);
                    ToastNotify.NetConnectionNotify(SubDistrictDocDiagActivity.this);
                }, dataMap) ;

    }
    private void VisibilitySmooth(int size) {
        if(size>0){
            mSmoothPager.setVisibility(View.GONE);
        }else{
            if(mBannerList.size()>0){
                mSmoothPager.setVisibility(View.VISIBLE);
            }
        }
    }

    private void Initialize() {
        mDocNameTIL = findViewById(R.id.text_input_doc_name) ;
        mDiagNameTIL = findViewById(R.id.text_input_diag_name) ;
        mSpeNameTIL = findViewById(R.id.text_input_specialist_name) ;
        mHomeDiagRV = findViewById(R.id.recycler_home_diag_frag) ;
        mHomeDocRV = findViewById(R.id.recycler_home_doc_frag) ;
        mDocNameTIE = findViewById(R.id.edit_doc_name) ;
        mDiagNameTIE = findViewById(R.id.edit_diag_name) ;
        mSpeNameTIE = findViewById(R.id.edit_specialist_name) ;
        mDistrictRL = findViewById(R.id.relative_district) ;
        mSwipeRL = findViewById(R.id.swipe_district_doc_diag) ;
        mSmoothPager = findViewById(R.id.smooth_banner_frag) ;
        mToastMsgTV = findViewById(R.id.text_list_msg) ;

        mNestedScroll = findViewById(R.id.nested_scroll_district) ;
        mProgressLoading = findViewById(R.id.progress_loading) ;
    }
    private void TextInputClear() {
        mDiagNameTIE.getText().clear();
        mDocNameTIE.getText().clear();
        mSpeNameTIE.getText().clear();
        mDiagBanText="";
        mDiagEngText="";
        mDocEngText = "";
        mDocBanText = "";
        mSpeBanText = "";
        mSpeEngText = "" ;

    }


    private void ChangeByLanguage() {
        if(AppAccess.languageEng){
            mDocNameTIL.setHint(SEARCH_DOC_NAME_ENG);
            mDiagNameTIL.setHint(SEARCH_DIAG_NAME_ENG);
            mSpeNameTIL.setHint(SEARCH_SPE_NAME_ENG);
        }else{
            mDocNameTIL.setHint(SEARCH_DOC_NAME_BAN);
            mDiagNameTIL.setHint(SEARCH_DIAG_NAME_BAN);
            mSpeNameTIL.setHint(SEARCH_SPE_NAME_BAN);
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onStart() {
        super.onStart();
        mHomeFragDiagAdapter.notifyDataSetChanged();
        mHomeFragDocAdapter.notifyDataSetChanged();

        ChangeByLanguage() ;
    }

}