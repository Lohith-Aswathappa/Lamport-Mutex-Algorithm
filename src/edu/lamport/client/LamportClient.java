package edu.lamport.client;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.lamport.LamportUtil;
import edu.lamport.server.ServerBean;

public class LamportClient {
	private ClientBean myBean;
	private HashMap<String, Queue<Request>> queues;
	public HashMap<Integer, Socket> clientSockets;
	public long clock;
	public int replyCount, clockDelta;
	public boolean inCriticalSection;
	
	private class Request implements Comparable<Request>{
		public long timestamp;
		public int id;
		public Request(int id, long timestamp) {
			this.id = id;
			this.timestamp = timestamp;
		}
		@Override
		public int compareTo(Request x) {
			if (x.timestamp < this.timestamp) {
				return 1;
			}
			else if (x.timestamp == this.timestamp) {
				if (x.id < this.id) {
					return 1;
				}
				else {
					return -1;
				}
			}
			else {
				return -1;
			}
		}
		
		@Override
		public boolean equals(Object req) {
			return (this.id == ((Request)req).id);
		}
	}
	
	public LamportClient(ClientBean cb) {
		this.myBean = cb;
		this.queues = new HashMap<String, Queue<Request>>();
		this.clientSockets = new HashMap<Integer, Socket>();
		this.clock = 0;
		this.replyCount = 1;
		this.clockDelta = 1;
		this.inCriticalSection = false;
	}
	public void startClient(List<ServerBean> servers, List<ClientBean> allClients) {
		LamportClient that = this;
		Thread clientHandlerThread = new Thread () {
			@Override
			public synchronized void run() {
				Socket s;
				ServerSocket ss;
				List<LamportClientHandler> clients = new ArrayList<LamportClientHandler>();
				try {
					ss = new ServerSocket(that.myBean.port);
					int numberOfOtherClients = allClients.size() - 1;
					while(numberOfOtherClients > 0) {
						s = ss.accept();
						LamportClientHandler handlerTmp = new LamportClientHandler(that, LamportUtil.PEER_TO_PEER_CONN, s);
						handlerTmp.start();
						clients.add(handlerTmp);
						numberOfOtherClients--;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				Iterator<LamportClientHandler> it = clients.iterator();
				while(it.hasNext()) {
					LamportClientHandler client = it.next();
					try {
						client.join();
					}
					catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		clientHandlerThread.start();
		try {
			Thread.sleep(10000);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		// connect to other clients 
		Iterator<ClientBean> clientIt = allClients.iterator();
		while(clientIt.hasNext()) {
			ClientBean client = clientIt.next();
			if (client.id != this.myBean.id) {
				try {
					this.clientSockets.put(client.id, new Socket(client.hostname, client.port));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Socket[] sockets = new Socket[servers.size()];
		try {
			Iterator<ServerBean> it = servers.iterator();
			int i = 0;
			while (it.hasNext()) {
				ServerBean sb = it.next();
				sockets[i++] = new Socket(sb.hostname, sb.port);
			}
		}
		catch(IOException ioEx) {
			ioEx.printStackTrace();
		}
		LamportClientHandler serverHandlerThread = new LamportClientHandler(this, LamportUtil.CLIENT_SERVER_CONN, sockets); 
		serverHandlerThread.start();
		
		try {
			clientHandlerThread.join();
			serverHandlerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public int getId() {
		return this.myBean.id;
	}
	
	public int getPort() {
		return this.myBean.port;
	}
	
	public void enqueue(String file, int id, long timestamp) {
		if(!this.queues.containsKey(file)) {
			this.queues.put(file, new PriorityQueue<Request>());
		}
		Request rTmp = new Request(id, timestamp);
		Queue<Request> queue = this.queues.get(file);
		queue.add(rTmp);
	}
	public boolean amIInQueue(String file, int id) {
		boolean isThere = false;
		if(this.queues.containsKey(file)) {
			Queue<Request> q = this.queues.get(file);
			return q.contains(id);
		}
		return isThere;
	}
	public Integer headOfQueue(String file) {
		if(!this.queues.containsKey(file)) {
			return null;
		}
		Queue<Request> queue = this.queues.get(file);
		if (queue == null) {
			return null;
		}
		Request nextRequest = queue.peek();
		if(nextRequest == null) {
			return null;
		}
 		return nextRequest.id;
	}
	
	public void dequeue(String file) {
		if(this.queues.containsKey(file)) {
			Queue<Request> queue = this.queues.get(file);
			if (queue != null) {
				queue.poll();
			}
		}
	}
	public void printQueue() {
		Iterator<String> it = this.queues.keySet().iterator();
		while(it.hasNext()) {
			String file = it.next();
			System.out.println(file);
			Queue<Request> q = this.queues.get(file);
			Iterator<Request> itTmp = q.iterator();
			while(itTmp.hasNext()) {
				Request r = itTmp.next();
				System.out.print(r.id+":"+r.timestamp+" ");
			}
			System.out.println("======");
		}
	}
}
