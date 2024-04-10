package com.sd.spartan.easyhealth.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sd.spartan.easyhealth.fragment.DiagDocListFragment;
import com.sd.spartan.easyhealth.fragment.DiagProfileFragment;

import org.jetbrains.annotations.NotNull;

public class VPagerDiagAdapter extends FragmentStatePagerAdapter {
    private final int mNumOfTabs;

    public VPagerDiagAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public @NotNull Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DiagDocListFragment();
            case 1:
                return new DiagProfileFragment();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}