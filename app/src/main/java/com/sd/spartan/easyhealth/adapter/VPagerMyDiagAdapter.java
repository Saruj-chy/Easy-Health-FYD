package com.sd.spartan.easyhealth.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sd.spartan.easyhealth.fragment.MyDoctorsFragment;
import com.sd.spartan.easyhealth.fragment.MyProfileFragment;

import org.jetbrains.annotations.NotNull;

public class VPagerMyDiagAdapter extends FragmentStatePagerAdapter {
    private final int mNumOfTabs;

    public VPagerMyDiagAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public @NotNull Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MyDoctorsFragment();
            case 1:
                return new MyProfileFragment();

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}