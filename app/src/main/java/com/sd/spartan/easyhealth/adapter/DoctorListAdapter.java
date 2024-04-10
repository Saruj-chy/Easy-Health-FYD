package com.sd.spartan.easyhealth.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.activity.DocProFragActivity;
import com.sd.spartan.easyhealth.database.DatabaseHelperTest;
import com.sd.spartan.easyhealth.fragment.DiagDocListFragment;
import com.sd.spartan.easyhealth.interfaces.OnGoogleInterface;
import com.sd.spartan.easyhealth.model.BuilderModel;
import com.sd.spartan.easyhealth.model.SerialNumModel;
import com.sd.spartan.easyhealth.model.SpecialistModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class DoctorListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mCtx;
    private List<BuilderModel> mItemList;
    private final OnGoogleInterface mOnGoogleInterface;

    private DatabaseHelperTest mDatabaseHelperTest;

    public DoctorListAdapter(Context mCtx, List<BuilderModel> mItemList, DiagDocListFragment mOnGoogleInterface) {
        this.mCtx = mCtx;
        this.mItemList = mItemList;
        this.mOnGoogleInterface = mOnGoogleInterface;
    }

    public void searchFilterList(List<BuilderModel> filteredList) {
        mItemList = filteredList;
    }





    @Override
    public RecyclerView.@NotNull ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.layout_doctor, null);
        mDatabaseHelperTest = new DatabaseHelperTest(mCtx) ;
        return new HomeViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.@NotNull ViewHolder holder, final int position) {
        BuilderModel mDocDiagModel = mItemList.get(position);
        ((HomeViewHolder) holder).bind(mDocDiagModel);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class HomeViewHolder extends RecyclerView.ViewHolder {

        private final TextView mDocNameTV, mDocDegreeTV, mDocTimeTV, mSerialTV, mDocPresentTV, mDocTitleSpeTV,
                mVisitFeeNameTV, mVisitFeeTV, mDocCmntNameTV, mDocCmntTV, mWebProTV, mWebLinkProTV;
        private final ConstraintLayout mSpeCL, mDocDetailsCL;
        private final LinearLayout mWebsiteLL, mVisitFeeLL, mDocComntLL;

        private final ImageButton mBorderFavImgBtn, mFillUpFavImgBtn;

        private final SerialNumberAdapter mSerialNumberAdapter;
        private final SpecialistAdapter mSpecialistAdapter;

        private final List<SerialNumModel> mSerList ;
        private final List<SpecialistModel> mSpeList ;


        public HomeViewHolder(View itemView) {
            super(itemView);

            mDocNameTV = itemView.findViewById(R.id.text_doc_name_layout);
            mDocDegreeTV = itemView.findViewById(R.id.text_doc_degree_layout);
            mDocTimeTV = itemView.findViewById(R.id.text_doc_time_layout);
            mVisitFeeNameTV = itemView.findViewById(R.id.text_visit_fee_name_layout);
            mVisitFeeTV = itemView.findViewById(R.id.text_visit_fee_layout);
            mDocCmntNameTV = itemView.findViewById(R.id.text_doc_cmnt_name_layout);
            mDocCmntTV = itemView.findViewById(R.id.text_doc_cmnt_layout);
            mSerialTV = itemView.findViewById(R.id.text_doc_serial_layout);
            RecyclerView mSerialRV = itemView.findViewById(R.id.recycler_doc_serial_layout);
            mDocPresentTV = itemView.findViewById(R.id.text_doc_present);
            mDocTitleSpeTV = itemView.findViewById(R.id.text_doc_spe_layout);
            RecyclerView mSpecialistRV = itemView.findViewById(R.id.recycler_doc_spe_layout);
            mSpeCL = itemView.findViewById(R.id.constraint_spe_layout);
            mDocDetailsCL = itemView.findViewById(R.id.constraint_doc_details);
            mWebsiteLL = itemView.findViewById(R.id.linear_website) ;
            mVisitFeeLL = itemView.findViewById(R.id.linear_visit_fee_layout);
            mDocComntLL = itemView.findViewById(R.id.linear_doc_cmnt_layout);
            mWebProTV = itemView.findViewById(R.id.text_website_pro) ;
            mWebLinkProTV = itemView.findViewById(R.id.text_website_link_pro) ;

            mBorderFavImgBtn = itemView.findViewById(R.id.img_favorite_border);
            mFillUpFavImgBtn = itemView.findViewById(R.id.img_favorite_fillup);


            mSerList = new ArrayList<>() ;
            mSpeList = new ArrayList<>() ;
            mSerialNumberAdapter = new SerialNumberAdapter(mCtx, mSerList);
            mSerialRV.setLayoutManager(new GridLayoutManager(mCtx, 1, LinearLayoutManager.HORIZONTAL, false));
            mSerialRV.setAdapter(mSerialNumberAdapter);


            mSpecialistAdapter = new SpecialistAdapter(mCtx, mSpeList);
            mSpecialistRV.setLayoutManager(new GridLayoutManager(mCtx, 1, LinearLayoutManager.HORIZONTAL, false));
            mSpecialistRV.setAdapter(mSpecialistAdapter);





        }

        @SuppressLint("NotifyDataSetChanged")
        public void bind(BuilderModel docDiagModel) {
            List<SerialNumModel> serialNumModelList = docDiagModel.getSer_list() ;
            mSerList.clear();
            for(int i=0; i<serialNumModelList.size(); i++){
                this.mSerList.add(this.mSerList.size() , serialNumModelList.get(i)) ;
            }
            mSerialNumberAdapter.notifyDataSetChanged();

            List<SpecialistModel> specialistModelList = docDiagModel.getSpe_list() ;
            if(specialistModelList.size() == 0){
                mSpeCL.setVisibility(View.GONE);
            }
            mSpeList.clear();
            for(int i=0; i<specialistModelList.size(); i++){
                this.mSpeList.add(this.mSpeList.size() , specialistModelList.get(i)) ;
            }
            mSpecialistAdapter.notifyDataSetChanged();

            ChangeItemView(docDiagModel, false) ;


            String doc_website = docDiagModel.getDoc_website() ;
            if(doc_website.equalsIgnoreCase("")){
                mWebsiteLL.setVisibility(View.GONE);
            } else{
                mWebsiteLL.setVisibility(View.VISIBLE);
            }
            mWebLinkProTV.setText(String.format("%s%s", HTTP_URL, doc_website));

            if(!docDiagModel.getTime_from_ban().equalsIgnoreCase("") ||
                    !docDiagModel.getTime_from_eng().equalsIgnoreCase("") ||
                    !docDiagModel.getDoc_time_detail_eng().equalsIgnoreCase("") ||
                    !docDiagModel.getDoc_time_detail_ban().equalsIgnoreCase("") ){
                ChangeItemView(docDiagModel, true);
                mDocTimeTV.setVisibility(View.VISIBLE);
            }else{
                mDocTimeTV.setVisibility(View.GONE);
            }


            if(docDiagModel.getDoc_present().equalsIgnoreCase(NO)){
                mDocPresentTV.setVisibility(View.VISIBLE);
            }else{
                mDocPresentTV.setVisibility(View.GONE);
            }

            if(docDiagModel.getDoc_visit_fee().equalsIgnoreCase("0.00")){
                mVisitFeeLL.setVisibility(View.GONE);
            }else{
                mVisitFeeLL.setVisibility(View.VISIBLE);
            }



            mDocDetailsCL.setOnClickListener(v -> {
                Intent intent = new Intent(mCtx, DocProFragActivity.class) ;
                intent.putExtra(DOC_ID, docDiagModel.getDoc_id() ) ;
                intent.putExtra(SUBDISTRICT_ID, docDiagModel.getSubdistrict_id() ) ;
                intent.putExtra(DOC_NAME_ENG, docDiagModel.getDoc_name_eng() ) ;
                intent.putExtra(DOC_NAME_BAN, docDiagModel.getDoc_name_ban() ) ;
                mCtx.startActivity(intent);
            });
            mWebLinkProTV.setOnClickListener(v -> mOnGoogleInterface.OnGoogle(HTTP_URL+docDiagModel.getDoc_website()));


            Cursor y = mDatabaseHelperTest.checkTable(DOC, AppAccess.clientID) ;
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
            mBorderFavImgBtn.setOnClickListener(v -> {
                boolean l = mDatabaseHelperTest.InsertDocTbl(docDiagModel.getDoc_id()) ;
                if(l){
                    mFillUpFavImgBtn.setVisibility(View.VISIBLE);
                    mBorderFavImgBtn.setVisibility(View.GONE);
                }else{
                    mFillUpFavImgBtn.setVisibility(View.GONE);
                    mBorderFavImgBtn.setVisibility(View.VISIBLE);
                }
            });
            mFillUpFavImgBtn.setOnClickListener(v -> {
                boolean l = mDatabaseHelperTest.DeleteDoctorTbl(docDiagModel.getDoc_id()) ;
                if(l){
                    mFillUpFavImgBtn.setVisibility(View.GONE);
                    mBorderFavImgBtn.setVisibility(View.VISIBLE);
                }else{
                    mFillUpFavImgBtn.setVisibility(View.VISIBLE);
                    mBorderFavImgBtn.setVisibility(View.GONE);
                }
            });


        }
        private void ChangeItemView(BuilderModel docDiagModel, boolean diagTime) {
            String inputType = docDiagModel.getDoc_time_input_type() ;
            if(AppAccess.languageEng){
                mDocNameTV.setText(docDiagModel.getDoc_name_eng());
                mDocDegreeTV.setText(docDiagModel.getDegree_eng());
                mDocPresentTV.setText(DOC_NOT_AVAILABLE_ENG);
                mSerialTV.setText(SERIAL_ENG);
                mWebProTV.setText(VISIT_FOR_SER_ENG);
                mVisitFeeNameTV.setText(VISIT_FEE_ENG);
                mVisitFeeTV.setText(String.format("%s%s", docDiagModel.getDoc_visit_fee(), TAKA_ENG));
                mDocTitleSpeTV.setText(SPECIALIST_ENG);

                if(diagTime && inputType.equalsIgnoreCase("1")){
                    StringBuilder scheduleEng = new StringBuilder() ;
                    if(!docDiagModel.getMonth_name_eng().equalsIgnoreCase("")  ){
                        scheduleEng.append("Every month ").append(docDiagModel.getMonth_name_eng()).append(" week ") ;
                    }
                    if(!docDiagModel.getWeek_name_eng().equalsIgnoreCase("")){
                        scheduleEng.append("every ").append(docDiagModel.getWeek_name_eng()) ;
                    }else{
                        scheduleEng.append("everyday ") ;
                    }

                    StringBuilder timeString = new StringBuilder().append("Time: ").append(scheduleEng.toString())
                            .append(" From ").append(docDiagModel.getTime_from_eng())
                            .append(" ").append(docDiagModel.getTime_name_from_eng()).append(" to ")
                            .append(docDiagModel.getTime_to_eng()).append(" ")
                            .append(docDiagModel.getTime_name_to_eng()).append(" ")
                            ;
                    if( !docDiagModel.getExtra_week_time_eng().equalsIgnoreCase("") ){
                        timeString.append("  and ").append(docDiagModel.getExtra_week_time_eng()) ;
                    }
                    mDocTimeTV.setText(timeString.toString() );

                    if(docDiagModel.getDoc_cmnt_eng().equalsIgnoreCase("")){
                        mDocComntLL.setVisibility(View.GONE);
                    }else{
                        mDocComntLL.setVisibility(View.VISIBLE);
                        mDocCmntNameTV.setText("e.g: ");
                        mDocCmntTV.setText(docDiagModel.getDoc_cmnt_eng());
                    }
                }else if(diagTime && inputType.equalsIgnoreCase("2")){
                    mDocTimeTV.setText(docDiagModel.getDoc_time_detail_eng());
                }
            }else{
                mDocNameTV.setText(docDiagModel.getDoc_name_ban());
                mDocDegreeTV.setText(docDiagModel.getDegree_ban());
                mDocPresentTV.setText(DOC_NOT_AVAILABLE_BAN);
                mSerialTV.setText(SERIAL_BAN);
                mWebProTV.setText(VISIT_FOR_SER_BAN);
                mDocTitleSpeTV.setText(SPECIALIST_BAN);
                mVisitFeeNameTV.setText(VISIT_FEE_BAN);
                mVisitFeeTV.setText(String.format("%s%s", docDiagModel.getDoc_visit_fee(), TAKA_BAN));

                if(diagTime && inputType.equalsIgnoreCase("1")){
                    StringBuilder scheduleBan = new StringBuilder() ;
                    if(!docDiagModel.getMonth_name_ban().equalsIgnoreCase("")  ){
                        scheduleBan.append("প্রতি মাসের ").append(docDiagModel.getMonth_name_ban()).append(" সপ্তাহ ") ;
                    }
                    if(!docDiagModel.getWeek_name_ban().equalsIgnoreCase("")){
                        scheduleBan.append("প্রতি ").append(docDiagModel.getWeek_name_ban()) ;
                    }else{
                        scheduleBan.append("প্রতিদিন ") ;
                    }

                    StringBuilder timeString = new StringBuilder().append("সময়ঃ ").append(scheduleBan.toString()).append(" ").append(docDiagModel.getTime_name_from_ban()).append(" ").append(docDiagModel.getTime_from_ban()).append(" ঘটিকা হতে ")
                            .append(docDiagModel.getTime_name_to_ban()).append(" ").append(docDiagModel.getTime_to_ban()).append(" ঘটিকা");

                    if( !docDiagModel.getExtra_week_time_ban().equalsIgnoreCase("") ){
                        timeString.append("  এবং ").append(docDiagModel.getExtra_week_time_ban() ) ;
                    }
                    mDocTimeTV.setText(timeString.toString() );

                    if(docDiagModel.getDoc_cmnt_eng().equalsIgnoreCase("")){
                        mDocComntLL.setVisibility(View.GONE);
                    }else{
                        mDocComntLL.setVisibility(View.VISIBLE);
                        mDocCmntNameTV.setText("বিঃদ্রঃ ");
                        mDocCmntTV.setText(docDiagModel.getDoc_cmnt_ban());
                    }
                }else if(diagTime && inputType.equalsIgnoreCase("2")){
                    mDocTimeTV.setText(docDiagModel.getDoc_time_detail_ban());
                }
            }
        }
    }
}