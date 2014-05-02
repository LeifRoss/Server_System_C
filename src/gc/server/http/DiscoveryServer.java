package gc.server.http;

import gc.server.com.MainFrame;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;


/**
 * 
 * GCServer DiscoveryServer
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class DiscoveryServer implements Runnable{


	private static final int DEFAULT_PORT = 9090;
	private static final int PACKET_SIZE = 256;
	private static final String MULTICAST_GROUP_DEFAULT = "228.5.6.7";
	private static final String KEY_CONFIG_GROUP = "group";
	
	private MulticastSocket socket;
	private DatagramPacket packet;
	private InetAddress group;
	private String group_address;

	private boolean running;
	private String host;

	public DiscoveryServer(String host){

		this.host = host;
		running = false;
		
		group_address = MainFrame.readConfig(KEY_CONFIG_GROUP);
		
		if(group_address == null || group_address.isEmpty()){
			group_address = MULTICAST_GROUP_DEFAULT;
		}
	}


	@Override
	public void run() {

		
		running = true;
		try {
			
			socket = new MulticastSocket(DEFAULT_PORT);
			group = InetAddress.getByName(group_address);
			socket.joinGroup(group);
			
		} catch ( IOException e) {
			MainFrame.error(e.getMessage());
			e.printStackTrace();
			return;
		}

		packet = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE);

		while(running){

			try {

				socket.receive (packet);
				byte[] buffer  = host.getBytes();
				packet.setData (buffer);
				packet.setLength (buffer.length);
				socket.send (packet);

			} catch (IOException ie){
				MainFrame.print("Discovery Socket closed");
			}

		}

	}


	/**
	 * Starts the server
	 */
	public void startServer(){

		Thread t = new Thread(this);
		t.start();
	}


	/**
	 * Stops the server
	 */
	public void stopServer(){

		running = false;

		if(group!=null && socket!=null && !socket.isClosed()){
			try {
				socket.leaveGroup(group);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(socket!=null){
			socket.close();
		}
	
	}

}
