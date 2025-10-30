package com.example.progetto.angelo.rosa.view;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.GenericContainer;

import com.example.progetto.angelo.rosa.controller.SchoolController;
import com.example.progetto.angelo.rosa.model.Student;
import com.example.progetto.angelo.rosa.repository.StudentMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

@RunWith(GUITestRunner.class)
public class ITViewControllerRepository extends AssertJSwingJUnitTestCase {

	/*
	 * Database here could be mocked with an in-memory database I used interaction
	 * with a real implementation anyway Note that it does not make sense to write
	 * integration tests for scenarios that only have to do with the view itself.
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

	// use the repository to populate the database, then we call the controller’s
	// method and verify that the frame’s list is populated.
	@Test
	@GUITest
	public void testAllStudents() {
		// use the repository to add students to the database
		Student student1 = new Student("1", "test1");
		Student student2 = new Student("2", "test2");
		studentRepository.save(student1);
		studentRepository.save(student2);

		studentRepository.findAll().forEach(System.out::println);
		// use the controller's allStudents
		GuiActionRunner.execute(() -> schoolController.allStudents());
		// and verify that the view's list is populated
		assertThat(window.list().contents()).containsExactly(student1.toString(), student2.toString());
	}

	// verify that the view and the controller interact correctly when a student is
	// added through
	// the “Add” button
	@Test
	@GUITest
	public void testAddButtonSuccess() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("test");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list().contents()).containsExactly(new Student("1", "test").toString());
	}

	/*
	 * For the scenario where a student with the same id is already present, we need
	 * to first add a student to the database through the repository. Then we verify
	 * that the student does not appear in the list and that instead an error
	 * message is shown in the view.
	 */
	@Test
	@GUITest
	public void testAddButtonError() {
		studentRepository.save(new Student("1", "existing"));
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("test");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list().contents()).isEmpty();
		window.label("errorMessageLabel")
				.requireText("Already existing student with id 1: " + new Student("1", "existing"));
	}

	/*
	 * verify that the view and the controller interact correctly when a student is
	 * deleted through the “Delete” button
	 */
	@Test
	@GUITest
	public void testDeleteButtonSuccess() {
		// use the controller to populate the view's list...
		GuiActionRunner.execute(() -> schoolController.newStudent(new Student("1", "toremove")));
		// ...with a student to select
		window.list().selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		assertThat(window.list().contents()).isEmpty();
	}

	@Test
	@GUITest
	public void testDeleteButtonError() {
		// manually add a student to the list, which will not be in the db
		Student student = new Student("1", "non existent");
		GuiActionRunner.execute(() -> studentSwingView.getListStudentsModel().addElement(student));
		window.list().selectItem(0);
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		assertThat(window.list().contents()).containsExactly(student.toString());
		window.label("errorMessageLabel").requireText("No existing student with id 1: " + student);
	}

}
