package Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import Chat.ChatMessager;
import Chat.ServerChat;
import Protocol.Message;

import javax.swing.JList;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JLabel;

public class ChatRoomGUI {

	public JFrame frame;
	public JList lstUser = null;
	public JScrollPane scrollPane;
	private ClientConnectGUI ConnectGUI = null;
	
	private Client client = null;
	private ServerChat receive = null;
	
	private JLabel lblUsername = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//ChatRoomGUI window = new ChatRoomGUI();
					//window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @wbp.parser.entryPoint
	 */
	public ChatRoomGUI(ClientConnectGUI ui,Client c) {
		initialize();
		ConnectGUI = ui;
		client = c;
		receive = new ServerChat(this.client);
		receive.start();
		lblUsername.setText(client.username);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				ConnectGUI.frame.setVisible(true);
				if(client!=null){
					for (int i=0; i<client.countUser ;i++){
						if (client.listUser[i].frmChat!=null){
							client.listUser[i].frmChat.frame.setVisible(false);;
							client.listUser[i].frmChat.frame.dispose();
							client.listUser[i].frmChat = null;
						}
					}
					client.sendMessage(new Message("offline","","",""));
					client.offline();
					receive.Close();
					receive.stop();
					
				}
			}
		});
		frame.setTitle("Chat Room");
		frame.setBounds(100, 100, 321, 438);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 52, 273, 326);
		frame.getContentPane().add(scrollPane);
		
		lstUser = new JList();
		scrollPane.setViewportView(lstUser);
		lstUser.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		lblUsername = new JLabel("");
		lblUsername.setBounds(12, 23, 56, 16);
		frame.getContentPane().add(lblUsername);
		lstUser.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount()==2){
					String username = lstUser.getSelectedValue().toString();
					PeerOnline receiver = client.findUserOnline(username);
					if (receiver.frmChat==null){
						receiver.frmChat = new ChatMessager(receiver,client);
						receiver.frmChat.frame.setVisible(true);
						//receiver.frmChat.frame.setTitle(username+ receiver.getIP()+ receiver.getPort());
					}
				}
			}
		});
	}
}
