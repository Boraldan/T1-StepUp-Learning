package ru.boraldan.aop.taskaop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.boraldan.aop.taskaop.domen.Tasks;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Tasks, Long>{

}