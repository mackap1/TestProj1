package com.app.logging;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MyMongoDBAppender extends AppenderSkeleton {

	private String hostname;
    private String port;
    private String databaseName;
    private String collectionName;
	
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	@Override
	public void close() {
		
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(LoggingEvent event) {
		MongoClient mongoCl = MongoClients.create("mongodb://"+hostname+":"+port);
		MongoDatabase mongoDb = mongoCl.getDatabase(databaseName);
		MongoCollection<Document> mongoColl = mongoDb.getCollection(collectionName);
		mongoColl.insertOne(getDocumentFromEvent(event));
	}

	private Document getDocumentFromEvent(LoggingEvent event) {
		Document eventDoc = new Document("_id", new ObjectId());
		eventDoc.append("date", getFormattedDate(event));
		eventDoc.append("time", getFormattedTime(event));
		eventDoc.append("level", event.getLevel().toString());
		eventDoc.append("class", event.getLocationInformation().getClassName());
		eventDoc.append("method", event.getLocationInformation().getMethodName());
		eventDoc.append("line", event.getLocationInformation().getLineNumber());
		eventDoc.append("message", event.getMessage().toString());
		return eventDoc;
	}
	
	private String getFormattedDate(LoggingEvent event) {
		Date date = new Date(event.getTimeStamp());
	    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");  
	    String strDate = formatter.format(date);  
	    return strDate;
	}
	
	private String getFormattedTime(LoggingEvent event) {
		Date date = new Date(event.getTimeStamp());
	    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");  
	    String strTime = formatter.format(date);  
	    return strTime;
	}
}
