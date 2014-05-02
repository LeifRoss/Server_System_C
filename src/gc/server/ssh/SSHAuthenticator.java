package gc.server.ssh;


import java.security.PublicKey;

import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

public class SSHAuthenticator implements PasswordAuthenticator, PublickeyAuthenticator {




	@Override
	public boolean authenticate(String username, String password, ServerSession session) {
	
		return SSHHandler.verify(username, password);
	}

	
	
	
	@Override
	public boolean authenticate(String arg0, PublicKey arg1, ServerSession arg2) {

		return true;
	}

}
