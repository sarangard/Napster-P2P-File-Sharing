package client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server side code for each client.
 * Duplicate for every peer.
 * 
 * Connects to other peer(s) as and when required to allow download of files.
 * 
 * @author Sarang
 */
public class PeerConnectionForDownload implements Runnable
{
	String fileDownloadPath;
	int portNumber;
	Socket peerSocket;
	DataInputStream inputStream;
	DataOutputStream outputStream;

	/*
	 * Public Constructor
	 */
	public PeerConnectionForDownload(String filePath, int userInputPort)
	{
		this.portNumber = userInputPort;
		this.fileDownloadPath = filePath;
	}

	@SuppressWarnings("resource")
	public void run()
	{
		try
		{		
			ServerSocket downloadSocket = new ServerSocket(portNumber);
			
			//Infinite loop
			while(true) 
			{
				//accept the connection from the socket
				peerSocket = downloadSocket.accept();
				System.out.println("Client connected for File sharing ...");

				outputStream = new DataOutputStream(peerSocket.getOutputStream());
				inputStream = new DataInputStream(peerSocket.getInputStream());

				//Communicate with destination peer  - Receive which file to download.
				String fileName = inputStream.readUTF();
				System.out.println("Requested file is: "+fileName);
				
				//Build the File Input Stream from the file path and filename.
				File filepath = new File(fileDownloadPath + fileName);
				FileInputStream fin = new FileInputStream(filepath);
				BufferedInputStream buffReader = new BufferedInputStream(fin);
				
				//check if the file exists to allow download
				if (!filepath.exists()) 
				{
					System.out.println("File does not exist. Cannot download requested file.");
					buffReader.close();
					return;
				}

				//Get the file size
				int size = (int) filepath.length();
				
				//Allocate a buffer to store contents of file
				byte[] buffContent = new byte[size];

				//Communicate with destination peer  - Send size of file.
				outputStream.writeLong(size);				
				
				// Begin copying data from file into buffer
				int startRead = 0, numOfRead = 0 ;	
				//read into buffContent, from StartRead until end of file
				while (startRead < buffContent.length && (numOfRead = buffReader.read(buffContent, startRead, buffContent.length - startRead)) >= 0) 
				{
					startRead = startRead + numOfRead;
				}
				//Validate all the bytes have been read
				if (startRead < buffContent.length) 
				{
					System.out.println("File Read Incompletely" + filepath.getName());
				}
				
				//Communicate with destination peer  - Send contents of file.
				outputStream.write(buffContent);
				
				buffReader.close();
			}// End of while loop.
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}// End of run()
}
