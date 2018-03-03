package edu.lamport.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.Random;

import edu.lamport.LamportUtil;

public class LamportClientHandler extends Thread {
	private Socket[] serverSockets;
	private Socket clientSocket;
	private LamportClient client;
	private int connectionType;
	
	public LamportClientHandler(LamportClient client, int type, Socket socket) {
		this.client = client;
		this.connectionType = type;
		this.clientSocket = socket;
	}
	
	public LamportClientHandler(LamportClient client, int type, Socket[] sockets) {
		this.client = client;
		this.connectionType = type;
		this.serverSockets = sockets;
	}
	
	private void clientServerProcess() {
		BufferedReader[] socketBufferedReader = new BufferedReader[this.serverSockets.length];
		InputStreamReader[] socketInputStreamReader = new InputStreamReader[this.serverSockets.length];
		BufferedWriter[] socketBufferedWriter = new BufferedWriter[this.serverSockets.length];
		OutputStreamWriter[] socketOutputStreamWriter = new OutputStreamWriter[this.serverSockets.length];
		try {
			for (int i = 0 ; i < this.serverSockets.length ;i++) {
				socketInputStreamReader[i] = new InputStreamReader(this.serverSockets[i].getInputStream());
				socketBufferedReader[i] = new BufferedReader(socketInputStreamReader[i]);
				socketOutputStreamWriter[i] = new OutputStreamWriter(this.serverSockets[i].getOutputStream());
				socketBufferedWriter[i] = new BufferedWriter(socketOutputStreamWriter[i]);
			}
		}
		catch(IOException ex) {
			ex.printStackTrace();
			return;
		}
		Random r = new Random();
		String[] files;
		try {
			LamportUtil.write(socketBufferedWriter[0], LamportUtil.ENQUIRE_CMD);
			String file = LamportUtil.read(socketBufferedReader[0]);
			System.out.println(file);
			files = file.split(";");
		}
		catch(IOException ioEx) {
			ioEx.printStackTrace();
			return;
		}
		
		try {
			Thread.sleep(r.nextInt(5)*1000);
			while(true) {
				int server_idx = r.nextInt(this.serverSockets.length);
				int cmd_index = r.nextInt(LamportUtil.COMMANDS.length);
				int file_index = r.nextInt(files.length);
				while(this.client.replyCount != 1 || this.client.amIInQueue(files[file_index], this.client.getId())) {
					Thread.sleep(500);
				}
				{
					this.client.clock += this.client.clockDelta;
					this.broadcastToClients(LamportUtil.REQUEST_CMD, files[file_index]);
					this.client.enqueue(files[file_index], this.client.getId(), this.client.clock);
					while(this.client.replyCount <= this.client.clientSockets.keySet().size() && !this.checkIfIamNext(files[file_index])) {
						System.out.println("Waiting for "+files[file_index]+" to be released...");
						Thread.sleep(2000);
					}
				}
				if (LamportUtil.COMMANDS[cmd_index].equals(LamportUtil.READ_CMD)) {
					System.out.println("Read file: "+files[file_index]);
					this.client.clock += this.client.clockDelta;
					LamportUtil.write(socketBufferedWriter[server_idx], LamportUtil.READ_CMD+" "+files[file_index]+" "+this.client.getId());
					String message = LamportUtil.read(socketBufferedReader[server_idx]);
					System.out.println(message);							
				}
				else if (LamportUtil.COMMANDS[cmd_index].equals(LamportUtil.WRITE_CMD)) {
					this.client.clock += this.client.clockDelta;
					System.out.println("Write file: "+files[file_index]+", Content: "+this.client.getId()+":"+this.client.clock);
					for (BufferedWriter bf: socketBufferedWriter) {
						LamportUtil.write(bf, LamportUtil.WRITE_CMD+" "+files[file_index]+" "+this.client.getId()+":"+this.client.clock);
					}
					for (BufferedReader br: socketBufferedReader) {
						LamportUtil.read(br);
					}
				}
				Thread.sleep(5000);
				this.client.clock += this.client.clockDelta;
				this.broadcastToClients(LamportUtil.RELEASE_CMD, files[file_index]);
				this.client.replyCount = 1;
				Integer processIdAtHead = this.client.headOfQueue(files[file_index]); 
				if(processIdAtHead != null && processIdAtHead == this.client.getId()) {
					this.client.dequeue(files[file_index]);
				}
				System.out.println("Released "+files[file_index]);
//				this.client.printQueue();
			}
		}
		catch(IOException ioEx) {
			ioEx.printStackTrace();
			return;
		}
		catch(InterruptedException interruptedEx) {
			interruptedEx.printStackTrace();
		}
	}
	private void broadcastToClients(String command, String file) throws IOException {
		Iterator<Integer> clients = this.client.clientSockets.keySet().iterator();
		while(clients.hasNext()) {
			Integer cTemp = clients.next();
			OutputStreamWriter osr = new OutputStreamWriter(this.client.clientSockets.get(cTemp).getOutputStream());
			BufferedWriter br = new BufferedWriter(osr);
			LamportUtil.write(br, command+" "+file+" "+this.client.getId()+" "+this.client.clock);
		}
	}
	private boolean checkIfIamNext(String file) {
		if (this.client.headOfQueue(file) == null || this.client.headOfQueue(file) == this.client.getId()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private void clientClientProcess() {
		InputStreamReader clientSockISR = null;
		BufferedReader clientSockBR = null;
		OutputStreamWriter clientSockOSW = null;
		BufferedWriter	clientSockBW = null;
		try {
			clientSockISR = new InputStreamReader(this.clientSocket.getInputStream());
			clientSockBR  = new BufferedReader(clientSockISR);
			clientSockOSW = new OutputStreamWriter(this.clientSocket.getOutputStream());
			clientSockBW = new BufferedWriter(clientSockOSW);
			
			while(true) {
				String line = clientSockBR.readLine();
				if (line != null) {
					String[] tokens = line.split(" ");
					
					if (LamportUtil.REQUEST_CMD.equalsIgnoreCase(tokens[0])) {
						String file = tokens[1];
						String process = tokens[2];
						String timestamp = tokens[3];
						this.client.clock = Math.max(Long.parseLong(timestamp), this.client.clock) + this.client.clockDelta;
						this.client.enqueue(file, Integer.parseInt(process), Long.parseLong(timestamp));
						this.client.clock += this.client.clockDelta;
						LamportUtil.write(clientSockBW, LamportUtil.REPLY_CMD+" "+this.client.clock);
					}
					else if (LamportUtil.RELEASE_CMD.equalsIgnoreCase(tokens[0])) {
//						System.out.println("Received RELEASE "+line);
						String file = tokens[1];
						String process = tokens[2];
						String timestamp = tokens[3];
						this.client.clock = Math.max(Long.parseLong(timestamp), this.client.clock) + this.client.clockDelta;
						Integer processIdAtHead = this.client.headOfQueue(file); 
						if(processIdAtHead != null && processIdAtHead == Integer.parseInt(process) ) {
							this.client.dequeue(file);
						}
//						this.client.printQueue();
					}
					else if (LamportUtil.REPLY_CMD.equalsIgnoreCase(tokens[0])) {
						String timestamp = tokens[1];
						this.client.clock = Math.max(Long.parseLong(timestamp), this.client.clock) + this.client.clockDelta;
						this.client.replyCount += 1;
					}
				}
			}
		} catch (IOException e) {
			try {
				if(clientSockISR != null) {
					clientSockISR.close();
				}
				if (clientSockBR != null) {
					clientSockBR.close();
				}
				e.printStackTrace();
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void run() {
		if (this.connectionType == LamportUtil.CLIENT_SERVER_CONN) {
			this.clientServerProcess();
		}
		else if (this.connectionType == LamportUtil.PEER_TO_PEER_CONN) {
			this.clientClientProcess();
		}
	}
	
}
