package com.sd.spartan.easyhealth.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
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

import static android.content.Context.MODE_PRIVATE;
import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class HomeFragment extends Fragment {
    private TextInputLayout mDocNameTIL, mDiagNameTIL, mSpeNameTIL;
    private TextInputEditText mDocNameTIE, mDiagNameTIE, mSpeNameTIE;
    private TextView mToastMsgTV ;
    private RecyclerView mHomeDiagRV, mHomeDocRV ;
    private RelativeLayout mHomeRL;
    private SwipeRefreshLayout swipeRefreshLayout ;
    private NestedScrollView mNestedScroll ;
    private ProgressBar mProgressLoading ;

    private List<BuilderModel> mDocSpeList ;
    private List<BuilderModel> mDiagSerList ;
    private HomeFragDiagAdapter homeFragDiagAdapter ;
    private HomeFragDocAdapter homeFragDocAdapter ;


    private String mSubDistrictId;
    private boolean changeDoc = true, diagFocus = false, docFocus = false, speFocus= false ;

    private CountDownTimer countDownTimer;
    long remainingRefreshTime = 2000, mCountIntervalTime = 2500 ;

    private int pageDiagNumb = 1, pageDocNumb = 1 ;
    private boolean doctorRvView = false;
    private boolean specialistView = false ;
    private String diagBanText="",diagEngText="", docBanText = "", docEngText = "", speBanText = "", speEngText = "" ;


    private SmoothViewpager mSmoothPager;
    private int mCurrentPage = 0;
    private List<BuilderModel> mBannerList ;
    private SmoothPagerAdapter mSmoothPagerAdapter;
    private CountDownTimer mBannerCountDownTimer;
    long mBannerRefreshTime = 60*60*1000 ;
    private boolean mBannerAction = true ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        initialize(root) ;
        mSubDistrictId = getContext().getSharedPreferences(USER, MODE_PRIVATE).getString(SUBDISTRICT_ID, "") ;

        mDocSpeList = new ArrayList<>() ;
        mDiagSerList = new ArrayList<>() ;

        loadAllAdapter() ;
        loadNextPageList() ;

        mDiagNameTIE.setOnFocusChangeListener((v, hasFocus) -> {
            diagFocus= hasFocus ;
            if( hasFocus){
                TextInputClear() ;
                changeDoc=true ;
                mDiagSerList.clear();
                loadDiagSerialList(1, mSubDistrictId,"","", false);
            }

        });
        mDiagNameTIE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cancelAutoRefresh();

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(diagFocus && s.toString().length()>0){
                    VisibilitySmooth(s.toString().length()) ;
                    if(AppAccess.languageEng){
                        diagEngText = s.toString() ;
                    }else{
                        diagBanText = s.toString() ;
                    }
                    countDownTimer = new CountDownTimer(1000, 1500) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            mProgressLoading.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onFinish() {
                            mProgressLoading.setVisibility(View.GONE);
                            loadDiagSerialList(1, mSubDistrictId, diagEngText, diagBanText, true );
                            cancelAutoRefresh();
                        }
                    };
                    countDownTimer.start() ;
                }
            }
        });
        mDocNameTIE.setOnFocusChangeListener((v, hasFocus) -> {
            docFocus = hasFocus ;
            if( hasFocus){
                TextInputClear() ;
                specialistView= false ;
            }
            if(changeDoc && hasFocus){
                changeDoc= false ;
                mDocSpeList.clear();
                loadDocSpeList(1, AppConfig.SEARCH_DOC_NAME_SUBDISTRICT, mSubDistrictId,"","","","", false);
            }
        });
        mDocNameTIE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cancelAutoRefresh();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(docFocus && s.toString().length()>0){
                    VisibilitySmooth(s.toString().length()) ;

                    if(AppAccess.languageEng){
                        docEngText = s.toString() ;
                    }else{
                        docBanText = s.toString() ;
                    }


                    countDownTimer = new CountDownTimer(1000, 1500) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            mProgressLoading.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFinish() {
                            mProgressLoading.setVisibility(View.GONE);
                            loadDocSpeList(1, AppConfig.SEARCH_DOC_NAME_SUBDISTRICT, mSubDistrictId,docEngText,docBanText,"","", true);
                            cancelAutoRefresh();
                        }
                    };
                    countDownTimer.start() ;
                }
            }
        });
        mSpeNameTIE.setOnFocusChangeListener((v, hasFocus) -> {
            speFocus = hasFocus ;
            if( hasFocus){
                TextInputClear() ;
                specialistView = true ;
            }
            if(changeDoc && hasFocus){
                changeDoc=false ;
                mDocSpeList.clear();
                loadDocSpeList(1, AppConfig.SEARCH_DOC_SPE_SUBDISTRICT, mSubDistrictId,"","","","", false);
            }
        });
        mSpeNameTIE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               cancelAutoRefresh();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(speFocus && s.toString().length()>0 ){
                    VisibilitySmooth(s.toString().length()) ;
                    if(AppAccess.languageEng){
                        speEngText = s.toString() ;
                    }else{
                        speBanText = s.toString() ;
                    }

                    countDownTimer = new CountDownTimer(1000, 1500) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            mProgressLoading.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onFinish() {
                            mProgressLoading.setVisibility(View.GONE);
                            loadDocSpeList(1, AppConfig.SEARCH_DOC_SPE_SUBDISTRICT, mSubDistrictId,"","",speEngText,speBanText, true);
                            cancelAutoRefresh();
                        }
                    };
                    countDownTimer.start() ;
                }
            }
        });

        AccessController.SetOnEditorActionListener(mDiagNameTIE, getActivity());
        AccessController.SetOnEditorActionListener(mDocNameTIE, getActivity());
        AccessController.SetOnEditorActionListener(mSpeNameTIE, getActivity());


        mBannerList = new ArrayList<>() ;
        loadBannerImage() ;
        mSmoothPagerAdapter = new SmoothPagerAdapter(getContext(), mBannerList) ;
        mSmoothPager.setAdapter(mSmoothPagerAdapter);
        mSmoothPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {

            }
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            public void onPageSelected(final int position) {
                mCurrentPage =position ;
            }
        });
        SetBannerRefresh() ;

        swipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.RED,Color.DKGRAY);
        swipeRefreshLayout.setOnRefreshListener(this::setAutoRefresh);


        return root;
    }

    private void TextInputClear() {
        mDiagNameTIE.getText().clear();
        mDocNameTIE.getText().clear();
        mSpeNameTIE.getText().clear();
        diagBanText="";
        diagEngText="";
        docBanText = "";
        docEngText = "";
        speBanText = "";
        speEngText = "" ;

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



    @SuppressLint("NotifyDataSetChanged")
    private void loadAllAdapter() {
        homeFragDiagAdapter = new HomeFragDiagAdapter(getContext(), mDiagSerList, "" ){
            @Override
            public void loadNextPage(int pageNumber) {
                pageNumber = pageDiagNumb++ ;
                homeFragDiagAdapter.setPageNumber(pageNumber);
            }
        } ;
        mHomeDiagRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
        mHomeDiagRV.setAdapter(homeFragDiagAdapter);
        homeFragDiagAdapter.notifyDataSetChanged();


        homeFragDocAdapter = new HomeFragDocAdapter(getContext(), mDocSpeList) {
            @Override
            public void loadNextPage(int pageNumber) {
                pageNumber = pageDocNumb++ ;
                homeFragDocAdapter.setPageNumber(pageNumber);
            }
        };
        mHomeDocRV.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL,false));
        mHomeDocRV.setAdapter(homeFragDocAdapter);
        homeFragDocAdapter.notifyDataSetChanged();
    }
    private void loadNextPageList() {
        if (mNestedScroll != null ) {
            mNestedScroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    countDownTimer = new CountDownTimer(remainingRefreshTime, mCountIntervalTime) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            mProgressLoading.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onFinish() {
                            mProgressLoading.setVisibility(View.GONE);
                            if(doctorRvView && !specialistView){
                                loadDocSpeList(pageDocNumb, AppConfig.SEARCH_DOC_NAME_SUBDISTRICT, mSubDistrictId, docEngText, docBanText, speEngText, speBanText, false);
                            } else if(doctorRvView && specialistView){
                                loadDocSpeList(pageDocNumb, AppConfig.SEARCH_DOC_SPE_SUBDISTRICT, mSubDistrictId, docEngText, docBanText, speEngText, speBanText, false);
                            }else{
                                loadDiagSerialList(pageDiagNumb, mSubDistrictId, diagEngText,diagBanText, false);
                            }
                            cancelAutoRefresh();
                        }
                    };
                    countDownTimer.start() ;

                }
            });

        }
    }

    private void initialize(View root) {
        mDocNameTIL = root.findViewById(R.id.text_input_doc_name) ;
        mDiagNameTIL = root.findViewById(R.id.text_input_diag_name) ;
        mSpeNameTIL = root.findViewById(R.id.text_input_specialist_name) ;
        mToastMsgTV = root.findViewById(R.id.text_list_msg) ;
        mHomeDiagRV = root.findViewById(R.id.recycler_home_diag_frag) ;
        mHomeDocRV = root.findViewById(R.id.recycler_home_doc_frag) ;
        mDocNameTIE = root.findViewById(R.id.edit_doc_name) ;
        mDiagNameTIE = root.findViewById(R.id.edit_diag_name) ;
        mSpeNameTIE = root.findViewById(R.id.edit_specialist_name) ;
        mHomeRL = root.findViewById(R.id.relative_home) ;
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_home) ;

        mSmoothPager = root.findViewById(R.id.smooth_banner_frag) ;
        mNestedScroll = root.findViewById(R.id.nested_scroll_home) ;
        mProgressLoading = root.findViewById(R.id.progress_loading) ;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadDocSpeList(int number, String url, String subDistrictId, String docNameEng, String docNameBan, String speNameEng, String speNameBan, boolean search) {
        doctorRvView = true ;
        mProgressLoading.setVisibility(View.VISIBLE);
        pageDocNumb =Math.max(number, 1) ;

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
                                homeFragDocAdapter.notifyDataSetChanged();
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
                            loadAllAdapter();
                            homeFragDocAdapter.notifyDataSetChanged();
                        } else{
                            mToastMsgTV.setVisibility(View.VISIBLE);
                            if(specialistView){
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
                    ToastNotify.NetConnectionNotify(getContext());
                }, hashmapDoc( pageDocNumb, subDistrictId, docNameEng, docNameBan, speNameEng, speNameBan )) ;
    }
    private HashMap<String, String> hashmapDoc( int number, String subDistrictId, String docNameEng, String docNameBan, String speNameEng, String speNameBan ) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DOC_NAME_ENG, docNameEng );
        dataMap.put(DOC_NAME_BAN, docNameBan );
        dataMap.put(SPE_NAME_ENG, speNameEng );
        dataMap.put(SPE_NAME_BAN, speNameBan );
        dataMap.put(SUBDISTRICT_ID, subDistrictId);
        dataMap.put(PAGE_NUM, number+"");
        dataMap.put(CLIENT_ID, AppAccess.clientID);
        return dataMap;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadDiagSerialList(int number, String subDistrictID, String diagNameEng, String diagNameBan, boolean search) {
        pageDiagNumb =Math.max(number, 1) ;
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(SUBDISTRICT_ID, subDistrictID);
        dataMap.put(DIAG_NAME_ENG, diagNameEng);
        dataMap.put(DIAG_NAME_BAN, diagNameBan);
        dataMap.put(PAGE_NUM, pageDiagNumb+"");
        dataMap.put(CLIENT_ID, AppAccess.clientID);

        doctorRvView= false ;
        mProgressLoading.setVisibility(View.VISIBLE);

        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.SEARCH_DIAG_SUBDISTRICT,
                response -> {
                    mToastMsgTV.setVisibility(View.GONE);
                    mProgressLoading.setVisibility(View.GONE);
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            mHomeDiagRV.setVisibility(View.VISIBLE);
                            mHomeDocRV.setVisibility(View.GONE);

                                if(search){
                                    mDiagSerList.clear();
                                    homeFragDiagAdapter.notifyDataSetChanged();
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
                                        .setDistrict_ban(jsonObject.getString(DISTRICT_TITLE_BAN))
                                        .setDistrict_eng(jsonObject.getString(DISTRICT_TITLE_ENG))
                                        .setSer_list(serialNumModelList)
                                        .build());
                            }
                            loadAllAdapter() ;
                            homeFragDiagAdapter.notifyDataSetChanged();
                        }else{
                            mToastMsgTV.setVisibility(View.VISIBLE);
                            AccessController.DataNullMsgShow(mToastMsgTV,"There is no other diagnostic by this name", "এই নামে আর কোন ডায়াগনষ্টিক নাই" );
                        }
                    } catch (JSONException e) {
                        mProgressLoading.setVisibility(View.GONE);
                    }
                }, error -> {
                    mProgressLoading.setVisibility(View.GONE);
                    ToastNotify.NetConnectionNotify(getContext());
                }, dataMap) ;
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
                    mHomeRL.setVisibility(View.GONE);
                }
                @Override
                public void onFinish() {
                    TextInputClear() ;
                    diagPrimaryState() ;
                    swipeRefreshLayout.setRefreshing(false);
                    cancelAutoRefresh() ;
                }
            };

            countDownTimer.start() ;
        }
    }

    private void diagPrimaryState() {
        mDocSpeList.clear() ;
        mDiagSerList.clear() ;
        changeDoc=true ;
        mHomeRL.setVisibility(View.VISIBLE);
        mHomeDiagRV.setVisibility(View.VISIBLE);
        mHomeDocRV.setVisibility(View.GONE);
        loadDiagSerialList(1, mSubDistrictId,"","", false) ;
    }

    private void cancelAutoRefresh(){
        if(countDownTimer!= null){
            countDownTimer.cancel();
            countDownTimer=null;
        }
    }


    private void loadBannerImage() {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DIAG_ID, "");
        dataMap.put(SUBDISTRICT_ID, MainActivity.mSubDistrictIdAdmin);
        dataMap.put(BANNER_STATE_ID_TITLE, String.valueOf(NUM_ONE));

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
                                mSmoothPager.setVisibility(View.VISIBLE);
                            }else{
                                mSmoothPager.setVisibility(View.GONE);
                            }
                            mSmoothPagerAdapter.notifyDataSetChanged();
                        }else{
                            mSmoothPager.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        mSmoothPager.setVisibility(View.GONE);
                    }
                }, error -> mSmoothPager.setVisibility(View.GONE), dataMap) ;

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

    @Override
    public void onStart() {
        super.onStart();
        countDownTimer=null ;
        clearInputET();
        diagPrimaryState();

        ChangeByLanguage();
    }
    private void ChangeByLanguage() {
        AppAccess.languageEng = getActivity().getSharedPreferences(USER, MODE_PRIVATE).getBoolean(LAN_ENG, false) ;
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



    @Override
    public void onStop() {
        super.onStop();
        cancelAutoRefresh();
        clearInputET();
    }

    private void clearInputET() {
        mDocNameTIE.clearFocus();
        mDiagNameTIE.clearFocus();
        mSpeNameTIE.clearFocus();
    }



}