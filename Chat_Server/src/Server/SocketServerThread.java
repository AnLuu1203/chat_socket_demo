package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import Protocol.Message;

public class SocketServerThread extends Thread{
	private Server server = null;
	private Socket client = null;
	
	public int ID = -1;
	public String username = "";
	public int port;
	public String IP = "";
	
	private ObjectInputStream inFromClient = null;
	private ObjectOutputStream outToClient = null;
	private ServerGUI gui = null;
	
	public SocketServerThread(Server server,Socket client,ServerGUI ui){
		this.server = server;
		this.client = client;
		this.gui = ui;
		this.ID = client.getPort();
		//String ip =  this.client.getInetAddress().toString();
		//this.IP = ip.substring(1);
	}
	
	public void Open(){
		try {
			this.outToClient = new ObjectOutputStream(this.client.getOutputStream());
			this.outToClient.flush();
			this.inFromClient = new ObjectInputStream(this.client.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		this.gui.txtInfo.append("Client port " + this.ID + " connected!!\n");
		while(true){
			try {
				Message msg = (Message) this.inFromClient.readObject();
				this.server.handleMessage(msg,this.ID);
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		}
	}
	
	public void SendMessage(Message m){
		try {
			this.outToClient.writeObject(m);
			this.outToClient.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void Close(){
		try {
			if (this.client!=null)
				client.close();
			if (this.inFromClient!=null)
				inFromClient.close();
			if (this.outToClient!=null)
				outToClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
