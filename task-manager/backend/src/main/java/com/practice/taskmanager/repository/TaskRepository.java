package com.practice.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.practice.taskmanager.model.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    
}
