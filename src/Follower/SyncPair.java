package Follower;

public class SyncPair<Integer, File> {
	
	
	
	public int operation;//0 for remove, 1 for download
	public File file;

	public SyncPair(int operation, File file) {
	this.operation = operation;
	this.file = file;
	}

}
