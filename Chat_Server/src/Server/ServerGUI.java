package Server;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JScrollPane;

public class ServerGUI {

	private JFrame frmServer;
	public JTextArea txtInfo;
	private JScrollPane scrollPane;
	private Server server;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGUI window = new ServerGUI();
					window.frmServer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ServerGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmServer = new JFrame();
		frmServer.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if (server!=null)
					server.StopServer();
			}
		});
		frmServer.setTitle("Server");
		frmServer.setBounds(100, 100, 450, 300);
		frmServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmServer.getContentPane().setLayout(null);
		
		JButton btnStart = new JButton("START");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnStart_Click();
				btnStart.setEnabled(false);
			}
		});
		btnStart.setBounds(12, 13, 97, 25);
		frmServer.getContentPane().add(btnStart);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 51, 408, 189);
		frmServer.getContentPane().add(scrollPane);
		
		this.txtInfo = new JTextArea();
		scrollPane.setViewportView(txtInfo);
		txtInfo.setEditable(false);
	}
	
	private void btnStart_Click(){
		server = new Server(this);
	}
}
