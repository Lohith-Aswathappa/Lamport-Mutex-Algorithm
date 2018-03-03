package edu.lamport.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LamportServer {
	private int id = -1;
	private String host = null, root = null;
	private int port = -1, capacity = 5;
	
	public LamportServer(int id, String hostname, int port, String rootDir, int capacity) {
		this.id = id;
		this.host = hostname;
		this.port = port;
		this.root = rootDir;
		this.capacity = capacity;
	}
	
	public void startServer() {
		if (this.port != -1) {
			Socket socket = null;
			ServerSocket lamportServerSocket = null;
			try {
				lamportServerSocket = new ServerSocket(this.port);
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
			int count = this.capacity;
			List<LamportServerHandler> handlers = new ArrayList<LamportServerHandler>(count); 
			while(count > 0) {
				try {
					System.out.println("Server listening at "+this.host+":"+this.port);
					socket = lamportServerSocket.accept();
				}
				catch(IOException ex) {
					ex.printStackTrace();
				}
				LamportServerHandler handler = new LamportServerHandler(this, socket);
				handler.start();
				handlers.add(handler);
				count -= 1;
			}
			System.out.println("Connection limit reached. The server will accept no more connection.");
			Iterator<LamportServerHandler> handlerIterator = handlers.iterator();
			while(handlerIterator.hasNext()) {
				LamportServerHandler h = handlerIterator.next();
				try {
					h.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				lamportServerSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public String getHostName() {
		return this.host;
	}
	public int getPort() {
		return this.port;
	}
	public String getRootDirectory() {
		return this.root;
	}
}
