package Chat;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import Client.PeerOnline;
import File.UploadFile;
import Protocol.Message;

public class ClientChat{
	private PeerOnline receiver = null;
	private ChatMessager chatGUI = null;
	
	private Socket client = null;
	private ObjectInputStream In = null;
	private ObjectOutputStream Out = null;
	
	public ClientChat(ChatMessager ui){
		receiver = ui.receiver;
		chatGUI = ui;
		try {
			client = new Socket(receiver.getIP(), receiver.getPort());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.Open();
	}
	
	public void Open(){
		try {
			this.Out = new ObjectOutputStream(this.client.getOutputStream());
			this.Out.flush();
			this.In = new ObjectInputStream(this.client.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			try {
				this.Close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	public void SendMessage(Message msg){
		try {
			this.Out.writeObject(msg);
			this.Out.flush();
			if (msg.type.equals("chat"))
				this.chatGUI.txtMessage.append(msg.content + "\n");
			this.chatGUI.txtSend.setText("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//this.chatGUI.txtMessage.append("OFFLINE\n");
			try {
				this.Close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public void Close() throws IOException{
		if (this.client!=null){
			this.client.close();
			this.client = null;
		}
	}
	
	public void readResFile(File file){
		try {
			Message res = (Message)this.In.readObject();
			if (res!=null){
				if (!res.content.equals("false")){
					int port = Integer.parseInt(res.content);
					UploadFile up = new UploadFile(receiver.getIP(), port, file);
					up.start();
				}else{
					int mc = JOptionPane.WARNING_MESSAGE;
                    JOptionPane.showMessageDialog (null, receiver.username + " rejected file request", "Notification", mc);
				}
			}
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//return null;
		}
	}
}
