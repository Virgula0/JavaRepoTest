package com.example.progetto.angelo.rosa.test;

import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.GenericContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.*;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;

import com.example.progetto.angelo.rosa.repository.StudentMongoRepository;
import com.mongodb.*;

@RunWith(GUITestRunner.class)
public class SchoolSwingAppE2E extends AssertJSwingJUnitTestCase {

	private static final String FIXTURE_SECOND_STUDENT_NAME = "second student";

	private static final String FIXTURE_SECOND_STUDENT_ID = "2";

	private static final String FIXTURE_FIRST_STUDENT_NAME = "first student";

	private static final String FIXTURE_FIRST_STUDENT_ID = "1";

	@SuppressWarnings({ "rawtypes", "resource" })
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer(
			StudentMongoRepository.IMAGE + ":" + StudentMongoRepository.VERSION)
			.withExposedPorts(StudentMongoRepository.PORT);

	private static final String DB_NAME = "test-db";

	private static final String COLLECTION_NAME = "test-collection";
	private MongoClient mongoClient;
	private FrameFixture window;

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	private void addTestStudentToDatabase(String id, String name) {
		Document d = new Document(StudentMongoRepository.ID_KEY, StudentMongoRepository.NAME_KEY);
		d.append(StudentMongoRepository.ID_KEY, id);
		d.append(StudentMongoRepository.NAME_KEY, name);
		mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME).insertOne(d);
	}

	@Override
	protected void onSetUp() {
		String containerIpAddress = mongo.getContainerIpAddress();
		Integer mappedPort = mongo.getFirstMappedPort();
		mongoClient = new MongoClient(containerIpAddress, mappedPort);
		// always start with an empty database
		mongoClient.getDatabase(DB_NAME).drop();
		// add some students to the database
		addTestStudentToDatabase(FIXTURE_FIRST_STUDENT_ID, FIXTURE_FIRST_STUDENT_NAME);
		addTestStudentToDatabase(FIXTURE_SECOND_STUDENT_ID, FIXTURE_SECOND_STUDENT_NAME);
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
	@GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list().contents())
				.anySatisfy(e -> assertThat(e).contains(FIXTURE_FIRST_STUDENT_ID, FIXTURE_FIRST_STUDENT_NAME))
				.anySatisfy(e -> assertThat(e).contains(FIXTURE_SECOND_STUDENT_ID, FIXTURE_SECOND_STUDENT_NAME));
	}

	@Test
	@GUITest
	public void testAddButtonSuccess() {
		window.textBox("idTextBox").enterText("10");
		window.textBox("nameTextBox").enterText("new student");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list().contents()).anySatisfy(e -> assertThat(e).contains("10", "new student"));
	}

	/*
	 * Note that in the following test, we only verify that an error is shown. We do
	 * not verify that the student we were trying to add is not effectively shown in
	 * the list.
	 */
	@Test
	@GUITest
	public void testAddButtonError() {
		window.textBox("idTextBox").enterText(FIXTURE_FIRST_STUDENT_ID);
		window.textBox("nameTextBox").enterText("new one");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.label("errorMessageLabel").text()).contains(FIXTURE_FIRST_STUDENT_ID,
				FIXTURE_FIRST_STUDENT_NAME);
	}

	@Test
	@GUITest
	public void testDeleteButtonSuccess() {
		window.list("studentList").selectItem(Pattern.compile(".*" + FIXTURE_FIRST_STUDENT_NAME + ".*"));
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		assertThat(window.list().contents()).noneMatch(e -> e.contains(FIXTURE_FIRST_STUDENT_NAME));
	}

	private void removeTestStudentFromDatabase(String id) {
		Document query = new Document(StudentMongoRepository.ID_KEY, id);
		mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME).findOneAndDelete(query);
	}

	@Test
	@GUITest
	public void testDeleteButtonError() {
		// select the student in the list...
		window.list("studentList").selectItem(Pattern.compile(".*" + FIXTURE_FIRST_STUDENT_NAME + ".*"));
		// ... in the meantime, manually remove the student from the database
		removeTestStudentFromDatabase(FIXTURE_FIRST_STUDENT_ID);
		// now press the delete button
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		// and verify an error is shown
		assertThat(window.label("errorMessageLabel").text()).contains(FIXTURE_FIRST_STUDENT_ID,
				FIXTURE_FIRST_STUDENT_NAME);
	}
	/*
	 * Summarizing, in these e2e tests, we never refer to the Java classes of our
	 * application. To further stress this, we launched our GUI application through
	 * AssertJ Swing by specifying the fully qualified name of the main class, not
	 * its Java type. Our e2e tests interact with our application only through the
	 * AssertJ Swing fixture, that is, through the application GUI.
	 */

}
