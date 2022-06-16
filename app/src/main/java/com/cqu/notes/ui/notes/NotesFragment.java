package com.cqu.notes.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cqu.notes.R;
import com.cqu.notes.database.DatabaseAction;
import com.cqu.notes.database.NotesItem;
import com.cqu.notes.databinding.FragmentNotesBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment {
    public static final int COMPLETED = -1;
    private NotesPagesAdapter notesPageAdapter;
    private FragmentNotesBinding binding;
    private List<NotesItem> types = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ImageView typeSetting = root.findViewById(R.id.type_setting);
        typeSetting.setOnClickListener(v -> startActivity(new Intent(getActivity(), TypeSetActivity.class)));
        notesPageAdapter = new NotesPagesAdapter(requireActivity());
        ViewPager2 viewPager2 = root.findViewById(R.id.viewPager2);
        viewPager2.setAdapter(notesPageAdapter);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position < notesPageAdapter.getItemCount())
                    notesPageAdapter.getFragment(position).update();
            }
        });
        TabLayout tabLayout = root.findViewById(R.id.tabLayout);
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if (position <= types.size())
                tab.setText(position == 0 ? "所有类别" : types.get(position - 1).getTitle());
        });
        mediator.attach();
        FloatingActionButton addNoteBtn = root.findViewById(R.id.add_notes);
        addNoteBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditNotesActivity.class);
            intent.putExtra("edit", false);
            startActivity(intent);
        });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        notesPageAdapter.clearPages();
        NotesPage notesPage = new NotesPage("全部类别");
        notesPageAdapter.addFragment(notesPage);
        new Thread(() -> {
            types = DatabaseAction.getInstance(getContext()).getDao().getAllType();
            Message msg = new Message();
            msg.what = COMPLETED;
            handler.sendMessage(msg);
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETED) {
                for (int i = 0; i < types.size(); i++) {
                    NotesPage notesPage = new NotesPage(types.get(i).getTitle());
                    notesPageAdapter.addFragment(notesPage);
                }
            }
        }
    };
}