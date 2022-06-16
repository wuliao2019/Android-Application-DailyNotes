package com.cqu.notes.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cqu.notes.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private final List<HistoryItem> mList;
    private final Context context;

    public HistoryAdapter(Context context, List<HistoryItem> mList) {
        this.context = context;
        this.mList = mList;
    }


    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HistoryViewHolder viewHolder;
        View inflate = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
        viewHolder = new HistoryViewHolder(inflate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.hisDate.setText(mList.get(position).getDate());
        holder.hisInfo.setText(mList.get(position).getInfo());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView hisDate;
        TextView hisInfo;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            hisDate = itemView.findViewById(R.id.his_date);
            hisInfo = itemView.findViewById(R.id.his_info);
        }
    }
}
