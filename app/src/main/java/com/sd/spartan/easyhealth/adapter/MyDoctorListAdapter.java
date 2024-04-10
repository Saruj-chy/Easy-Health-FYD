package com.sd.spartan.easyhealth.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.sd.spartan.easyhealth.MainActivity;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.activity.DocProFragActivity;
import com.sd.spartan.easyhealth.activity.UpdateDoctorActivity;
import com.sd.spartan.easyhealth.fragment.MyDoctorsFragment;
import com.sd.spartan.easyhealth.interfaces.OnDeleteInterface;
import com.sd.spartan.easyhealth.interfaces.OnGoogleInterface;
import com.sd.spartan.easyhealth.model.BuilderModel;
import com.sd.spartan.easyhealth.model.SerialNumModel;
import com.sd.spartan.easyhealth.model.SpecialistModel;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class MyDoctorListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mCtx;
    private final List<BuilderModel> mItemList;
    private final OnDeleteInterface onDeleteInterface ;
    private final OnGoogleInterface onGoogleInterface ;

    public MyDoctorListAdapter(Context mCtx, List<BuilderModel> mItemList, MyDoctorsFragment myDoctorsFragment) {
        this.mCtx = mCtx;
        this.mItemList = mItemList;
        this.onDeleteInterface = myDoctorsFragment;
        this.onGoogleInterface = myDoctorsFragment;
    }

    @SuppressLint("InflateParams")
    @Override
    public RecyclerView.@NotNull ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_my_doctor, null);
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
        private final TextView mDocCodeTV, mDocNameTV, mDocDegreeTV, mDocTimeTV, mSerialTV, mVisitFeeNameTV, mVisitFeeTV, mDocCmntNameTV, mDocCmntTV, mProUpdateTV, mDocPresentTV, mDocTitleSpeTV, mWebProTV, mWebLinkProTV;
        private final ImageButton mUpImgBtn, mDeleteImgBtn;
        private final ConstraintLayout constraintSpe ;
        private final LinearLayout linearSerial, websiteLinear, visitFeeLinear, docComntLinear ;
        private final ConstraintLayout docProConstraint ;
        private final SerialNumberAdapter serialNumberAdapter ;
        private final SpecialistAdapter specialistAdapter ;
        private final List<SerialNumModel> mSerList ;
        private final List<SpecialistModel> mSpeList  ;

        @SuppressLint("NotifyDataSetChanged")
        public HomeViewHolder(View itemView) {
            super(itemView);

            mDocCodeTV = itemView.findViewById(R.id.text_doc_code_layout);
            mDocNameTV = itemView.findViewById(R.id.text_doc_name_layout);
            mDocDegreeTV = itemView.findViewById(R.id.text_doc_degree_layout);
            mDocTimeTV = itemView.findViewById(R.id.text_doc_time_layout);
            mSerialTV = itemView.findViewById(R.id.text_doc_serial_layout);
            mVisitFeeNameTV = itemView.findViewById(R.id.text_visit_fee_name_layout);
            mVisitFeeTV = itemView.findViewById(R.id.text_visit_fee_layout);
            mDocCmntNameTV = itemView.findViewById(R.id.text_doc_cmnt_name_layout);
            mDocCmntTV = itemView.findViewById(R.id.text_doc_cmnt_layout);
            RecyclerView mSerialRV = itemView.findViewById(R.id.recycler_doc_serial_layout);
            mProUpdateTV = itemView.findViewById(R.id.text_doc_pro_update);
            mUpImgBtn = itemView.findViewById(R.id.imgbtn_doc_update);
            mDeleteImgBtn = itemView.findViewById(R.id.imgbtn_doc_delete);
            mDocPresentTV = itemView.findViewById(R.id.text_doc_present);
            mDocTitleSpeTV = itemView.findViewById(R.id.text_doc_spe_layout);
            RecyclerView mSpecialistRV = itemView.findViewById(R.id.recycler_doc_spe_layout);
            constraintSpe = itemView.findViewById(R.id.constraint_spe_layout);
            linearSerial = itemView.findViewById(R.id.linear_serial_layout);
            visitFeeLinear = itemView.findViewById(R.id.linear_visit_fee_layout);
            docComntLinear = itemView.findViewById(R.id.linear_doc_cmnt_layout);
            websiteLinear = itemView.findViewById(R.id.linear_website) ;
            docProConstraint = itemView.findViewById(R.id.constraint_my_doc) ;
            mWebProTV = itemView.findViewById(R.id.text_website_pro) ;
            mWebLinkProTV = itemView.findViewById(R.id.text_website_link_pro) ;


            mSerList = new ArrayList<>() ;
            mSpeList = new ArrayList<>() ;
            serialNumberAdapter = new SerialNumberAdapter(mCtx, mSerList);
            mSerialRV.setLayoutManager(new GridLayoutManager(mCtx, 1, LinearLayoutManager.HORIZONTAL, false));
            mSerialRV.setAdapter(serialNumberAdapter);
            serialNumberAdapter.notifyDataSetChanged();

            specialistAdapter = new SpecialistAdapter(mCtx, mSpeList);
            mSpecialistRV.setLayoutManager(new GridLayoutManager(mCtx, 1, LinearLayoutManager.HORIZONTAL, false));
            mSpecialistRV.setAdapter(specialistAdapter);
            specialistAdapter.notifyDataSetChanged();


        }

        @SuppressLint("NotifyDataSetChanged")
        public void bind(BuilderModel docDiagModel) {
            List<SerialNumModel> serialNumModelList = docDiagModel.getSer_list() ;
            mSerList.clear();
            if(serialNumModelList.size() == 0){
                linearSerial.setVisibility(View.GONE);
            }else{
                linearSerial.setVisibility(View.VISIBLE);
            }
            for(int i=0; i<serialNumModelList.size(); i++){
                this.mSerList.add(this.mSerList.size() , serialNumModelList.get(i)) ;
            }
            serialNumberAdapter.notifyDataSetChanged();

            List<SpecialistModel> specialistModelList = docDiagModel.getSpe_list() ;
            mSpeList.clear();
            for(int i=0; i<specialistModelList.size(); i++){
                this.mSpeList.add(this.mSpeList.size() , specialistModelList.get(i)) ;
            }
            specialistAdapter.notifyDataSetChanged();
            if(specialistModelList.size() == 0 ){
                constraintSpe.setVisibility(View.GONE);
            }else{
                constraintSpe.setVisibility(View.VISIBLE);
            }


            if(docDiagModel.getDiag_id().equals(MainActivity.mDiagIdAdmin)){
                mDocCodeTV.setVisibility(View.VISIBLE);
                mDocCodeTV.setText(String.format("code: %s", docDiagModel.getDoc_code()));
            }else{
                mDocCodeTV.setVisibility(View.GONE);
            }



            if(docDiagModel.getDoc_visit_fee().equalsIgnoreCase("0.00")){
                visitFeeLinear.setVisibility(View.GONE);
            }else{
                visitFeeLinear.setVisibility(View.VISIBLE);
            }

            ChangeItemView(docDiagModel, false) ;

            String doc_website = docDiagModel.getDoc_website() ;
            if(doc_website.equalsIgnoreCase("") ){
                websiteLinear.setVisibility(View.GONE);
            }else{
                websiteLinear.setVisibility(View.VISIBLE);
            }
            mWebLinkProTV.setText(String.format("%s%s", HTTP_URL, doc_website));

            if(docDiagModel.getDoc_present().equalsIgnoreCase(NO)){
                mDocPresentTV.setVisibility(View.VISIBLE);
            }else{
                mDocPresentTV.setVisibility(View.GONE);
            }

            if( !docDiagModel.getTime_from_ban().equalsIgnoreCase("") ||
                    !docDiagModel.getTime_from_eng().equalsIgnoreCase("") ||
                    !docDiagModel.getDoc_time_detail_eng().equalsIgnoreCase("") ||
                    !docDiagModel.getDoc_time_detail_ban().equalsIgnoreCase("")   ){
                ChangeItemView(docDiagModel, true) ;
                mDocTimeTV.setVisibility(View.VISIBLE);

                mProUpdateTV.setVisibility(View.GONE);
                mUpImgBtn.setVisibility(View.VISIBLE);
            }else{
                mDocTimeTV.setVisibility(View.GONE);
                mUpImgBtn.setVisibility(View.GONE);
                mProUpdateTV.setVisibility(View.VISIBLE);
            }





            mUpImgBtn.setOnClickListener(v -> {
                Intent intent = new Intent(mCtx, UpdateDoctorActivity.class) ;
                intent.putExtra(DOC_ID, docDiagModel.getDoc_id() ) ;
                intent.putExtra(DOC_CODE, docDiagModel.getDoc_code() ) ;
                intent.putExtra(DIAG_ID, MainActivity.mDiagIdAdmin) ;
                intent.putExtra(SUBDISTRICT_ID, docDiagModel.getSubdistrict_id() ) ;
                mCtx.startActivity(intent);
            });
            mProUpdateTV.setOnClickListener(v -> {
                Intent intent = new Intent(mCtx, UpdateDoctorActivity.class) ;
                intent.putExtra(DOC_ID, docDiagModel.getDoc_id() ) ;
                intent.putExtra(DOC_CODE, docDiagModel.getDoc_code() ) ;
                intent.putExtra(DIAG_ID, MainActivity.mDiagIdAdmin) ;
                intent.putExtra(SUBDISTRICT_ID, docDiagModel.getSubdistrict_id() ) ;
                mCtx.startActivity(intent);
            });
            mDeleteImgBtn.setOnClickListener(v -> {
                if(AppAccess.languageEng){
                    onDeleteInterface.onDelete(docDiagModel.getDoc_id(), docDiagModel.getDiag_id(), docDiagModel.getDoc_name_eng());
                }else{
                    onDeleteInterface.onDelete(docDiagModel.getDoc_id(), docDiagModel.getDiag_id(), docDiagModel.getDoc_name_ban());
                }

            });
            mWebLinkProTV.setOnClickListener(v -> onGoogleInterface.OnGoogle(HTTP_URL+docDiagModel.getDoc_website()));
            docProConstraint.setOnClickListener(v -> {
                Intent intent = new Intent(mCtx, DocProFragActivity.class) ;
                intent.putExtra(DOC_ID, docDiagModel.getDoc_id() ) ;
                intent.putExtra(SUBDISTRICT_ID, docDiagModel.getSubdistrict_id() ) ;
                intent.putExtra(DOC_NAME_BAN, docDiagModel.getDoc_name_ban() ) ;
                intent.putExtra(DOC_NAME_ENG, docDiagModel.getDoc_name_eng() ) ;
                mCtx.startActivity(intent);
            });



        }

        private void ChangeItemView(BuilderModel docDiagModel, boolean docTime) {
           String inputType = docDiagModel.getDoc_time_input_type() ;
            if(AppAccess.languageEng){
                mDocNameTV.setText(docDiagModel.getDoc_name_eng());
                mDocDegreeTV.setText(docDiagModel.getDegree_eng());
                mDocPresentTV.setText(DOC_NOT_AVAILABLE_ENG);
                mSerialTV.setText(SERIAL_ENG);
                mWebProTV.setText(VISIT_FOR_SER_ENG);
                mVisitFeeTV.setText(String.format("%s%s", docDiagModel.getDoc_visit_fee(), TAKA_ENG));
                mVisitFeeNameTV.setText(VISIT_FEE_ENG);
                mDocTitleSpeTV.setText(SPECIALIST_ENG);

                if(docTime && inputType.equalsIgnoreCase("1")){
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
                }else if(docTime && inputType.equalsIgnoreCase("2")){
                    mDocTimeTV.setText(docDiagModel.getDoc_time_detail_eng());
                }
                if(docDiagModel.getDoc_cmnt_eng().equalsIgnoreCase("")){
                    docComntLinear.setVisibility(View.GONE);
                }else{
                    docComntLinear.setVisibility(View.VISIBLE);
                    mDocCmntNameTV.setText("e.g.: ");
                    mDocCmntTV.setText(docDiagModel.getDoc_cmnt_eng());
                }
            }
            else{
                mDocNameTV.setText(docDiagModel.getDoc_name_ban());
                mDocDegreeTV.setText(docDiagModel.getDegree_ban());
                mDocPresentTV.setText(DOC_NOT_AVAILABLE_BAN);
                mSerialTV.setText(SERIAL_BAN);
                mWebProTV.setText(VISIT_FOR_SER_BAN);
                mVisitFeeTV.setText(String.format("%s%s", docDiagModel.getDoc_visit_fee(), TAKA_BAN));
                mVisitFeeNameTV.setText(VISIT_FEE_BAN);
                mDocTitleSpeTV.setText(SPECIALIST_BAN);

                if(docTime  && inputType.equalsIgnoreCase("1")){
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
                        timeString.append(" এবং ").append(docDiagModel.getExtra_week_time_ban()) ;
                    }
                    mDocTimeTV.setText(timeString.toString() );
                }else if(docTime && inputType.equalsIgnoreCase("2")){
                    mDocTimeTV.setText(docDiagModel.getDoc_time_detail_ban());
                }


                if(docDiagModel.getDoc_cmnt_eng().equalsIgnoreCase("")){
                    docComntLinear.setVisibility(View.GONE);
                }else{
                    docComntLinear.setVisibility(View.VISIBLE);
                    mDocCmntNameTV.setText("বিঃদ্রঃ ");
                    mDocCmntTV.setText(docDiagModel.getDoc_cmnt_ban());
                }

            }
        }

    }


}