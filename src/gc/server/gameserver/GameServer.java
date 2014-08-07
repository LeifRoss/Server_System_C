package gc.server.gameserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gc.server.interserver.InterServer;

public class GameServer implements Runnable {

	private static final int PACKET_DEFAULT_SIZE = Short.MAX_VALUE;
		
	private InterServer interServer;
	private ExecutorService service;

	private DatagramSocket server_socket;
	private DatagramPacket packet;
	
	private Thread thread;
	private boolean running, valid;
	
	private int port;
	
	public GameServer(){
		
		
		setup();
	}

	
	private void setup(){
				
		interServer = new InterServer(new GameInterHandler(),"gameserver");
		service = Executors.newCachedThreadPool();
		valid = true;		
	}

	

	private void handleRequest(){

		if(!valid){
			running = false;
			return;
		}

		try {

			server_socket.receive(packet);

			//UDPHandlerThread handler = new UDPHandlerThread(packet, this, request_handler);
			//service.execute(handler);

		} catch (IOException e) {
			e.printStackTrace();
		} 

	}
	

	@Override
	public void run() {
		
		
		running = true;

		try {

			server_socket = new DatagramSocket(port);
			packet = new DatagramPacket(new byte[PACKET_DEFAULT_SIZE], PACKET_DEFAULT_SIZE);

		} catch (IOException e) {
			e.printStackTrace();
			valid = false;
		}


		while(running){
			handleRequest();			
		}
		
		
	}
	
	

	/**
	 * Start the server
	 */
	public void startServer(){

		if(running){
			return;
		}

		thread = new Thread(this);
		thread.start();	
	}


	/**
	 * Stop the server
	 */
	public void stopServer(){

		if(!running){
			return;
		}

		running = false;

		if(server_socket != null){
			server_socket.close();
		}

		service.shutdown();

	}



	
}
