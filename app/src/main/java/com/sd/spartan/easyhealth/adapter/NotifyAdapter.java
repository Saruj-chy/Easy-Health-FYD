package com.sd.spartan.easyhealth.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.database.DatabaseHelperTest;
import com.sd.spartan.easyhealth.interfaces.OnDeleteInterface;
import com.sd.spartan.easyhealth.model.NotifyModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NotifyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mCtx;
    private final List<NotifyModel> mItemList;
    private DatabaseHelperTest mDatabaseHelperTest;
    private final OnDeleteInterface onDeleteInterface ;

    public NotifyAdapter(Context mCtx, List<NotifyModel> mItemList, OnDeleteInterface onDeleteInterface) {
        this.mCtx = mCtx;
        this.mItemList = mItemList;
        this.onDeleteInterface = onDeleteInterface ;
    }



    @SuppressLint("InflateParams")
    @Override
    public RecyclerView.@NotNull ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_notification, null);
        mDatabaseHelperTest = new DatabaseHelperTest(mCtx) ;
        return new NotifyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.@NotNull ViewHolder holder, final int position) {
        NotifyModel mNotifyModel = mItemList.get(position);
        ((NotifyViewHolder) holder).bind(mNotifyModel);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class NotifyViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout relativeNotify ;
        private final TextView mNotifyTitleTV, mNotifyMsgTV;
        private final ImageView mDotNotifyImgView;
        private final ImageButton mDeleteNotifyImgBtn;

        public NotifyViewHolder(View itemView) {
            super(itemView);

            relativeNotify = itemView.findViewById(R.id.relative_notify);
            mNotifyTitleTV = itemView.findViewById(R.id.text_notify_title_layout);
            mNotifyMsgTV = itemView.findViewById(R.id.text_notify_msg_layout);
            mDotNotifyImgView = itemView.findViewById(R.id.img_notify);
            mDeleteNotifyImgBtn = itemView.findViewById(R.id.imgbtn_notify_delete);

        }

        @SuppressLint("SetTextI18n")
        public void bind(NotifyModel notifyModel) {

            mNotifyTitleTV.setText( notifyModel.getNotify_title() );
            mNotifyMsgTV.setText( notifyModel.getNotify_msg() );

            if(notifyModel.getNotify_read().equalsIgnoreCase("0")){
                mDotNotifyImgView.setVisibility(View.VISIBLE);
            }else{
                mDotNotifyImgView.setVisibility(View.GONE);
            }

            relativeNotify.setOnClickListener(v -> {
                mDatabaseHelperTest.NotificationUpdate(notifyModel.getNotify_id(), 1+"", notifyModel.getNotify_item_id()) ;
                mDotNotifyImgView.setVisibility(View.GONE);

            });
            mDeleteNotifyImgBtn.setOnClickListener(v -> onDeleteInterface.onDelete(notifyModel.getNotify_id(), notifyModel.getNotify_item_id(), notifyModel.getNotify_title()));

        }
    }
}