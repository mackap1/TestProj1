package com.app.connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConnectionTests {

	private static final String QUERY_IS_CONNECTED = "SELECT * FROM person WHERE person_name = 'Ivan'";

	public static void main(String[] args) throws InterruptedException {
		ConnectionTests connTest = new ConnectionTests();
		ConnectionManager connMana = new ConnectionManager();
		//Thread.sleep(30000);
		if (connTest.isConnected(connMana.getConnection()))
			System.out.println("Connection is opened");
		else
			System.out.println("Connection is closed");
	}

	public boolean isConnected(Connection connection) {
        try {
			if(connection == null) return false;
			if (connection.isClosed()) {
				return false;
			}

			try (Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(QUERY_IS_CONNECTED)) {
				if (resultSet == null) {
					return false;
				}
			} catch (Exception e) {
				System.out.println("isConnected: Query error " + e);
				return false;
			}
		} catch (Exception e) {
			System.out.println("isConnected: Check connection error " + e);
			return false;
		}
		return true;
	}
}
