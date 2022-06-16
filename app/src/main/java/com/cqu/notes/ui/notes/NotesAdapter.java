package com.cqu.notes.ui.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cqu.notes.R;
import com.cqu.notes.database.NotesItem;

import java.util.Calendar;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private List<NotesItem> notesItems;
    private final Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView noteTitle;
        TextView noteRemark;
        TextView type;
        TextView date;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.note_card);
            noteTitle = view.findViewById(R.id.note_title);
            noteRemark = view.findViewById(R.id.note_remark);
            type = view.findViewById(R.id.type);
            date = view.findViewById(R.id.moti_date);
        }

    }

    public void refresh(List<NotesItem> notesItems) {
        this.notesItems = notesItems;
        notifyDataSetChanged();
    }

    public void remove(int position) {
        notesItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, notesItems.size());
    }

    public NotesAdapter(List<NotesItem> notesItems, Context context) {
        this.notesItems = notesItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_item, parent, false);
        return new ViewHolder(view);
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        NotesItem notesItem = notesItems.get(position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(notesItem.getModifiedTime());
        holder.noteTitle.setText(notesItem.getTitle());
        holder.noteRemark.setText(notesItem.getRemark());
        holder.type.setText(notesItem.getNoteType());
        holder.date.setText(String.format(context.getResources().getString(R.string.month_day), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
        if (mOnItemClickListener != null) {
            holder.cardView.setOnClickListener(v -> mOnItemClickListener.onClick(position, v));
            holder.cardView.setOnLongClickListener(v -> {
                mOnItemClickListener.onLongClick(position, v);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return notesItems.size();
    }
}
