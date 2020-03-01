package com.app.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {

	private Server server;
	private List<Connection> connectionPool;
	private List<Connection> usedConnections = new ArrayList<Connection>();
	private static int INITIAL_POOL_SIZE = 10;
	//private static int MAX_POOL_SIZE = 100;
	
	public ConnectionPool(Server server, List<Connection> connPool) {
		this.server = server;
		this.connectionPool = connPool;
	}
	
	public static ConnectionPool create(Server server) throws SQLException {
		List<Connection> pool = new ArrayList<Connection>(INITIAL_POOL_SIZE);
		for(int i = 0; i < INITIAL_POOL_SIZE; i++) {
			pool.add(createConnection(server));
		}
		return new ConnectionPool(server, pool);
	}
	
	public Connection getConnection()  {
		/* improvement - dynamic pool
		  if(connectionPool.isEmpty()) {
			if(usedConnections.size() < MAX_POOL_SIZE) {
				connectionPool.add(createConnection(server));
			} else {
				throw new RuntimeException("Maximum pool size reached, no available connections");
			}
		}*/
		Connection connection = connectionPool.remove(connectionPool.size() - 1);
		usedConnections.add(connection);
		return connection;
	}
	
	public boolean releaseConnection(Connection connection) {
		connectionPool.add(connection);
		return usedConnections.remove(connection);
	}
	
	private static Connection createConnection(Server server) throws SQLException {
		return DriverManager.getConnection(server.getUrl(), server.getUser(), server.getPassword());
	}
	
	public int getSize() {
		return connectionPool.size() + usedConnections.size();
	}
	
	public boolean isFailover() {
		return !server.isMaster();
	}
	
	public void shutdown() throws SQLException {
		usedConnections.forEach(this::releaseConnection);
		for(Connection c : connectionPool) {
			c.close();
		}
		connectionPool.clear();
	}
}
