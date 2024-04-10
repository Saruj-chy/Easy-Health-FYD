package com.sd.spartan.easyhealth.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.interfaces.OnGoogleInterface;
import com.sd.spartan.easyhealth.model.BuilderModel;

import java.util.List;

import static com.sd.spartan.easyhealth.AccessControl.AppConstants.*;

public class TutorialsAdapter extends RecyclerView.Adapter<TutorialsAdapter.TutorialsViewHolder>
{
    private final Context mCtx;
    private final List<BuilderModel> mItemList;
    private final OnGoogleInterface onGoogleInterface ;

    public TutorialsAdapter(Context mCtx, List<BuilderModel> toolsList, OnGoogleInterface onGoogleInterface) {
        this.mCtx = mCtx;
        this.mItemList = toolsList;
        this.onGoogleInterface = onGoogleInterface;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public TutorialsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_tutorials_list, null);
        return new TutorialsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TutorialsViewHolder holder, final int position) {

        final BuilderModel tutorialsModel = mItemList.get(position);
        (holder).bind(tutorialsModel);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class TutorialsViewHolder extends RecyclerView.ViewHolder
    {
        private final ConstraintLayout tutorialsConstraint ;
        private final TextView mTitleTV, mDetailsTV;
        private final YouTubeThumbnailView mYouTubePlayerView;

        public TutorialsViewHolder(@NonNull View itemView) {
            super(itemView);

            tutorialsConstraint = itemView.findViewById(R.id.constraint_tutorials_list);
            mTitleTV = itemView.findViewById(R.id.text_title);
            mDetailsTV = itemView.findViewById(R.id.text_details);
            mYouTubePlayerView = itemView.findViewById(R.id.youtube_thumbnail);
        }

        public void bind(BuilderModel tutorialsModel) {
            String title = tutorialsModel.getTutorial_title() ;
            String details = tutorialsModel.getTutorial_details() ;

            if(title.equalsIgnoreCase("")){
                mTitleTV.setVisibility(View.GONE);
            }else{
                mTitleTV.setVisibility(View.VISIBLE);
                mTitleTV.setText(tutorialsModel.getTutorial_title());
            }
            if(details.equalsIgnoreCase("")){
                mDetailsTV.setVisibility(View.GONE);
            }else{
                mDetailsTV.setVisibility(View.VISIBLE);
                mDetailsTV.setText(tutorialsModel.getTutorial_details());
            }

            mYouTubePlayerView.initialize(mCtx.getResources().getString(R.string.youtube_api_key), new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                    youTubeThumbnailLoader.setVideo(tutorialsModel.getTutorial_video_id());

                    youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                        @Override
                        public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                            youTubeThumbnailLoader.release();
                        }

                        @Override
                        public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                        }
                    });
                }
                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                }
            });

            tutorialsConstraint.setOnClickListener(v -> onGoogleInterface.OnGoogle(YOUTUBE_LINK+tutorialsModel.getTutorial_video_id()));
        }
    }
}