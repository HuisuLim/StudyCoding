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

import com.example.StudyCoding.Database.TaskDatabase.Task;
import com.example.StudyCoding.Database.TaskDatabase.TaskRepository;
import com.example.StudyCoding.ViewSelectedProblemWithCode;
import com.example.StudyCoding.R;
import com.example.StudyCoding.Fragment.Adapter.SpacingItemDecoration;
import com.example.StudyCoding.Fragment.Adapter.TaskAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProblemListFragment extends Fragment implements TaskAdapter.OnTaskClickListener  {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_task_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
        recyclerView.addItemDecoration(new SpacingItemDecoration(spacingInPixels));

        loadTasks();

        adapter = new TaskAdapter(requireContext(), taskList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadTasks() {
        TaskRepository repository = new TaskRepository(requireContext());
        taskList = new ArrayList<>();
        List<String> allTasks = repository.getAllTasks();

        for (String taskData : allTasks) {
            String[] parts = taskData.split(", ");
            String url = parts[1].split(": ")[1];
            String title = parts[3].split(": ")[1];
            taskList.add(new Task(url, title));
        }

        repository.close();
    }
    @Override
    public void onTaskClick(String url) {
        Intent intent = new Intent(requireContext(), ViewSelectedProblemWithCode.class);
        intent.putExtra("selected_url", url);
        startActivity(intent);
    }
}
