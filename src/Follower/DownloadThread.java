package Follower;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class DownloadThread implements Runnable {

	public MasterConnection master_connection;
	public static final String DEFAULT_SERVER_ADDRESS = "localhost";
	public int DEFAULT_DOWNLOAD_SOCKET_PORT = 7001;
	public int DEFAULT_DOWNLOAD_DATASOCKET_PORT = 7002;
	public Socket downloadSocket;
	public static DatagramSocket dataSocket;
	private BufferedReader br;
	private PrintWriter pw;
	public boolean downloadRun= true;

	public DownloadThread() {
		master_connection = Main.master_connection;
	}

	public void Start() {
		System.out.println("Download Thread has started...");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(downloadRun) {
			
			try {
				
				downloadFiles(Main.syncFiles);
				Thread.sleep(1000);
				
			} catch (InterruptedException | NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public boolean Connect() {

		try {
			downloadSocket = new Socket(master_connection.DEFAULT_SERVER_ADDRESS, DEFAULT_DOWNLOAD_SOCKET_PORT);
			br = new BufferedReader(new InputStreamReader(downloadSocket.getInputStream()));
			pw = new PrintWriter(downloadSocket.getOutputStream());

			System.out.println("Connection Successful, address:" + master_connection.DEFAULT_SERVER_ADDRESS + ", port :"
					+ DEFAULT_DOWNLOAD_SOCKET_PORT);
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
			downloadRun=false;
			br.close();
			pw.close();
			downloadSocket.close();
			System.out.println("Connection Closed. Address: " + master_connection.DEFAULT_SERVER_ADDRESS + ", port:"
					+ DEFAULT_DOWNLOAD_SOCKET_PORT);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean downloadFiles(ArrayList<SyncPair<Integer, String>> SyncFiles)
			throws NumberFormatException, IOException {

		ArrayList<String> downloadList = new ArrayList<String>();

		for (SyncPair p : SyncFiles) {
			if (p.operation == 1) {
				downloadList.add((String) p.file);
			}
		}

		String message;
		for (String file : downloadList) {

			// Tell the file to the master
			message = file;
			pw.println(message);
			pw.flush();

			// Read the size
			System.out.println("What is the size of file "+file+"?");
			
			// Get the size and Download the file
			downloadFile(file, Integer.parseInt(br.readLine()));
		}

		return true;
	}

	public boolean downloadFile(String filename, int fileSize) throws IOException {

		try {
			System.out.println("File downloading... File name:" + filename);
			dataSocket = new DatagramSocket(DEFAULT_DOWNLOAD_DATASOCKET_PORT);

			byte[] data = new byte[fileSize];
			String path = System.getProperty("user.home") + "/Desktop/GoogleDrive/" + filename;

			DatagramPacket datagramPacket = new DatagramPacket(data, fileSize);
			dataSocket.receive(datagramPacket);

			File newFile = new File(path);
			FileOutputStream fileOutputStream = new FileOutputStream(newFile);
			fileOutputStream.write(data);
			fileOutputStream.flush();
			fileOutputStream.close();
			System.out.println("File " + filename + " successfully recieved.");

			return true;

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

}
