package kanefreeman.jpa.repositories;

import kanefreeman.jpa.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ProjectRepository extends JpaRepository<Project, Integer>, QuerydslPredicateExecutor<Project> {
}
