package com.example.progetto.angelo.rosa.controller;

import com.example.progetto.angelo.rosa.model.Student;
import com.example.progetto.angelo.rosa.repository.StudentRepository;
import com.example.progetto.angelo.rosa.view.StudentView;

/*
 * The controller processes user requests from the view by delegating database operations
 * to the repository and presents results to the user by delegating to the view
 * Controller takes care to make some checks, for example saving more student having the same id
 * This is not actually needed in a real scenario because primary key cannot be duplicated and the insert
 * would fail
 * IMPORTANT: database operations such as querying and updating should be executed in transactions
 * For transaction we need to add another layer called services separated from repositories and manage them from there
 * TIP: They can be used at controller level somehow
 * */
public class SchoolController {
	private StudentView studentView;
	private StudentRepository studentRepository;

	public SchoolController(StudentView studentView, StudentRepository studentRepo) {
		this.studentView = studentView;
		this.studentRepository = studentRepo;
	}

	public void allStudents() {
		studentView.showAllStudents(studentRepository.findAll());
	}

	public void newStudent(Student student) {
		Student existingStudent = studentRepository.findById(student.getId());

		if (existingStudent != null) {
			studentView.showError("Already existing student with id " + student.getId(), existingStudent);
			return;
		}

		studentRepository.save(student);
		studentView.studentAdded(student);
	}

	public void deleteStudent(Student student) {
		if (studentRepository.findById(student.getId()) == null) {
			studentView.showError("No existing student with id " + student.getId(), student);
			return;
		}

		studentRepository.delete(student.getId());
		studentView.studentRemoved(student);
	}
	
	public int we() {
		int a =1;
		if (a ==1) {
			return 0;
		}
		
		return -1;
	}
}
