package com.example.StudyCoding.Fragment.Adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.StudyCoding.Fragment.Fragment_Code_Executer;
import com.example.StudyCoding.Fragment.Fragment_Saved_Problem;

public class Adapter_ViewPager extends FragmentStateAdapter {

    private String url;

    public Adapter_ViewPager(@NonNull FragmentActivity fragmentActivity, String url) {
        super(fragmentActivity);
        this.url = url;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);

        if (position == 0) {
            Fragment_Saved_Problem fragment = new Fragment_Saved_Problem();
            fragment.setArguments(bundle);
            return fragment;
        } else {
             Fragment_Code_Executer fragment = new Fragment_Code_Executer();
            fragment.setArguments(bundle);
            return fragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Two fragments
    }
}
