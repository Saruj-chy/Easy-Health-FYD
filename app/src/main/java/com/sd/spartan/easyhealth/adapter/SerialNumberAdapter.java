package com.sd.spartan.easyhealth.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.model.SerialNumModel;
import org.jetbrains.annotations.NotNull;
import java.util.List;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;


public class SerialNumberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mCtx;
    private final List<SerialNumModel> mItemList;

    public SerialNumberAdapter(Context mCtx, List<SerialNumModel> mItemList) {
        this.mCtx = mCtx;
        this.mItemList = mItemList;
    }

    @SuppressLint("InflateParams")
    @Override
    public RecyclerView.@NotNull ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_serial_num, null);
        return new HomeViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.@NotNull ViewHolder holder, final int position) {
        SerialNumModel mSerialNumModel = mItemList.get(position);
        ((HomeViewHolder) holder).bind(mSerialNumModel);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class HomeViewHolder extends RecyclerView.ViewHolder {
        private final TextView mSerialNumTV;

        public HomeViewHolder(View itemView) {
            super(itemView);

            mSerialNumTV = itemView.findViewById(R.id.text_serial_layout);
        }

        public void bind(SerialNumModel serialNumModel) {
            if(AppAccess.languageEng){
                mSerialNumTV.setText(serialNumModel.getSerial_num_eng());
            }else {
                mSerialNumTV.setText(serialNumModel.getSerial_num_ban());
            }

            mSerialNumTV.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(TEL_PHN+ serialNumModel.getSerial_num_eng()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
                mCtx.startActivity(intent);

            });

        }
    }
}