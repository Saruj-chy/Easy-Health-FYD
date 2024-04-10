package com.sd.spartan.easyhealth.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.database.DatabaseHelperTest;
import com.sd.spartan.easyhealth.model.BuilderModel;
import com.sd.spartan.easyhealth.model.SerialNumModel;

import java.util.ArrayList;
import java.util.List;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class DiagnosticListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mCtx;
    private final List<BuilderModel> mItemList;
    private DatabaseHelperTest mDatabaseHelperTest;

    public DiagnosticListAdapter(Context mCtx, List<BuilderModel> mItemList) {
        this.mCtx = mCtx ;
        this.mItemList = mItemList ;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.layout_diagonostic, null);
        mDatabaseHelperTest = new DatabaseHelperTest(mCtx) ;
        return new HomeViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        BuilderModel mDocDiagModel = mItemList.get(position);
        ((HomeViewHolder) holder).bind(mDocDiagModel);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class HomeViewHolder extends RecyclerView.ViewHolder {
        private final TextView mDiagNameTV, mDiagTimeTV, mDocPresentTV, mSerialTV, mVisitFeeNameTV, mVisitFeeTV, mDocCmntNameTV, mDocCmntTV;
        private final SerialNumberAdapter mSerialNumAdapter;
        private final ImageButton mBorderFavImgBtn, mFillUpFavImgBtn;
        private final List<SerialNumModel> mSerList ;
        private final LinearLayout mVisitFeeLinear, mDocComntLinear;

        public HomeViewHolder(View itemView) {
            super(itemView);

            mDiagNameTV = itemView.findViewById(R.id.text_diag_name_layout);
            mDiagTimeTV = itemView.findViewById(R.id.text_diag_time_layout);
            mDocPresentTV = itemView.findViewById(R.id.text_doc_present);
            mSerialTV = itemView.findViewById(R.id.text_serial);
            RecyclerView mSerialRV = itemView.findViewById(R.id.recycler_doc_serial_layout);
            mBorderFavImgBtn = itemView.findViewById(R.id.img_favorite_border);
            mFillUpFavImgBtn = itemView.findViewById(R.id.img_favorite_fillup);
            mVisitFeeLinear = itemView.findViewById(R.id.linear_visit_fee_layout);
            mDocComntLinear = itemView.findViewById(R.id.linear_doc_cmnt_layout);
            mVisitFeeNameTV = itemView.findViewById(R.id.text_visit_fee_name_layout);
            mVisitFeeTV = itemView.findViewById(R.id.text_visit_fee_layout);
            mDocCmntNameTV = itemView.findViewById(R.id.text_doc_cmnt_name_layout);
            mDocCmntTV = itemView.findViewById(R.id.text_doc_cmnt_layout);

            mSerList = new ArrayList<>() ;
            mSerialNumAdapter = new SerialNumberAdapter(mCtx, mSerList);
            mSerialRV.setLayoutManager(new GridLayoutManager(mCtx, 1, LinearLayoutManager.HORIZONTAL, false));
            mSerialRV.setAdapter(mSerialNumAdapter);
        }

        @SuppressLint("NotifyDataSetChanged")
        public void bind(BuilderModel docDiagModel) {

            List<SerialNumModel> serialNumModelList = docDiagModel.getSer_list() ;
            mSerList.clear();
            for(int i=0; i<serialNumModelList.size(); i++){
                this.mSerList.add(this.mSerList.size() , serialNumModelList.get(i)) ;
            }
            mSerialNumAdapter.notifyDataSetChanged();

            ChangeByLanguage(docDiagModel, false);

            if(!docDiagModel.getTime_from_ban().equalsIgnoreCase("") ||
                    !docDiagModel.getTime_from_eng().equalsIgnoreCase("") ||
                    !docDiagModel.getDoc_time_detail_eng().equalsIgnoreCase("") ||
                    !docDiagModel.getDoc_time_detail_ban().equalsIgnoreCase("") ){
                ChangeByLanguage(docDiagModel, true);
                 mDiagTimeTV.setVisibility(View.VISIBLE);
            }else{
                mDiagTimeTV.setVisibility(View.GONE);
            }

            if(docDiagModel.getDoc_present().equalsIgnoreCase(NO)){
                mDocPresentTV.setVisibility(View.VISIBLE);
            }else{
                mDocPresentTV.setVisibility(View.GONE);
            }

            if(docDiagModel.getDoc_visit_fee().equalsIgnoreCase("0.00")){
                mVisitFeeLinear.setVisibility(View.GONE);
            }else{
                mVisitFeeLinear.setVisibility(View.VISIBLE);
            }

            Cursor y = mDatabaseHelperTest.checkTable(DIAG, AppAccess.clientID) ;
            if (y.moveToFirst()) {
                while (true) {
                    if(docDiagModel.getDiag_id().equalsIgnoreCase(y.getString(1))){
                        mFillUpFavImgBtn.setVisibility(View.VISIBLE);
                        mBorderFavImgBtn.setVisibility(View.GONE);
                    }
                    if (y.isLast())
                        break;
                    y.moveToNext();
                }
            }
            mBorderFavImgBtn.setOnClickListener(v -> {
                boolean l = mDatabaseHelperTest.InsertDiagTbl(docDiagModel.getDiag_id()) ;
                if(l){
                    mFillUpFavImgBtn.setVisibility(View.VISIBLE);
                    mBorderFavImgBtn.setVisibility(View.GONE);
                }else{
                    mFillUpFavImgBtn.setVisibility(View.GONE);
                    mBorderFavImgBtn.setVisibility(View.VISIBLE);
                }
            });
            mFillUpFavImgBtn.setOnClickListener(v -> {
                boolean l = mDatabaseHelperTest.DeleteDiagnosticTbl(docDiagModel.getDiag_id()) ;
                if(l){
                    mFillUpFavImgBtn.setVisibility(View.GONE);
                    mBorderFavImgBtn.setVisibility(View.VISIBLE);
                }else{
                    mFillUpFavImgBtn.setVisibility(View.VISIBLE);
                    mBorderFavImgBtn.setVisibility(View.GONE);
                }
            });

        }

        private void ChangeByLanguage(BuilderModel docDiagModel, boolean week) {
            String inputType = docDiagModel.getDoc_time_input_type() ;
            if(AppAccess.languageEng){
                String add_eng = docDiagModel.getSubdistrict_eng()+", "+docDiagModel.getDistrict_eng()  ;
                String diag_details = docDiagModel.getDiag_address_eng()+", "+ add_eng ;

                String s1= docDiagModel.getDiag_name_eng();
                String s2 = s1+", "+diag_details ;
                SpannableString ss1=  new SpannableString(s2);
                ss1.setSpan(new RelativeSizeSpan(1.5f), 0, s1.length(), 0);
                ss1.setSpan(R.color.color_primary_1, 0, s1.length(), 0);
                mDiagNameTV.setText(ss1);

                mDocPresentTV.setText(DOC_NOT_AVAILABLE_ENG);
                mSerialTV.setText(SERIAL_ENG);
                mVisitFeeNameTV.setText(VISIT_FEE_ENG);
                mVisitFeeTV.setText(String.format("%s%s", docDiagModel.getDoc_visit_fee(), TAKA_ENG));

                if(week && inputType.equalsIgnoreCase("1")){
                    String weekEng = "" ;
                    if(!docDiagModel.getWeek_name_eng().equalsIgnoreCase("")){
                        weekEng = "Every "+ docDiagModel.getWeek_name_eng() ;
                    }
                    mDiagTimeTV.setText(new StringBuilder().append("Time: ").append(weekEng).append(" From ")

                            .append(docDiagModel.getTime_from_eng()).append(" ")
                            .append(docDiagModel.getTime_name_from_eng()).append(" To ")

                            .append(docDiagModel.getTime_to_eng()).append(" ")
                            .append(docDiagModel.getTime_name_to_eng()).append(" ") );

                    if(docDiagModel.getDoc_cmnt_eng().equalsIgnoreCase("")){
                        mDocComntLinear.setVisibility(View.GONE);
                    }else{
                        mDocComntLinear.setVisibility(View.VISIBLE);
                        mDocCmntNameTV.setText("e.g.: ");
                        mDocCmntTV.setText(docDiagModel.getDoc_cmnt_eng());
                    }

                }else if(week && inputType.equalsIgnoreCase("2")){
                    mDiagTimeTV.setText(docDiagModel.getDoc_time_detail_eng());
                }
            }else{
                String add_ban = docDiagModel.getSubdistrict_ban()+", "+docDiagModel.getDistrict_ban()  ;
                String diag_details = docDiagModel.getDiag_address_ban()+", "+ add_ban ;

                String s1= docDiagModel.getDiag_name_ban();
                String s2 = s1+", "+diag_details ;
                SpannableString ss1=  new SpannableString(s2);
                ss1.setSpan(new RelativeSizeSpan(1.5f), 0, s1.length(), 0);
                ss1.setSpan(R.color.color_primary_1, 0, s1.length(), 0);
                mDiagNameTV.setText(ss1);

                mDocPresentTV.setText(DOC_NOT_AVAILABLE_BAN);
                mSerialTV.setText(SERIAL_BAN);
                mVisitFeeNameTV.setText(VISIT_FEE_BAN);
                mVisitFeeTV.setText(String.format("%s%s", docDiagModel.getDoc_visit_fee(), TAKA_BAN));

                if(week && inputType.equalsIgnoreCase("1")){
                    String weekBan = "" ;
                    if(!docDiagModel.getWeek_name_ban().equalsIgnoreCase("")){
                        weekBan = "প্রতি "+ docDiagModel.getWeek_name_ban() ;
                    }
                    mDiagTimeTV.setText(new StringBuilder().append("সময়ঃ ").append(weekBan).append(" ")
                            .append(docDiagModel.getTime_name_from_ban()).append(" ")
                            .append(docDiagModel.getTime_from_ban()).append(" ঘটিকা হতে ")
                            .append(docDiagModel.getTime_name_to_ban()).append(" ")
                            .append(docDiagModel.getTime_to_ban()).append(" ঘটিকা"));

                    if(docDiagModel.getDoc_cmnt_eng().equalsIgnoreCase("")){
                        mDocComntLinear.setVisibility(View.GONE);
                    }else{
                        mDocComntLinear.setVisibility(View.VISIBLE);
                        mDocCmntNameTV.setText("বিঃদ্রঃ ");
                        mDocCmntTV.setText(docDiagModel.getDoc_cmnt_ban());
                    }

                }else if(week && inputType.equalsIgnoreCase("2")){
                    mDiagTimeTV.setText(docDiagModel.getDoc_time_detail_ban());
                }
            }
        }
    }
}