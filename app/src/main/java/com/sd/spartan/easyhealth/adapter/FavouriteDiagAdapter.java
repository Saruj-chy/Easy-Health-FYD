package com.sd.spartan.easyhealth.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.activity.DiagProFragActivity;
import com.sd.spartan.easyhealth.activity.DistrictDocDiagActivity;
import com.sd.spartan.easyhealth.database.DatabaseHelperTest;
import com.sd.spartan.easyhealth.interfaces.OnDeleteInterface;
import com.sd.spartan.easyhealth.model.BuilderModel;
import com.sd.spartan.easyhealth.model.SerialNumModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class FavouriteDiagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mCtx;
    private final List<BuilderModel> mItemList;
    private final String mClassName;
    private final OnDeleteInterface mOnDeleteInterface;

    private DatabaseHelperTest databaseHelperTest ;


    public FavouriteDiagAdapter(Context mCtx, List<BuilderModel> mItemList, String className, OnDeleteInterface onDeleteInterface) {
        this.mCtx = mCtx;
        this.mItemList = mItemList;
        this.mClassName = className;
        this.mOnDeleteInterface = onDeleteInterface;
    }

    @SuppressLint("InflateParams")
    @Override
    public RecyclerView.@NotNull ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);

        View view;
        view = inflater.inflate(R.layout.layout_diag_serial, null);
        databaseHelperTest = new DatabaseHelperTest(mCtx) ;

        return new HomeViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        BuilderModel docDiagModel = mItemList.get(position);
        ((HomeViewHolder) holder).bind(docDiagModel);

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class HomeViewHolder extends RecyclerView.ViewHolder {
        private final TextView mDiagNameTV;
        private final TextView mSerialTV;
        private final TextView mAddressTV;
        private final ConstraintLayout mSerialCL;
        private final ConstraintLayout mDiagViewCL;
        private final SerialNumberAdapter mSerialNumberAdapter;
        private final List<SerialNumModel> mSerList ;

        private final ImageButton borderFavImg;
        private final ImageButton fillupFavImg ;


        @SuppressLint("NotifyDataSetChanged")
        public HomeViewHolder(View itemView) {
            super(itemView);
            mDiagNameTV = itemView.findViewById(R.id.text_diag_name_layout);
            mSerialTV = itemView.findViewById(R.id.text_diag_serial_num_layout);
            mAddressTV = itemView.findViewById(R.id.text_diag_address_layout);
            mSerialCL = itemView.findViewById(R.id.constraint_serial);
            mDiagViewCL = itemView.findViewById(R.id.constraint_diag_name);
            RecyclerView mSerialRV = itemView.findViewById(R.id.recycler_diag_serial_num_layout);
            borderFavImg = itemView.findViewById(R.id.img_favorite_border);
            fillupFavImg = itemView.findViewById(R.id.img_favorite_fillup);

            mSerList = new ArrayList<>() ;
            mSerialNumberAdapter = new SerialNumberAdapter(mCtx, mSerList);
            LinearLayoutManager mManagerLLM = new LinearLayoutManager(mCtx, LinearLayoutManager.HORIZONTAL, false);
            mSerialRV.setLayoutManager(mManagerLLM);
            mSerialRV.setLayoutManager(new GridLayoutManager(mCtx, 2));
            mSerialRV.setAdapter(mSerialNumberAdapter);
            mSerialNumberAdapter.notifyDataSetChanged();

        }

        @SuppressLint("NotifyDataSetChanged")
        public void bind(BuilderModel docDiagModel) {
            List<SerialNumModel> serialNumModelList = docDiagModel.getSer_list() ;
            mSerList.clear();
            for(int i=0; i<serialNumModelList.size(); i++){
                this.mSerList.add(this.mSerList.size() , serialNumModelList.get(i)) ;
            }
            mSerialNumberAdapter.notifyDataSetChanged();

            ChangeByLanguageBy(docDiagModel) ;

            if(docDiagModel.getSer_list().size() != 0){
                mSerialTV.setVisibility(View.VISIBLE);
                mSerialCL.setVisibility(View.VISIBLE);
            }else{
                mSerialTV.setVisibility(View.GONE);
                mSerialCL.setVisibility(View.GONE);
            }

            mDiagViewCL.setOnClickListener(v -> {
                Intent intent = new Intent(mCtx, DiagProFragActivity.class) ;
                intent.putExtra(DIAG_ID, docDiagModel.getDiag_id() ) ;
                intent.putExtra(SUBDISTRICT_ID, docDiagModel.getSubdistrict_id() ) ;
                intent.putExtra(DIAG_NAME_ENG, docDiagModel.getDiag_name_eng() ) ;
                intent.putExtra(DIAG_NAME_BAN, docDiagModel.getDiag_name_ban() ) ;
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                mCtx.startActivity(intent);
            });

            Cursor y = databaseHelperTest.checkTable(DIAG, AppAccess.clientID) ;
            if (y.moveToFirst()) {
                while (true) {
                    if(docDiagModel.getDiag_id().equalsIgnoreCase(y.getString(1))){
                        fillupFavImg.setVisibility(View.VISIBLE);
                        borderFavImg.setVisibility(View.GONE);
                    }
                    if (y.isLast())
                        break;
                    y.moveToNext();
                }
            }
            fillupFavImg.setOnClickListener(v -> mOnDeleteInterface.onDelete(docDiagModel.getDoc_id(), docDiagModel.getDiag_id(), docDiagModel.getDiag_name_ban()));


        }

        private void ChangeByLanguageBy(BuilderModel docDiagModel) {
            if(AppAccess.languageEng){
                String add_eng;
                if (DISTRICT_SMALL.equals(mClassName)) {
                    add_eng = docDiagModel.getSubdistrict_eng() + ", " + DistrictDocDiagActivity.mDistrictEng;
                } else {
                    add_eng = docDiagModel.getSubdistrict_eng() + ", " + docDiagModel.getDistrict_eng();
                }

                mDiagNameTV.setText(docDiagModel.getDiag_name_eng());
                mAddressTV.setText(String.format("%s, %s", docDiagModel.getDiag_address_eng(), add_eng));
                mSerialTV.setText(CONTACT_ENG);
            }else{
                String add_ban;
                if (DISTRICT_SMALL.equals(mClassName)) {
                    add_ban = docDiagModel.getSubdistrict_ban() + ", " + DistrictDocDiagActivity.mDistrictBan;
                } else {
                    add_ban = docDiagModel.getSubdistrict_ban() + ", " + docDiagModel.getDistrict_ban();
                }

                mDiagNameTV.setText(docDiagModel.getDiag_name_ban());
                mAddressTV.setText(String.format("%s, %s", docDiagModel.getDiag_address_ban(), add_ban));
                mSerialTV.setText(CONTACT_BAN);
            }


        }

    }
}