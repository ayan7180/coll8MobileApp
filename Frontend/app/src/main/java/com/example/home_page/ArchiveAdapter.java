package com.example.home_page;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.home_page.Archive;

import java.util.List;

public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.ViewHolder> {

    private final List<Archive> archiveList;
    private final OnArchiveClickListener listener;

    public ArchiveAdapter(List<Archive> archiveList, OnArchiveClickListener listener) {
        this.archiveList = archiveList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.archive_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Archive archive = archiveList.get(position);
        holder.titleTextView.setText(archive.getTitle());
        holder.itemView.setOnClickListener(v -> listener.onArchiveClick(archive.getId()));
    }

    @Override
    public int getItemCount() {
        return archiveList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.archiveTitleTextView);
        }
    }

    public interface OnArchiveClickListener {
        void onArchiveClick(int archiveId);
    }
}
