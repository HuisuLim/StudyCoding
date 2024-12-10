package com.example.StudyCoding.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.StudyCoding.CodeExecuterActivity;
import com.example.StudyCoding.Database.CodeDatabase.CodeTask;
import com.example.StudyCoding.R;
import com.example.StudyCoding.Fragment.Adapter.CodeTaskAdapter;
import com.example.StudyCoding.Fragment.Adapter.SpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class CodeListFragment extends Fragment implements CodeTaskAdapter.OnTaskClickListener {

    private RecyclerView recyclerView;
    private CodeTaskAdapter adapter;
    private List<CodeTask> codeTaskList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_task_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Add spacing between items
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
        recyclerView.addItemDecoration(new SpacingItemDecoration(spacingInPixels));

        loadTasks();

        adapter = new CodeTaskAdapter(requireContext(), codeTaskList, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadTasks() {
        CodeTask codeTaskHelper = new CodeTask(requireContext()); // CodeTask 클래스의 인스턴스 생성
        codeTaskList = new ArrayList<>(); // CodeTask 리스트 초기화

        // 모든 CodeTask 데이터 가져오기
        List<CodeTask> allCodeTasks = codeTaskHelper.getAllCodeTasks();

        // 사용자정의/로 시작하는 데이터만 필터링
        for (CodeTask codeTask : allCodeTasks) {
            if (codeTask.getUrl().startsWith("사용자정의/")) {
                codeTaskList.add(codeTask); // CodeTask 객체 추가
            }
        }

        codeTaskHelper.close(); // 리소스 정리
    }

    @Override
    public void onTaskClick(String url) {
        Intent intent = new Intent(requireContext(), CodeExecuterActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }
}
