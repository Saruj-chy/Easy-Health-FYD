package com.sd.spartan.easyhealth.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.model.SpecialistModel;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class SpecialistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mCtx;
    private final List<SpecialistModel> mItemList;

    public SpecialistAdapter(Context mCtx, List<SpecialistModel> mItemList ) {
        this.mCtx = mCtx;
        this.mItemList = mItemList;
    }

    @SuppressLint("InflateParams")
    @Override
    public RecyclerView.@NotNull ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_doc_specialist, null);
        return new SpecialistViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.@NotNull ViewHolder holder, int position) {
        SpecialistModel specialistModel = mItemList.get(position);

        ((SpecialistViewHolder) holder).bind(specialistModel);
    }


    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    static class SpecialistViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTextSpecialist;

        public SpecialistViewHolder(View itemView) {
            super(itemView);

            mTextSpecialist = itemView.findViewById(R.id.text_specialist_layout);
        }

        public void bind(SpecialistModel specialistModel) {
            if(AppAccess.languageEng){
                if(!specialistModel.getSpecialist_name_eng().equalsIgnoreCase("")){
                    mTextSpecialist.setText(specialistModel.getSpecialist_name_eng());
                }else{
                    mTextSpecialist.setVisibility(View.GONE);
                }
            }else{
                if(!specialistModel.getSpecialist_name_ban().equalsIgnoreCase("")){
                    mTextSpecialist.setText(specialistModel.getSpecialist_name_ban());
                }else{
                    mTextSpecialist.setVisibility(View.GONE);
                }
            }

        }

    }


}