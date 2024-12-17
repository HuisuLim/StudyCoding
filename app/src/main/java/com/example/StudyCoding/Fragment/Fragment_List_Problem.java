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

import com.example.StudyCoding.Database.Database_Problem_Info.ProblemTask;
import com.example.StudyCoding.Database.Database_Problem_Info.ProblemTaskRepository;
import com.example.StudyCoding.Activity_ViewPager_SavedProblemWithCode;
import com.example.StudyCoding.R;
import com.example.StudyCoding.Fragment.Adapter.Decoration_SpacingItem;
import com.example.StudyCoding.Fragment.Adapter.Adapter_Saved_List;

import java.util.ArrayList;
import java.util.List;

public class Fragment_List_Problem extends Fragment implements Adapter_Saved_List.OnTaskClickListener  {

    private RecyclerView recyclerView;
    private Adapter_Saved_List adapter;
    private List<ProblemTask> problemTaskList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_problem_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
        recyclerView.addItemDecoration(new Decoration_SpacingItem(spacingInPixels));

        loadTasks();

        adapter = new Adapter_Saved_List(requireContext(), problemTaskList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadTasks() {
        ProblemTaskRepository repository = new ProblemTaskRepository(requireContext());
        problemTaskList = new ArrayList<>();
        List<String> allTasks = repository.getAllTasks();

        for (String taskData : allTasks) {
            String[] parts = taskData.split(", ");
            String url = parts[1].split(": ")[1];
            String title = parts[3].split(": ")[1];
            problemTaskList.add(new ProblemTask(url, title));
        }

        repository.close();
    }
    @Override
    public void onTaskClick(String url) {
        Intent intent = new Intent(requireContext(), Activity_ViewPager_SavedProblemWithCode.class);
        intent.putExtra("selected_url", url);
        startActivity(intent);
    }
}
