package com.cqu.notes.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.cqu.notes.R;
import com.cqu.notes.database.DatabaseAction;
import com.cqu.notes.database.NotesItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditNotesActivity extends AppCompatActivity {
    private boolean edit;
    private EditText editTitle;
    private EditText editRemark;
    private TextView chooseType;
    private List<NotesItem> notesTypes;
    private final List<String> types = new ArrayList<>();
    private String type = "未分类";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        Intent intent = getIntent();
        edit = intent.getBooleanExtra("edit", false);
        setTitle(edit ? "查看/编辑笔记" : "添加笔记");
        editTitle = findViewById(R.id.edit_title);
        editRemark = findViewById(R.id.edit_remark);
        chooseType = findViewById(R.id.choose_type);
        FloatingActionButton submitBtn = findViewById(R.id.submit);
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, (options1, option2, option3, v) -> {
            //返回的分别是三个级别的选中位置
            type = types.get(options1);
            chooseType.setText(type);
        }).build();
        new Thread(() -> {
            types.add("未分类");
            notesTypes = DatabaseAction.getInstance(this).getDao().getAllType();
            for (int i = 0; i < notesTypes.size(); i++) {
                types.add(notesTypes.get(i).getTitle());
            }
            pvOptions.setPicker(types);
        }).start();
        if (edit)
            initEdit();
        chooseType.setOnClickListener(v -> pvOptions.show());
        submitBtn.setOnClickListener(v -> {
            long t = Calendar.getInstance().getTimeInMillis();
            if (!edit) {
                new Thread(() -> DatabaseAction.getInstance(this).getDao().insert(new NotesItem(1, t, editTitle.getText().toString(), editRemark.getText().toString(), type))).start();
            } else {
                NotesPage.editItem.setModifiedTime(t);
                NotesPage.editItem.setTitle(editTitle.getText().toString());
                NotesPage.editItem.setRemark(editRemark.getText().toString());
                NotesPage.editItem.setNoteType(type);
                new Thread(() -> DatabaseAction.getInstance(this).getDao().update(NotesPage.editItem)).start();
            }
            finish();
        });
    }

    private void initEdit() {
        type = NotesPage.editItem.getNoteType();
        editTitle.setText(NotesPage.editItem.getTitle());
        editRemark.setText(NotesPage.editItem.getRemark());
        chooseType.setText(type);
    }
}
