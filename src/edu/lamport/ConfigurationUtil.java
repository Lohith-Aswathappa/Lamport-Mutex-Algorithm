package edu.lamport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.lamport.client.ClientBean;
import edu.lamport.server.ServerBean;

public class ConfigurationUtil {
	
	public static List<ServerBean> getAllServers(String serverConfigFile) {
		List<ServerBean> servers = new ArrayList<ServerBean>();
		File f = new File(serverConfigFile);
		if (f.isFile()) {
			BufferedReader reader = null;
			FileReader freader = null;
			try {
				freader = new FileReader(f);
				reader = new BufferedReader(freader);
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] tokens = line.split(",");
					if (tokens.length == 4) { 
						servers.add(new ServerBean(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2]), tokens[3]));
					}
				}
			}
			catch(IOException ioEx) {
				ioEx.printStackTrace();
			}
			finally {
				try {
					if (freader != null)
						freader.close();
					if(reader != null)
						reader.close();
				}
				catch(IOException ioEx) {
					ioEx.printStackTrace();
				}
			}
		}
		return servers;
	}
	
	public static ServerBean getServerMetaData(String serverConfigFile, int serverId) {
		ServerBean result = null;
		File f = new File(serverConfigFile);
		if (f.isFile()) {
			BufferedReader reader = null;
			FileReader freader = null;
			try {
				freader = new FileReader(f);
				reader = new BufferedReader(freader);
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] tokens = line.split(",");
					if (tokens.length == 4 && Integer.parseInt(tokens[0]) == serverId) { 
						result = new ServerBean(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2]), tokens[3]);
						break;
					}
				}
			}
			catch(IOException ioEx) {
				ioEx.printStackTrace();
			}
			finally {
				try {
					if (freader != null)
						freader.close();
					if(reader != null)
						reader.close();
				}
				catch(IOException ioEx) {
					ioEx.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public static ClientBean getClientMetaData(String clientConfigFile, int clientId) {
		ClientBean result = null;
		File f = new File(clientConfigFile);
		if (f.isFile()) {
			BufferedReader reader = null;
			FileReader freader = null;
			try {
				freader = new FileReader(f);
				reader = new BufferedReader(freader);
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] tokens = line.split(",");
					if (tokens.length == 3 && Integer.parseInt(tokens[0]) == clientId) { 
						result = new ClientBean(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2]));
						break;
					}
				}
			}
			catch(IOException ioEx) {
				ioEx.printStackTrace();
			}
			finally {
				try {
					if (freader != null)
						freader.close();
					if(reader != null)
						reader.close();
				}
				catch(IOException ioEx) {
					ioEx.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public static List<ClientBean> getAllClients(String clientConfigFile) {
		List<ClientBean> result = new ArrayList<ClientBean>();
		File f = new File(clientConfigFile);
		if (f.isFile()) {
			BufferedReader reader = null;
			FileReader freader = null;
			try {
				freader = new FileReader(f);
				reader = new BufferedReader(freader);
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] tokens = line.split(",");
					if (tokens.length == 3) { 
						result.add(new ClientBean(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2])));
					}
				}
			}
			catch(IOException ioEx) {
				ioEx.printStackTrace();
			}
			finally {
				try {
					if (freader != null)
						freader.close();
					if(reader != null)
						reader.close();
				}
				catch(IOException ioEx) {
					ioEx.printStackTrace();
				}
			}
		}
		return result;
	}
}
