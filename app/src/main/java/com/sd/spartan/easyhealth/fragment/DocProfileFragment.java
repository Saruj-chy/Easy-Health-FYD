package com.sd.spartan.easyhealth.fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.activity.DocProFragActivity;
import com.sd.spartan.easyhealth.adapter.SpecialistAdapter;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;
import com.sd.spartan.easyhealth.database.DatabaseHelperTest;
import com.sd.spartan.easyhealth.model.SpecialistModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class DocProfileFragment extends Fragment {
    private LinearLayout workPlaceLL, specialistLL ;
    private ConstraintLayout supDocConstraint;
    private TextView docDegreeNameTV, docDegreeTV, docWorkPlaceNameTV, docWorkPlaceTV, docSpecialistNameTV ;
    private RecyclerView docSpecialistRV;
    private ImageButton borderFavImg, mFillUpFavImg;

    private List<SpecialistModel> mSpeList ;
    private SpecialistAdapter nestedSpecialistAdapter ;
    private DatabaseHelperTest databaseHelperTest ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_doctor_profile, container, false);

        initialize(root) ;
        databaseHelperTest = new DatabaseHelperTest(getContext()) ;
        mSpeList = new ArrayList<>() ;

        loadDoctorAdapter() ;
        loadDoctorProfile() ;

        return root ;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadDoctorAdapter() {
        nestedSpecialistAdapter = new SpecialistAdapter(getContext(), mSpeList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        docSpecialistRV.setLayoutManager(linearLayoutManager) ;
        docSpecialistRV.setAdapter(nestedSpecialistAdapter) ;
        nestedSpecialistAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadDoctorProfile() {
        mSpeList.clear();

        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.RETRIVE_DOC_PROFILE,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            supDocConstraint.setVisibility(View.VISIBLE);

                            JSONObject doc_JO = object.getJSONObject(DOC_BIO);

                            ChangeProLanguage(doc_JO);
                            favouriteItemCheck() ;

                            JSONArray ja_spe = object.getJSONArray(SPE_LIST) ;
                            if(ja_spe.length() > 0){
                                specialistLL.setVisibility(View.VISIBLE);
                            }else{
                                specialistLL.setVisibility(View.GONE);
                            }
                            for (int i = 0; i < ja_spe.length(); i++) {
                                JSONObject jsonObject = ja_spe.getJSONObject(i);
                                mSpeList.add(new SpecialistModel(
                                        jsonObject.getString(SPE_ID),
                                        jsonObject.getString(SPE_NAME_BAN),
                                        jsonObject.getString(SPE_NAME_ENG),
                                        jsonObject.getString(DOC_ID)
                                )) ;
                            }
                            nestedSpecialistAdapter.notifyDataSetChanged();
                        }else{
                            supDocConstraint.setVisibility(View.GONE);
                        }
                    } catch (JSONException ignored) {
                    }
                }, error -> ToastNotify.NetConnectionNotify(getContext()), hashmap()) ;
    }

    private void ChangeProLanguage(JSONObject doc_jo) {
        try {
           if(AppAccess.languageEng){
               docDegreeNameTV.setText(QUALIFY_ENG);
               docDegreeTV.setText(doc_jo.getString(DEGREE_ENG));
               docSpecialistNameTV.setText(SPECIALIST_ENG);

               String workplace = doc_jo.getString(WORK_PLACE_ENG) ;
               if(workplace.equalsIgnoreCase("")){
                   workPlaceLL.setVisibility(View.GONE);
               }else{
                   workPlaceLL.setVisibility(View.VISIBLE);
               }
               docWorkPlaceNameTV.setText(WORK_ENG);
               docWorkPlaceTV.setText(workplace);
           }else{
               docDegreeNameTV.setText(QUALIFY_BAN);
               docDegreeTV.setText(doc_jo.getString(DEGREE_BAN));
               docSpecialistNameTV.setText(SPECIALIST_BAN);

               String workplace = doc_jo.getString(WORK_PLACE_BAN) ;
               if(workplace.equalsIgnoreCase("")){
                   workPlaceLL.setVisibility(View.GONE);
               }else{
                   workPlaceLL.setVisibility(View.VISIBLE);
               }
               docWorkPlaceNameTV.setText(WORK_BAN);
               docWorkPlaceTV.setText(workplace);
           }
        } catch (JSONException ignored) {
        }

    }

    private HashMap<String, String> hashmap() {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(DOC_ID, DocProFragActivity.mDocID);
        dataMap.put(CLIENT_ID, AppAccess.clientID);
        return  dataMap;
    }

    private void favouriteItemCheck() {
        Cursor y = databaseHelperTest.checkTable(DOC, AppAccess.clientID) ;
        if (y.moveToFirst()) {
            while (true) {
                if(DocProFragActivity.mDocID.equalsIgnoreCase(y.getString(1))){
                    mFillUpFavImg.setVisibility(View.VISIBLE) ;
                    borderFavImg.setVisibility(View.GONE) ;
                }
                if (y.isLast())
                    break;
                y.moveToNext();
            }
        }
        borderFavImg.setOnClickListener(v -> {
            boolean l = databaseHelperTest.InsertDocTbl(DocProFragActivity.mDocID) ;
            if(l){
                mFillUpFavImg.setVisibility(View.VISIBLE);
                borderFavImg.setVisibility(View.GONE);
            }else{
                mFillUpFavImg.setVisibility(View.GONE);
                borderFavImg.setVisibility(View.VISIBLE);
            }
        });
        mFillUpFavImg.setOnClickListener(v -> {
            boolean l = databaseHelperTest.DeleteDoctorTbl(DocProFragActivity.mDocID) ;
            if(l){
                mFillUpFavImg.setVisibility(View.GONE);
                borderFavImg.setVisibility(View.VISIBLE);
            }else{
                mFillUpFavImg.setVisibility(View.VISIBLE);
                borderFavImg.setVisibility(View.GONE);
            }
        });
    }



    private void initialize(View root) {
        workPlaceLL = root.findViewById(R.id.linear_doc_workplace) ;
        specialistLL = root.findViewById(R.id.linear_doc_specialist) ;
        supDocConstraint = root.findViewById(R.id.constraint_sup_pro) ;

        docDegreeNameTV = root.findViewById(R.id.text_degree_name) ;
        docDegreeTV = root.findViewById(R.id.text_doc_degree) ;
        docWorkPlaceNameTV = root.findViewById(R.id.text_work_place_name) ;
        docWorkPlaceTV = root.findViewById(R.id.text_work_place) ;
        docSpecialistNameTV = root.findViewById(R.id.text_spe_name) ;
        docSpecialistRV = root.findViewById(R.id.recycler_doc_spe) ;
        borderFavImg = root.findViewById(R.id.img_favorite_border);
        mFillUpFavImg = root.findViewById(R.id.img_favorite_fillup);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseHelperTest.close();
    }
}