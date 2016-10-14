package File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

public class DownloadFile extends Thread{
	private ServerSocket server = null;
	
	private InputStream In = null;
	private FileOutputStream Out = null;
	
	private String saveTo = "";
	
	public DownloadFile(String saveTo){
		this.saveTo = saveTo;
		try {
			server = new ServerSocket(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		try {
			Socket socket = server.accept();
			
			this.In = socket.getInputStream();
			this.Out = new FileOutputStream(this.saveTo);
			
			byte[] buffer = new byte[1024];
			int count;
			
			while ((count = this.In.read(buffer)) >= 0){
				this.Out.write(buffer, 0, count);
			}
			this.Out.flush();
			
			JOptionPane.showMessageDialog (null, "Download complete !!!", "Downfile", JOptionPane.INFORMATION_MESSAGE);
			
			if(Out != null){ Out.close(); }
            if(In != null){ In.close(); }
            if(socket != null){ socket.close(); }
            if (server!=null) server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getPort(){
		return server.getLocalPort();
	}
}
