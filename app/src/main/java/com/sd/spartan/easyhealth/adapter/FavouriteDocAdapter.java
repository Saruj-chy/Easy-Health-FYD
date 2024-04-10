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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.activity.DocProFragActivity;
import com.sd.spartan.easyhealth.database.DatabaseHelperTest;
import com.sd.spartan.easyhealth.interfaces.OnDeleteInterface;
import com.sd.spartan.easyhealth.model.BuilderModel;
import com.sd.spartan.easyhealth.model.SpecialistModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class FavouriteDocAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mCtx;
    private final List<BuilderModel> mItemList;
    private final OnDeleteInterface mOnDeleteInterface;
    private DatabaseHelperTest databaseHelperTest ;


    public FavouriteDocAdapter(Context mCtx, List<BuilderModel> mItemList, OnDeleteInterface onDeleteInterface) {
        this.mCtx = mCtx;
        this.mItemList = mItemList;
        this.mOnDeleteInterface = onDeleteInterface;
    }

    @Override
    public RecyclerView.@NotNull ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.layout_doc_spe, null);
        databaseHelperTest = new DatabaseHelperTest(mCtx) ;
        return new HomeViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.@NotNull ViewHolder holder, final int position) {

        BuilderModel docDiagModel = mItemList.get(position);
        ((HomeViewHolder) holder).bind(docDiagModel);

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class HomeViewHolder extends RecyclerView.ViewHolder {
        private final TextView mDocNameTV, mDocTitleSpeTV, mDocDegreeTV;
        private final ConstraintLayout mSpeCL, mDocNameCL;
        private final ImageButton mBorderFavImgBtn, mFillUpFavImgBtn;

        private final SpecialistAdapter mSpecialistAdapter;
        private final List<SpecialistModel> mSpeList ;


        @SuppressLint("NotifyDataSetChanged")
        public HomeViewHolder(View itemView) {
            super(itemView);
            mDocNameTV = itemView.findViewById(R.id.text_doc_name_layout);
            mDocTitleSpeTV = itemView.findViewById(R.id.text_doc_spe_title_layout);
            mDocDegreeTV = itemView.findViewById(R.id.text_doc_degree_layout);
            mSpeCL = itemView.findViewById(R.id.constraint_specialist);
            mDocNameCL = itemView.findViewById(R.id.constraint_doc_name);
            RecyclerView mNestedRV = itemView.findViewById(R.id.recycler_specialist_layout);

            mBorderFavImgBtn = itemView.findViewById(R.id.img_favorite_border);
            mFillUpFavImgBtn = itemView.findViewById(R.id.img_favorite_fillup);

            mSpeList = new ArrayList<>() ;
            mSpecialistAdapter = new SpecialistAdapter(mCtx, mSpeList);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mCtx, LinearLayoutManager.HORIZONTAL, false);
            mNestedRV.setLayoutManager(mLinearLayoutManager);
            mNestedRV.setAdapter(mSpecialistAdapter);
            mSpecialistAdapter.notifyDataSetChanged();



        }


        @SuppressLint("NotifyDataSetChanged")
        public void bind(BuilderModel docDiagModel) {
            List<SpecialistModel> specialistModels = docDiagModel.getSpe_list() ;
            mSpeList.clear();
            for(int i=0; i<specialistModels.size(); i++){
                this.mSpeList.add(this.mSpeList.size() , specialistModels.get(i)) ;
            }
            mSpecialistAdapter.notifyDataSetChanged();

            ChangeByLanguage(docDiagModel) ;

            if(docDiagModel.getSpe_list().size() != 0){
                mDocTitleSpeTV.setVisibility(View.VISIBLE);
                mSpeCL.setVisibility(View.VISIBLE);
            }else{
                mDocTitleSpeTV.setVisibility(View.GONE);
                mSpeCL.setVisibility(View.GONE);
            }

            mDocNameCL.setOnClickListener(v -> {
                Intent intent = new Intent(mCtx, DocProFragActivity.class) ;
                intent.putExtra(DOC_ID, docDiagModel.getDoc_id() ) ;
                intent.putExtra(DOC_NAME_ENG, docDiagModel.getDoc_name_eng() ) ;
                intent.putExtra(DOC_NAME_BAN, docDiagModel.getDoc_name_ban() ) ;
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                mCtx.startActivity(intent);
            });

            Cursor y = databaseHelperTest.checkTable(DOC, AppAccess.clientID) ;
            if (y.moveToFirst()) {
                while (true) {
                    if(docDiagModel.getDoc_id().equalsIgnoreCase(y.getString(1))){
                        mFillUpFavImgBtn.setVisibility(View.VISIBLE) ;
                        mBorderFavImgBtn.setVisibility(View.GONE) ;
                    }
                    if (y.isLast())
                        break;
                    y.moveToNext();
                }
            }
            mFillUpFavImgBtn.setOnClickListener(v -> mOnDeleteInterface.onDelete(docDiagModel.getDoc_id(), docDiagModel.getDiag_id(), docDiagModel.getDoc_name_ban()));

        }

        private void ChangeByLanguage(BuilderModel docDiagModel) {
            if(AppAccess.languageEng){
                mDocNameTV.setText(docDiagModel.getDoc_name_eng());
                mDocDegreeTV.setText(docDiagModel.getDegree_eng());
                mDocTitleSpeTV.setText(SPECIALIST_ENG);
            }else{
                mDocNameTV.setText(docDiagModel.getDoc_name_ban());
                mDocDegreeTV.setText(docDiagModel.getDegree_ban());
                mDocTitleSpeTV.setText(SPECIALIST_BAN);
            }

        }
    }

}