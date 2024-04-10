package com.sd.spartan.easyhealth.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.spartan.easyhealth.AccessControl.ToastNotify;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.activity.DocProFragActivity;
import com.sd.spartan.easyhealth.adapter.DiagnosticListAdapter;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.model.BuilderModel;
import com.sd.spartan.easyhealth.model.ClassBuilder;
import com.sd.spartan.easyhealth.model.SerialNumModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class DocDiagListFragment extends Fragment {
    private ConstraintLayout mDocFragCons;
    private RecyclerView doctorDiagListRV ;
    private ProgressBar progressBar ;
    private DiagnosticListAdapter diagonosticListAdapter ;
    private List<BuilderModel> mDiagList ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_doctor_diag, container, false);
        doctorDiagListRV = root.findViewById(R.id.recycler_diag_details_pro) ;
        mDocFragCons = root.findViewById(R.id.constraint_doc_frag) ;
        progressBar= root.findViewById(R.id.progress) ;

        mDiagList = new ArrayList<>() ;
        loadDiagAdapter() ;
        loadDoctorProfile(DocProFragActivity.mDocID) ;

        return root ;
    }

    private void loadDiagAdapter() {
        diagonosticListAdapter = new DiagnosticListAdapter(getContext(), mDiagList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        doctorDiagListRV.setLayoutManager(linearLayoutManager);
        doctorDiagListRV.setAdapter(diagonosticListAdapter);

    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadDoctorProfile(String docid) {


        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.RETRIVE_DOC_DIAG_LIST,
                response -> {
            progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            mDiagList.clear();
                            mDocFragCons.setVisibility(View.VISIBLE);

                            JSONArray ja_diag = object.getJSONArray(DIAG_LIST) ;
                            for (int i = 0; i < ja_diag.length(); i++) {
                                JSONObject jsonObject = ja_diag.getJSONObject(i);

                                JSONArray jsonArray = jsonObject.getJSONArray(SER_LIST) ;
                                List<SerialNumModel> serialNumModelList = new ArrayList<>() ;
                                for(int j=0; j<jsonArray.length(); j++){
                                    JSONObject JO = jsonArray.getJSONObject(j);
                                    serialNumModelList.add(new SerialNumModel(
                                            JO.getString(SER_NUM_BAN),
                                            JO.getString(SER_NUM_ENG),
                                            JO.getString(DIAG_ID)
                                    ));
                                }
                                mDiagList.add(new ClassBuilder()
                                        .setDiag_id(jsonObject.getString(DIAG_ID))
                                        .setDoc_id(jsonObject.getString(DOC_ID))
                                        .setDoc_time_detail_eng(jsonObject.getString(DOC_TIME_DETAIL_ENG))
                                        .setDoc_time_detail_ban(jsonObject.getString(DOC_TIME_DETAIL_BAN))
                                        .setDoc_time_input_type(jsonObject.getString(DOC_TIME_INPUT_TYPE))
                                        .setDiag_name_eng(jsonObject.getString(DIAG_NAME_ENG))
                                        .setDiag_name_ban(jsonObject.getString(DIAG_NAME_BAN))
                                        .setDiag_address_eng(jsonObject.getString(DIAG_ADDRESS_ENG))
                                        .setDiag_address_ban(jsonObject.getString(DIAG_ADDRESS_BAN))
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
                                        .setDoc_visit_fee(jsonObject.getString(DOC_VISIT_FEE))
                                        .setDoc_cmnt_eng(jsonObject.getString(DOC_CMNT_ENG))
                                        .setDoc_cmnt_ban(jsonObject.getString(DOC_CMNT_BAN))
                                        .setDistrict_ban(jsonObject.getString(DISTRICT_TITLE_BAN))
                                        .setDistrict_eng(jsonObject.getString(DISTRICT_TITLE_ENG))
                                        .setSubdistrict_ban(jsonObject.getString(SUBDISTRICT_TITLE_BAN))
                                        .setSubdistrict_eng(jsonObject.getString(SUBDISTRICT_TITLE_ENG))
                                        .setDoc_present(jsonObject.getString(DOC_PRESENT))
                                        .setSer_list(serialNumModelList)
                                        .build());


                            }
                            diagonosticListAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException ignored) {
                    }
                }, error -> {
                    ToastNotify.NetConnectionNotify(getContext()) ;
                    progressBar.setVisibility(View.GONE);
                }, hashmap(docid)) ;
    }

    private HashMap<String, String> hashmap(String docid) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DOC_ID, docid);
        dataMap.put(CLIENT_ID, AppAccess.clientID);
        return  dataMap;
    }

    public void reload(){
        loadDoctorProfile(DocProFragActivity.mDocID) ;
    }
}