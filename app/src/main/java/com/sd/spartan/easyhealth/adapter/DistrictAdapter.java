package com.sd.spartan.easyhealth.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.activity.DistrictDocDiagActivity;
import com.sd.spartan.easyhealth.activity.SubDistrictActivity;
import com.sd.spartan.easyhealth.model.BuilderModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class DistrictAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mCtx;
    private final List<BuilderModel> mItemList;

    public DistrictAdapter(Context mCtx, List<BuilderModel> mItemList) {
        this.mCtx = mCtx;
        this.mItemList = mItemList;
    }




    @Override
    public RecyclerView.@NotNull ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.layout_division, null);
        return new DistrictViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.@NotNull ViewHolder holder, final int position) {
        BuilderModel districtModel = mItemList.get(position);
        ((DistrictViewHolder) holder).bind(districtModel);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class DistrictViewHolder extends RecyclerView.ViewHolder {
        private final TextView mNameTV;

        public DistrictViewHolder(View itemView) {
            super(itemView);

            mNameTV = itemView.findViewById(R.id.text_name_layout);
        }

        public void bind(BuilderModel districtModel) {
            if(AppAccess.languageEng){
                mNameTV.setText(districtModel.getDistrict_eng());
            }else {
                mNameTV.setText(districtModel.getDistrict_ban());
            }

            String select_nav = mCtx.getSharedPreferences(USER, MODE_PRIVATE).getString(SELECT_NAV, "") ;
            mNameTV.setOnClickListener(v -> {
                Intent intent;
                if(select_nav.equalsIgnoreCase(DISTRICT_SMALL)){
                    intent = new Intent(mCtx, DistrictDocDiagActivity.class);
                    intent.putExtra(DISTRICT_ID, districtModel.getDistrict_id()) ;
                    intent.putExtra(DISTRICT_TITLE_BAN, districtModel.getDistrict_ban()) ;
                    intent.putExtra(DISTRICT_TITLE_ENG, districtModel.getDistrict_eng()) ;
                }else{
                    intent = new Intent(mCtx, SubDistrictActivity.class);
                    intent.putExtra(DISTRICT_ID, districtModel.getDistrict_id()) ;
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                mCtx.startActivity(intent);

            });

        }
    }
}