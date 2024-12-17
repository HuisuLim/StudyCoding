package com.example.StudyCoding.Fragment.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
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
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.StudyCoding.API.API_BrowseAI;
import com.example.StudyCoding.API.Client_BrowseAI;
import com.example.StudyCoding.Activity_Web_Viewer;
import com.example.StudyCoding.BuildConfig;
import com.example.StudyCoding.Database.Database_Problem.Problem;
import com.example.StudyCoding.Database.Database_Problem_Info.ProblemTaskRepository;
import com.example.StudyCoding.Models.BrowseAIModels.RobotRetrievedResponse;
import com.example.StudyCoding.Models.BrowseAIModels.RobotTaskRequest;
import com.example.StudyCoding.Models.BrowseAIModels.RobotTaskResponse;
import com.example.StudyCoding.R;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Adpater_Search_Problem extends RecyclerView.Adapter<Adpater_Search_Problem.ProblemViewHolder> {

    private List<Problem> problemList;
    private static final String TAG = "API_RESPONSE";
    private Context context;
    private ProblemTaskRepository repository;

    // 콜백 인터페이스 정의
    public interface OnDownloadClickListener {
        void onDownloadClick(String url);
    }

    private OnDownloadClickListener downloadClickListener;

    public Adpater_Search_Problem(List<Problem> problemList) {
        this.problemList = problemList;
    }
    public void setOnDownloadClickListener(OnDownloadClickListener listener) {
        this.downloadClickListener = listener;
    }
    @NonNull
    @Override
    public ProblemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Adapter에서 context 획득
        context = parent.getContext();
        // Repository 초기화
        repository = new ProblemTaskRepository(context);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_problem, parent, false);
        return new ProblemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProblemViewHolder holder, int position) {
        Problem problem = problemList.get(position);
        holder.bind(problem);


    }

    @Override
    public int getItemCount() {
        return problemList.size();
    }

    class ProblemViewHolder extends RecyclerView.ViewHolder {
        TextView Number, Title, Level, Tags;
        ImageButton webButton, downloadButton;
        LinearLayout slidingPage; // 슬라이딩 레이아웃
        LinearLayout mainLayout; // 기존 레이아웃
        boolean isSlidingPageVisible = false; // 상태 확인 변수

        public ProblemViewHolder(@NonNull View itemView) {
            super(itemView);
            Number = itemView.findViewById(R.id.problem_number);
            Title = itemView.findViewById(R.id.problem_title);
            Level = itemView.findViewById(R.id.problem_level);
            Tags = itemView.findViewById(R.id.problem_tags);
            webButton = itemView.findViewById(R.id.webButton);
            downloadButton = itemView.findViewById(R.id.downloadButton);
            slidingPage = itemView.findViewById(R.id.slidingpage);

            // 초기 상태는 GONE
            slidingPage.setVisibility(View.GONE);
            mainLayout = itemView.findViewById(R.id.mainLayout); // 기존 레이아웃 (ID 추가 필요)

            // 아이템 클릭 이벤트
            itemView.setOnClickListener(v -> toggleSlidingPage());

        }
        private void toggleSlidingPage() {
            if (isSlidingPageVisible) {
                // 슬라이딩 아웃 애니메이션
                Animation slideOut = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.translate_left);
                slidingPage.startAnimation(slideOut);
                slidingPage.setVisibility(View.GONE);
            } else {
                // 슬라이딩 인 애니메이션
                Animation slideIn = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.translate_right);
                slidingPage.setVisibility(View.VISIBLE);
                slidingPage.startAnimation(slideIn);
            }
            isSlidingPageVisible = !isSlidingPageVisible; // 상태 토글
        }


        void bind(Problem problem) {
            String levelName = getLevelName(problem.getLevel());
            int levelColor = getLevelColor(itemView.getContext(), problem.getLevel());
            Number.setText(String.valueOf(problem.getProblemId()));
            Title.setText(problem.getTitleKo());
            Level.setText(levelName);
            Level.setTextColor(levelColor);
            // 태그들을 한 줄로 묶어 표시
            StringBuilder tags = new StringBuilder();
            if (problem.getTag1() != null) tags.append(problem.getTag1()).append(" ");
            if (problem.getTag2() != null) tags.append(problem.getTag2()).append(" ");
            if (problem.getTag3() != null) tags.append(problem.getTag3()).append(" ");
            if (problem.getTag4() != null) tags.append(problem.getTag4()).append(" ");
            if (problem.getTag5() != null) tags.append(problem.getTag5());
            Tags.setText(tags.toString());
            // downloadButton 클릭 시 getProblemFromWeb 기능 실행
            webButton.setOnClickListener(v -> {
                int problemId = problem.getProblemId();
                String dynamicUrl = "https://www.acmicpc.net/problem/" + problemId;

                // Intent를 사용해 WebViewerActivity로 이동
                Intent intent = new Intent(itemView.getContext(), Activity_Web_Viewer.class);
                intent.putExtra("WEB_URL", dynamicUrl); // URL 전달
                itemView.getContext().startActivity(intent);
                if (isSlidingPageVisible) {
                    toggleSlidingPage();
                }
            });

            downloadButton.setOnClickListener(v -> {
                int problemId = problem.getProblemId();
                String dynamicUrl = "https://www.acmicpc.net/problem/" + problemId;

                // 여기서는 콜백만 호출
                if (downloadClickListener != null) {
                    downloadClickListener.onDownloadClick(dynamicUrl);
                }
                // 슬라이딩 페이지 닫기
                if (isSlidingPageVisible) {
                    toggleSlidingPage();
                }
            });
        }
    }



    private String getLevelName(int level) {
        switch (level) {
            case 0:  return "Unrated / Not Ratable";
            case 1:  return "Bronze V";
            case 2:  return "Bronze IV";
            case 3:  return "Bronze III";
            case 4:  return "Bronze II";
            case 5:  return "Bronze I";
            case 6:  return "Silver V";
            case 7:  return "Silver IV";
            case 8:  return "Silver III";
            case 9:  return "Silver II";
            case 10: return "Silver I";
            case 11: return "Gold V";
            case 12: return "Gold IV";
            case 13: return "Gold III";
            case 14: return "Gold II";
            case 15: return "Gold I";
            case 16: return "Platinum V";
            case 17: return "Platinum IV";
            case 18: return "Platinum III";
            case 19: return "Platinum II";
            case 20: return "Platinum I";
            case 21: return "Diamond V";
            case 22: return "Diamond IV";
            case 23: return "Diamond III";
            case 24: return "Diamond II";
            case 25: return "Diamond I";
            case 26: return "Ruby V";
            case 27: return "Ruby IV";
            case 28: return "Ruby III";
            case 29: return "Ruby II";
            case 30: return "Ruby I";
            default: return "Unknown";
        }
    }

    private int getLevelColor(Context context, int level) {
        if (level >= 1 && level <= 5) {
            // Bronze
            return context.getResources().getColor(R.color.bronze_color);
        } else if (level >= 6 && level <= 10) {
            // Silver
            return context.getResources().getColor(R.color.silver_color);
        } else if (level >= 11 && level <= 15) {
            // Gold
            return context.getResources().getColor(R.color.gold_color);
        } else if (level >= 16 && level <= 20) {
            // Platinum
            return context.getResources().getColor(R.color.platinum_color);
        } else if (level >= 21 && level <= 25) {
            // Diamond
            return context.getResources().getColor(R.color.diamond_color);
        } else if (level >= 26 && level <= 30) {
            // Ruby
            return context.getResources().getColor(R.color.ruby_color);
        } else {
            // Unrated or not in range
            return context.getResources().getColor(R.color.default_color);
        }
    }



}
