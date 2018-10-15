package Follower;
import java.io.File;
import java.util.ArrayList;

public class UploadThread implements Runnable {
	
	public MasterConnection master_connection;
	public Follower follower;

	
	public UploadThread() {
		master_connection = Main.master_connection;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		
		
	}
	public void Start() {
		System.out.println("Upload Thread has been started.");
	}
	
}
