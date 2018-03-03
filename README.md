## Lamport's Mutual Exclusion in Distributed Environment

### Project Description
##### Goal:
The goal of the project is to implement the lamport's mutual exclusion algorithm in a distributed environment consisting of 3 servers and 5 clients. This project is a part of academic curriculum.

##### System characteristics:
- The server is a dumb server. It doesn't have multi-threading feature or a way to synchronize requests.
- Because of the server's lack of smartness, the clients must communicate among each other, synchronize between each other and make requests.
- The servers and clients must run in 8 different machines.

### Steps to run the program:
Before you run the program, you have to configure the clients and servers. The configuration can be provided in `res/servers.txt` and `res/clients.txt` files. The number of clients depends on the number of machines configured in `res/clients.txt`. Similarly, the number of servers depends on the number of machines configured in `res/servers.txt`. The format of the two files will be described later.

##### Steps:
- Connect to <clients_count + servers_count> different machines in the same intranet using ssh connections in <clients_count + servers_count> different terminals. In case of running all machines in localhost, there is no need for ssh; The program can be run in terminal locally.
- Send the code files to the machines.
- Change the current working directory of the ssh terminals to the bin folder present in the project zip.
- To start a server, run  `java Server <server_id> <absolute_path_to_servers.txt> <capacity>`. Here, server_id should start from 1 and be assigned to each server incrementally. `capacity` is optional parameter (default to 5).
- To start a client, run `java Client <client_id> <absolute_path_to_clients.txt> <absolute_path_to_servers.txt>`. The `client_id` should also start from 1 and be incremented by 1 for each subsequent client.

**Note:** The servers and clients should all be started within 10 seconds. Otherwise, the program will fail.

##### Configuration files:
The servers.txt file contains a list of servers. The parameters of the server are given comma separated(*No spaces before or after the comma, please!*). The following is the format:
`server_id,hostname,port,path_to_root_folder`
Here, path_to_root_folder is the path to the folder which will emulate the Linux' root directory. This was needed as the different machines on which the project was tested ran as virtual machines over a single physical machine.

The clients.txt file contains a list of clients. The parameters of the client are given comma separated(*No spaces before or after the comma, please!*). The following is the format:
`client_id,hostname,port`

#### Final Note:
The program will never terminate. Presently, no termination condition have been specified. The program can be stopped by stopping all clients using Ctrl+C or Cmd+C and then stopping the servers.
