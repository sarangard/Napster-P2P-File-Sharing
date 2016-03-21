package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import helper.Helper;


/**
 * Client Side code.
 * Allows for communication with the client. 
 * 
 * args - clientPort
 * 
 * Allows the client to do following,
 * 1. To register files with Server.
 * 2. To search for files with Server.
 * 3. To download a file from another peer.
 * 4. Close connection.
 * 
 * @author Sarang
 */
public class Client 
{
	/*
	 * Starts the client interface for each client. 
	 * Expects an argument <port_number>.
	 * Considers port_number as PeerID.
	 * 
	 * Default Location of files for all the clients is "D:/AOSProject/"
	 */
	public static void main(String[] args) 
	{
		
		/* Files Location - Change this as required */
		final String fileDirectory = "D:/Test-Files/";
		
		Helper helper = new Helper();

		// Infinite loop
		while(true)
		{
			try 
			{	//IP Addr of local machine
				//Socket cAsServer = new Socket("localhost",8888);
				//IP Addr of leaflock.cs.ttu.edu
				Socket cAsServer = new Socket("localhost",8888);

				// User Input Port is used as the Client ID.
				int userInputPort = Integer.parseInt(args[0]);

				//New thread for uploading files
				Thread t1 = new Thread(new PeerConnectionForDownload(fileDirectory, userInputPort));
				t1.start();

				int clientOption = 0;

				do
				{
					System.out.println("");
					System.out.println("**** MENU ****");
					System.out.println("1. Register Files");
					System.out.println("2. Search for a File");
					System.out.println("3. Download a File");
					System.out.println("4. Exit");

					//DataInputStream consoleInput = new DataInputStream(System.in);
					//String clientOptionString = consoleInput.readLine();
					
					Scanner consoleInput = new Scanner(System.in);
					String clientOptionString = consoleInput.nextLine();
					
					// Parses the client option if it isParsable().
					boolean isParse = helper.isParsable(clientOptionString);
					if(isParse)
					{
						clientOption = Integer.parseInt(clientOptionString);
					}
					else
					{
						System.out.println("Entered option is invalid.");
						clientOption = 0;
					}

					DataInputStream fromServerStream = new DataInputStream(cAsServer.getInputStream());
					DataOutputStream toServerStream = new DataOutputStream(cAsServer.getOutputStream());
					
					//
					switch(clientOption)
					{
						//Register files with the Central Index Server
						case 1:	
							// Send the choice to Peer
							toServerStream.writeUTF(String.valueOf(clientOption));
							toServerStream.flush();	//forces data to the underlying output stream
	
							consoleInput.nextLine();
							System.out.println("Enter the filename to register [with extension]: ");
							String fileToRegister = consoleInput.nextLine();
	
							//Handling erraneous user input - cannot accept blank as filename.
							if(fileToRegister.isEmpty() || fileToRegister==null)
							{
								break;
							}
							else
							{
								toServerStream.writeUTF(fileToRegister);
								toServerStream.writeUTF(Integer.toString(userInputPort));
								break;
							}

						//Search for file 
						case 2:	
							// Send the choice to Peer
							toServerStream.writeUTF(String.valueOf(clientOption));
							toServerStream.flush();	//forces data to the underlying output stream

							consoleInput.nextLine();
							System.out.println("Enter the filename to look up: ");
							String searchFileName = consoleInput.nextLine();
							
							//Handling erraneous user input - cannot accept blank as filename.
							if(searchFileName.isEmpty() || searchFileName==null)
							{
								break;
							}
							else
							{
								//Send the filename to be looked up to the peer.
								toServerStream.writeUTF(searchFileName);
		
								//Receive the search results for the entered filename from the peer.
								String searchResults = fromServerStream.readUTF();
								System.out.println(searchResults);
							}
							
							break;

						//Download the file
						case 3:								
							consoleInput.nextLine();
							System.out.println("Enter the filename to download: ");
							String fileToDownload = consoleInput.nextLine();

							//Handling erraneous user input - cannot accept blank as filename.
							if(fileToDownload.isEmpty() || fileToDownload==null)
							{
								break;
							}
							else
							{
								consoleInput.nextLine();
								System.out.println("Enter the PeerID: ");
								String peerToDownloadFrom = consoleInput.nextLine();
								
								//Handling erraneous user input - cannot accept blank as peerID.
								if(peerToDownloadFrom.isEmpty() || peerToDownloadFrom==null)
								{
									break;
								}
								else
								{
									long startTime = System.currentTimeMillis();
									helper.downloadFileFromPeer(fileToDownload, peerToDownloadFrom, userInputPort);
									long endTime = System.currentTimeMillis();
									System.out.println("Time required for download: " + (endTime-startTime) + "msec");
									break;
								}		
							}

						// EXIT
						case 4:
							// Send the choice to Peer
							System.out.println("Closing Client...!!!");
							toServerStream.writeUTF(String.valueOf(clientOption));

							// Closing socket connection
							cAsServer.close();
							//Closing consoleInput
							consoleInput.close();
							//Normal shutdown of process
							System.exit(0);
							
							break;

						default:
							break;

					}
				}while(clientOption != 4);
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}	

}
