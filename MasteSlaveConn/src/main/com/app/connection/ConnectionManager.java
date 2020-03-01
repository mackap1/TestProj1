package com.app.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class ConnectionManager {
	
	private Server master;
	private Server slave;
    private ConnectionPool connPool;
    private static int MAX_NUMBER_ATTEMPTS = 10;
    private int number_attempts = 0;
    private static final Logger logger = LogManager.getLogger(ConnectionManager.class);
    
    public ConnectionManager() {
    	init();
    }
    
    private void init() {
    	DOMConfigurator.configure("log4j.xml");
    	setServers();
    	setConnectionPool(master);
    }
	
	private void setServers() {
		Properties prop = new Properties();
		try {
		    prop.load(new FileInputStream("db_config.cfg"));
			master = new Server(prop.getProperty("master_url"), prop.getProperty("master_username"), 
					prop.getProperty("master_password"), true);
            slave = new Server(prop.getProperty("slave_url"), prop.getProperty("slave_username"), 
            		prop.getProperty("slave_password"), false);
            logger.debug("Setting master and slave servers");

		} catch(IOException e) {
			logger.error("Set servers problem: " + e.getMessage());
		}
		
	}

	private void setConnectionPool(Server server) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			logger.debug("I'm trying to connect to - " + ((server.isMaster()) ? "master" : "slave"));
			connPool = ConnectionPool.create(server);
			logger.debug("Created connection pool - " + ((server.isMaster()) ? "master" : "slave"));
		} catch(SQLException e) {
			number_attempts++;
			if(MAX_NUMBER_ATTEMPTS >= number_attempts) {
				if(server.isMaster()) {
					logger.error("Set master connection pool problem, I try connect to slave: " + e);
					setConnectionPool(slave);
				} else {
					logger.error("Set slave connection pool problem, I try connect to master: " + e);
					setConnectionPool(master);
				}
			} else {
				logger.error("All attempts to connect to the database are exhausted");
				connPool = null;
			}
		} catch(Exception e) {
			logger.error("Set connection pool another problem: " + e);
		}
	}
	
	public Connection getConnection() {
		number_attempts = 0;
		if(connPool != null) {
			//if failover - attempt create connect to master
			if(connPool.isFailover()) {
				logger.warn("Failover mode - I'll try to connect to master server!");
				setConnectionPool(master);
				if(connPool == null) {
					logger.error("All attempts to connect to the database are exhausted");
					return null;
				}
			}
		} else {
			setConnectionPool(master);
		}
		return (connPool != null) ? connPool.getConnection() : null;
	}
}
