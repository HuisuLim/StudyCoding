package com.example.StudyCoding;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.StudyCoding.Database.TaskDatabase.Task;
import com.example.StudyCoding.Database.TaskDatabase.TaskRepository;
import com.example.StudyCoding.ProblemList.SpacingItemDecoration;
import com.example.StudyCoding.ProblemList.TaskAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProblemListActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add spacing between items
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
        recyclerView.addItemDecoration(new SpacingItemDecoration(spacingInPixels));

        loadTasks();

        adapter = new TaskAdapter(this, taskList, this);
        recyclerView.setAdapter(adapter);
    }

    private void loadTasks() {
        TaskRepository repository = new TaskRepository(this);
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
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("selected_url", url);
        startActivity(intent);
    }
}
