package com.example.progetto.angelo.rosa.main;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Simple app accessing MongoDB.
 */
public class Main {
	public static void main(String[] args) {
		String mongoHost = "mongodb"; // default hostname from Docker setup
		if (args.length > 0)
			mongoHost = args[0];
		// default port for MongoDB is 27017
		MongoClient mongoClient = new MongoClient(mongoHost);
		MongoDatabase db = mongoClient.getDatabase("mydb");
		MongoCollection<Document> collection = db.getCollection("examples");
		Document doc = new Document("name", "Greeting").append("type", "Hello World!");
		collection.insertOne(doc);
		// should print "Hello World!"
		System.out.println(collection.find().first().get("type"));
		mongoClient.close();
	}
}
