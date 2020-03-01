package com.app.logging;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MyMongoSearcher {

	private String hostname;
	private String port;
	private String databaseName;
	private String collectionName;
	
	public MyMongoSearcher(String hostname, String port, String databaseName, String collectionName) {
		this.hostname = hostname;
		this.port = port;
		this.databaseName = databaseName;
		this.collectionName = collectionName;
	}
	
	public String findDocumentsToJson(String text, boolean caseSensitive, boolean diacriticSensitive) {
		StringBuffer jsonString = new StringBuffer();
		for(Document doc : searchDocuments(text, caseSensitive, diacriticSensitive)) {
			jsonString.append(doc.toJson());
			jsonString.append(System.lineSeparator());
		}
		return jsonString.toString();
	}
	
	public List<Document> searchDocuments(String text, boolean caseSensitive, boolean diacriticSensitive) {
		MongoClient mongoCl = MongoClients.create("mongodb://"+hostname+":"+port);
		MongoDatabase mongoDb = mongoCl.getDatabase(databaseName);
		MongoCollection<Document> mongoColl = mongoDb.getCollection(collectionName);
		List<Document> documents = new ArrayList<Document>();
		
		try {
            MongoCursor<Document> cursor = null;
            cursor = mongoColl.find(new Document("$text", new Document("$search", text).
            		append("$caseSensitive", new Boolean(caseSensitive)).
            		append("$diacriticSensitive", new Boolean(diacriticSensitive)))).iterator();
 
            while (cursor.hasNext()) {
                Document article = cursor.next();
                documents.add(article);
            }
            cursor.close();
 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoCl.close();
        }
		return documents;
	}
}
