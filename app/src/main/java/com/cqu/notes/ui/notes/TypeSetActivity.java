package com.cqu.notes.ui.notes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cqu.notes.R;
import com.cqu.notes.database.DatabaseAction;
import com.cqu.notes.database.NotesItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TypeSetActivity extends AppCompatActivity {
    public static final int COMPLETED = -1;
    public static final int COMPLETED2 = -2;
    public static final int DELETE = -3;
    private static List<NotesItem> typeList = new ArrayList<>();
    private static TypeAdapter adapter;
    private static RecyclerView typeListView;
    private static NotesItem tempTA;
    private static int tempPos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_list);
        setTitle("编辑类别");
        FloatingActionButton addType = findViewById(R.id.add_type);
        FloatingActionButton saveType = findViewById(R.id.save_type);
        typeListView = findViewById(R.id.type_list);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.list_anim);
        typeListView.setLayoutAnimation(layoutAnimationController);
        typeListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TypeAdapter(this, typeList);
        typeListView.setAdapter(adapter);
        adapter.setOnItemClickListener(new TypeAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, View v) {
                editDialog(position);
            }

            @Override
            public void onLongClick(int position, View v) {

            }
        });
        helper.attachToRecyclerView(typeListView);
        addType.setOnClickListener(v -> addDialog());
        saveType.setOnClickListener(v -> finish());
        new Thread(() -> {
            typeList = DatabaseAction.getInstance(this).getDao().getAllType();
            Message msg = new Message();
            msg.what = COMPLETED;
            handler.sendMessage(msg);
        }).start();
    }

    private void addDialog() {
        Dialog dialog = new Dialog(this);
        //去掉标题线
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(this).inflate(R.layout.add_type_dialog, null, false);
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
        EditText editText = view.findViewById(R.id.edit_type_name);
        Button button = view.findViewById(R.id.submit_btn);
        button.setOnClickListener(v -> {
            if (String.valueOf(editText.getText()).equals(""))
                Toast.makeText(this, "请输入类别名称！", Toast.LENGTH_SHORT).show();
            else {
                new Thread(() -> {
                    if (DatabaseAction.getInstance(this).getDao().existType(String.valueOf(editText.getText()))) {
                        Looper.prepare();
                        Toast.makeText(this, "该类别已存在！", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } else {
                        tempTA = new NotesItem(0, typeList.size(), String.valueOf(editText.getText()));
                        DatabaseAction.getInstance(this).getDao().insert(tempTA);
                        tempTA = DatabaseAction.getInstance(this).getDao().getAllType().get(typeList.size());
                        Message msg = new Message();
                        msg.what = COMPLETED2;
                        handler.sendMessage(msg);
                        dialog.dismiss();
                    }
                }).start();
            }
        });
    }

    private void editDialog(int position) {
        Dialog dialog = new Dialog(this);
        //去掉标题线
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(this).inflate(R.layout.add_type_dialog, null, false);
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
//        window.setWindowAnimations(R.style.animStyle);  //添加动画
        EditText editText = view.findViewById(R.id.edit_type_name);
        editText.setText(typeList.get(position).getTitle());
        Button button = view.findViewById(R.id.submit_btn);
        TextView textView = view.findViewById(R.id.dialog_title);
        textView.setText("编辑类别");
        button.setText("修改");
        button.setOnClickListener(v -> {
            if (String.valueOf(editText.getText()).equals(""))
                Toast.makeText(this, "请输入类别名称！", Toast.LENGTH_SHORT).show();
            else {
                new Thread(() -> {
                    if (!String.valueOf(editText.getText()).equals(typeList.get(position).getTitle()) && DatabaseAction.getInstance(this).getDao().existType(String.valueOf(editText.getText()))) {
                        Looper.prepare();
                        Toast.makeText(this, "该类别已存在！", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } else {
                        typeList.get(position).setTitle(String.valueOf(editText.getText()));
                        DatabaseAction.getInstance(this).getDao().update(typeList.get(position));
                        Message msg = new Message();
                        msg.what = COMPLETED;
                        handler.sendMessage(msg);
                        dialog.dismiss();
                    }
                }).start();
            }
        });
    }

    private static final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETED) {
                adapter.setList(typeList);
                typeListView.scheduleLayoutAnimation();
            } else if (msg.what == COMPLETED2) {
                adapter.add(tempTA);
                adapter.updateOrder();
            } else if (msg.what == DELETE) {
                adapter.remove(tempPos);
                adapter.updateOrder();
            }
        }
    };

    static class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.Vh> {
        private final Context context;
        public List<NotesItem> typesList;

        public TypeAdapter(Context context, List<NotesItem> typesList) {
            this.context = context;
            this.typesList = typesList;
        }

        @NonNull
        @Override
        public TypeAdapter.Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Vh(LayoutInflater.from(context).inflate(R.layout.type_item, parent, false));
        }

        @Override
        public void onBindViewHolder(TypeAdapter.Vh holder, final int position) {
            holder.typeName.setText(typesList.get(position).getTitle());
            holder.deleteBtn.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("删除类别");
                builder.setMessage("将同时删除该类别的笔记！");
                //点击确定执行
                builder.setPositiveButton("确定", (arg0, arg1) -> new Thread(() -> {
                    tempPos = holder.getAdapterPosition();
                    DatabaseAction.getInstance(context).getDao().deleteNote(typesList.get(tempPos).getTitle());
                    DatabaseAction.getInstance(context).getDao().delete(typesList.get(tempPos));
                    Message msg = new Message();
                    msg.what = DELETE;
                    handler.sendMessage(msg);
                }).start());
                //点击取消执行
                builder.setNegativeButton("返回", (arg0, arg1) -> {

                });
                AlertDialog b = builder.create();
                b.show();//显示对话框
            });
            if (mOnItemClickListener != null) {
                holder.cardView.setOnClickListener(v -> mOnItemClickListener.onClick(holder.getAdapterPosition(), v));
                holder.cardView.setOnLongClickListener(v -> {
                    mOnItemClickListener.onLongClick(holder.getAdapterPosition(), v);
                    return true;
                });
            }
        }

        public interface OnItemClickListener {
            void onClick(int position, View v);

            void onLongClick(int position, View v);
        }

        private OnItemClickListener mOnItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        @Override
        public int getItemCount() {
            return typesList.size();
        }

        public void setList(List<NotesItem> newList) {
            this.typesList = newList;
            notifyDataSetChanged();
        }

        public void add(NotesItem item) {
            int position = typesList.size();
            typesList.add(item);
            notifyItemInserted(position);
        }

        public void add(int position, NotesItem item) {
            typesList.add(position, item);
            notifyItemInserted(position);
        }

        public void remove(int position) {
            typesList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, typesList.size());
        }

        public void updateOrder() {
            for (int i = 0; i < typesList.size(); i++) {
                typesList.get(i).setPos(i);
                int finalI = i;
                new Thread(() -> DatabaseAction.getInstance(context).getDao().update(typesList.get(finalI))).start();
            }
        }

        static class Vh extends RecyclerView.ViewHolder {
            private final TextView typeName;
            private final ImageView deleteBtn;
            private final CardView cardView;

            public Vh(View itemView) {
                super(itemView);
                typeName = itemView.findViewById(R.id.type_name);
                deleteBtn = itemView.findViewById(R.id.delete_btn);
                cardView = itemView.findViewById(R.id.type_card);
            }
        }
    }


    ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        //线性布局和网格布局都可以使用
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFrag = 0;
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                dragFrag = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                dragFrag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            }
            return makeMovementFlags(dragFrag, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            //得到当拖拽的viewHolder的Position
            int fromPosition = viewHolder.getAdapterPosition();
            //拿到当前拖拽到的item的viewHolder
            int toPosition = target.getAdapterPosition();
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(typeList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(typeList, i, i - 1);
                }
            }
            adapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //侧滑删除可以使用；
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        /**
         * 长按选中Item的时候开始调用
         * 长按高亮
         */
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
//                viewHolder.itemView.setBackgroundColor(Color.RED);
                //获取系统震动服务//震动70毫秒
                Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                vib.vibrate(VibrationEffect.createOneShot(30, 128));
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        //手指松开的时候还原高亮
        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setBackgroundColor(0);
            adapter.notifyDataSetChanged();  //完成拖动后刷新适配器，这样拖动后删除就不会错乱
            adapter.updateOrder();
        }
    });
}
