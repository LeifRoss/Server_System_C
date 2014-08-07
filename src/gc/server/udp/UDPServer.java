package gc.server.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class UDPServer implements Runnable {


	private static final int PACKET_DEFAULT_SIZE = Short.MAX_VALUE;

	private ExecutorService service;

	private DatagramSocket server_socket;
	private DatagramPacket packet;
	private UDPHandler request_handler;

	private Thread thread;

	private boolean running;
	private boolean valid;
	private short port;


	public UDPServer(short port, UDPHandler request_handler){

		this.port = port;
		this.request_handler = request_handler;	
		
		setup();
	}


	private void setup(){

		valid = true;
		service = Executors.newCachedThreadPool();
	}


	private void handleRequest(){

		if(!valid){
			running = false;
			return;
		}

		try {

			server_socket.receive(packet);

			UDPHandlerThread handler = new UDPHandlerThread(packet, this, request_handler);
			service.execute(handler);

		} catch (IOException e) {
			e.printStackTrace();
		} 

	}



	public void send(DatagramPacket packet){

		try {
			server_socket.send(packet);
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
