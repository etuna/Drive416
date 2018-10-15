package Follower;
import java.io.File;
import java.util.ArrayList;

public class SyncThread implements Runnable{

	
	public String MasterIP;
	public int MasterPort;
	public MasterConnection master_connection;
	public Follower follower;
	public ArrayList<File> oldFiles, currentFiles;
	public ArrayList<SyncPair<Integer, File>> syncFiles;
	
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
