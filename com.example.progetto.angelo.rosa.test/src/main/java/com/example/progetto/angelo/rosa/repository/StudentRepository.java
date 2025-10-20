package com.example.progetto.angelo.rosa.repository;

import java.util.List;

import com.example.progetto.angelo.rosa.model.Student;

public interface StudentRepository {
	public List<Student> findAll();

	Student findById(String id);

	void save(Student student);

	void delete(String id);
}
