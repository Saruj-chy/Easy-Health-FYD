package com.sd.spartan.easyhealth.fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.activity.DiagProFragActivity;
import com.sd.spartan.easyhealth.adapter.SerialNumberAdapter;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.database.DatabaseHelperTest;
import com.sd.spartan.easyhealth.model.SerialNumModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class DiagProfileFragment extends Fragment {
    private LinearLayout serialNumbLL, websiteLL;
    private ConstraintLayout supDocConstraint;
    private TextView diagAddressNameTV, diagAddressTV, diagSerialNameTV, websiteNameTV, websiteTV;
    private RecyclerView diagSerialNumRV ;
    private ImageButton borderFavImg, fillupFavImg ;

    private List<SerialNumModel> mSerialList ;
    private SerialNumberAdapter serialNumberAdapter ;
    private DatabaseHelperTest databaseHelperTest ;
    private final CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder() ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_diag_pro, container, false);
        Initialize(root);
        databaseHelperTest = new DatabaseHelperTest(getContext()) ;
        mSerialList = new ArrayList<>() ;

        loadAdapter() ;
        loadDiagnosticProfile(DiagProFragActivity.mDiagID, DiagProFragActivity.mSubdistrictID) ;


        builder.setToolbarColor(getResources().getColor(R.color.color_primary_1));
        builder.setShowTitle(true);
        websiteTV.setOnClickListener(v -> openCustomTabs(getActivity(), builder.build(), Uri.parse(websiteTV.getText().toString().trim()) ));
        return root ;
    }

    public void openCustomTabs(FragmentActivity activity, CustomTabsIntent customTabsIntent, Uri uri){
        customTabsIntent.intent.setPackage(CHROME_PACKAGE);
        customTabsIntent.launchUrl(activity,uri);

    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadAdapter() {
        serialNumberAdapter = new SerialNumberAdapter(getContext(), mSerialList);
        LinearLayoutManager serialNumLLM = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        diagSerialNumRV.setLayoutManager(serialNumLLM);
        diagSerialNumRV.setAdapter(serialNumberAdapter);
        serialNumberAdapter.notifyDataSetChanged();

    }


    @SuppressLint("NotifyDataSetChanged")
    private void loadDiagnosticProfile(String diagid, String subDistrictId) {
        mSerialList.clear();
        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.RETRIVE_DIAG_PROFILE,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            supDocConstraint.setVisibility(View.VISIBLE);

                            JSONObject diagJO = object.getJSONObject(DIAG_BIO);
                            ChangeProLanguage(diagJO);
                            favouriteItemCheck() ;

                            String diag_website = diagJO.getString(DIAG_WEB) ;
                            if(diag_website.equalsIgnoreCase("")){
                                websiteLL.setVisibility(View.GONE);
                            }else{
                                websiteLL.setVisibility(View.VISIBLE);
                            }
                            websiteTV.setText(String.format("%s%s", HTTP_URL, diag_website));



                            JSONArray ja_ser = object.getJSONArray(SER_LIST) ;
                            if(ja_ser.length()==0){
                                serialNumbLL.setVisibility(View.GONE);
                            }else{
                                serialNumbLL.setVisibility(View.VISIBLE);
                            }
                            for(int j=0; j<ja_ser.length(); j++){
                                JSONObject JO = ja_ser.getJSONObject(j);
                                mSerialList.add(new SerialNumModel(
                                        JO.getString(DIAG_SER_ID),
                                        JO.getString(SER_NUM_BAN),
                                        JO.getString(SER_NUM_ENG),
                                        JO.getString(DIAG_ID)
                                ));
                            }
                            serialNumberAdapter.notifyDataSetChanged();

                        }
                    } catch (JSONException ignored) {
                    }
                }, error -> supDocConstraint.setVisibility(View.GONE), hashmap(diagid, subDistrictId)) ;
    }

    private void ChangeProLanguage(JSONObject diag_jo) {
        try {
        if(AppAccess.languageEng){
            diagAddressNameTV.setText(ADDRESS_ENG);
            String fullAdd = diag_jo.getString(DIAG_ADDRESS_ENG)+", "+
                    diag_jo.getString(SUBDISTRICT_TITLE_ENG)+", "+diag_jo.getString(DISTRICT_TITLE_ENG);
            diagAddressTV.setText(fullAdd);
            diagSerialNameTV.setText(SERIAL_ENG);
            websiteNameTV.setText(VISIT_SITE_ENG);
        }else{
            diagAddressNameTV.setText(ADDRESS_BAN);
            String fullAdd = diag_jo.getString(DIAG_ADDRESS_BAN)+", "+ diag_jo.getString(SUBDISTRICT_TITLE_BAN)+", "+diag_jo.getString(DISTRICT_TITLE_BAN);
            diagAddressTV.setText(fullAdd);
            diagSerialNameTV.setText(SERIAL_BAN);
            websiteNameTV.setText(VISIT_SITE_BAN);
        }
        } catch (JSONException ignored) {
        }


    }

    private void favouriteItemCheck() {
        Cursor y = databaseHelperTest.checkTable(DIAG, AppAccess.clientID) ;
        if (y.moveToFirst()) {
            while (true) {
                if(DiagProFragActivity.mDiagID.equalsIgnoreCase(y.getString(1))){
                    fillupFavImg.setVisibility(View.VISIBLE);
                    borderFavImg.setVisibility(View.GONE);
                }
                if (y.isLast())
                    break;
                y.moveToNext();
            }
        }
        borderFavImg.setOnClickListener(v -> {
            boolean l = databaseHelperTest.InsertDiagTbl(DiagProFragActivity.mDiagID) ;
            if(l){
                fillupFavImg.setVisibility(View.VISIBLE);
                borderFavImg.setVisibility(View.GONE);
            }else{
                fillupFavImg.setVisibility(View.GONE);
                borderFavImg.setVisibility(View.VISIBLE);
            }
        });
        fillupFavImg.setOnClickListener(v -> {
            boolean l = databaseHelperTest.DeleteDiagnosticTbl(DiagProFragActivity.mDiagID) ;
            if(l){
                fillupFavImg.setVisibility(View.GONE);
                borderFavImg.setVisibility(View.VISIBLE);
            }else{
                fillupFavImg.setVisibility(View.VISIBLE);
                borderFavImg.setVisibility(View.GONE);
            }
        });
    }

    private HashMap<String, String> hashmap(String diagid, String subDistrictId) {
        HashMap<String, String> dataMap = new HashMap<>() ;
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code)) ;
        dataMap.put(DIAG_ID, diagid+"");
        dataMap.put(SUBDISTRICT_ID, subDistrictId+"");
        dataMap.put(CLIENT_ID, AppAccess.clientID);
        return  dataMap;
    }

    private void Initialize(View root) {
        serialNumbLL = root.findViewById(R.id.linear_serial_num) ;
        websiteLL = root.findViewById(R.id.linear_diag_website) ;
        supDocConstraint = root.findViewById(R.id.constraint_sup_pro) ;

        diagAddressNameTV = root.findViewById(R.id.text_address_name) ;
        diagAddressTV = root.findViewById(R.id.text_diag_address) ;
        diagSerialNameTV = root.findViewById(R.id.text_serial_numb_name) ;
        websiteNameTV = root.findViewById(R.id.text_website_name) ;
        websiteTV = root.findViewById(R.id.text_diag_website) ;
        diagSerialNumRV = root.findViewById(R.id.recycler_diag_serial) ;
        borderFavImg = root.findViewById(R.id.img_favorite_border);
        fillupFavImg = root.findViewById(R.id.img_favorite_fillup);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseHelperTest.close();
    }

}