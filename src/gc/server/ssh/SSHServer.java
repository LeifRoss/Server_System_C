package gc.server.ssh;

import java.io.IOException;
import java.util.Arrays;

import gc.server.com.MainFrame;
import gc.server.util.Util;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.KeyExchange;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.kex.DHG1;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;


public class SSHServer{

	private static final int SSH_STD_PORT = 22;
	private static final String SSH_KEY_LOCATION = Util.getAssetsLocation()+"config//hostkey.ser";
	private SshServer sshd;
	private SSHAuthenticator authenticator;
	private int port;


	public SSHServer(int port){
		this.port = port;
		setup();
	}

	public SSHServer(){
		this(SSH_STD_PORT);
	}

	
	private void setup(){


		sshd = SshServer.setUpDefaultServer();
		sshd.setPort(port);

		authenticator = new SSHAuthenticator();

		sshd.setPasswordAuthenticator(authenticator);
		//sshd.setPublickeyAuthenticator(authenticator);


		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(SSH_KEY_LOCATION));		
		sshd.setShellFactory(new SSHShellFactory());
		sshd.setKeyExchangeFactories(Arrays.<NamedFactory<KeyExchange>>asList(new DHG1.Factory()));
	}



	public void start(){

		try {
			sshd.start();
			MainFrame.print("SSH server started on port["+port+"]");
		} catch (IOException e) {
			MainFrame.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public void stop(){

		try {
			sshd.stop(true);
			MainFrame.print("SSH server stopped on port["+port+"]");
		} catch (InterruptedException e) {
			MainFrame.error(e.getMessage());
			e.printStackTrace();
		}

	}

}
