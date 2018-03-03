import edu.lamport.ConfigurationUtil;
import edu.lamport.server.LamportServer;
import edu.lamport.server.ServerBean;

public class Server {

	public static void main(String[] args) {
		int serverId = Integer.parseInt(args[0]);
		String serverConfigFilePath = args[1];
		int capacity = 5;
		if (args.length > 2) {
			capacity = Integer.parseInt(args[2]);
		}
		ServerBean sb = ConfigurationUtil.getServerMetaData(serverConfigFilePath, serverId);
		LamportServer server = new LamportServer(serverId, sb.hostname, sb.port, sb.rootDirectory, capacity);
		server.startServer();
	}

}
