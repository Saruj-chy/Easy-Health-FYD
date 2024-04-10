package com.sd.spartan.easyhealth.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.sd.spartan.easyhealth.MainActivity;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.AccessControl.AppConfig;
import com.sd.spartan.easyhealth.model.InsertModel;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class InsertDoctorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mCtx;
    private final List<InsertModel> mItemList;

    public InsertDoctorAdapter(Context mCtx, List<InsertModel> mItemList) {
        this.mCtx = mCtx;
        this.mItemList = mItemList;
    }



    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_insert_doctor, parent,false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.@NotNull ViewHolder holder, final int position) {
        ((HomeViewHolder) holder).bind(mItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class HomeViewHolder extends RecyclerView.ViewHolder {

        private final TextView mDetailsTV;
        private final TextInputEditText mDocCodeTIE, mDocNameEngTIE, mDocNameBanTIE, mDegEngTIE, mDegBanTIE, mSpeNameEngTIE, mSpeNameBanTIE,
                mWorkPlaceEngTIE, mWorkPlaceBanTIE;
        private final ImageButton mDocRefImgBtn;

        public HomeViewHolder(View itemView) {
            super(itemView);

            mDetailsTV = itemView.findViewById(R.id.text_doctor_details);
            mDocCodeTIE = itemView.findViewById(R.id.edit_doc_code);
            mDocNameEngTIE = itemView.findViewById(R.id.edit_doc_name_eng);
            mDocNameBanTIE = itemView.findViewById(R.id.edit_doc_name_ban);
            mDegEngTIE = itemView.findViewById(R.id.edit_degree_eng);
            mDegBanTIE = itemView.findViewById(R.id.edit_degree_ban);
            mSpeNameEngTIE = itemView.findViewById(R.id.edit_specialist_name_eng);
            mSpeNameBanTIE = itemView.findViewById(R.id.edit_specialist_name_ban);
            mWorkPlaceEngTIE = itemView.findViewById(R.id.edit_work_place_eng);
            mWorkPlaceBanTIE = itemView.findViewById(R.id.edit_work_place_ban);
            mDocRefImgBtn = itemView.findViewById(R.id.img_doc_refresh);
        }

        @SuppressLint("SetTextI18n")
        public void bind(InsertModel insertModel) {

            if(mItemList.size()<=1){
                mDetailsTV.setVisibility(View.GONE);
            }else{
                mDetailsTV.setVisibility(View.VISIBLE);
                mDetailsTV.setText(DOCTOR_ENG_sml+(getAdapterPosition()+1));
            }


            mDocCodeTIE.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    insertModel.setDoc_code(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            mDocNameEngTIE.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    insertModel.setDoc_name_eng(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            mDocNameBanTIE.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    insertModel.setDoc_name_ban(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            mDegEngTIE.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    insertModel.setDegree_eng(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            mDegBanTIE.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    insertModel.setDegree_ban(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            mSpeNameEngTIE.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    insertModel.setSpecialist_eng(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            mSpeNameBanTIE.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    insertModel.setSpecialist_ban(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            mWorkPlaceEngTIE.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    insertModel.setWork_place_eng(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            mWorkPlaceBanTIE.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    insertModel.setWork_place_ban(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            mDocRefImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   String docCode = Objects.requireNonNull(mDocCodeTIE.getText()).toString().trim();
                    AppAccess.getAppController().getAppNetworkController().makeRequest(AppConfig.REFRESH_DOC_CODE,
                            response -> {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    if (object.getString(ERROR).equalsIgnoreCase(FALSE)) {
                                        JSONObject objectRef = object.getJSONObject(DATA);

                                        String objDocEng = objectRef.getString(DOC_NAME_ENG) ;
                                        String objDocBan = objectRef.getString(DOC_NAME_BAN) ;
                                        String subDocNameEng = objDocEng.substring(NUM_SEVEN) ;
                                        String subDocNameBan = objDocBan.substring(NUM_EIGHT) ;

                                        mDocNameBanTIE.setText(subDocNameBan);
                                        mDocNameEngTIE.setText( subDocNameEng );

                                        mDegBanTIE.setText(objectRef.getString(DEGREE_BAN));
                                        mDegEngTIE.setText(objectRef.getString(DEGREE_ENG));
                                        mWorkPlaceBanTIE.setText(objectRef.getString(WORK_PLACE_BAN));
                                        mWorkPlaceEngTIE.setText(objectRef.getString(WORK_PLACE_ENG));

                                        StringBuilder spe_ban = new StringBuilder();
                                        StringBuilder spe_eng = new StringBuilder();
                                        JSONArray ja_spe = objectRef.getJSONArray(SPE_LIST);
                                        for (int i = 0; i < ja_spe.length(); i++) {
                                            JSONObject jsonObject = ja_spe.getJSONObject(i);

                                           if(i== ja_spe.length()-1){
                                               spe_ban.append(jsonObject.getString(SPE_NAME_BAN)).append("");
                                               spe_eng.append(jsonObject.getString(SPE_NAME_ENG)).append("");
                                           }else{
                                               spe_ban.append(jsonObject.getString(SPE_NAME_BAN)).append(", ");
                                               spe_eng.append(jsonObject.getString(SPE_NAME_ENG)).append(", ");
                                           }
                                        }
                                        mSpeNameBanTIE.setText(spe_ban.toString());
                                        mSpeNameEngTIE.setText(spe_eng.toString());

                                    }
                                } catch (JSONException ignored) {
                                }
                            }, error -> {
                            }, hashRefreshMap(docCode));
                }

                private HashMap<String, String> hashRefreshMap(String docCode) {
                    HashMap<String, String> dataMap = new HashMap<>();
                    dataMap.put(mCtx.getString(R.string.prj_code_name), mCtx.getResources().getString(R.string.prj_code));
                    dataMap.put(DOC_CODE, docCode);
                    dataMap.put(DIAG_ID, MainActivity.mDiagIdAdmin);

                    return dataMap;
                }
            });

        }


    }
    @SuppressLint("NotifyDataSetChanged")
    public void addData() {
        notifyDataSetChanged();
    }
}