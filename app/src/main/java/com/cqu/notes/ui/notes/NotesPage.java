package com.cqu.notes.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.cqu.notes.R;
import com.cqu.notes.database.DatabaseAction;
import com.cqu.notes.database.NotesItem;

import java.util.ArrayList;
import java.util.List;

public class NotesPage extends Fragment {
    public static final int COMPLETED = -1;
    public static NotesItem editItem;
    private RecyclerView recyclerView;
    private NotesAdapter notesAdapter;
    private final String typeName;
    private List<NotesItem> notesItems = new ArrayList<>();

    public NotesPage(String typeName) {
        this.typeName = typeName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_notes, container, false);
        recyclerView = view.findViewById(R.id.notes_list);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        notesAdapter = new NotesAdapter(notesItems, getContext());
        recyclerView.setAdapter(notesAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.list_anim);
        recyclerView.setLayoutAnimation(layoutAnimationController);
        update();
        notesAdapter.setOnItemClickListener(new NotesAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, View v) {
                Intent intent = new Intent(getActivity(), EditNotesActivity.class);
                intent.putExtra("edit", true);
                editItem = notesItems.get(position);
                startActivity(intent);
            }

            @Override
            public void onLongClick(int position, View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.long_click_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.delete) {
                        NotesItem temp = notesItems.get(position);
                        new Thread(() -> DatabaseAction.getInstance(getContext()).getDao().delete(temp)).start();
                        notesAdapter.remove(position);
                    }
                    return false;
                });
                popupMenu.show();
            }
        });
        return view;
    }

    public void update() {
        new Thread(() -> {
            notesItems = DatabaseAction.getInstance(getContext()).getDao().getNote(typeName);
            Message msg = new Message();
            msg.what = COMPLETED;
            handler.sendMessage(msg);
        }).start();
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETED) {
                if (notesAdapter != null) {
                    notesAdapter.refresh(notesItems);
                    recyclerView.scheduleLayoutAnimation();
                }
            }
        }
    };
}