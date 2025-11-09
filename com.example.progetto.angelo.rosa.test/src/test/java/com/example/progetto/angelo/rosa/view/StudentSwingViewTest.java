package com.example.progetto.angelo.rosa.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JListFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.progetto.angelo.rosa.controller.SchoolController;
import com.example.progetto.angelo.rosa.model.Student;

@RunWith(GUITestRunner.class)
public class StudentSwingViewTest extends AssertJSwingJUnitTestCase {

	// DO NOT INTERACT WITH COMPUTER WHEN RUNNING UI TESTS
	// asertj swing fixture
	// used to create an interaction with our window
	// used to interact with student swing view
	private FrameFixture window;

	// used for interacting with GUI components
	private StudentSwingView studentSwingView;

	@Mock
	private SchoolController schoolController;

	private AutoCloseable closeable;

	// onSetUp called by the superclass before each test
	// The Robot passed to the fixture’s constructor is taken from the superclass.
	// let's start with a new fresh user interface each time
	@Override
	protected void onSetUp() throws Exception {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			studentSwingView = new StudentSwingView();
			studentSwingView.setSchoolController(schoolController); // important! this should be set here otherwise
			// doesn't work
			return studentSwingView;
		});
		window = new FrameFixture(super.robot(), studentSwingView);
		window.show(); // shows the frame to test
	}

	// called by the superclass after each test
	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	@Test
	public void assertControllerIsTheRightOne() {
		assertThat(studentSwingView.getSchoolController()).isSameAs(schoolController).isEqualTo(schoolController);
	}

	@Test
	@GUITest
	public void assertStartRunsCorrectly() throws Exception {
		// Run inside the Swing Event Dispatch Thread
		SwingUtilities.invokeAndWait(() -> {
			studentSwingView.start();
		});
		// Verify controller interaction
		verify(schoolController, times(1)).allStudents();
		// Check that frame is visible
		assertThat(studentSwingView.isVisible()).isTrue();
		// Clean up (important for Swing tests)
		SwingUtilities.invokeAndWait(() -> studentSwingView.dispose());
	}

	// BASIC TESTS
	@Test
	@GUITest // @GUITest you tell AssertJ Swing to take a screenshot of the desktop when a
	// JUnit GU test fails. Screenshots of failed tests will be saved in the
	// directory failed-gui-tests
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("id")); // id label
		window.textBox("idTextBox").requireEnabled(); // id textbox
		window.label(JLabelMatcher.withText("name")); // name label
		window.textBox("nameTextBox").requireEnabled(); // name test box
		window.button(JButtonMatcher.withText("Add")).requireDisabled(); // add button disabled
		window.list("studentList"); // studentList
		window.button(JButtonMatcher.withText("Delete Selected")).requireDisabled(); // delete button disabled
		window.label("errorMessageLabel").requireText(" "); // error message
	}

	// Add event handler → key → keyReleased
	@Test
	@GUITest
	public void testWhenIdAndNameAreNonEmptyThenAddButtonShouldBeEnabled() {
		window.textBox("idTextBox").enterText("1"); // be aware! we must use enterText not set text as this method
		// emulates user input and gives the listener the possibility to
		// intercept the event
		window.textBox("nameTextBox").enterText("test");
		window.button(JButtonMatcher.withText("Add")).requireEnabled();
	}

	@Test
	@GUITest
	public void testWhenEitherIdOrNameAreBlankThenAddButtonShouldBeDisabled() {
		JTextComponentFixture idTextBox = window.textBox("idTextBox");
		JTextComponentFixture nameTextBox = window.textBox("nameTextBox");

		idTextBox.enterText("1");
		nameTextBox.enterText(" ");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();

		idTextBox.setText("");
		nameTextBox.setText("");

		idTextBox.enterText(" ");
		nameTextBox.enterText("test");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
	}

	@Test
	@GUITest
	public void testListEmpty() {
		JListFixture list = window.list("studentList");
		assertThat(list.contents().length).isZero();
	}

	@Test
	@GUITest
	public void testDeleteButtonShouldBeEnabledOnlyWhenAStudentIsSelected() {
		// this test uses GuiActionRunner.execute for Event Dispatch Thread (EDT)
		// and relies on listener aded to the list with valueChanged listener
		GuiActionRunner.execute(() -> studentSwingView.getListStudentsModel().addElement(new Student("1", "test")));
		window.list("studentList").selectItem(0);
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Delete Selected"));
		deleteButton.requireEnabled();
		window.list("studentList").clearSelection();
		deleteButton.requireDisabled();
	}

	@Test
	public void testsShowAllStudentsShouldAddStudentDescriptionsToTheList() {
		Student student1 = new Student("1", "test1");
		Student student2 = new Student("2", "test2");
		GuiActionRunner.execute(() -> studentSwingView.showAllStudents(Arrays.asList(student1, student2)));
		String[] listContents = window.list().contents();
		assertThat(listContents).containsExactly(student1.toString(), student2.toString());
	}

	@Test
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		Student student = new Student("1", "test1");
		GuiActionRunner.execute(() -> studentSwingView.showError("error message", student));
		window.label("errorMessageLabel").requireText("error message: " + student);
	}

	@Test
	public void testStudentAddedShouldAddTheStudentToTheListAndResetTheErrorLabel() {
		// provoke error first
		Student student = new Student("1", "test1");
		// add student and check errorMessage removed
		GuiActionRunner.execute(() -> studentSwingView.studentAdded(student));
		String[] listContents = window.list().contents();
		assertThat(listContents).containsExactly(student.toString());
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	public void testStudentDeletedShouldDeleteTheStudentFromTheListAndResetTheErrorLabel() {
		// setup
		Student student = new Student("1", "test1");
		Student student2 = new Student("2", "test2");
		GuiActionRunner.execute(() -> {
			studentSwingView.getListStudentsModel().addElement(student);
			studentSwingView.getListStudentsModel().addElement(student2);
		});
		String[] listContents = window.list().contents();
		String[] studentStrings = new String[] { student.toString(), student2.toString() };
		assertThat(listContents).containsExactly(studentStrings);

		// execute
		window.list("studentList").selectItem(0);
		GuiActionRunner.execute(() -> studentSwingView.studentRemoved(student)); // remove

		// verify
		listContents = window.list().contents();
		assertThat(listContents).containsExactly(student2.toString());
		window.label("errorMessageLabel").requireText(" ");
	}

	// TESTS WITH MOCKED SCHOOL CONTROLLER
	// All we need to care about, when unit testing the frame, is that it implements
	// the StudentView methods
	// these tests needs action listeners on add and delete buttons
	@Test
	@GUITest
	public void testAddButtonShouldDelegateToSchoolControllerNewStudent() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("test");
		window.button(JButtonMatcher.withText("Add")).click();
		verify(schoolController).newStudent(new Student("1", "test"));
	}

	@Test
	public void testDeleteButtonShouldDelegateToSchoolControllerDeleteStudent() {
		Student student1 = new Student("1", "test1");
		Student student2 = new Student("2", "test2");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Student> listStudentsModel = studentSwingView.getListStudentsModel();
			listStudentsModel.addElement(student1);
			listStudentsModel.addElement(student2);
		});
		window.list("studentList").selectItem(1);
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		verify(schoolController).deleteStudent(student2);
	}

	// we tested correctly all the frame at this point
}