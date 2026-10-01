package com.practice.taskmanager;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.practice.taskmanager.model.Task;
import com.practice.taskmanager.repository.TaskRepository;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    private final TaskRepository repository;

    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Task> getTasks() {
        return this.repository.findAll();
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return this.repository.save(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Integer id, @RequestBody Task updatedTask) {
        Task task = repository.findById(id).orElseThrow();

        task.setTitle(updatedTask.getTitle());
        task.setCompleted(updatedTask.isCompleted());

        return repository.save(task);
    }
}
