package com.sd.spartan.easyhealth.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import com.sd.spartan.easyhealth.MainActivity;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.adapter.InsertDoctorAdapter;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;
import com.sd.spartan.easyhealth.model.InsertModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class InsertDoctorActivity extends AppCompatActivity {
    private EditText mDocCountET ;
    private Button mDocCountBtn, mDocInsertBtn ;
    private RecyclerView mDocAddRV ;
    private ProgressDialog mProgressLoading ;

    private List<InsertModel> mInsertList ;
    private InsertDoctorAdapter mInsertDoctorAdapter ;
    private List<String> mSpeEngList,  mSpeBanList ;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_doctor);

        Initialize() ;
        Toolbar toolbar = findViewById(R.id.app_bar_insert_doctor) ;
        setSupportActionBar(toolbar) ;
        ActionBar actionBar = getSupportActionBar() ;
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true) ;
        actionBar.setDisplayShowCustomEnabled(true) ;
        setTitle(R.string.insert_doctor) ;
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        mInsertList = new ArrayList<>() ;
        for(int i=0; i<1; i++){
            mInsertList.add(new InsertModel("","","","",
                    "","","","",""));

        }

        mDocAddRV.setHasFixedSize(true);
        mDocAddRV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mInsertDoctorAdapter = new InsertDoctorAdapter(getApplicationContext(), mInsertList);
        mDocAddRV.setAdapter(mInsertDoctorAdapter);
        GridLayoutManager mGridLM = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        mDocAddRV.setLayoutManager(mGridLM);
        mInsertDoctorAdapter.notifyDataSetChanged();

        mDocCountBtn.setOnClickListener(v -> {
            mInsertList.clear();
            mInsertDoctorAdapter.notifyDataSetChanged();
            String num = mDocCountET.getText().toString().trim() ;
            if(num.equalsIgnoreCase("") || Integer.parseInt(num) <1){
                num=1+"" ;
            }

            for(int i = 0; i<Integer.parseInt(num); i++){
                mInsertList.add(new InsertModel("","","","",
                        "","","","",""));
            }
            mInsertDoctorAdapter.notifyDataSetChanged();
        });

        mDocInsertBtn.setOnClickListener(v -> {
            mInsertDoctorAdapter.addData();
            boolean dataInsert = true ;
            JSONArray array = new JSONArray();
            for (int i = 0; i < mInsertList.size() ; i++ ) {
                JSONObject object = new JSONObject();
                if( mInsertList.get(i).getDoc_code() == null || mInsertList.get(i).getDoc_code().equals("")){
                    dataInsert=false ;
                }

                mSpeEngList = new ArrayList<>();
                mSpeBanList = new ArrayList<>();

                if(mInsertList.get(i).getSpecialist_eng().length() > 0){
                    List<String> spe_eng_items = Arrays.asList(mInsertList.get(i).getSpecialist_eng().split("\\s*,\\s*"));
                    List<String> spe_ban_items = Arrays.asList(mInsertList.get(i).getSpecialist_ban().split("\\s*,\\s*"));

                    List<String> spe_eng_itemsList = new ArrayList<>() ;
                    List<String> spe_ban_itemsList = new ArrayList<>() ;
                    for(int j=0; j<spe_eng_items.size(); j++){
                        if(!spe_eng_items.get(j).equalsIgnoreCase("")){
                            spe_eng_itemsList.add(spe_eng_items.get(j).trim()) ;
                        }
                    }
                    for(int j=0; j<spe_ban_items.size(); j++){
                        if(!spe_ban_items.get(j).equalsIgnoreCase("")){
                            spe_ban_itemsList.add(spe_ban_items.get(j).trim()) ;
                        }
                    }


                    int totalSize = Math.max(spe_eng_itemsList.size(), spe_ban_itemsList.size()) ;
                    for(int j=0; j<totalSize; j++){
                        String speEng;
                        String speBan;
                        if(spe_eng_itemsList.size()<= j ){
                            speEng = "" ;
                        }else{
                            speEng = spe_eng_itemsList.get(j) ;
                        }
                        if(spe_ban_itemsList.size() <= j){
                            speBan = "" ;
                        }else{
                            speBan = spe_ban_itemsList.get(j) ;
                        }
                        mSpeEngList.add("\""+speEng+"\"");
                        mSpeBanList.add("\""+speBan+"\"");
                    }
                }

                try {
                    object.put(DIAG_ID, MainActivity.mDiagIdAdmin);
                    object.put(DOC_CODE, mInsertList.get(i).getDoc_code() );
                    object.put(DOC_NAME_ENG , DOCTOR_ENG+ mInsertList.get(i).getDoc_name_eng() );
                    object.put(DOC_NAME_BAN , DOCTOR_BAN+ mInsertList.get(i).getDoc_name_ban() );
                    object.put(DEGREE_ENG , mInsertList.get(i).getDegree_eng() );
                    object.put(DEGREE_BAN , mInsertList.get(i).getDegree_ban() );
                    object.put(WORK_PLACE_ENG , mInsertList.get(i).getWork_place_eng() );
                    object.put(WORK_PLACE_BAN , mInsertList.get(i).getWork_place_ban() );
                    object.put(SPE_NAME_ENG , mSpeEngList.toString() );
                    object.put(SPE_NAME_BAN , mSpeBanList.toString() );
                    object.put(SUBDISTRICT_ID , MainActivity.mSubDistrictIdAdmin);

                    array.put(object);
                }catch (JSONException ignored) {
                }
            }
            if(!dataInsert){
                ToastNotify.ShowToast(InsertDoctorActivity.this, TOAST_DOC_CODE) ;
            }else{
                onInsertData(array) ;
            }

        });


    }


    void onInsertData(JSONArray array) {
        mProgressLoading.setTitle(mInsertList.size()+ LOADING_DOC_ADD);
        mProgressLoading.setMessage(LOADING_PLZ_WAIT);
        mProgressLoading.show();

        AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.MULTIPLE_DOC_INSERT,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString(ERROR).equalsIgnoreCase(FALSE)) {
                            onBackPressed();
                        }
                        ToastNotify.ShowToast(InsertDoctorActivity.this,object.getString(STATE) );
                    } catch (JSONException ignored) {

                    }
                    mProgressLoading.dismiss();
                }, error -> {
                    ToastNotify.NetConnectionNotify(InsertDoctorActivity.this);
                    mProgressLoading.dismiss();
                }, hashmap(array) ) ;

    }
    private HashMap<String, String> hashmap(JSONArray array){
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        dataMap.put(ARRAY, String.valueOf(array));
        return  dataMap;
    }


    private void Initialize() {
        mDocCountET = findViewById(R.id.edit_doc_add_count) ;
        mDocCountBtn = findViewById(R.id.btn_doc_add_count) ;
        mDocInsertBtn = findViewById(R.id.btn_doc_insert) ;
        mDocAddRV = findViewById(R.id.recycler_doc_add) ;

        mProgressLoading = new ProgressDialog(this);
    }
}