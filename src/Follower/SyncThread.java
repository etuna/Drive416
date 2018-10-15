package Follower;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class SyncThread implements Runnable{

	public String MasterIP;
	public int MasterPort;
	public MasterConnection master_connection;
	public Follower follower;
	public ArrayList<File> oldFiles, currentFiles;
	public static ArrayList<SyncPair<Integer, File>> syncFiles;
    private BufferedReader is;
    private PrintWriter os;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
	
	}

	
	public SyncThread(String MasterIP, int MasterPort) {
		this.MasterIP = MasterIP;
		this.MasterPort = MasterPort;
		follower = Main.follower;
		master_connection = Main.master_connection;
	}
	
	public void Start() {
		System.out.println("Sync Thread has been started.");
	}
	
	public void getCurrentFiles() {
		
		try {
			Socket socket = new Socket(master_connection.DEFAULT_SERVER_ADDRESS, master_connection.DEFAULT_SERVER_PORT);
			
			  is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	          os = new PrintWriter(socket.getOutputStream());

	          System.out.println("Successfully connected to " + master_connection.DEFAULT_SERVER_ADDRESS + " on port " + master_connection.DEFAULT_SERVER_PORT);
	          
	          
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public ArrayList<SyncPair<Integer, File>> compareFiles(ArrayList<File> currentFiles) {
		syncFiles.clear();
		
		oldFiles = follower.files;
		this.currentFiles = currentFiles;
		
		
		for(File f: currentFiles) {
			if(!oldFiles.contains(f)) {
				syncFiles.add(new SyncPair(1,f));
			}
		}
		for(File f: oldFiles) {
			if(!currentFiles.contains(f)) {
				syncFiles.add(new SyncPair(0,f));
			}
		}
		
		
		return syncFiles;
	}
	
}
