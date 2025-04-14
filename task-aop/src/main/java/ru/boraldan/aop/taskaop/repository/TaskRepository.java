package ru.boraldan.aop.taskaop.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task.entity.Tasks;

import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Tasks, UUID>{

}