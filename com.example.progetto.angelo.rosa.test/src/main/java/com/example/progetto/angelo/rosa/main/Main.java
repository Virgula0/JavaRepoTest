package com.example.progetto.angelo.rosa.main;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import com.example.progetto.angelo.rosa.controller.SchoolController;
import com.example.progetto.angelo.rosa.repository.StudentMongoRepository;
import com.example.progetto.angelo.rosa.view.StudentSwingView;
import com.mongodb.*;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

// Log4j
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Simple app accessing MongoDB.
 */
@Command(mixinStandardHelpOptions = true)
public class Main implements Callable<Void> {
	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;

	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "school";

	@Option(names = { "--db-collection" }, description = "Collection name")
	private String collectionName = "student";

	public static void main(String[] args) {
		new CommandLine(new Main()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				StudentMongoRepository studentRepository = new StudentMongoRepository(
						new MongoClient(new ServerAddress(mongoHost, mongoPort)), databaseName, collectionName);
				StudentSwingView studentView = new StudentSwingView();
				SchoolController schoolController = new SchoolController(studentView, studentRepository);
				studentView.setSchoolController(schoolController);
				studentView.setVisible(true);
				schoolController.allStudents();
			} catch (Exception e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception", e);
			}
		});
		return null;
	}
}
