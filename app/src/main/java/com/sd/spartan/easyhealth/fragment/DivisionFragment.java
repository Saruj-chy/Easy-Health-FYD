package com.sd.spartan.easyhealth.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.adapter.DivisionAdapter;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.AccessControl.ToastNotify;
import com.sd.spartan.easyhealth.model.BuilderModel;
import com.sd.spartan.easyhealth.model.ClassBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class DivisionFragment extends Fragment {
    private ConstraintLayout divisionConstraint ;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mDivisionRV;
    private ProgressBar mProgressBar ;
    private DivisionAdapter mDivisionAdapter ;

    private List<BuilderModel> mDivisionList ;
    private CountDownTimer countDownTimer;
    long remainingRefreshTime = 2000, mCountIntervalTime = 2500 ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_division, container, false);
        initialize(root) ;
        mDivisionList = new ArrayList<>() ;

        loadDivisionAdapter() ;
        loadDivisionList();

        swipeRefreshLayout.setColorSchemeColors(Color.GREEN,Color.RED,Color.BLUE);
        swipeRefreshLayout.setOnRefreshListener(this::setAutoRefresh);

        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadDivisionAdapter() {
        mDivisionAdapter = new DivisionAdapter(getContext(), mDivisionList ) ;
        mDivisionRV.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mDivisionRV.setAdapter(mDivisionAdapter);
        mDivisionAdapter.notifyDataSetChanged();
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
                    divisionConstraint.setVisibility(View.GONE);
                    loadDivisionList() ;
                }

                @Override
                public void onFinish() {
                    divisionConstraint.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                    cancelAutoRefresh() ;
                }
            };

            countDownTimer.start() ;
        }
    }
    private void cancelAutoRefresh(){
        if(countDownTimer!= null){
            countDownTimer.cancel();
            countDownTimer=null;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadDivisionList() {
        mDivisionList.clear();
        AppAccess.getAppController().getAppNetworkController().makeRequest( AppConfig.DIVISION_URL,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getString(ERROR).equalsIgnoreCase(FALSE)){
                            mDivisionRV.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                            JSONArray jsonArray = object.getJSONArray(DATA) ;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                mDivisionList.add(new ClassBuilder()
                                        .setDivision_id(jsonObject.getString(DIVISION_ID))
                                        .setDivision_eng(jsonObject.getString(DIVISION_TITLE_ENG))
                                        .setDivision_ban(jsonObject.getString(DIVISION_TITLE_BAN))
                                        .build()) ;
                            }
                            mDivisionAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException ignored) {
                    }
                }, error -> ToastNotify.NetConnectionNotify(getContext()), hashmap()) ;

    }
    private HashMap<String, String> hashmap() {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(getString(R.string.prj_code_name), getResources().getString(R.string.prj_code));
        return  dataMap;
    }



    private void initialize(View root) {
        mDivisionRV = root.findViewById(R.id.recycler_division) ;
        mProgressBar = root.findViewById(R.id.progress_division) ;

        swipeRefreshLayout = root.findViewById(R.id.swipe_division_frag) ;
        divisionConstraint = root.findViewById(R.id.constraint_division_frag) ;
    }
}