package com.sd.spartan.easyhealth.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.model.BuilderModel;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mCtx;
    private final List<BuilderModel> mItemList;

    public MessageAdapter(Context mCtx, List<BuilderModel> mItemList) {
        this.mCtx = mCtx ;
        this.mItemList = mItemList ;
    }

    @NotNull
    @SuppressLint("InflateParams")
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_msg_reply, null);
        return new NotifyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.@NotNull ViewHolder holder, final int position) {
        BuilderModel mMessageModel = mItemList.get(position);
        ((NotifyViewHolder) holder).bind(mMessageModel);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    static class NotifyViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout replyCardCons;
        private final TextView msgTV, replyTV, replyToastTV ;

        public NotifyViewHolder(View itemView) {
            super(itemView);
            replyCardCons = itemView.findViewById(R.id.constraint_reply_card);
            msgTV = itemView.findViewById(R.id.text_msg_layout);
            replyTV = itemView.findViewById(R.id.text_reply_layout);
            replyToastTV = itemView.findViewById(R.id.text_reply_toast);
        }

        @SuppressLint("SetTextI18n")
        public void bind(BuilderModel messageModel) {

            msgTV.setText( messageModel.getE_msg() );
            replyTV.setText( messageModel.getE_reply() );
            if(!messageModel.getE_reply().equalsIgnoreCase("")){
               replyToastTV.setVisibility(View.VISIBLE);
            }

            replyToastTV.setOnClickListener(v->{
                replyToastTV.setVisibility(View.GONE);
                replyCardCons.setVisibility(View.VISIBLE);
            });

            replyCardCons.setOnClickListener(v->{
                replyToastTV.setVisibility(View.VISIBLE);
                replyCardCons.setVisibility(View.GONE);
            });
        }
    }
}