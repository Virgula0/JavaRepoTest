package com.example.progetto.angelo.rosa.repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.example.progetto.angelo.rosa.model.Student;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

public class StudentMongoRepository implements StudentRepository {

	public static final String SCHOOL_DB_NAME = "school";
	public static final String STUDENT_COLLECTION_NAME = "student";
	private MongoCollection<Document> studentCollection;
	
	private static final String ID_KEY = "id";
	private static final String NAME_KEY = "name";

	public StudentMongoRepository(MongoClient client) {
		studentCollection = client.getDatabase(SCHOOL_DB_NAME).getCollection(STUDENT_COLLECTION_NAME);
	}

	@Override
	public List<Student> findAll() {
		return StreamSupport.stream(studentCollection.find().spliterator(), false)
				.map(d -> documentToStudent(d)).collect(Collectors.toList());
	}

	private Student documentToStudent(Document d) {
		return new Student("" + d.get(ID_KEY), "" + d.get(NAME_KEY));
	}

	@Override
	public Student findById(String id) {
		// TODO Auto-generated method stub
		return StreamSupport.stream(studentCollection.find().spliterator(), false)
				.map(d -> documentToStudent(d))
				.filter(x -> Objects.equals(x.getId(),id))
				.findFirst()
				.orElse(null);
	}

	@Override
	public void save(Student student) {
		Document d = new Document(ID_KEY,NAME_KEY);
		d.append(ID_KEY, student.getId());
		d.append(NAME_KEY, student.getName());
		this.studentCollection.insertOne(d);
	}

	@Override
	public void delete(String id) {
		Document query = new Document(ID_KEY, id);
		studentCollection.findOneAndDelete(query);
	}
}