package gc.server.ssh;

import gc.server.com.MainFrame;
import gc.server.util.CommandListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;

public class SSHShellFactory implements Factory<Command>{



	public static class SSHShell implements Command, Runnable, CommandListener{

		private static final String MOTD = "GCServer[0.16] SSH";
		
		private InputStream in;
		private OutputStream out;
		private OutputStream err;
		private ExitCallback callback;
		private Environment environment;
		private Thread thread;


		@Override
		public void run() {
			
			printMOTD();
			print("");
			
			MainFrame.getCommandHandler().add(this);
			BufferedReader r = new BufferedReader(new InputStreamReader(in));

			String buffer = "";
			int shift = 0;

			try {
				for (;;) {

					char c = (char)r.read();
					System.out.println(c+" "+(int)c);
					if(c >= 31 && c <= 126){
						buffer += c;
						out.write(c);
						out.flush();
					
					}else if(c == 127 && buffer.length() > 0){
						
						buffer = buffer.substring(0, buffer.length()-1);
						out.write(c);
						out.flush();
						
					}else if(c == 9){
						buffer = "";
						print("");
						continue;
					}else if(c > 255){
					
						break;
					}

					if(c == '\n' || c == '\r'){

						out.write('\n');
						out.flush();
						
						if(buffer.isEmpty()){
							buffer = "";
							print("");
							continue;
						}
						
						if (buffer.equals("exit")) {
							break;
						}

						System.out.println("input: "+buffer);
						MainFrame.getCommandHandler().execute(buffer);
						buffer = "";

					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				MainFrame.getCommandHandler().remove(this);
				callback.onExit(0);
				closeStreams();
			}

		}

		private void closeStreams(){

			try {
				if(out!=null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(err!=null)
					err.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void destroy() {
			thread.interrupt();
		}

		@Override
		public void setErrorStream(OutputStream err) {
			this.err = err;
		}

		@Override
		public void setExitCallback(ExitCallback exit) {
			this.callback = exit;
		}

		@Override
		public void setInputStream(InputStream in) {
			this.in = in;
		}

		@Override
		public void setOutputStream(OutputStream out) {
			this.out = out;
		}

		@Override
		public void start(Environment env) throws IOException {
			environment = env;
			thread = new Thread(this, "SSHShell");
			thread.start();
		}

		
		

		public void printMOTD() {

			if(out == null ){
				return;
			}
			

			try {
				out.write(("\r\n"+MOTD).getBytes("UTF-8"));
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		
		@Override
		public void print(String in) {

			if(out == null ){
				return;
			}

			in = in.replace("\n", "\r\n");

			try {
				out.write((in+"\r\nConsole:> ").getBytes("UTF-8"));
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}


	@Override
	public Command create() {
		return new SSHShell();
	}


}
