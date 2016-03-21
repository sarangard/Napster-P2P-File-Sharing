package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This Class creates the Server and listens for requests from clients.
 * It creates a Peer for each client request.
 * 
 * args - No arguments
 * 
 * @author Sarang
 */
public class CentralIndexServer
{
	//PortNumber for Server Socket
	public static int portNumber = 8888;

	/*
	 * Main method for CentralIndexServer
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException 
	{
		//Listen for connections at portNumber
		ServerSocket indexServer = new ServerSocket(portNumber);

		//wait for connection from clients/peer
		System.out.println("Server is Up and Running ...");

		while(true)
		{
			Socket socket = indexServer.accept();	//accept client connection

			//Thread Creation in Process
			Thread t = new Thread(new Peer(socket));
			t.start();
		}
	}// End of main
}