package com.example.StudyCoding.Fragment.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.StudyCoding.Database.Database_Problem_Info.ProblemTask;
import com.example.StudyCoding.Database.Database_Problem_Info.ProblemTaskRepository;
import com.example.StudyCoding.R;

import java.util.List;

public class Adapter_Saved_List extends RecyclerView.Adapter<Adapter_Saved_List.TaskViewHolder> {

    private Context context;
    private List<ProblemTask> problemTaskList;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(String url);
    }

    public Adapter_Saved_List(Context context, List<ProblemTask> problemTaskList, OnTaskClickListener listener) {
        this.context = context;
        this.problemTaskList = problemTaskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_for_list, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        ProblemTask problemTask = problemTaskList.get(position);
        holder.titleTextView.setText(problemTask.getTitle());
        holder.urlTextView.setText(problemTask.getUrl());
        holder.problemNumberView.setText(problemTask.getProblemNumber());

        // 짧게 클릭했을 때 처리 (액티비티로 이동)
        holder.itemView.setOnClickListener(v -> {
            if (!holder.isSlidingPageVisible) { // 슬라이딩 레이아웃이 열리지 않았을 때만 실행
                if (listener != null) {
                    listener.onTaskClick(problemTask.getUrl());
                }
            }
        });

        // 길게 클릭했을 때 슬라이딩 레이아웃 표시
        holder.itemView.setOnLongClickListener(v -> {
            holder.toggleSlidingPage(); // 애니메이션 호출
            return true; // 이벤트 소비
        });

        // 삭제 버튼 처리
        holder.deleteButton.setOnClickListener(v -> {
            ProblemTaskRepository repository = new ProblemTaskRepository(context);
            repository.deleteRowByUrl(problemTask.getUrl());

            problemTaskList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, problemTaskList.size());
            // 슬라이딩 레이아웃이 열려있다면 닫기
            if (holder.isSlidingPageVisible) {
                holder.toggleSlidingPage();
            }
        });

        holder.viewButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(problemTask.getUrl());
                }
            
            // 슬라이딩 레이아웃이 열려있다면 닫기
            if (holder.isSlidingPageVisible) {
                holder.toggleSlidingPage();
            }
        });
    }

    @Override
    public int getItemCount() {
        return problemTaskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView urlTextView;
        TextView problemNumberView;
        ImageButton deleteButton, viewButton;
        LinearLayout slidingPage;
        boolean isSlidingPageVisible = false;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            urlTextView = itemView.findViewById(R.id.urlTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            viewButton = itemView.findViewById(R.id.viewButton);
            problemNumberView = itemView.findViewById(R.id.problemNumber);
            slidingPage = itemView.findViewById(R.id.swipeLayout);
            slidingPage.setVisibility(View.GONE);
        }

        // 슬라이딩 페이지 토글 메서드
        private void toggleSlidingPage() {
            if (isSlidingPageVisible) {
                Animation slideOut = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.translate_left);
                slidingPage.startAnimation(slideOut);
                slidingPage.setVisibility(View.GONE);
            } else {
                Animation slideIn = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.translate_right);
                slidingPage.setVisibility(View.VISIBLE);
                slidingPage.startAnimation(slideIn);
            }
            isSlidingPageVisible = !isSlidingPageVisible;
        }
    }
}
