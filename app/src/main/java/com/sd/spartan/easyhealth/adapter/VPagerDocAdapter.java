package com.sd.spartan.easyhealth.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sd.spartan.easyhealth.fragment.DocDiagListFragment;
import com.sd.spartan.easyhealth.fragment.DocProfileFragment;

import org.jetbrains.annotations.NotNull;


public class VPagerDocAdapter extends FragmentStatePagerAdapter {

    private final int mNumOfTabs;

    public VPagerDocAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public @NotNull Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DocDiagListFragment();
            case 1:
                return new DocProfileFragment();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}