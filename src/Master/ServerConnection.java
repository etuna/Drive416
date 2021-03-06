package Master;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ServerConnection {

	//Default Values
	public static final int DEFAULT_SERVER_PORT = 10001;
	public static final int DEFAULT_DATAGRAM_PORT = 10002;
	
	//Variables
	private String serverAddress;
	private int serverPort;
	public static ServerSocket serverSocket;
	public static Socket socket;
	public static DatagramSocket dataSocket;
	public static BufferedReader br; //Input Stream
	public static PrintWriter pw; //Output Stream
	public static ServerConnection server_connection;
	
	public static ServerConnection getInstance() {
		if(server_connection == null) {
			server_connection = new ServerConnection(DEFAULT_SERVER_PORT);
			return server_connection;
		} else {
			return server_connection;
		}
	}
	
	
	public ServerConnection(int port) {
		serverPort = port;
	}
	
	
	public boolean Connect() {
		
		try {
			serverSocket = new ServerSocket(serverPort);
			System.out.println("Opened a server socket on "+serverPort);
			
			Socket socket = serverSocket.accept();
			serverAddress = socket.getRemoteSocketAddress().toString();
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(socket.getOutputStream());
			
			System.out.println("Server Connection Successful, address:"+serverAddress+", port :"+serverPort);
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
			socket.close();
			System.out.println("Server Connection Closed. Address: "+serverAddress+", port:"+serverPort);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return false;
	}
	
	
	public boolean uploadFile(File f) throws IOException {
		
		
		try {
			System.out.println("File uploading... File name:"+f.getName());
			dataSocket = new DatagramSocket();
			FileInputStream fileInputStream = new FileInputStream(f);			
			DataInputStream dataInputStream = new DataInputStream(fileInputStream);
			
			int fileSize = (int) f.length();
			
			byte[] data = new byte[fileSize];
			byte[] incomingData = new byte[1024];
			
			int read = 0;
			int numRead = 0;
			
			while(read < data.length && (numRead = dataInputStream.read(data, read, data.length - read)) >= 0) {
				read += numRead;
			}
			
			DatagramPacket datagramPacket = new DatagramPacket(data, data.length, Inet4Address.getLocalHost(), DEFAULT_DATAGRAM_PORT);
			dataSocket.send(datagramPacket);
			
			System.out.println("File "+f.getName()+" has been sent to the server.");
			
			DatagramPacket incomingPacket =  new DatagramPacket(incomingData, incomingData.length);
			dataSocket.receive(incomingPacket);
			String response = ""+incomingPacket.getData().toString();
			System.out.println("Response from Server: "+response);
			
			fileInputStream.close();
			dataInputStream.close();
			dataSocket.close();
			System.out.println("File upload complete. File sent:"+f.getName());
			return true;
						
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Error occured during the file upload!");
		return false;
	}
	
	public boolean downloadFile(String filename, int fileSize) throws IOException {
		
		try {
			System.out.println("File downloading... File name:"+filename);
			dataSocket = new DatagramSocket();
			
			byte[] data = new byte[fileSize];
			String path = System.getProperty("user.home") + "/Desktop/GoogleDrive/"+filename;
					
			DatagramPacket datagramPacket = new DatagramPacket(data, fileSize);
			dataSocket.receive(datagramPacket);
			
			File newFile = new File(path);
			FileOutputStream fileOutputStream = new FileOutputStream(newFile);
			fileOutputStream.write(data);
			fileOutputStream.flush();
			fileOutputStream.close();
			System.out.println("File "+filename+" successfully recieved.");
			
			return true;
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return false;
	}
	
	
}
