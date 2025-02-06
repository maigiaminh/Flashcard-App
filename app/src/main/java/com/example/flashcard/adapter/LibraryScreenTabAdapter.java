package com.example.flashcard.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.flashcard.FolderFragment;
import com.example.flashcard.TopicFragment;

public class LibraryScreenTabAdapter extends FragmentStateAdapter {

    public LibraryScreenTabAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TopicFragment();
            case 1:
                return new FolderFragment();
            default:
                return new TopicFragment();
        }
    }
}
