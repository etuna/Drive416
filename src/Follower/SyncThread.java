package Follower;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class SyncThread implements Runnable {

	public String MasterIP;
	public int MasterPort;
	public MasterConnection master_connection;
	public Follower follower;
	public ArrayList<String> oldFiles, currentFiles, localCurrentFiles;
	public static ArrayList<SyncPair<Integer, File>> syncFiles;
	private BufferedReader br;
	private PrintWriter pw;
	private Socket socket;
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	public SyncThread(String MasterIP, int MasterPort) {
		this.MasterIP = MasterIP;
		this.MasterPort = MasterPort;
		follower = Main.follower;
		master_connection = Main.master_connection;
		socket= master_connection.socket;
		br = master_connection.br;
		pw = master_connection.pw;
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

	public ArrayList<SyncPair<Integer, File>> compareFiles(ArrayList<String> currentFiles) {
		syncFiles.clear();

		oldFiles = follower.files;
		this.currentFiles = currentFiles;

		for (String f : currentFiles) {
			if (!oldFiles.contains(f)) {
				syncFiles.add(new SyncPair(1, f));
			}
		}
		for (String f : oldFiles) {
			if (!currentFiles.contains(f)) {
				syncFiles.add(new SyncPair(0, f));
			}
		}

		return syncFiles;
	}

}
