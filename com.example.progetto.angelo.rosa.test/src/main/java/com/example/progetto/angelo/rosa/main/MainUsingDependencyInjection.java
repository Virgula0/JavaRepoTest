package com.example.progetto.angelo.rosa.main;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import com.example.progetto.angelo.rosa.controller.SchoolController;
import com.example.progetto.angelo.rosa.repository.StudentMongoRepository;
import com.example.progetto.angelo.rosa.repository.StudentRepository;
import com.example.progetto.angelo.rosa.view.StudentSwingView;
import com.example.progetto.angelo.rosa.view.StudentView;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;
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
public class MainUsingDependencyInjection implements Callable<Void> {
	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;

	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "school";

	@Option(names = { "--db-collection" }, description = "Collection name")
	private String collectionName = "student";

	public static void main(String[] args) {
		new CommandLine(new MainUsingDependencyInjection()).execute(args);
	}

	private static interface MyControllerFactory {
		SchoolController create(StudentView view);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			Module module = new AbstractModule() {
				@Override
				protected void configure() {
					// choose Mongo as database (choose StudentMySqlRepository when switching)
					bind(StudentRepository.class).annotatedWith(SchoolController.RepoType.class)
							.to(StudentMongoRepository.class);

					// inject repository first
					// inject strings of database name and collection name
					bind(String.class).annotatedWith(StudentMongoRepository.DatabaseName.class)
							.toInstance(databaseName);
					bind(String.class).annotatedWith(StudentMongoRepository.CollectionName.class)
							.toInstance(collectionName);

					bind(MongoClient.class).toInstance(new MongoClient(new ServerAddress(mongoHost, mongoPort)));

					bind(StudentRepository.class).to(StudentMongoRepository.class); // Whenever something requires
																					// StudentRepository, provide a
																					// StudentMongoRepository.

					install(new FactoryModuleBuilder().implement(SchoolController.class, SchoolController.class)
							.build(MyControllerFactory.class)); // Whenever a SchoolController is needed, use
																// MyControllerFactory to build it
				}

				/*
				 * with this providers we're saying to juice hot to construct repository /a/ i
				 * injected directly the repository instead in the configure method using
				 * providers can be useful for returning a database connection rather than
				 * another, example: accept a database type from the command line and check if
				 * use mongo or mysql and return a StudentMySqlRepository or
				 * StudentMongoRepository
				 * 
				 * @Provides StudentRepository provideStudentRepository() { if
				 * ("mysql".equalsIgnoreCase(dbTypeFinal)) { // Example: use JDBC Connection
				 * connection = DriverManager.getConnection("jdbc:mysql://localhost/school",
				 * "user", "pwd"); return new StudentMySqlRepository(connection); } else {
				 * MongoClient client = new MongoClient(new ServerAddress(mongoHost,
				 * mongoPort)); return new StudentMongoRepository(client, databaseName,
				 * collectionName); } }
				 */
			};

			try {
				Injector injector = Guice.createInjector(module); // Create an Injector that knows how to build and
																	// inject dependencies.
				MyControllerFactory controllerFactory = injector.getInstance(MyControllerFactory.class); // request a
																											// MyControllerFactory
																											// from
																											// guice
				StudentSwingView view = new StudentSwingView();
				SchoolController controller = (SchoolController) controllerFactory.create(view); // you ask the
																									// MyControllerFactory
																									// to create a
																									// SchoolController
																									// passing in the
																									// StudentSwingView
				view.setSchoolController(controller);
				view.setVisible(true);
				controller.allStudents();
			} catch (Exception e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception", e);
			}
		});
		return null;
	}
}
