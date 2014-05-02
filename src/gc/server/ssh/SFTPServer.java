package gc.server.ssh;

import gc.server.com.MainFrame;
import gc.server.util.Util;

import java.io.IOException;
import java.util.Arrays;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.KeyExchange;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.Session;
import org.apache.sshd.common.file.FileSystemView;
import org.apache.sshd.common.file.nativefs.NativeFileSystemFactory;
import org.apache.sshd.common.file.nativefs.NativeFileSystemView;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.kex.DHG1;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.sftp.SftpSubsystem;
import org.apache.sshd.server.shell.ProcessShellFactory;

public class SFTPServer {

	private static final int SFTP_STD_PORT = 21;
	private static final String SSH_KEY_LOCATION = Util.getAssetsLocation()+"config//hostkey.ser";
	private SshServer sshd;
	private int port;

	
	public SFTPServer(){
		this(SFTP_STD_PORT);
	}

	public SFTPServer(int port){
		this.port = port;
		setup();
	}


	private void setup(){


		sshd = SshServer.setUpDefaultServer();

		sshd.setPort(port);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(SSH_KEY_LOCATION));		
		sshd.setCommandFactory(new ScpCommandFactory());
		sshd.setShellFactory(new ProcessShellFactory());
		sshd.setPasswordAuthenticator(new SSHAuthenticator());
		sshd.setKeyExchangeFactories(Arrays.<NamedFactory<KeyExchange>>asList(new DHG1.Factory()));
		sshd.setSubsystemFactories(Arrays.<NamedFactory<Command>> asList(new SftpSubsystem.Factory()));

		sshd.setFileSystemFactory(getModifiedNativeFileSystemFactory());
	}



	private NativeFileSystemFactory getModifiedNativeFileSystemFactory() {

		return new NativeFileSystemFactory() {

			@Override
			public FileSystemView createFileSystemView(Session session) {

				String userName = session.getUsername();
				NativeFileSystemView nfsv = new SFTPView(userName, isCaseInsensitive());

				return nfsv;
			}

		};
	}


	public void start(){

		try {
			sshd.start();
			MainFrame.print("SFTP server started on port["+port+"]");
		} catch (IOException e) {
			MainFrame.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public void stop(){

		try {
			sshd.stop(true);
			MainFrame.print("SFTP server stopped on port["+port+"]");
		} catch (InterruptedException e) {
			MainFrame.error(e.getMessage());
			e.printStackTrace();
		}

	}



}
