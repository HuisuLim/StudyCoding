package com.example.StudyCoding.Fragment.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.StudyCoding.Database.CodeDatabase.CodeTask;
import com.example.StudyCoding.R;

import java.util.List;

public class CodeTaskAdapter extends RecyclerView.Adapter<CodeTaskAdapter.CodeTaskViewHolder> {

    private final List<CodeTask> codeTasks;
    private final OnTaskClickListener listener;
    private Context context;

    public interface OnTaskClickListener {
        void onTaskClick(String url);
    }


    public CodeTaskAdapter(Context context, List<CodeTask> codeTasks, OnTaskClickListener listener) {
        this.context = context; // context 초기화
        this.codeTasks = codeTasks;
        this.listener = listener;
    }


    @NonNull
    @Override
    public CodeTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new CodeTaskViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CodeTaskAdapter.CodeTaskViewHolder holder, int position) {
        CodeTask task = codeTasks.get(position);
        holder.urlTextView.setText(task.getUrl());
        holder.actionButton.setText("Open");
        holder.actionButton.setOnClickListener(v -> listener.onTaskClick(task.getUrl()));
    }

    @Override
    public int getItemCount() {
        return codeTasks.size();
    }

    static class CodeTaskViewHolder extends RecyclerView.ViewHolder {
        TextView urlTextView;
        Button actionButton;

        public CodeTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            urlTextView = itemView.findViewById(R.id.urlTextView);
            actionButton = itemView.findViewById(R.id.actionButton);
        }
    }

}
