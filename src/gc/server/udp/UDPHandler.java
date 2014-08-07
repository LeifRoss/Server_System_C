package gc.server.udp;

import java.net.DatagramPacket;
import java.net.InetAddress;

public abstract class UDPHandler {

	private UDPHandlerThread thread;
	

	public UDPHandler(){	
		
	}
		
	
	public abstract void handle(byte[] data, InetAddress address, int port);
		
	
	public UDPHandlerThread getThread() {
		return thread;
	}


	public void setThread(UDPHandlerThread thread) {
		this.thread = thread;
	}

	
		
	public String toString(byte[] data){
		return new String(data);
	}
	
	
	public void respond(String in){		
		respond(in.getBytes());
	}
	
	
	public void respond(byte[] in){	
		
		DatagramPacket packet = new DatagramPacket(in, in.length, thread.getAddress(), thread.getPort());
		thread.getServer().send(packet);
	}
	
	
}
