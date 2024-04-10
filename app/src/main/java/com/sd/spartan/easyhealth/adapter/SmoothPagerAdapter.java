package com.sd.spartan.easyhealth.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.viewpager.widget.PagerAdapter;
import com.sd.spartan.easyhealth.R;
import com.sd.spartan.easyhealth.model.BuilderModel;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class SmoothPagerAdapter extends PagerAdapter {
    private final Context mContext;
    private final List<BuilderModel> mBannerList;

    public SmoothPagerAdapter(Context mContext, List<BuilderModel> mBannerList) {
        this.mContext = mContext;
        this.mBannerList = mBannerList;
    }

    @Override
    public int getCount() {
        return mBannerList.size();
    }

    @Override
    public @NotNull View instantiateItem(@NotNull ViewGroup container, int position) {
        final BuilderModel bannerModel = mBannerList.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_smooth_pager, container, false);
        ImageView img_slider = view.findViewById(R.id.img_slider);
        LoadImage.loadImageInView(bannerModel.getBanner_img(), R.drawable.easy_health_logo, img_slider);
        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem (ViewGroup container, int position, @NotNull Object object){
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject (View view, @NotNull Object object){
        return view.equals(object);
    }
}