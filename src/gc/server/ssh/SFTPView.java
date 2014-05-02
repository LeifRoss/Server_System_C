package gc.server.ssh;

import java.io.File;

import gc.server.util.Util;



import org.apache.sshd.common.file.SshFile;
import org.apache.sshd.common.file.nativefs.NativeFileSystemView;
import org.apache.sshd.common.file.nativefs.NativeSshFile;

public class SFTPView extends NativeFileSystemView {

	private static final String ROOT_DIR = Util.getAssetsLocation();
	private final String root_dir;

	
	public SFTPView(String userName, boolean caseInsensitive) {
		super(userName, caseInsensitive);

		root_dir = SecureSshFile.normalizeSeparateChar(ROOT_DIR);
	}

	@Override
	public SshFile getFile(String file) {
		return getFile("/", file);
	}
	
	
	@Override
	public SshFile getFile(SshFile baseDir, String file) {
		return getFile(baseDir.getAbsolutePath(), file);
	}

	@Override
	protected SshFile getFile(String dir, String file) {
		
		
		String physicalName = SecureSshFile.getPhysicalName("/", dir, file, false);
		File f = new File(root_dir, physicalName); 

		// strip the root directory and return
		String userFileName = physicalName.substring("/".length() - 1);
		boolean readOnly = !f.canWrite();
		
		return new SecureSshFile(userFileName, f, this.getUserName(), readOnly, this);
	}



	static class SecureSshFile extends NativeSshFile {
		final boolean isReadOnly;
		//
		public SecureSshFile(final String fileName, final File file, final String userName, final boolean isReadOnly, NativeFileSystemView view) {
			super(view,fileName, file, userName);
			this.isReadOnly = isReadOnly;
		}
		//
		public boolean isWritable() {
			if (isReadOnly)
				return false;
			return super.isWritable();
		}
	}
	
	
	
	
}






