package edu.lamport.server;

public class ServerBean {
	public int id, port;
	public String hostname, rootDirectory;
	
	public ServerBean() {}
	public ServerBean(int id, String hostname, int port, String rootDir ) {
		this.id = id;
		this.hostname = hostname;
		this.port = port;
		this.rootDirectory = rootDir;
	} 
}
