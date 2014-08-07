package gc.server.udp;

import gc.server.com.MainFrame;

import java.net.InetAddress;

public class ExampleHandler extends UDPHandler {




	@Override
	public void handle(byte[] data, InetAddress address, int port) {
		// TODO Auto-generated method stub
		// assign all variables inside handle and it wont screw up kthx
		
		int i = 0;
		
		for(int r = 0; r < 1000; r++){
			i++;
		}

		MainFrame.print(""+i);
		i = i - 1000;
		respond("Hello Client! "+i);
	}

}
