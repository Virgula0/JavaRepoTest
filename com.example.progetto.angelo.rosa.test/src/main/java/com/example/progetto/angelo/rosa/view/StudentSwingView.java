package com.example.progetto.angelo.rosa.view;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.example.progetto.angelo.rosa.controller.SchoolController;
import com.example.progetto.angelo.rosa.model.Student;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class StudentSwingView extends JFrame implements StudentView {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField idText;
	private JLabel nameLabel;
	private JTextField idName;
	private JButton buttonAdd;
	private JList<Student> list;
	private JScrollPane scrollPane;
	private JButton deleteButton;
	private JLabel errorMessage;

	// used in test, we will add and remove Student objects for not adding direclty
	// elements to the JList
	private DefaultListModel<Student> listStudentsModel;

	DefaultListModel<Student> getListStudentsModel() { // package private method used fot testing purpose only
		return listStudentsModel;
	}

	private transient SchoolController schoolController;

	// needed because we cannot pass it directly to the constructor so we add a set
	// method useful
	// for integration tests when will use real controller
	public void setSchoolController(SchoolController schoolController) {
		this.schoolController = schoolController;
	}

	/**
	 * Create the frame.
	 */
	public StudentSwingView() {

		listStudentsModel = new DefaultListModel<>();
		list = new JList<>(listStudentsModel);

		setTitle("Student View");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 927, 586);
		contentPane = new JPanel();
		contentPane.setMaximumSize(new Dimension(55000, 55000));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 830, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JLabel idLabel = new JLabel("id");
		GridBagConstraints gbc_idLabel = new GridBagConstraints();
		gbc_idLabel.anchor = GridBagConstraints.EAST;
		gbc_idLabel.insets = new Insets(0, 0, 5, 5);
		gbc_idLabel.gridx = 0;
		gbc_idLabel.gridy = 1;
		contentPane.add(idLabel, gbc_idLabel);

		idText = new JTextField();

		idText.setName("idTextBox");
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 1;
		contentPane.add(idText, gbc_textField);
		idText.setColumns(10);

		nameLabel = new JLabel("name");
		nameLabel.setName("stduentName");
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.anchor = GridBagConstraints.EAST;
		gbc_nameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_nameLabel.gridx = 0;
		gbc_nameLabel.gridy = 2;
		contentPane.add(nameLabel, gbc_nameLabel);

		idName = new JTextField();
		idName.setName("nameTextBox");
		GridBagConstraints gbc_idName = new GridBagConstraints();
		gbc_idName.insets = new Insets(0, 0, 5, 0);
		gbc_idName.fill = GridBagConstraints.HORIZONTAL;
		gbc_idName.gridx = 1;
		gbc_idName.gridy = 2;
		contentPane.add(idName, gbc_idName);
		idName.setColumns(10);

		buttonAdd = new JButton("Add");
		buttonAdd.setEnabled(false);
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridwidth = 2;
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 3;
		contentPane.add(buttonAdd, gbc_btnNewButton);

		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 4;
		contentPane.add(scrollPane, gbc_scrollPane);

		// list = new JList<>(); not needed because already intialized with our student
		// list
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(list);
		list.setName("studentList");

		deleteButton = new JButton("Delete Selected");
		deleteButton.setName("deleteButton");
		deleteButton.setEnabled(false);
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_1.gridwidth = 2;
		gbc_btnNewButton_1.gridx = 0;
		gbc_btnNewButton_1.gridy = 5;
		contentPane.add(deleteButton, gbc_btnNewButton_1);

		errorMessage = new JLabel(" ");
		errorMessage.setName("errorMessageLabel");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.gridwidth = 2;
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 6;
		contentPane.add(errorMessage, gbc_lblNewLabel_2);

		eventsHandler(); // we call this at the end to let all the components to be initialized
	}

	private void eventsHandler() {
		KeyAdapter keyAdapter = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				buttonAdd.setEnabled(!idText.getText().isBlank() && !idName.getText().isBlank());
			}
		};
		idText.addKeyListener(keyAdapter);
		idName.addKeyListener(keyAdapter);

		// list
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				deleteButton.setEnabled(list.getSelectedIndex() != -1);
			}
		});

		buttonAdd.addActionListener(e -> schoolController.newStudent(new Student(idText.getText(), idName.getText())));
		deleteButton.addActionListener(e -> schoolController.deleteStudent(list.getSelectedValue()));
	}

	@Override
	public void showAllStudents(List<Student> students) {
		students.stream().forEach(listStudentsModel::addElement);
	}

	@Override
	public void showError(String message, Student student) {
		errorMessage.setText(message + ": " + student.toString());
	}

	@Override
	public void studentAdded(Student student) {
		listStudentsModel.addElement(student);
		errorMessage.setText(" ");
	}

	@Override
	public void studentRemoved(Student student) {
		listStudentsModel.removeElement(student);
		errorMessage.setText(" ");
	}
}
