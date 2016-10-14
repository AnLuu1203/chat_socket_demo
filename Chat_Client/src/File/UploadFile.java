package File;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

public class UploadFile extends Thread {
	private Socket socket = null;
	private FileInputStream In = null;
	private OutputStream Out = null;
	
	private File file = null;
	
	public UploadFile(String addr, int port, File file){
		this.file = file;
		try {
			socket = new Socket(addr,port);
			this.Open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void Open(){
		try {
			Out = socket.getOutputStream();
			In = new FileInputStream(this.file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void run(){
		byte[] buffer = new byte[1024];
		int count;
		
		try {
			while((count = this.In.read(buffer))>=0){
				this.Out.write(buffer, 0, count);;
			}
			this.Out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JOptionPane.showMessageDialog(null, "File Upload complete!!!", "Upfile", JOptionPane.INFORMATION_MESSAGE);
		
		try {
			if (this.In!=null) this.In.close();
			if (this.Out!=null) this.Out.close();
			if (this.socket!=null) this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
