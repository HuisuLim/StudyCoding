package com.example.StudyCoding.ProblemList;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.StudyCoding.CodeExecuterFragment;
import com.example.StudyCoding.ProblemViewFragment;

public class ViewPageAdapter extends FragmentStateAdapter {

    private String url;

    public ViewPageAdapter(@NonNull FragmentActivity fragmentActivity, String url) {
        super(fragmentActivity);
        this.url = url;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);

        if (position == 0) {
            ProblemViewFragment fragment = new ProblemViewFragment();
            fragment.setArguments(bundle);
            return fragment;
        } else {
             CodeExecuterFragment fragment = new CodeExecuterFragment();
            fragment.setArguments(bundle);
            return fragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Two fragments
    }
}
