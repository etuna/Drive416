package Master;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class SyncFollowerThread implements Runnable {

	public static final int DEFAULT_SYNC_SOCKET_PORT = 5000;
	public static final int DEFAULT_SYNC_DATASOCKET_PORT = 5001;
	public static final String DEFAULT_SERVER_ADDRESS = "localhost";
	
	public MasterConnection master_connection;
	public Follower follower;
	public ArrayList<String> currentFiles, localCurrentFiles;
	public  ArrayList<SyncPair<Integer, String>> syncFiles;
	private BufferedReader br;
	private PrintWriter pw;
	private Socket syncSocket;
	private DatagramSocket dataSocket;
	public boolean syncRun = true;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(syncRun) {
			
			try {
				compareFiles(getCurrentFiles(), getLocalCurrentFiles());
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public SyncFollowerThread() {
	
		follower = Main.follower;
		syncFiles = Main.syncFiles;
		master_connection = Main.master_connection;

	}

	public void Start() {
		System.out.println("Sync Thread has been started.");
	}
	
	public ArrayList<String> getLocalCurrentFiles(){
		
		File Folder = new File(follower.DESKTOP_PATH+"/GoogleDrive");
		File[] listOfFiles = Folder.listFiles();
		
		for(int i = 0; i<listOfFiles.length; i++) {
			localCurrentFiles.add(listOfFiles[i].getName());
		}
		
		return localCurrentFiles;
		
	}
	
	
	
public boolean Connect() {
		
		try {
			syncSocket = new Socket(DEFAULT_SERVER_ADDRESS, DEFAULT_SYNC_SOCKET_PORT);
			br = new BufferedReader(new InputStreamReader(syncSocket.getInputStream()));
			pw = new PrintWriter(syncSocket.getOutputStream());
			
			System.out.println("Connection Successful, address:"+DEFAULT_SERVER_ADDRESS+", port :"+DEFAULT_SYNC_SOCKET_PORT);
			return true;
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean Disconnect() {
		
		try {
			br.close();
			pw.close();
			syncSocket.close();
			System.out.println("Connection Closed. Address: "+DEFAULT_SERVER_ADDRESS+", port:"+DEFAULT_SYNC_SOCKET_PORT);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return false;
	}

	public ArrayList<String> getCurrentFiles() {

		try {

			 /*
            Sends the message to the server via PrintWriter
             */
            pw.println("currentfiles");
            pw.flush();
            /*
            Reads a line from the server via Buffer Reader
             */
            String response = br.readLine();
			
            //response in the form of : filename1#filename2#filename3#.......
            //take them into an array : currentFilesArray
            
            String[] currentFilesArray = response.split("#");
            
            //add them to our arraylist : currentFiles
            for(int i= 0; i<currentFilesArray.length;i++) {
            	currentFiles.add(currentFilesArray[i]);
            }
			
            return currentFiles;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<SyncPair<Integer, String>> compareFiles(ArrayList<String> currentFiles,ArrayList<String> localCurrentFiles) {
		syncFiles.clear();

		//sync follower
		ArrayList<String> oldFiles = follower.files;
		
		this.currentFiles = currentFiles;

		for (String f : currentFiles) {
			if (!localCurrentFiles.contains(f)) {
				syncFiles.add(new SyncPair(1, f)); //Download
			}
		}
		for (String f : oldFiles) {
			if (!currentFiles.contains(f)) {
				syncFiles.add(new SyncPair(0, f)); //Remove
			}
		}
		for(String f: localCurrentFiles) {
			if(!currentFiles.contains(f)) {
				syncFiles.add(new SyncPair(2,f)); //Upload
			}
		}
		
		follower.files = localCurrentFiles;
		return syncFiles;
	}
	

}
