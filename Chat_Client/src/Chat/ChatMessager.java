package Chat;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import Client.Client;
import Client.PeerOnline;
import Protocol.Message;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JScrollPane;
import java.awt.Color;

public class ChatMessager {

	public JFrame frame;
	public PeerOnline receiver = null;
	public JTextArea txtMessage;
	public JTextArea txtSend;
	public ClientChat send = null;
	private Client client = null;
	
	public JButton btnSend = null;
	public JButton btnFile = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//ChatMessager window = new ChatMessager();
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
	public ChatMessager(PeerOnline peer,Client c) {
		this.receiver = peer;
		this.client = c;
		initialize();
		send = new ClientChat(this);
		send.SendMessage(new Message("info", client.username, "", ""));
		this.frame.setTitle("Chatting " + receiver.username);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				send.SendMessage(new Message("nochat","","",""));
				receiver.frmChat = null;
				try {
					send.Close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		frame.setBounds(100, 100, 481, 329);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				send.SendMessage(new Message("chat",client.username,"",txtSend.getText()));
			}
		});
		btnSend.setBounds(341, 244, 97, 25);
		frame.getContentPane().add(btnSend);
		
		btnFile = new JButton("File");
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chooseFile();
			}
		});
		btnFile.setBounds(341, 195, 97, 25);
		frame.getContentPane().add(btnFile);
		
		JScrollPane scrollPaneMessage = new JScrollPane();
		scrollPaneMessage.setBounds(12, 13, 439, 165);
		frame.getContentPane().add(scrollPaneMessage);
		
		txtMessage = new JTextArea();
		DefaultCaret caret = (DefaultCaret)txtMessage.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		txtMessage.setForeground(new Color(0, 0, 0));
		scrollPaneMessage.setViewportView(txtMessage);
		txtMessage.setEditable(false);
		
		JScrollPane scrollPaneSend = new JScrollPane();
		scrollPaneSend.setBounds(12, 196, 301, 66);
		frame.getContentPane().add(scrollPaneSend);
		
		txtSend = new JTextArea();
		scrollPaneSend.setViewportView(txtSend);
	}
	
	private void chooseFile(){
		JFileChooser filechoose = new JFileChooser();
		filechoose.showDialog(this.frame,"Choose File");
		File file = filechoose.getSelectedFile();
		//System.out.println(file.getName());
		if (file == null) return;
		
		if (file.length() >= 8*1024){
			JOptionPane.showMessageDialog(null, "File to large", "Warning", JOptionPane.WARNING_MESSAGE);
		}else{
			send.SendMessage(new Message("file_req",client.username,"",file.getName()));
			send.readResFile(file);
		}
	}
}
