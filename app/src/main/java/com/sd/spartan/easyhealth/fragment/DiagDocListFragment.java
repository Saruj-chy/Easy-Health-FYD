package com.sd.spartan.easyhealth.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.activity.DiagProFragActivity;
import com.sd.spartan.easyhealth.adapter.DoctorListAdapter;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;
import com.sd.spartan.easyhealth.interfaces.OnGoogleInterface;
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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class DiagDocListFragment extends Fragment implements OnGoogleInterface {
    private RecyclerView mDocListRV;
    private ConstraintLayout mSearchConstraint, mDocListFragCons;
    private LinearLayout searchLinearCircle ;
    private TextInputLayout docNameTIL, specialistNameTIL;
    private TextInputEditText docNameTET, specialistNameTET ;
    private CircleImageView searchCircleImg ;
    private ImageButton cancelImgBtn ;
    private ProgressBar progressBar ;

    private DoctorListAdapter doctorListAdapter ;
    private List<BuilderModel> mDocList ;

    private final CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder() ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_diag_doc_list, container, false);

        Initialize(root) ;

        mDocList = new ArrayList<>() ;
        LoadAdapter() ;
        LoadDiagnosticProfile(DiagProFragActivity.mDiagID, DiagProFragActivity.mSubdistrictID) ;
        builder.setToolbarColor(getResources().getColor(R.color.color_primary_1));
        builder.setShowTitle(true);


        searchCircleImg.setOnClickListener(v -> {
            docNameTET.setText("");
            specialistNameTET.setText("");
            ChangeByLanguage() ;
            mSearchConstraint.setVisibility(View.VISIBLE);
            searchLinearCircle.setVisibility(View.GONE);
            docNameTET.requestFocus() ;
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        });

        cancelImgBtn.setOnClickListener(v -> {
            mSearchConstraint.setVisibility(View.GONE);
            searchLinearCircle.setVisibility(View.VISIBLE);

            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        });

        docNameTET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Objects.requireNonNull(specialistNameTET.getText()).length()>0){
                    specialistNameTET.setText("");
                }
                searchNameDocFilter(s.toString(), 1, mDocList ) ;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        specialistNameTET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Objects.requireNonNull(docNameTET.getText()).length()>0){
                    docNameTET.setText("");
                }
                searchNameDocFilter(s.toString(), 2, mDocList ) ;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return root ;
    }
    @SuppressLint("NotifyDataSetChanged")
    private void searchNameDocFilter(String text, int type, List<BuilderModel> arrayList) {
        List<BuilderModel> filteredList = new ArrayList<>();

        switch (type){
            case 1:
                for (BuilderModel item : arrayList) {
                    if(AppAccess.languageEng){
                        if (item.getDoc_name_eng().toLowerCase().contains(text.toLowerCase())){
                            filteredList.add(item);
                        }
                    }else{
                        if (item.getDoc_name_ban().toLowerCase().contains(text.toLowerCase())){
                            filteredList.add(item);
                        }
                    }

                }

                break;
            case 2:
                for (BuilderModel item : arrayList) {
                    int temp=0 ;

                    for (SpecialistModel specialistModel : item.getSpe_list()){
                        if(AppAccess.languageEng){
                            if (specialistModel.getSpecialist_name_eng().toLowerCase().contains(text.toLowerCase())){
                                if(temp != 1){
                                    filteredList.add(item) ;
                                    temp = 1 ;
                                }
                            }
                        }else{
                            if (specialistModel.getSpecialist_name_ban().toLowerCase().contains(text.toLowerCase())){
                                if(temp != 1){
                                    filteredList.add(item) ;
                                    temp = 1 ;
                                }
                            }
                        }
                    }
                }

                break;
            default:
                break;
        }

        doctorListAdapter.searchFilterList(filteredList);
        doctorListAdapter.notifyDataSetChanged();
    }

    private void Initialize(View root) {
        searchLinearCircle = root.findViewById(R.id.linear_search_circle) ;
        mSearchConstraint = root.findViewById(R.id.constraint_search) ;
        mDocListRV = root.findViewById(R.id.recycler_doc_details_pro) ;
        docNameTIL = root.findViewById(R.id.text_input_doc_name) ;
        specialistNameTIL = root.findViewById(R.id.text_input_specialist_name) ;
        docNameTET = root.findViewById(R.id.edit_doc_name) ;
        specialistNameTET = root.findViewById(R.id.edit_specialist_name) ;
        searchCircleImg = root.findViewById(R.id.circle_img_search) ;
        cancelImgBtn = root.findViewById(R.id.img_btn_cancel) ;
        mDocListFragCons = root.findViewById(R.id.constraint_diag_frag) ;
        progressBar = root.findViewById(R.id.progress) ;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadAdapter() {
        doctorListAdapter = new DoctorListAdapter(getContext(), mDocList, DiagDocListFragment.this);
        LinearLayoutManager doctorListLLM = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mDocListRV.setLayoutManager(doctorListLLM);
        mDocListRV.setAdapter(doctorListAdapter);
        doctorListAdapter.notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    private void LoadDiagnosticProfile(String diagId, String subDistrictId) {

        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.RETRIVE_DIAG_DOC_LIST,
                response -> {
            progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            mDocList.clear();
                            mDocListFragCons.setVisibility(View.VISIBLE);



                            JSONArray ja_doc = object.getJSONArray(DOC_LIST) ;
                            for (int i = 0; i < ja_doc.length(); i++) {
                                JSONObject jsonObject = ja_doc.getJSONObject(i);

                                JSONArray JA_ser = jsonObject.getJSONArray(SER_LIST) ;
                                List<SerialNumModel> serialNumModelList = new ArrayList<>() ;
                                for(int j=0; j<JA_ser.length(); j++){
                                    JSONObject JO = JA_ser.getJSONObject(j);
                                    serialNumModelList.add(new SerialNumModel(
                                            JO.getString(SER_NUM_BAN),
                                            JO.getString(SER_NUM_ENG),
                                            JO.getString(DIAG_ID)
                                    ));
                                }


                                JSONArray JA_spe = jsonObject.getJSONArray(SPE_LIST);
                                List<SpecialistModel> specialistModelList = new ArrayList<>() ;
                                for(int j=0; j<JA_spe.length(); j++){
                                    JSONObject JO = JA_spe.getJSONObject(j);
                                    specialistModelList.add(new SpecialistModel(
                                            JO.getString(SPE_ID),
                                            JO.getString(SPE_NAME_BAN),
                                            JO.getString(SPE_NAME_ENG),
                                            JO.getString(DOC_ID)
                                    ));
                                }


                                mDocList.add(new ClassBuilder()
                                        .setDiag_id(jsonObject.getString(DIAG_ID))
                                        .setDoc_id(jsonObject.getString(DOC_ID))
                                        .setDoc_code(jsonObject.getString(DOC_CODE))
                                        .setDoc_time_detail_eng(jsonObject.getString(DOC_TIME_DETAIL_ENG))
                                        .setDoc_time_detail_ban(jsonObject.getString(DOC_TIME_DETAIL_BAN))
                                        .setDoc_time_input_type(jsonObject.getString(DOC_TIME_INPUT_TYPE))
                                        .setDoc_name_eng(jsonObject.getString(DOC_NAME_ENG))
                                        .setDoc_name_ban(jsonObject.getString(DOC_NAME_BAN))
                                        .setDegree_eng(jsonObject.getString(DEGREE_ENG))
                                        .setDegree_ban(jsonObject.getString(DEGREE_BAN))
                                        .setWork_place_eng(jsonObject.getString(WORK_PLACE_ENG))
                                        .setWork_place_ban(jsonObject.getString(WORK_PLACE_BAN))
                                        .setSubdistrict_id(jsonObject.getString(SUBDISTRICT_ID))
                                        .setMonth_name_eng(jsonObject.getString(MONTH_NAME_ENG))
                                        .setMonth_name_ban(jsonObject.getString(MONTH_NAME_BAN))
                                        .setWeek_name_eng(jsonObject.getString(WEEK_NAME_ENG))
                                        .setWeek_name_ban(jsonObject.getString(WEEK_NAME_BAN))
                                        .setTime_name_from_eng(jsonObject.getString(TIME_NAME_FROM_ENG))
                                        .setTime_name_from_ban(jsonObject.getString(TIME_NAME_FROM_BAN))
                                        .setTime_from_eng(jsonObject.getString(TIME_FROM_ENG))
                                        .setTime_from_ban(jsonObject.getString(TIME_FROM_BAN))
                                        .setTime_name_to_eng(jsonObject.getString(TIME_NAME_TO_ENG))
                                        .setTime_name_to_ban(jsonObject.getString(TIME_NAME_TO_BAN))
                                        .setTime_to_eng(jsonObject.getString(TIME_TO_ENG))
                                        .setTime_to_ban(jsonObject.getString(TIME_TO_BAN))
                                        .setExtra_week_time_eng(jsonObject.getString(EXTRA_WEEK_TIME_ENG))
                                        .setExtra_week_time_ban(jsonObject.getString(EXTRA_WEEK_TIME_BAN))
                                        .setDoc_website(jsonObject.getString(DOC_WEB))
                                        .setDoc_visit_fee(jsonObject.getString(DOC_VISIT_FEE))
                                        .setDoc_cmnt_eng(jsonObject.getString(DOC_CMNT_ENG))
                                        .setDoc_cmnt_ban(jsonObject.getString(DOC_CMNT_BAN))
                                        .setDoc_present(jsonObject.getString(DOC_PRESENT))
                                        .setSer_list(serialNumModelList)
                                        .setSpe_list(specialistModelList)
                                        .build());
                            }
                            doctorListAdapter.notifyDataSetChanged();

                        }
                    } catch (JSONException ignored) {
                    }
                }, error -> {
                    ToastNotify.NetConnectionNotify(getContext()) ;
                    progressBar.setVisibility(View.GONE);
                }, hashmap(diagId, subDistrictId)) ;
    }
    private HashMap<String, String> hashmap(String diagId, String subDistrictId) {
        HashMap<String, String> dataMap = new HashMap<>() ;
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code)) ;
        dataMap.put(DIAG_ID, diagId+"");
        dataMap.put(SUBDISTRICT_ID, subDistrictId+"");
        dataMap.put(CLIENT_ID, AppAccess.clientID);
        return  dataMap;
    }

    @Override
    public void OnGoogle(String link) {
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.setPackage(CHROME_PACKAGE);
        customTabsIntent.launchUrl(getActivity(), Uri.parse(link));
    }

    public void reload(){
        LoadDiagnosticProfile(DiagProFragActivity.mDiagID, DiagProFragActivity.mSubdistrictID) ;
    }

    private void ChangeByLanguage() {
        if(AppAccess.languageEng){
            docNameTIL.setHint(SEARCH_DOC_NAME_ENG);
            specialistNameTIL.setHint(SEARCH_SPE_NAME_ENG);
        }else{
            docNameTIL.setHint(SEARCH_DOC_NAME_BAN);
            specialistNameTIL.setHint(SEARCH_SPE_NAME_BAN);
        }
    }



}