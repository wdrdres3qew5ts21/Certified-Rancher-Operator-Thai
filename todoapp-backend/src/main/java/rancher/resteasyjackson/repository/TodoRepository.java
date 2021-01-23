package rancher.resteasyjackson.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import rancher.resteasyjackson.model.Todo;

public interface TodoRepository extends JpaRepository<Todo, Integer>{

}
