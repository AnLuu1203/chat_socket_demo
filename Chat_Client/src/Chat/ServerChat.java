package Chat;

import java.io.IOException;
import java.net.ServerSocket;

import Client.Client;

public class ServerChat extends Thread{
	private ServerSocket server = null;
	private Client client = null;
	
	public ServerChat(Client client){
		this.client = client;
		try {
			this.server = new ServerSocket(client.port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		while (true){
			try {
				new SocketServerChatThread(server.accept(), client).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void Close(){
		try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
