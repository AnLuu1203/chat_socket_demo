package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import Protocol.Message;

public class Client implements Runnable {
	public int port; 
	public String username;
	
	private Socket client = null;
	private ClientConnectGUI ConnectGUI = null;
	
	private ObjectInputStream inFromServer = null;
	private ObjectOutputStream outToServer = null;
	
	public Thread clientThread = null;
	
	private ChatRoomGUI frmChatRoom = null;
	
	public PeerOnline listUser[] = null;
	public int countUser = 0;
	
	public Client(ClientConnectGUI ui,String add, int portServer){
		this.ConnectGUI = ui;
		try {
			this.client = new Socket(add, portServer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Can't Connect to Server!!", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		this.Open();
		this.StartClient();
	}
	
	public void Open(){
		try {
			outToServer = new ObjectOutputStream(client.getOutputStream());
			outToServer.flush();
			inFromServer = new ObjectInputStream(client.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendMessage(Message m){
		try {
			outToServer.writeObject(m);
			outToServer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run(){
		while (true){
			try {
				Message msg = (Message) inFromServer.readObject();
				this.handleMessage(msg);
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void StartClient(){
		if (clientThread==null){
			clientThread = new Thread(this);
			clientThread.start();
		}
	}
	

	@SuppressWarnings("deprecation")
	public void StopClient() throws IOException{
		if (this.inFromServer!=null){
			this.inFromServer.close();
		}
		if (this.outToServer!=null){
			this.outToServer.close();
		}
		if (this.client!=null){
			this.client.close();
		}
		
		if (clientThread!=null){
			clientThread.stop();
			clientThread = null;
		}
	}
	
	private DefaultListModel<String> model = new DefaultListModel<String>();
	
	public synchronized void handleMessage(Message m){
		if (m.type.equals("login")){
			Login(m.content.equals("true"));
			return;
		}
		
		if (m.type.equals("AddUser")){
			addToListUser(m);
			return;
		}
		
		if (m.type.equals("RemoveUser")){
			removeFromListUser(m);
			return;
		}
	}
	
	private void Login(boolean isSuccess){
		if (isSuccess == false){
			int mc = JOptionPane.INFORMATION_MESSAGE;
			JOptionPane.showMessageDialog(null, "User exists!!","Notification", mc);
		}
		else{
			frmChatRoom = new ChatRoomGUI(this.ConnectGUI,this);
			frmChatRoom.frame.setVisible(true);
			listUser = new PeerOnline[100];
			this.ConnectGUI.frame.setVisible(false);
		}
	}
	//Thêm 1 User vào list
	private void addToListUser(Message m){
		model.addElement(m.username);
		listUser[countUser] = new PeerOnline(m.username, m.IP, m.port);
		countUser++;
		
		frmChatRoom.lstUser.setModel(model);
		//frmChatRoom.scrollPane.setViewportView(frmChatRoom.lstUser);
	}
	//Xóa 1 User vào list
	private void removeFromListUser(Message m){
		model.removeElement(m.PeerSender);
		for (int i=0 ;i<countUser; i++){
			if (listUser[i].username.equals(m.PeerSender)){
				if (listUser[i].frmChat!=null){
					listUser[i].frmChat.txtMessage.append("\t"+listUser[i].username + " is OFFLINE!!\n");
					listUser[i].frmChat.btnSend.setEnabled(false);
					listUser[i].frmChat.btnFile.setEnabled(false);
					try {
						listUser[i].frmChat.send.Close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				for (int j=i; j<countUser-1; j++){
					listUser[j] = listUser[j+1];
				}
				listUser[countUser-1] = null;
				countUser--;
				break;
			}
		}
		frmChatRoom.lstUser.setModel(model);
		//frmChatRoom.scrollPane.setViewportView(frmChatRoom.lstUser);
	}
	
	public void offline(){
		model.removeAllElements();
		listUser = null;
		countUser = 0;
		this.username = "";
		this.port = 0;
	}
	
	public String findIP(String name){
		for (int i=0; i<countUser; i++){
			if (listUser[i].username.equals(name)){
				return listUser[i].getIP();
			}
		}
		return "";
	}
	
	public int findPort(String name){
		for (int i=0; i<countUser; i++){
			if (listUser[i].username.equals(name)){
				return listUser[i].getPort();
			}
		}
		return 0;
	}
	
	//Tìm user dựa vào username
	public PeerOnline findUserOnline(String username){
		for (int i=0; i<countUser; i++){
			if (listUser[i].username.equals(username)){
				return listUser[i];
			}
		}
		return null;
	}
	
	//Kiểm tra 1 port có được sử dụng chưa, nếu chưa return true
	public static boolean isAvailablePort(int port) {
	    ServerSocket serverSocket = null;
	    DatagramSocket dataSocket = null;
	    try {
	      serverSocket = new ServerSocket(port);
	      serverSocket.setReuseAddress(true);
	      dataSocket = new DatagramSocket(port);
	      dataSocket.setReuseAddress(true);
	      return true;
	    } catch (final IOException e) {
	      return false;
	    } finally {
	      if (dataSocket != null) {
	        dataSocket.close();
	      }
	      if (serverSocket != null) {
	        try {
	          serverSocket.close();
	        } catch (final IOException e) {
	          // can never happen
	        }
	      }
	    }
	  }
}
