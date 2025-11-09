package com.example.progetto.angelo.rosa.main;

import com.example.progetto.angelo.rosa.controller.SchoolController;
import com.example.progetto.angelo.rosa.controller.SchoolControllerFactory;
import com.example.progetto.angelo.rosa.repository.StudentMongoRepository;
import com.example.progetto.angelo.rosa.repository.StudentMongoRepository.MongoHost;
import com.example.progetto.angelo.rosa.repository.StudentMongoRepository.MongoPort;
import com.example.progetto.angelo.rosa.repository.StudentRepository;
import com.example.progetto.angelo.rosa.view.StudentSwingView;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.mongodb.MongoClient;

public class SchoolSwingMongoDefaultModule extends AbstractModule {
	/*
	 * Builder pattern
	 */
	private String mongoHost;
	private int mongoPort;
	private String databaseName;
	private String collectionName;

	public SchoolSwingMongoDefaultModule defaultParams() {
		this.mongoHost = "localhost";
		this.mongoPort = StudentMongoRepository.PORT;
		this.databaseName = StudentMongoRepository.SCHOOL_DB_NAME;
		this.collectionName = StudentMongoRepository.STUDENT_COLLECTION_NAME;
		return this;
	}

	public SchoolSwingMongoDefaultModule mongoHost(String mongoHost) {
		this.mongoHost = mongoHost;
		return this;
	}

	public SchoolSwingMongoDefaultModule mongoPort(int mongoPort) {
		this.mongoPort = mongoPort;
		return this;
	}

	public SchoolSwingMongoDefaultModule databaseName(String databaseName) {
		this.databaseName = databaseName;
		return this;
	}

	public SchoolSwingMongoDefaultModule collectionName(String collectionName) {
		this.collectionName = collectionName;
		return this;
	}

	@Override
	protected void configure() {
		bind(StudentRepository.class).annotatedWith(SchoolController.RepoType.class).to(StudentMongoRepository.class);

		// inject repository first
		bind(String.class).annotatedWith(MongoHost.class).toInstance(mongoHost);
		bind(Integer.class).annotatedWith(MongoPort.class).toInstance(mongoPort);
		bind(String.class).annotatedWith(StudentMongoRepository.DatabaseName.class).toInstance(databaseName);
		bind(String.class).annotatedWith(StudentMongoRepository.CollectionName.class).toInstance(collectionName);

		// not needed as we use a provider for this
		// bind(MongoClient.class).toInstance(new MongoClient(new
		// ServerAddress(mongoHost, mongoPort)));

		bind(StudentRepository.class).to(StudentMongoRepository.class); // Whenever something requires
		// StudentRepository, provide a
		// StudentMongoRepository.

		install(new FactoryModuleBuilder().implement(SchoolController.class, SchoolController.class)
				.build(SchoolControllerFactory.class)); // Whenever a SchoolController is needed, use
		// MyControllerFactory to build it
	}

	@Provides
	public MongoClient mongoClient(@MongoHost String host, @MongoPort int port) {
		return new MongoClient(host, port);
	}

	@Provides
	StudentSwingView studentView(SchoolControllerFactory schoolControllerFactory) {
		StudentSwingView view = new StudentSwingView();
		view.setSchoolController(schoolControllerFactory.create(view));
		return view;
	}
}
