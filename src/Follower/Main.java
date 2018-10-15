package Follower;

import java.util.ArrayList;

public class Main {

	//Default Values
	public static final String DEFAULT_MASTER_IP = "localhost";
	public static final int DEFAULT_MASTER_PORT = 9999;
	
	//Variables
	public static Follower follower;
	public static String MasterIP;
	public static int MasterPort;
	static SyncThread syncThread;
	static UploadThread uploadThread;
	public static MasterConnection master_connection;
	public static ArrayList<SyncPair<Integer, String>> syncFiles;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
				
		Init();
		
		//Sync Thread
		syncThread = new SyncThread(MasterIP, MasterPort);
		syncThread.Start();
		syncThread.run();
		
		//Upload Thread
		uploadThread = new UploadThread();
		uploadThread.Start();
		uploadThread.run();
				
	}
	
	
	public static boolean Init() {
		
		follower = Follower.getInstance();
		syncFiles = new ArrayList<SyncPair<Integer, String>>();
		setupMaster(DEFAULT_MASTER_IP, DEFAULT_MASTER_PORT);
		return true;
	}
	
	public static void setupMaster(String address, int port) {
		MasterIP = address;
		MasterPort = port;
		master_connection = MasterConnection.getInstance();
	}
	
	
	
	

}
