package com.cqu.notes.ui.todo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cqu.notes.R;
import com.cqu.notes.database.DatabaseAction;
import com.cqu.notes.database.NotesItem;
import com.cqu.notes.databinding.FragmentTodoBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TodoFragment extends Fragment {
    public static final int COMPLETED = -1;
    public static final int COMPLETED2 = -2;
    private FragmentTodoBinding binding;
    private RecyclerView recyclerView;
    private List<NotesItem> todoList = new ArrayList<>();
    private TodoAdapter todoAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTodoBinding.inflate(inflater, container, false);
        ConstraintLayout root = binding.getRoot();
        FloatingActionButton addTodoBtn = binding.addTodoBtn;
        recyclerView = binding.todoList;
        updateData();
        addTodoBtn.setOnClickListener(v -> addDialog());
        todoAdapter = new TodoAdapter(getContext(), todoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(todoAdapter);
        todoAdapter.setOnItemClickListener(new TodoAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, View v) {

            }

            @Override
            public void onLongClick(int position, View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.long_click_menu2, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.edit_todo) {
                        editDialog(position);
                    } else if (item.getItemId() == R.id.delete_todo) {
                        NotesItem temp = todoList.get(position);
                        new Thread(() -> DatabaseAction.getInstance(getContext()).getDao().delete(temp)).start();
                        todoAdapter.remove(position);
                    }
                    return false;
                });
                popupMenu.show();
            }
        });
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.list_anim);
        recyclerView.setLayoutAnimation(layoutAnimationController);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /** 添加对话框 */
    private void addDialog() {
        Dialog dialog = new Dialog(getContext());
        //去掉标题线
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.add_todo_dialog, null, false);
        dialog.setContentView(view);
        //背景透明
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER; // 居中位置
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.animStyle);  //添加动画
        EditText editText = view.findViewById(R.id.edit_todo_name);
        TextView selectTime = view.findViewById(R.id.select_time);
        SimpleDateFormat df = new SimpleDateFormat("事项时间：yyyy年MM月dd日 HH:mm", Locale.CHINA);
        selectTime.setText(df.format(Calendar.getInstance().getTime()));
        final long[] eventTime = new long[1];
        eventTime[0] = System.currentTimeMillis();
        selectTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(eventTime[0]);
            DatePickerDialog dialogT = new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> new TimePickerDialog(getContext(), (view2, hourOfDay, minute) -> {
                calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                eventTime[0] = calendar.getTimeInMillis();
                selectTime.setText(df.format(calendar.getTime()));
            },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true).show(),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialogT.show();
        });
        Button button = view.findViewById(R.id.submit2_btn);
        button.setOnClickListener(v -> {
            if (String.valueOf(editText.getText()).equals(""))
                Toast.makeText(getContext(), "请输入待办事项！", Toast.LENGTH_SHORT).show();
            else {
                new Thread(() -> {
                    NotesItem tempTA = new NotesItem(2, eventTime[0], String.valueOf(editText.getText()), false);
                    DatabaseAction.getInstance(getContext()).getDao().insert(tempTA);
                    todoList = DatabaseAction.getInstance(getContext()).getDao().getAllTodo();
                    Message msg = new Message();
                    msg.what = COMPLETED;
                    handler.sendMessage(msg);
                    dialog.dismiss();
                }).start();
            }
        });
    }

    /** 编辑对话框 */
    private void editDialog(int position) {
        Dialog dialog = new Dialog(getContext());
        //去掉标题线
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.add_todo_dialog, null, false);
        dialog.setContentView(view);
        //背景透明
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER; // 居中位置
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        TextView selectTime = view.findViewById(R.id.select_time);
        EditText editText = view.findViewById(R.id.edit_todo_name);
        editText.setText(todoList.get(position).getTitle());
        Button button = view.findViewById(R.id.submit2_btn);
        TextView textView = view.findViewById(R.id.dialog2_title);
        textView.setText("编辑待办");
        button.setText("修改");
        SimpleDateFormat df = new SimpleDateFormat("事项时间：yyyy年MM月dd日 HH:mm", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        final long[] eventTime = new long[1];
        eventTime[0] = todoList.get(position).getEventTime();
        calendar.setTimeInMillis(eventTime[0]);
        selectTime.setText(df.format(calendar.getTime()));
        selectTime.setOnClickListener(v -> {
            DatePickerDialog dialogT = new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> new TimePickerDialog(getContext(), (view2, hourOfDay, minute) -> {
                calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                eventTime[0] = calendar.getTimeInMillis();
                selectTime.setText(df.format(calendar.getTime()));
            },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true).show(),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialogT.show();
        });
        button.setOnClickListener(v -> {
            if (String.valueOf(editText.getText()).equals(""))
                Toast.makeText(getContext(), "请输入待办事项！", Toast.LENGTH_SHORT).show();
            else {
                new Thread(() -> {
                    todoList.get(position).setTitle(String.valueOf(editText.getText()));
                    todoList.get(position).setEventTime(eventTime[0]);
                    DatabaseAction.getInstance(getContext()).getDao().update(todoList.get(position));
                    Message msg = new Message();
                    msg.what = COMPLETED2;
                    msg.arg1 = position;
                    handler.sendMessage(msg);
                    dialog.dismiss();
                }).start();
            }
        });
    }

    /** 更新数据 */
   public void updateData() {
        new Thread(() -> {
            todoList = DatabaseAction.getInstance(getContext()).getDao().getAllTodo();
            Message msg = new Message();
            msg.what = COMPLETED;
            handler.sendMessage(msg);
        }).start();
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETED) {
                if (todoAdapter != null) {
                    todoAdapter.refresh(todoList);
                    recyclerView.scheduleLayoutAnimation();
                }
            } else if (msg.what == COMPLETED2) {
                if (todoAdapter != null)
                    todoAdapter.updateItem(msg.arg1);
            }
        }
    };
}