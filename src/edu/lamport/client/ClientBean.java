package edu.lamport.client;

public class ClientBean {
	public int id, port;
	public String hostname;
	
	public ClientBean(int id, String hostname, int port) {
		this.id = id;
		this.hostname = hostname;
		this.port = port;
	}
}
