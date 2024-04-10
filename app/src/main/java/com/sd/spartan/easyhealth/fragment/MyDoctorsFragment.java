package com.sd.spartan.easyhealth.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.activity.InsertDoctorActivity;
import com.sd.spartan.easyhealth.activity.MyDiagProfileFragActivity;
import com.sd.spartan.easyhealth.adapter.MyDoctorListAdapter;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;
import com.sd.spartan.easyhealth.interfaces.OnDeleteInterface;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;


public class MyDoctorsFragment extends Fragment implements OnDeleteInterface, OnGoogleInterface {
    private RecyclerView docListRV;
    private CircleImageView addImgBtn ;
    private TextView mDiagPermitTV, addDoctorTV ;
    private ConstraintLayout docListConstraint;
    private ProgressBar progressBar ;

    private List<BuilderModel> mDocList ;
    private MyDoctorListAdapter doctorListAdapter;

    private MaterialAlertDialogBuilder materialAlertDialogBuilder;
    private AlertDialog dialog ;

    private final CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder() ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_my_doctors, container, false);

        initialize(root) ;
        mDocList = new ArrayList<>() ;

        loadAdapter();
        loadDiagnosticProfile(MyDiagProfileFragActivity.mDiagID, MyDiagProfileFragActivity.mSubDistrictID);

        ChangeAddText() ;
        addImgBtn.setOnClickListener(v ->
                startActivity(new Intent(getContext(), InsertDoctorActivity.class)
                ));

        builder.setToolbarColor(getResources().getColor(R.color.color_primary_1));
        builder.setShowTitle(true);

        return  root ;
    }

    private void ChangeAddText() {
        if(AppAccess.languageEng){
            addDoctorTV.setText(ADD_NEW_ENG);
        }else{
            addDoctorTV.setText(ADD_NEW_BAN);
        }
    }

    private void CreateDeletePopup(String docId, String diagId, String name) {
        View view = getLayoutInflater().inflate(R.layout.popup_delete,  null);
        LinearLayout mYesLL = view.findViewById(R.id.linear_yes_no) ;
        ProgressBar mProgress = view.findViewById(R.id.progress_delete) ;
        TextView mNameItem = view.findViewById(R.id.text_del_name);
        TextView btnYes = view.findViewById(R.id.text_yes);
        TextView btnNo = view.findViewById(R.id.text_no);

        mNameItem.setText(String.format("%s?", name));

        btnYes.setOnClickListener(v -> {
            mProgress.setVisibility(View.VISIBLE);
            mYesLL.setVisibility(View.INVISIBLE);
            deleteDoctorProfile(docId, diagId);
        });

        btnNo.setOnClickListener(v -> dialog.cancel());


        materialAlertDialogBuilder.setView(view);
        dialog = materialAlertDialogBuilder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }


    @SuppressLint("NotifyDataSetChanged")
    private void deleteDoctorProfile(String docId, String diagId) {
        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.DELETE_SPECIFIC_DOC,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        ToastNotify.ShowToast(getContext(), object.getString(STATE));

                        loadDiagnosticProfile(MyDiagProfileFragActivity.mDiagID, MyDiagProfileFragActivity.mSubDistrictID);
                        doctorListAdapter.notifyDataSetChanged();
                        dialog.cancel();
                    } catch (JSONException ignored) {
                    }
                }, error -> {

                }, deleteHashmap( docId, diagId )) ;
    }

    private HashMap<String, String> deleteHashmap(String docId, String diagId) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DOC_ID, docId) ;
        dataMap.put(DIAG_ID, diagId) ;
        return  dataMap;
    }

    private void initialize(View root) {
        docListRV = root.findViewById(R.id.recycler_doc_details_pro) ;
        addImgBtn = root.findViewById(R.id.circle_img_add) ;
        mDiagPermitTV = root.findViewById(R.id.text_diag_permit) ;
        addDoctorTV = root.findViewById(R.id.text_add_doctor) ;
        docListConstraint = root.findViewById(R.id.constraint_doclist) ;
        progressBar = root.findViewById(R.id.progress) ;

        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext(), R.style.MaterialAlertDialog_rounded) ;


    }
    @SuppressLint("NotifyDataSetChanged")
    private void loadAdapter() {
        doctorListAdapter = new MyDoctorListAdapter(getContext(), mDocList, this);
        LinearLayoutManager doctorListLLM = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        docListRV.setLayoutManager(doctorListLLM);
        docListRV.setAdapter(doctorListAdapter);
        doctorListAdapter.notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    private void loadDiagnosticProfile(String diagId, String subDistrictId) {
        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.RETRIVE_DIAG_DOC_LIST,
                response -> {
            progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            mDocList.clear();
                            docListConstraint.setVisibility(View.VISIBLE);

                            String admin_status = object.getJSONObject(DIAG_BIO).getString(ADMIN_STATUS) ;
                            if(admin_status.equalsIgnoreCase(NO)){
                                mDiagPermitTV.setVisibility(View.VISIBLE);
                                docListConstraint.setVisibility(View.GONE);
                            }else{
                                mDiagPermitTV.setVisibility(View.GONE);
                                docListConstraint.setVisibility(View.VISIBLE);
                                JSONArray ja_doc = object.getJSONArray(DOC_LIST) ;
                                for (int i = 0; i < ja_doc.length(); i++) {
                                    JSONObject jsonObject = ja_doc.getJSONObject(i);

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
                                            .setDoc_name_eng(jsonObject.getString(DOC_NAME_ENG))
                                            .setDoc_name_ban(jsonObject.getString(DOC_NAME_BAN))
                                            .setDegree_eng(jsonObject.getString(DEGREE_ENG))
                                            .setDegree_ban(jsonObject.getString(DEGREE_BAN))
                                            .setWork_place_eng(jsonObject.getString(WORK_PLACE_ENG))
                                            .setWork_place_ban(jsonObject.getString(WORK_PLACE_BAN))
                                            .setDoc_time_detail_eng(jsonObject.getString(DOC_TIME_DETAIL_ENG))
                                            .setDoc_time_detail_ban(jsonObject.getString(DOC_TIME_DETAIL_BAN))
                                            .setDoc_time_input_type(jsonObject.getString(DOC_TIME_INPUT_TYPE))
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
                        }
                    } catch (JSONException ignored) {
                    }
                }, error -> {
                    ToastNotify.NetConnectionNotify(getContext()) ;
                    progressBar.setVisibility(View.GONE);
                }, hashmap(diagId, subDistrictId)) ;
    }
    private HashMap<String, String> hashmap(String diagid, String subDistrictId) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DIAG_ID, diagid+"");
        dataMap.put(SUBDISTRICT_ID, subDistrictId+"");

        return  dataMap;
    }

    @Override
    public void onDelete(String docId, String diagId, String name) {
        CreateDeletePopup(docId, diagId, name);
    }

    @Override
    public void OnGoogle(String link) {
        openCustomTabs(getActivity(), builder.build(), Uri.parse(link) );
    }
    public static void openCustomTabs(FragmentActivity activity, CustomTabsIntent customTabsIntent, Uri uri){
        customTabsIntent.intent.setPackage(CHROME_PACKAGE);
        customTabsIntent.launchUrl(activity,uri);

    }

    public void reload(){
        loadDiagnosticProfile(MyDiagProfileFragActivity.mDiagID, MyDiagProfileFragActivity.mSubDistrictID);
    }


}