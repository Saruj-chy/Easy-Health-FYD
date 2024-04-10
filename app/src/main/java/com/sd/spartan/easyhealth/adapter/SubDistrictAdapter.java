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
import com.sd.spartan.easyhealth.activity.SubDistrictDocDiagActivity;
import com.sd.spartan.easyhealth.AccessControl.AppConstants;
import com.sd.spartan.easyhealth.interfaces.CreateUserInterface;
import com.sd.spartan.easyhealth.model.BuilderModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SubDistrictAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final Context mCtx;
    private final List<BuilderModel> mItemList;
    private final CreateUserInterface mCreateUserInterface ;

    public SubDistrictAdapter(Context mCtx, List<BuilderModel> mItemList, CreateUserInterface mCreateUserInterface) {
        this.mCtx = mCtx;
        this.mItemList = mItemList;
        this.mCreateUserInterface = mCreateUserInterface;
    }

    @SuppressLint("InflateParams")
    @Override
    public RecyclerView.@NotNull ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_division, null);
        return new DistrictViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.@NotNull ViewHolder holder, final int position) {
        BuilderModel subDistrictModel = mItemList.get(position);
        ((DistrictViewHolder) holder).bind(subDistrictModel);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class DistrictViewHolder extends RecyclerView.ViewHolder {
        TextView mNameTV;

        public DistrictViewHolder(View itemView) {
            super(itemView);
            mNameTV = itemView.findViewById(R.id.text_name_layout);
        }

        public void bind(BuilderModel subDistrictModel) {
            if(AppAccess.languageEng){
                mNameTV.setText(subDistrictModel.getSubdistrict_eng());
            }else{
                mNameTV.setText(subDistrictModel.getSubdistrict_ban());
            }

            itemView.setOnClickListener(v -> {
                if(AppAccess.createUser){
                    mCreateUserInterface.CreateUser(AppAccess.userPhnNumber, subDistrictModel.getSubdistrict_id());
                }else{
                    Intent intent = new Intent(mCtx, SubDistrictDocDiagActivity.class) ;
                    intent.putExtra(AppConstants.SUBDISTRICT_ID, subDistrictModel.getSubdistrict_id()) ;
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    mCtx.startActivity(intent);
                }
            });

        }
    }
}