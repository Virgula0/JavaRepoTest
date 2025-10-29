package com.example.progetto.angelo.rosa.controller;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.GenericContainer;

import com.example.progetto.angelo.rosa.model.Student;
import com.example.progetto.angelo.rosa.repository.StudentMongoRepository;
import com.example.progetto.angelo.rosa.repository.StudentRepository;
import com.example.progetto.angelo.rosa.view.StudentView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

public class SchoolControllerRepositoryIT {
	/*
	 * Here we mock what we will test in the UI with e2e tests integration tests
	 * tests only positive cases :)
	 * we test both controller and repository with integration tests
	 */
	@Mock
	private StudentView studentView;

	private StudentRepository studentRepository;

	private SchoolController schoolController;

	private AutoCloseable closeable;

	@SuppressWarnings({ "rawtypes", "resource" })
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer(
			StudentMongoRepository.IMAGE + ":" + StudentMongoRepository.VERSION)
			.withExposedPorts(StudentMongoRepository.PORT);
	private static MongoClient client;
	private static MongoDatabase database;

	@BeforeClass
	public static void beforeClassSetup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(StudentMongoRepository.PORT)));
		database = client.getDatabase(StudentMongoRepository.SCHOOL_DB_NAME);
	}

	@Before
	public void beforeSetup() {
		closeable = MockitoAnnotations.openMocks(this); // init all @Mocks within this class

		// refresh repo
		studentRepository = new StudentMongoRepository(client);

		// make sure we always start with a clean database for each test
		database.drop();
		// re-init school controller
		schoolController = new SchoolController(studentView, studentRepository); // studentView has been mocked
	}

	@AfterClass
	public static void tearDown() throws Exception {
		// make sure we always start with a clean database for each test
		database.drop();
		client.close();
	}

	@Test
	public void testAllStudents() {
		Student student = new Student("1", "test");
		studentRepository.save(student);
		schoolController.allStudents();

		verify(studentView).showAllStudents(Arrays.asList(student));
	}

	@Test
	public void testNewStudent() {
		Student student = new Student("1", "test");
		schoolController.newStudent(student);
		verify(studentView).studentAdded(student);
	}

	@Test
	public void testDeleteStudent() {
		Student studentToDelete = new Student("1", "test");
		studentRepository.save(studentToDelete);
		schoolController.deleteStudent(studentToDelete);
		verify(studentView).studentRemoved(studentToDelete);
	}
}
