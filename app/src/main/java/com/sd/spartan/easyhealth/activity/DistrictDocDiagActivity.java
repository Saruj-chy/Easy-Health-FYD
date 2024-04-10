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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.av.smoothviewpager.Smoolider.SmoothViewpager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sd.spartan.easyhealth.MainActivity;
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
import static com.sd.spartan.easyhealth.AccessControl.AppConstants.MSG;

public class DistrictDocDiagActivity extends AppCompatActivity {
    private TextInputLayout mDocNameTIL, mDiagNameTIL, mSpeNameTIL;
    private TextInputEditText mDocNameTIE, mDiagNameTIE, mSpeNameTIE;
    private RecyclerView mHomeDiagRV, mHomeDocRV;
    private RelativeLayout mDistrictRL;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NestedScrollView mNestedScroll ;
    private ProgressBar mProgressLoading;

    private List<BuilderModel> mDocSpeList;
    private List<BuilderModel> mDiagSerList;
    private HomeFragDiagAdapter mHomeFragDiagAdapter;
    private HomeFragDocAdapter mHomeFragDocAdapter;

    public static String mDistrictID, mDistrictBan, mDistrictEng;
    private boolean mChangeDoc = true, mDoctorRvView = false, diagFocus = false, docFocus = false, speFocus= false;
    private boolean mSpecialistView = false ;

    private CountDownTimer mCountDownTimer;
    private final long mRemainingRefreshTime = 2000, millisInFuture = 1000, mCountIntervalTime = 1500 ;
    private int mPageDiagNumb = 1, mPageDocNumb = 1 ;

    private String mDiagBanText="", mDiagEngText ="", mDocBanText = "", mDocEngText = "", mSpeBanText = "", mSpeEngText = "" ;

    private SmoothViewpager mSmothPager;
    private int mCurrentPage = 0;
    private CountDownTimer mBannerCountDownTimer;
    private final long mBannerRefreshTime = 60*60*1000 ;
    private boolean mBannerAction = true ;
    private final List<BuilderModel> mBannerList = new ArrayList<>() ;
    private SmoothPagerAdapter mSmoothPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mDistrictID = getIntent().getStringExtra(DISTRICT_ID).trim();
        mDistrictBan = getIntent().getStringExtra(DISTRICT_TITLE_BAN).trim();
        mDistrictEng = getIntent().getStringExtra(DISTRICT_TITLE_ENG).trim();

        mDocSpeList = new ArrayList<>() ;
        mDiagSerList = new ArrayList<>() ;


        LoadDiagSerialList(1, mDistrictID,"","", false) ;
        LoadNextPageList() ;
        LoadAllAdapter() ;

        mDiagNameTIE.setOnFocusChangeListener((v, hasFocus) -> {
            diagFocus= hasFocus ;
            if(hasFocus){
                mChangeDoc =true ;
                TextInputClear();
                mDiagSerList.clear();
                LoadDiagSerialList(1, mDistrictID, "", "" ,false);

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
                if(diagFocus && s.toString().length()>0 ) {
                    VisibilitySmoth(s.toString().length()) ;
                    if(AppAccess.languageEng){
                        mDiagEngText = s.toString() ;
                    }else{
                        mDiagBanText = s.toString() ;
                    }
                    mCountDownTimer = new CountDownTimer(millisInFuture, mCountIntervalTime) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            mProgressLoading.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onFinish() {
                            mProgressLoading.setVisibility(View.GONE);
                            LoadDiagSerialList(1, mDistrictID, mDiagEngText, mDiagBanText, true );
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
                mSpecialistView = false ;
                DoctorListFocusClick();
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
                    VisibilitySmoth(s.toString().length()) ;
                    if(AppAccess.languageEng){
                        mDocEngText = s.toString() ;
                    }else{
                        mDocBanText = s.toString() ;
                    }

                    mCountDownTimer = new CountDownTimer(millisInFuture, mCountIntervalTime) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            mProgressLoading.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onFinish() {
                            mProgressLoading.setVisibility(View.GONE);
                            LoadDocSpeList(1, AppConfig.SEARCH_DOCTOR_DISTRICT, mDistrictID, mDocEngText, mDocBanText,"","", true);
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
                mSpecialistView = true ;
                DoctorListFocusClick();
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
                if(speFocus && s.toString().length()>0){
                    VisibilitySmoth(s.toString().length()) ;
                    if(AppAccess.languageEng){
                        mSpeEngText = s.toString() ;
                    }else{
                        mSpeBanText = s.toString() ;
                    }

                    mCountDownTimer = new CountDownTimer(millisInFuture, mCountIntervalTime) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            mProgressLoading.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onFinish() {
                            mProgressLoading.setVisibility(View.GONE);
                            LoadDocSpeList(1, AppConfig.SEARCH_DOCTOR_SPE_DISTRICT, mDistrictID,"","", mSpeEngText, mSpeBanText, true);
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

        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.RED,Color.DKGRAY);
        mSwipeRefreshLayout.setOnRefreshListener(this::SetAutoRefresh);

        LoadBannerImage() ;
        mSmoothPagerAdapter = new SmoothPagerAdapter(this, mBannerList) ;
        mSmothPager.setAdapter(mSmoothPagerAdapter);
        mSmothPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
    private void DoctorListFocusClick() {
        if(mChangeDoc){
            mChangeDoc = false ;
            mDocSpeList.clear();
            LoadDocSpeList(1, AppConfig.SEARCH_DOCTOR_DISTRICT, mDistrictID,"","","","", false);
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private void LoadAllAdapter() {
        mHomeFragDiagAdapter = new HomeFragDiagAdapter(DistrictDocDiagActivity.this, mDiagSerList, "district" ){
            @Override
            public void loadNextPage(int pageNumber) {
                pageNumber = mPageDiagNumb++ ;
                mHomeFragDiagAdapter.setPageNumber(pageNumber);
            }
        } ;
        mHomeDiagRV.setLayoutManager(new LinearLayoutManager(DistrictDocDiagActivity.this,LinearLayoutManager.VERTICAL,false));
        mHomeDiagRV.setAdapter(mHomeFragDiagAdapter);
        mHomeFragDiagAdapter.notifyDataSetChanged();


        mHomeFragDocAdapter = new HomeFragDocAdapter(DistrictDocDiagActivity.this, mDocSpeList) {
            @Override
            public void loadNextPage(int pageNumber) {
                pageNumber = mPageDocNumb++ ;
                mHomeFragDocAdapter.setPageNumber(pageNumber);
            }
        };
        mHomeDocRV.setLayoutManager(new LinearLayoutManager(DistrictDocDiagActivity.this,LinearLayoutManager.VERTICAL,false));
        mHomeDocRV.setAdapter(mHomeFragDocAdapter);
        mHomeFragDocAdapter.notifyDataSetChanged();

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
                            if(mDoctorRvView && !mSpecialistView){
                                LoadDocSpeList(mPageDocNumb, AppConfig.SEARCH_DOCTOR_DISTRICT, mDistrictID, mDocEngText, mDocBanText, mSpeEngText, mSpeBanText, false);
                            } else if(mDoctorRvView && mSpecialistView){
                                LoadDocSpeList(mPageDocNumb, AppConfig.SEARCH_DOCTOR_SPE_DISTRICT, mDistrictID, mDocEngText, mDocBanText, mSpeEngText, mSpeBanText, false);
                            }else{
                                LoadDiagSerialList(mPageDiagNumb, mDistrictID, mDiagEngText, mDiagBanText, false);
                            }
                            CancelAutoRefresh();
                        }
                    };
                    mCountDownTimer.start() ;
                }
            });
        }
    }
    private void SetAutoRefresh(){
        if(mCountDownTimer == null){
            mCountDownTimer = new CountDownTimer(mRemainingRefreshTime, 500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mDistrictRL.setVisibility(View.GONE);
                }

                @Override
                public void onFinish() {
                    mDocSpeList.clear();
                    mDiagSerList.clear();
                    mChangeDoc =true ;
                    mSwipeRefreshLayout.setRefreshing(false);
                    mDistrictRL.setVisibility(View.VISIBLE);
                    mHomeDiagRV.setVisibility(View.VISIBLE);
                    mHomeDocRV.setVisibility(View.GONE);
                    LoadDiagSerialList(1, mDistrictID,"","", false);
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
    private void LoadDocSpeList(int number, String url, String district_id, String docNameEng, String docNameBan, String speNameEng, String speNameBan, boolean search) {
        mDoctorRvView = true ;
        mProgressLoading.setVisibility(View.VISIBLE);
        mPageDocNumb =Math.max(number, 1) ;
        AppAccess.getAppController().getAppNetworkController().makeRequest(url,
                response -> {
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
                                        .setSpe_list(specialistModelList)
                                        .build());
                            }
                            mHomeFragDocAdapter.notifyDataSetChanged();
                        }else{
                            ToastNotify.ShowToast(DistrictDocDiagActivity.this,object.getString(MSG));
                        }
                    } catch (JSONException e) {
                        mProgressLoading.setVisibility(View.GONE);
                    }
                }, error -> {
                    mProgressLoading.setVisibility(View.GONE);
                    ToastNotify.NetConnectionNotify(DistrictDocDiagActivity.this);
                }, hashmapDoc(mPageDocNumb, district_id, docNameEng, docNameBan, speNameEng, speNameBan)) ;
    }
    private HashMap<String, String> hashmapDoc(int number, String district_id, String docNameEng, String docNameBan, String speNameEng, String speNameBan) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DOC_NAME_ENG, docNameEng );
        dataMap.put(DOC_NAME_BAN, docNameBan );
        dataMap.put(SPE_NAME_ENG, speNameEng );
        dataMap.put(SPE_NAME_BAN, speNameBan );
        dataMap.put(DISTRICT_ID, district_id);
        dataMap.put(PAGE_NUM, number+"");
        dataMap.put(CLIENT_ID, AppAccess.clientID);
        return  dataMap;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadDiagSerialList(int number, String district_id, String diagNameEng, String diagNameBan, boolean search) {
        mDoctorRvView = false ;
        mProgressLoading.setVisibility(View.VISIBLE);
        mPageDiagNumb =Math.max(number, 1) ;
        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.SEARCH_DIAG_DISTRICT,
                response -> {
                    mProgressLoading.setVisibility(View.GONE);

                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            mHomeDiagRV.setVisibility(View.VISIBLE) ;
                            mHomeDocRV.setVisibility(View.GONE) ;
                            if(search){
                                mDiagSerList.clear();
                                mHomeFragDiagAdapter.notifyDataSetChanged();
                            }

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
                                        .setSer_list(serialNumModelList)
                                        .build()
                                );
                            }
                            mHomeFragDiagAdapter.notifyDataSetChanged();
                        }else{
                            ToastNotify.ShowToast(DistrictDocDiagActivity.this,object.getString(MSG));
                        }
                    } catch (JSONException ignored) {
                    }
                }, error -> {
                    mProgressLoading.setVisibility(View.GONE);
                    ToastNotify.NetConnectionNotify(DistrictDocDiagActivity.this);
                }, hashmapDiag(mPageDiagNumb, district_id, diagNameEng, diagNameBan )) ;
    }
    private HashMap<String, String> hashmapDiag(int number, String district_id, String diagNameEng, String diagNameBan) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DIAG_NAME_ENG, diagNameEng);
        dataMap.put(DIAG_NAME_BAN, diagNameBan);
        dataMap.put(DISTRICT_ID, district_id);
        dataMap.put(PAGE_NUM, number+"");
        dataMap.put(CLIENT_ID, AppAccess.clientID);
        return  dataMap;
    }

    private void LoadBannerImage() {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DIAG_ID, "");
        dataMap.put(SUBDISTRICT_ID, MainActivity.mSubDistrictIdAdmin);
        dataMap.put(DISTRICT_ID, mDistrictID);
        dataMap.put(BANNER_STATE_ID_TITLE, String.valueOf(NUM_two));

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
                                mSmothPager.setVisibility(View.VISIBLE);
                            }else{
                                mSmothPager.setVisibility(View.GONE);
                            }
                            mSmoothPagerAdapter.notifyDataSetChanged();
                        }else{
                            mSmothPager.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        mSmothPager.setVisibility(View.GONE);
                    }
                }, error -> mSmothPager.setVisibility(View.GONE), dataMap) ;

    }
    private void SetBannerRefresh(){
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


    private void VisibilitySmoth(int size) {
        if(size>0){
            mSmothPager.setVisibility(View.GONE);
        }else{
            if(mBannerList.size()>0){
            mSmothPager.setVisibility(View.VISIBLE);
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
        mNestedScroll = findViewById(R.id.nested_scroll_district) ;
        mSmothPager = findViewById(R.id.smooth_banner_frag) ;

        mDistrictRL = findViewById(R.id.relative_district) ;
        mSwipeRefreshLayout = findViewById(R.id.swipe_district_doc_diag) ;
        mProgressLoading = findViewById(R.id.progress_loading) ;
    }
    private void ChangebyLanguage() {
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onStart() {
        super.onStart();
        mCountDownTimer =null ;
        mHomeFragDiagAdapter.notifyDataSetChanged();
        mHomeFragDocAdapter.notifyDataSetChanged();

        ChangebyLanguage() ;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



}