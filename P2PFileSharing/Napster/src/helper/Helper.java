package helper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * This helper class consists of helper methods.
 * 1. downloadFIleFromServer(fileToDownload, peerToDownloadFrom, inputPort) - downloads a file from given peerID to a folder named inPort.
 * 2. isParsable(inputString) - returns a boolean whether the given inputString can be parsed into an Integer. 
 * 3. displayFilesList(filemap) - displays the list of files and the corresponding list of peers containing that file.
 * 
 * @author Sarang
 */
public class Helper 
{	
	/*
	 * Default Constructor
	 */
	public Helper()
	{
		
	}

	/*
	 * This method is used to download the file requested by the peer (inputPort) from peer (peerToDownloadFrom)
	 */
	public void downloadFileFromPeer(String fileToDownload, String peerToDownloadFrom, int inputPort) throws IOException
	{
		/* Files Location - Change this as required */
		//File location on local machine. Path to download the file to.
		String downloadLocation = "D:/Download-Files/" + inputPort + "/";
		
		//Create directory folder for peer to copy the file to if it doesn't exist.
		File createDirectory = new File(downloadLocation);
		if(!createDirectory.exists())
		{
			//System.out.println("Creating a new folder named: " + inputPort);
			createDirectory.mkdir();
			//System.out.println("The file will be found at: " + downloadLocation);
		}
		
		//Establish connection with source peer
		int peerID = Integer.parseInt(peerToDownloadFrom);
		Socket peerClient = new Socket("localhost", peerID);
		System.out.println("Downloading File to location " + downloadLocation);

		DataOutputStream dOut = new DataOutputStream(peerClient.getOutputStream());
		DataInputStream dIn = new DataInputStream(peerClient.getInputStream());

		//Communication with source peer - Send which file to download.
		dOut.writeUTF(fileToDownload);
		dOut.flush();
		
		//Communication with source peer - Receive size of file.
		long buffSize = dIn.readLong();
		int newBuffSize = (int) buffSize;
		
		//Create a buffer with size of file to read.
		byte[] b = new byte[newBuffSize];

		//Communication with source peer - Receive file data.
		dIn.read(b);

		//Write the contents of buffer b into the destination file
		try 
		{
			String strFilePath = downloadLocation + fileToDownload;
			FileOutputStream writeFileStream = new FileOutputStream(strFilePath);
			writeFileStream.write(b);
			writeFileStream.close();

			System.out.println("Downloaded Successfully");
			peerClient.close();

		} 
		catch (FileNotFoundException ex) 
		{
			System.out.println("FileNotFoundException : " + ex);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		};
	}
	
	/*
	 * Checks if given input is parsable into integer.
	 */
	public boolean isParsable(String input)
	{
	    boolean parsable = true;
	    
	    try
	    {
	        Integer.parseInt(input);
	    }
	    catch(NumberFormatException e)
	    {	    	
	        parsable = false;
	    }
	    
	    return parsable;
	}
	
	/*
	 * Displays the list of files and the corresponding list of peers with that file.
	 */
	public void displayFilesList(HashMap<String, ArrayList<String>> filemap)
	{
		System.out.println("List of all the files on different Peers");
		for(Entry<String, ArrayList<String>> entry : filemap.entrySet())
		{
			System.out.println("File Name is: "+entry.getKey()+" PeerID is: "+entry.getValue());
		}
	}
	
}
