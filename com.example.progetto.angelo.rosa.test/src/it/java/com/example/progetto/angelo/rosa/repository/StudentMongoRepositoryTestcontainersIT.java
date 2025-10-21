package com.example.progetto.angelo.rosa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.example.progetto.angelo.rosa.model.Student;
import com.mongodb.*;

/*
 * Integration tests are meant to test only positive cases, negative ones should be handled by junit tests already
 * IMPORTANT: integration tests on repository are different from junit tests on repository
 * in both we can use test containers, eventually you can use memory database in junit test for testing repository
 */
public class StudentMongoRepositoryTestcontainersIT {

	@SuppressWarnings({ "rawtypes", "resource" })
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer(
			StudentMongoRepository.IMAGE + ":" + StudentMongoRepository.VERSION)
			.withExposedPorts(StudentMongoRepository.PORT);

	private MongoClient client;
	private StudentMongoRepository studentRepository;
	private MongoCollection<Document> studentCollection;

	private void addTestStudentToDatabase(String id, String name) {
		studentCollection.insertOne(new Document().append("id", id).append("name", name));
	}

	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(StudentMongoRepository.PORT)));
		studentRepository = new StudentMongoRepository(client);
		MongoDatabase database = client.getDatabase(StudentMongoRepository.SCHOOL_DB_NAME);
		// make sure we always start with a clean database
		database.drop();
		studentCollection = database.getCollection(StudentMongoRepository.STUDENT_COLLECTION_NAME);
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testFindAll() {
		addTestStudentToDatabase("1", "test1");
		addTestStudentToDatabase("2", "test2");

		assertThat(studentRepository.findAll()).containsExactly(new Student("1", "test1"), new Student("2", "test2"));
	}

	@Test
	public void testFindById() {
		addTestStudentToDatabase("1", "test1");
		addTestStudentToDatabase("2", "test2");

		assertThat(studentRepository.findById("2")).isEqualTo(new Student("2", "test2"));
	}
}