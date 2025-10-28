package com.example.progetto.angelo.rosa.view;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import com.example.progetto.angelo.rosa.controller.SchoolController;
import com.example.progetto.angelo.rosa.model.Student;
import com.example.progetto.angelo.rosa.repository.StudentMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

public class ModelViewControllerIT extends AssertJSwingJUnitTestCase {
	/*
	 * This class verifies that actions performed on the Swing view will lead to
	 * changes to the state of the database. We need only 2 methods
	 */

	private StudentSwingView studentSwingView;
	private StudentMongoRepository studentRepository;
	private SchoolController schoolController;

	private FrameFixture window;

	@SuppressWarnings({ "rawtypes", "resource" })
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer(
			StudentMongoRepository.IMAGE + ":" + StudentMongoRepository.VERSION)
			.withExposedPorts(StudentMongoRepository.PORT);
	private static MongoClient client;
	private static MongoDatabase database;

	@BeforeClass
	public static void beforeClassSetup() {
		client = new MongoClient(
				new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(StudentMongoRepository.PORT)));
		database = client.getDatabase(StudentMongoRepository.SCHOOL_DB_NAME);
	}

	@Override
	protected void onSetUp() throws Exception {
		// make sure we always start with a clean database for each test
		database.drop();
		GuiActionRunner.execute(() -> {
			// refresh repo
			studentRepository = new StudentMongoRepository(client);
			studentSwingView = new StudentSwingView();
			// re-init school controller
			schoolController = new SchoolController(studentSwingView, studentRepository);
			studentSwingView.setSchoolController(schoolController);
			return studentSwingView;
		});
		window = new FrameFixture(robot(), studentSwingView);
		window.show();
	}

	@AfterClass
	public static void afterClass() {
		client.close();
	}

	@Test
	public void testAddStudent() {
		// use the UI to add a student...
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("test");
		window.button(JButtonMatcher.withText("Add")).click();
		// ...verify that it has been added to the database
		assertThat(studentRepository.findById("1")).isEqualTo(new Student("1", "test"));
	}

	@Test
	public void testDeleteStudent() {
		// add a student needed for tests
		studentRepository.save(new Student("99", "existing"));
		// use the controller's allStudents to make the student
		// appear in the GUI list
		GuiActionRunner.execute(() -> schoolController.allStudents());
		// ...select the existing student
		window.list().selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		// verify that the student has been deleted from the db
		assertThat(studentRepository.findById("99")).isNull();
	}
}
