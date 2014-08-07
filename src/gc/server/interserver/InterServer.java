package gc.server.interserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InterServer implements Runnable {


	private InterServerHandler handler;
	private ServerSocket server_socket;
	private HashMap<String, InterServerConnection> connections;
	private int port;

	private boolean running;

	private ExecutorService service;
	private String server_name;


	/**
	 * InterServer 
	 * Handle communication between servers
	 * @param handler
	 * @param name
	 */
	public InterServer(InterServerHandler handler, String name){

		this.server_name = name;
		this.handler = handler;
		setup();
	}


	private void setup(){

		connections = new HashMap<String,InterServerConnection>();
		service = Executors.newCachedThreadPool();

		try {
			server_socket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	
	/**
	 * Return the IP address to a server
	 * @param name
	 * @return
	 */
	public String getAddressByName(String name){
		
		if(connections.containsKey(name)){
			
			InterServerConnection c = connections.get(name);
			return c.getSocket().getInetAddress().getHostAddress();
		}
		
		return null;
	}
	

	/**
	 * Connect to a remote server
	 * @param address
	 * @param port
	 * @param name
	 */
	public void connect(String address, int port){

		try {

			Socket socket = new Socket(InetAddress.getByName(address),port);
			establishConnection(socket);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Establish connection and check for duplicates
	 * @param socket
	 * @return
	 */
	private InterServerConnection establishConnection(Socket socket){


		for(InterServerConnection connection: connections.values()){

			if(connection == null || connection.getSocket().isClosed()){
				continue;
			}

			if(connection.getSocket().getInetAddress().equals(socket.getInetAddress()) && connection.getSocket().getPort() == socket.getPort() ){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}	
		}

		InterServerConnection c = new InterServerConnection(socket, this);

		connections.put(c.toString(), c);
		service.execute(c);

		return c;
	}


	/**
	 * Send a message to a remote server
	 * @param name
	 * @param packet
	 * @return
	 */
	public boolean send(String name, InterServerPacket packet){

		if(connections.containsKey(name)){

			InterServerConnection connection = connections.get(name);
			return connection.send(packet);	
		}		

		return false;
	}


	/**
	 * Deliver response to a request
	 * @param in
	 * @return
	 */
	public InterServerPacket getResponse(InterServerPacket in){
		return handler.handle(in);
	}


	/**
	 * Start the server
	 */
	public void startServer(){

		if(!running){

			running = true;	
			Thread t = new Thread(this);
			t.start();
		}

	}


	/**
	 * Stop the server 
	 */
	public void stopServer(){

		if(running){

			running = false;

			try {
				server_socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			for(InterServerConnection c: connections.values()){

				if(c == null){
					continue;
				}

				c.close();
			}

			connections.clear();

			service.shutdownNow();
		}
	}


	/**
	 * Create the first packet sent after connection has been established
	 * This packet contains the server name and stats.
	 * @return
	 */
	public InterServerPacket createInitPacket(){		

		return new InterServerPacket(server_name);
	}


	@Override
	public void run() {

		while(running){

			try {

				Socket connection = server_socket.accept();
				InterServerConnection c = establishConnection(connection);

				if(c != null){
					c.send(createInitPacket());
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}



}
