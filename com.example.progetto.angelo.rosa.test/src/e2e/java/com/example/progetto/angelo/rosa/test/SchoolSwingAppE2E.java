package com.example.progetto.angelo.rosa.test;

import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.GenericContainer;
import static org.assertj.swing.launcher.ApplicationLauncher.*;

import javax.swing.JFrame;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;

import com.example.progetto.angelo.rosa.repository.StudentMongoRepository;
import com.mongodb.*;

@RunWith(GUITestRunner.class)
public class SchoolSwingAppE2E extends AssertJSwingJUnitTestCase {

	@SuppressWarnings({ "rawtypes", "resource" })
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer(
			StudentMongoRepository.IMAGE + ":" + StudentMongoRepository.VERSION)
			.withExposedPorts(StudentMongoRepository.PORT);

	private static final String DB_NAME = "test-db";

	private static final String COLLECTION_NAME = "test-collection";
	private MongoClient mongoClient;
	private FrameFixture window;

	private StudentMongoRepository studentRepository;

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	private void addTestStudentToDatabase(String id, String name) {
		mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME)
				.insertOne(new Document().append("id", id).append("name", name));
	}

	@Override
	protected void onSetUp() {
		String containerIpAddress = mongo.getContainerIpAddress();
		Integer mappedPort = mongo.getFirstMappedPort();
		mongoClient = new MongoClient(containerIpAddress, mappedPort);
		// always start with an empty database
		mongoClient.getDatabase(DB_NAME).drop();
		// add some students to the database
		addTestStudentToDatabase("1", "first student");
		addTestStudentToDatabase("2", "second student");
		// start the Swing application
		application("com.example.progetto.angelo.rosa.main.Main").withArgs("--mongo-host=" + containerIpAddress,
				"--mongo-port=" + mappedPort.toString(), "--db-name=" + DB_NAME, "--db-collection=" + COLLECTION_NAME)
				.start();
		// get a reference of its JFrame
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Student View".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
	}
	
	@Test
	public void e(){
		
	}
	

}
