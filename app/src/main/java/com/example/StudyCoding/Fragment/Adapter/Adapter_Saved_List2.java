package com.example.StudyCoding.Fragment.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.StudyCoding.Database.Database_Code.CodeTask;
import com.example.StudyCoding.Database.Database_Problem_Info.ProblemTaskRepository;
import com.example.StudyCoding.R;

import java.util.List;

public class Adapter_Saved_List2 extends RecyclerView.Adapter<Adapter_Saved_List2.CodeTaskViewHolder> {

    private final List<CodeTask> codeTasks;
    private final OnTaskClickListener listener;
    private Context context;

    public interface OnTaskClickListener {
        void onTaskClick(String url);
    }


    public Adapter_Saved_List2(Context context, List<CodeTask> codeTasks, OnTaskClickListener listener) {
        this.context = context; // context 초기화
        this.codeTasks = codeTasks;
        this.listener = listener;
    }


    @NonNull
    @Override
    public CodeTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_for_list, parent, false);
        return new CodeTaskViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull Adapter_Saved_List2.CodeTaskViewHolder holder, int position) {
        CodeTask task = codeTasks.get(position);
        String extractedPrefix = removePrefix(task.getUrl());
        holder.titleTextView.setText(extractedPrefix);
        holder.urlTextView.setVisibility(View.GONE);
        holder.problemNumberView.setVisibility(View.GONE);

        holder.viewButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(task.getUrl());
            }
            // 슬라이딩 레이아웃이 열려있다면 닫기
            if (holder.isSlidingPageVisible) {
                holder.toggleSlidingPage();
            }
        });
        // 짧게 클릭했을 때 처리 (액티비티로 이동)
        holder.itemView.setOnClickListener(v -> {
            if (!holder.isSlidingPageVisible) { // 슬라이딩 레이아웃이 열리지 않았을 때만 실행
                if (listener != null) {
                    listener.onTaskClick(task.getUrl());
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
            repository.deleteRowByUrl(task.getUrl());


            codeTasks.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, codeTasks.size());
            if (holder.isSlidingPageVisible) {
                holder.toggleSlidingPage();
            }
        });
    }

    @Override
    public int getItemCount() {
        return codeTasks.size();
    }

    static class CodeTaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView urlTextView;
        TextView problemNumberView;
        ImageButton deleteButton, viewButton;
        LinearLayout slidingPage;
        boolean isSlidingPageVisible = false;

        public CodeTaskViewHolder(@NonNull View itemView) {
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

    private String removePrefix(String url) {
        String prefix = "사용자정의/";
        if (url != null && url.startsWith(prefix)) {
            return url.substring(prefix.length()); // 사용자정의/ 이후의 문자열 반환
        }
        return url; // 접두사가 없으면 원래 URL 반환
    }

}

