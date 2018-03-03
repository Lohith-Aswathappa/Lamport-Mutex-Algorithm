import java.util.List;

import edu.lamport.ConfigurationUtil;
import edu.lamport.client.ClientBean;
import edu.lamport.client.LamportClient;
import edu.lamport.server.ServerBean;

public class Client {

	public static void main(String[] args) {
		int clientId = Integer.parseInt(args[0]);
		String clientConfigFilePath = args[1];
		String serverConfigFilePath = args[2];
		ClientBean cb = ConfigurationUtil.getClientMetaData(clientConfigFilePath, clientId);
		LamportClient client = new LamportClient(cb);
		List<ClientBean> cbs = ConfigurationUtil.getAllClients(clientConfigFilePath);
		List<ServerBean> sbs = ConfigurationUtil.getAllServers(serverConfigFilePath);
		client.startClient(sbs, cbs);
	}

}
