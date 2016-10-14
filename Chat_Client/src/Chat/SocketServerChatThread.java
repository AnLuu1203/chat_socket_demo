package Chat;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import Client.Client;
import Client.PeerOnline;
import File.DownloadFile;
import Protocol.Message;

public class SocketServerChatThread extends Thread{
	private Socket socket = null;
	private PeerOnline sender = null;
	private Client client = null;
	
	private ObjectInputStream In = null;
	private ObjectOutputStream Out = null;
	
	public SocketServerChatThread(Socket socket,Client c){
		this.socket = socket;
		this.client = c;
		this.Open();
	}
	
	public void run(){
		while(true){
			try {
				Message msg = (Message) this.In.readObject();
				handleMessage(msg);
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				this.Close();
			}
		}
	}
	
	public void Open(){
		try {
			this.Out = new ObjectOutputStream(socket.getOutputStream());
			this.Out.flush();
			this.In = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			this.Close();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void Close(){
		try {
			if (this.socket!=null)
				this.socket.close();
			this.stop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void handleMessage(Message msg){
		if (msg.type.equals("chat")){
			if (sender.frmChat==null){
				sender.frmChat = new ChatMessager(sender,client);
				sender.frmChat.frame.setVisible(true);
			}
			sender.frmChat.txtMessage.append(msg.PeerSender + ": " + msg.content + "\n");
			return;
		}
		if (msg.type.equals("info")){
			this.sender = client.findUserOnline(msg.PeerSender);
			return;
		}
		if (msg.type.equals("nochat")){
			this.Close();
		}
		if (msg.type.equals("file_req")){
			int accept = JOptionPane.showConfirmDialog(null, "Download file " + msg.content + " from " + msg.PeerSender);
			if (accept == 0){
				JFileChooser jf = new JFileChooser();
                jf.setSelectedFile(new File(msg.content));
                int returnVal = jf.showSaveDialog(null);
                
                String saveTo = jf.getSelectedFile().getPath();
                if(saveTo != null && returnVal == JFileChooser.APPROVE_OPTION){
                    DownloadFile dwn = new DownloadFile(saveTo);
                    dwn.start();
                    try {
						this.Out.writeObject(new Message("file_res", "", "", ""+dwn.getPort()));
						this.Out.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					};
                }else{
                	try {
						this.Out.writeObject(new Message("file_res", "", "", "false"));
						this.Out.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
                }
			}else{
            	try {
					this.Out.writeObject(new Message("file_res", "", "", "false"));
					this.Out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
            }
		}
	}
}
