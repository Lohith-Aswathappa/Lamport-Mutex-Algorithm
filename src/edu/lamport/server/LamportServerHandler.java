package edu.lamport.server;

import java.util.Iterator;
import java.util.List;

import edu.lamport.LamportUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Paths;

public class LamportServerHandler extends Thread {
	LamportServer server;
	private Socket socket;
	
	public LamportServerHandler(LamportServer server, Socket sock) {
		this.server = server;
		this.socket = sock;
	}
	private void closeResources(Socket socket, BufferedReader socketBufferedReader, BufferedWriter socketBufferedWriter, InputStreamReader socketInputStreamReader, OutputStreamWriter socketOutputStreamWriter) throws IOException {
		if (socketBufferedReader != null)
			socketBufferedReader.close();
		if (socketInputStreamReader != null)
			socketInputStreamReader.close();
		if (socketOutputStreamWriter != null)
			socketOutputStreamWriter.close();
		if (socketBufferedWriter != null)
			socketBufferedWriter.close();
		if (socket != null)
			socket.close();
	}
	@Override
	public void run() {
		BufferedReader socketBufferedReader = null;
		InputStreamReader socketInputStreamReader = null;
		BufferedWriter socketBufferedWriter = null;
		OutputStreamWriter socketOutputStreamWriter = null;
		try {
			socketInputStreamReader = new InputStreamReader(this.socket.getInputStream());
			socketBufferedReader = new BufferedReader(socketInputStreamReader);
			socketOutputStreamWriter = new OutputStreamWriter(this.socket.getOutputStream());
			socketBufferedWriter = new BufferedWriter(socketOutputStreamWriter);
		}
		catch(IOException ex) {
			ex.printStackTrace();
			return;
		}
		while(true) {
			try {
				String request = socketBufferedReader.readLine();
				System.out.println("Request received: "+request);
				if ((request == null) || (request.equalsIgnoreCase(LamportUtil.TERMINATE_CMD))) {
					this.closeResources(socket, socketBufferedReader, socketBufferedWriter, socketInputStreamReader, socketOutputStreamWriter);
					return;
				}
				else {
					String[] tokens = request.split(" ");
					if (tokens[0].equalsIgnoreCase(LamportUtil.ENQUIRE_CMD)) {
						List<String> files = ServerUtil.getInstance().getAllFiles(this.server.getRootDirectory());
						Iterator<String> filesIterator = files.iterator();
						StringBuilder allFiles = new StringBuilder();
						while(filesIterator.hasNext()) {
							allFiles.append(filesIterator.next());
							if(filesIterator.hasNext()) {
								allFiles.append(";");
							}
						}
						LamportUtil.write(socketBufferedWriter, allFiles.toString());
					}
					else if (tokens[0].equalsIgnoreCase(LamportUtil.READ_CMD)) {
						String fileName = null;
						try {
							fileName = tokens[1];
							fileName = Paths.get(this.server.getRootDirectory(),fileName).toString();
						}
						catch(ArrayIndexOutOfBoundsException outOfBoundsExInstance) {
							outOfBoundsExInstance.printStackTrace();
						}
						if(fileName != null) {
							LamportUtil.write(socketBufferedWriter, ServerUtil.getInstance().readFile(fileName));
						}
						else {
							LamportUtil.write(socketBufferedWriter, LamportUtil.FAILURE);
						}
					}
					else if (tokens[0].equalsIgnoreCase(LamportUtil.WRITE_CMD)) {
						String fileName = null;
						String content = null;
						try {
							fileName = tokens[1];
							fileName = Paths.get(this.server.getRootDirectory(),fileName).toString();
							content = tokens[2];
						}
						catch(ArrayIndexOutOfBoundsException outOfBoundsEx) {
							outOfBoundsEx.printStackTrace();
						}
						if(ServerUtil.getInstance().appendToFile(fileName, content)) {
							LamportUtil.write(socketBufferedWriter, LamportUtil.SUCCESS);
						}
						else {
							LamportUtil.write(socketBufferedWriter, LamportUtil.FAILURE);
						}
					}
				}
			}
			catch(IOException ex) {
				ex.printStackTrace();
				try {
					this.closeResources(socket, socketBufferedReader, socketBufferedWriter, socketInputStreamReader, socketOutputStreamWriter);
				}
				catch(IOException io_ex) {
					io_ex.printStackTrace();
				}
				return;
			}
		}
	}
}
