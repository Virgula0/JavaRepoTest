package com.example.progetto.angelo.rosa.main;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
// Log4j
import java.util.logging.Logger;

import com.example.progetto.angelo.rosa.view.StudentSwingView;
import com.google.inject.Guice;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

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

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				Guice.createInjector(new SchoolSwingMongoDefaultModule()
						.mongoHost(mongoHost)
						.mongoPort(mongoPort)
						.databaseName(databaseName)
						.collectionName(collectionName))
				.getInstance(StudentSwingView.class)
				.start(); // call the view start method
			} catch (Exception e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception", e);
			}
		});
		return null;
	}

	/*
	 * My attempt
	 * 
	 * @Override public Void call() throws Exception { EventQueue.invokeLater(() ->
	 * { Module module = new AbstractModule() {
	 * 
	 * @Override protected void configure() { // choose Mongo as database (choose
	 * StudentMySqlRepository when switching)
	 * bind(StudentRepository.class).annotatedWith(SchoolController.RepoType.class)
	 * .to(StudentMongoRepository.class);
	 * 
	 * // inject repository first // inject strings of database name and collection
	 * name
	 * bind(String.class).annotatedWith(StudentMongoRepository.DatabaseName.class)
	 * .toInstance(databaseName);
	 * bind(String.class).annotatedWith(StudentMongoRepository.CollectionName.class)
	 * .toInstance(collectionName);
	 * 
	 * bind(MongoClient.class).toInstance(new MongoClient(new
	 * ServerAddress(mongoHost, mongoPort)));
	 * 
	 * bind(StudentRepository.class).to(StudentMongoRepository.class); // Whenever
	 * something requires // StudentRepository, provide a // StudentMongoRepository.
	 * 
	 * install(new FactoryModuleBuilder().implement(SchoolController.class,
	 * SchoolController.class) .build(SchoolControllerFactory.class)); // Whenever a
	 * SchoolController is needed, use // MyControllerFactory to build it } /* with
	 * this providers we're saying to juice hot to construct repository /a/ i
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
	 * collectionName); } } \/ };
	 * 
	 * try { Injector injector = Guice.createInjector(module); // Create an Injector
	 * that knows how to build and // inject dependencies. SchoolControllerFactory
	 * controllerFactory = injector.getInstance(SchoolControllerFactory.class); //
	 * request a // MyControllerFactory // from // guice StudentSwingView view = new
	 * StudentSwingView(); SchoolController controller = (SchoolController)
	 * controllerFactory.create(view); // you ask the // MyControllerFactory // to
	 * create a // SchoolController // passing in the // StudentSwingView
	 * view.setSchoolController(controller); view.setVisible(true);
	 * controller.allStudents(); } catch (Exception e) {
	 * Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception", e); }
	 * }); return null; }
	 */
}
