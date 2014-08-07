package gc.server.udp;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class UDPHandlerThread implements Runnable {
	
	private UDPServer server;
	private UDPHandler handler;
	private DatagramPacket packet;
	
	private byte[] recieved_data;
	private InetAddress address;
	private int port;
	
	
	public UDPHandlerThread(DatagramPacket packet, UDPServer server, UDPHandler handler){

		this.packet = packet;
		this.server = server;
		int length = packet.getLength();
		int start = packet.getOffset();

		byte[] data = packet.getData();
		recieved_data = new byte[length];

		for(int i = 0; i < length; i++){		
			int idx = start + i;
			recieved_data[idx] = data[idx];		
		}

		this.address = packet.getAddress();
		this.port = packet.getPort();
		this.handler = handler;
		handler.setThread(this);
		
	}


	public UDPServer getServer() {
		return server;
	}


	public void setServer(UDPServer server) {
		this.server = server;
	}


	public DatagramPacket getPacket() {
		return packet;
	}


	public void setPacket(DatagramPacket packet) {
		this.packet = packet;
	}


	public InetAddress getAddress() {
		return address;
	}


	public void setAddress(InetAddress address) {
		this.address = address;
	}


	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}


	@Override
	public void run() {
		handler.handle(recieved_data, address, port);
	}


}
