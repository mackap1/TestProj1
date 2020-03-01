package com.app.connection;

public class Server {
	
	private String url;
	private String user;
	private String password;
	private boolean isMaster;
	public Server(String url, String user, String password, boolean isMaster) {
		super();
		this.url = url;
		this.user = user;
		this.password = password;
		this.isMaster = isMaster;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isMaster() {
		return isMaster;
	}
	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}
}
