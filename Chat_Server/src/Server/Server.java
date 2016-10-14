package Server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import Protocol.Message;

public class Server implements Runnable{
	private SocketServerThread client[];
	private ServerSocket server = null;
	private Thread serverThread = null;
	private int numClient = 0;
	private int portServer = 8000;
	private ServerGUI gui = null;
	
	public Server(ServerGUI ui){
		this.client = new SocketServerThread[100];
		this.gui = ui;
		try {
			this.server = new ServerSocket(this.portServer);
			String info = "SERVER:\n+ IP Address: " + getLocalAddress().getHostAddress()
						+ "\n+ Port: " + server.getLocalPort() + "\n";
			this.gui.txtInfo.append(info);
			this.StartServer();
			//server.getInetAddress().getHostName()
			
			//InetAddress.getLocalHost().getHostName()
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		while (true){
			try {
				//gui.txtInfo.append("Waiting for Client.......\n");
				addClient(server.accept());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void StartServer(){
		if (serverThread == null){
			serverThread = new Thread(this);
			serverThread.start();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void StopServer(){
		if (serverThread!=null){
			serverThread.stop();
			serverThread = null;
		}
	}
	
	public synchronized void addClient(Socket c){
		//Tạo 1 thread mới
		client[numClient] = new SocketServerThread(this,c, this.gui);
		client[numClient].Open(); // Mở cổng kết nối
		client[numClient].start(); //chạy thread
		numClient++;
	}
	
	@SuppressWarnings("deprecation")
	public void removeClient(int ID){
		for (int i=0; i<numClient; i++){
			if (client[i].ID == ID){
				SocketServerThread temp = client[i];
				this.gui.txtInfo.append("Client port " + ID + " disconnected!!\n");
				for (int j=i; j<numClient-1; j++){
					client[j] = client[j+1];
				}
				numClient--;
				temp.Close();
				temp.stop();
			}
		}
	}
	
	public synchronized void handleMessage(Message m,int ID){
		if (m.type.equals("disconnect")){
			String name = findClient(ID).username;
			if (!name.equals("")){
				this.gui.txtInfo.append(name + " is OFFLINE!!\n");
				sendRemoveUser(name);
			}
			
			removeClient(ID);
			return;
		}
		
		//Đăng nhập
		if (m.type.equals("login")){
			if (findClient(m.PeerSender)==null){
				//Login thành công
				loginMessage(m, ID);
				sendAddUser(ID);
				SocketServerThread s = findClient(ID);
				s.IP = m.PerrReceiver;
				s.port = Integer.parseInt(m.content);
				sendAll(new Message(s.username,s.IP,s.port));
				//System.out.println(findClient(ID).IP + findClient(ID).port);
			}
			else{
				//User đã tồn tại, báo cho client
				findClient(ID).SendMessage(new Message("login","","","false"));
			}
			return;
		}
		if (m.type.equals("offline")){
			SocketServerThread socket = findClient(ID);
			String name = socket.username;
			sendRemoveUser(name);
			this.gui.txtInfo.append(socket.username + " is OFFLINE!!\n");
			socket.username = "";
			socket.port = 0;
		}
		
	}
	
	private synchronized SocketServerThread findClient(int ID){
		for (int i=0; i<numClient; i++){
			if (client[i].ID == ID){
				return client[i];
			}
		}
		return null;
	}
	private synchronized SocketServerThread findClient(String name){
		for (int i=0; i<numClient; i++){
			if (client[i].username.equals(name)){
				return client[i];
			}
		}
		return null;
	}
	
	private void loginMessage(Message m, int ID){
		SocketServerThread socket = findClient(ID);
		
		if (socket == null) return;
		
		socket.username = m.PeerSender;
		socket.port = Integer.parseInt(m.content);
		socket.SendMessage(new Message("login", "", socket.username, "true"));
		
		this.gui.txtInfo.append(socket.username + " is ONLINE!\n");
	}
	
	public synchronized void sendAddUser(int ID){
		SocketServerThread socket = findClient(ID);
		for (int i=0; i<numClient; i++){
			if (!client[i].username.equals("") && !client[i].username.equals(socket.username)){
				socket.SendMessage(new Message(client[i].username, client[i].IP, client[i].port));
			}
		}
		//socket.SendMessage(new Message("","",0));
	}
	
	public synchronized void sendAll(Message m){
		for (int i=0; i<numClient; i++){
			if (!client[i].username.equals("") && !client[i].username.equals(m.username)){
				client[i].SendMessage(m);
			}
		}
	}
	
	public synchronized void sendRemoveUser(String username){
		//SocketServerThread s = findClient(username);
		for (int i=0; i<numClient; i++){
			if (!client[i].username.equals("") && !client[i].username.equals(username)){
				client[i].SendMessage(new Message("RemoveUser",username,"",""));
			}
		}
	}
	
	public static InetAddress getLocalAddress() throws SocketException
    {
      Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
      while( ifaces.hasMoreElements() )
      {
        NetworkInterface iface = ifaces.nextElement();
        Enumeration<InetAddress> addresses = iface.getInetAddresses();

        while( addresses.hasMoreElements() )
        {
          InetAddress addr = addresses.nextElement();
          if( addr instanceof Inet4Address && !addr.isLoopbackAddress() )
          {
            return addr;
          }
        }
      }

      return null;
    }
}
