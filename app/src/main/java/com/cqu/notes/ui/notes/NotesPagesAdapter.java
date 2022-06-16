package com.cqu.notes.ui.notes;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotesPagesAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragments;
    private final List<Long> fragmentsHash;

    public NotesPagesAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        fragments = new ArrayList<>();
        fragmentsHash = new ArrayList<>();
    }

    //添加一个Fragment
    public void addFragment(Fragment fragment) {
        fragments.add(fragment);
        fragmentsHash.add((long) fragment.hashCode());
        notifyDataSetChanged();
    }

    //删除一个Fragment
    public void removeFragment(int position) {
        if (fragments.size() > position) {
            fragments.remove(position);
            fragmentsHash.remove(position);
            notifyDataSetChanged();
        }
    }

    public void clearPages() {
        fragments.clear();
        fragmentsHash.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return fragments.get(position).hashCode();
    }

    @Override
    public boolean containsItem(long itemId) {
        return fragmentsHash.contains(itemId);
    }

    public NotesPage getFragment(int position) {
        if (fragments.size() == 0)
            return null;
        return (NotesPage) fragments.get(position);
    }
}