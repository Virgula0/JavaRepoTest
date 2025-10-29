package com.example.progetto.angelo.rosa.main;

import java.awt.EventQueue;

import com.example.progetto.angelo.rosa.controller.SchoolController;
import com.example.progetto.angelo.rosa.repository.StudentMongoRepository;
import com.example.progetto.angelo.rosa.view.StudentSwingView;
import com.mongodb.*;

/**
 * Simple app accessing MongoDB.
 */
public class Main {
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				String mongoHost = "localhost";
				int mongoPort = 27017;
				if (args.length > 0)
					mongoHost = args[0];
				if (args.length > 1)
					mongoPort = Integer.parseInt(args[1]);
				StudentMongoRepository studentRepository = new StudentMongoRepository(new MongoClient(new ServerAddress(mongoHost, mongoPort)), "school", "student");
				StudentSwingView studentView = new StudentSwingView();
				SchoolController schoolController = new SchoolController(studentView, studentRepository);
				studentView.setSchoolController(schoolController);
				studentView.setVisible(true);
				schoolController.allStudents();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
