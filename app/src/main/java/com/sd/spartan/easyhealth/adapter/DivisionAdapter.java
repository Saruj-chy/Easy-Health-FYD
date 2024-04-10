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
import com.sd.spartan.easyhealth.activity.DistrictActivity;
import com.sd.spartan.easyhealth.AccessControl.AppConstants;
import com.sd.spartan.easyhealth.model.BuilderModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DivisionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mCtx;
    private final List<BuilderModel> mItemList;


    public DivisionAdapter(Context mCtx, List<BuilderModel> mItemList) {
        this.mCtx = mCtx;
        this.mItemList = mItemList;
    }


    @Override
    public RecyclerView.@NotNull ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.layout_division, null);
        return new DivisionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.@NotNull ViewHolder holder, final int position) {
        BuilderModel divisionModel = mItemList.get(position);
        ((DivisionViewHolder) holder).bind(divisionModel);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class DivisionViewHolder extends RecyclerView.ViewHolder {
       private final TextView mNameTV;
        public DivisionViewHolder(View itemView) {
            super(itemView);

            mNameTV = itemView.findViewById(R.id.text_name_layout);
        }

        public void bind(BuilderModel divisionModel) {
            if(AppAccess.languageEng){
                mNameTV.setText(divisionModel.getDivision_eng());
            }else{
                mNameTV.setText(divisionModel.getDivision_ban());
            }

            mNameTV.setOnClickListener(v -> {
                Intent intent = new Intent(mCtx, DistrictActivity.class) ;
                intent.putExtra(AppConstants.DIVISION_ID, divisionModel.getDivision_id()) ;
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                mCtx.startActivity(intent);
            });

        }
    }
}