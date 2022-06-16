package com.cqu.notes.ui.todo;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cqu.notes.R;
import com.cqu.notes.database.DatabaseAction;
import com.cqu.notes.database.NotesItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private List<NotesItem> mList;
    private final Context context;

    public TodoAdapter(Context context, List<NotesItem> mList) {
        this.context = context;
        this.mList = mList;
    }

    public void refresh(List<NotesItem> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public void updateItem(int position) {
        notifyItemChanged(position);
    }

    public void remove(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mList.size());
    }


    @NonNull
    @Override
    public TodoAdapter.TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TodoAdapter.TodoViewHolder viewHolder;
        View inflate = LayoutInflater.from(context).inflate(R.layout.todo_item, parent, false);
        viewHolder = new TodoAdapter.TodoViewHolder(inflate);
        return viewHolder;
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
    public void onBindViewHolder(@NonNull TodoAdapter.TodoViewHolder holder, int position) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mList.get(position).getEventTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
        holder.todoTitle.setText(mList.get(position).getTitle());
        holder.todoTime.setText(df.format(calendar.getTime()));
        holder.done.setChecked(mList.get(position).isDone());
        if (mList.get(position).isDone())
            holder.todoTitle.setPaintFlags(holder.todoTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); //中划线
        else
            holder.todoTitle.setPaintFlags(holder.todoTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));  // 取消设置的的划线
        if (mOnItemClickListener != null) {
            holder.cardView.setOnClickListener(v -> mOnItemClickListener.onClick(holder.getAdapterPosition(), v));
            holder.cardView.setOnLongClickListener(v -> {
                mOnItemClickListener.onLongClick(holder.getAdapterPosition(), v);
                return true;
            });
        }
        holder.done.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed())
                return;
            mList.get(position).setDone(isChecked);
            if (isChecked)
                holder.todoTitle.setPaintFlags(holder.todoTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); //中划线
            else
                holder.todoTitle.setPaintFlags(holder.todoTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));  // 取消设置的的划线
            new Thread(() -> DatabaseAction.getInstance(context).getDao().update(mList.get(position))).start();
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView todoTitle;
        TextView todoTime;
        CheckBox done;
        CardView cardView;

        public TodoViewHolder(View itemView) {
            super(itemView);
            todoTitle = itemView.findViewById(R.id.todo_title);
            todoTime = itemView.findViewById(R.id.todo_time);
            done = itemView.findViewById(R.id.checkBox);
            cardView = itemView.findViewById(R.id.card);
        }
    }
}
