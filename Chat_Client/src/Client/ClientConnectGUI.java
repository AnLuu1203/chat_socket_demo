package Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import Protocol.Message;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ClientConnectGUI {

	public JFrame frame;
	private JTextField txtIPAddrServer;
	private JTextField txtPortServer;
	private JTextField txtUsername;
	private Client client = null;
	private JTextField txtPortClient;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientConnectGUI window = new ClientConnectGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientConnectGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if (client!=null){
					client.sendMessage(new Message("disconnect", "", "", ""));
					try {
						client.StopClient();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.exit(0);
			}
		});
		frame.setBounds(100, 100, 393, 431);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblServer = new JLabel("SERVER");
		lblServer.setBounds(12, 32, 56, 16);
		frame.getContentPane().add(lblServer);
		
		JLabel lblIPAddrServer = new JLabel("IP Address");
		lblIPAddrServer.setBounds(35, 61, 77, 16);
		frame.getContentPane().add(lblIPAddrServer);
		
		txtIPAddrServer = new JTextField();
		txtIPAddrServer.setText("localhost");
		txtIPAddrServer.setBounds(124, 58, 187, 22);
		frame.getContentPane().add(txtIPAddrServer);
		txtIPAddrServer.setColumns(10);
		
		JLabel lblPortServer = new JLabel("Port");
		lblPortServer.setBounds(35, 93, 77, 16);
		frame.getContentPane().add(lblPortServer);
		
		txtPortServer = new JTextField();
		txtPortServer.setText("8000");
		txtPortServer.setColumns(10);
		txtPortServer.setBounds(124, 90, 187, 22);
		frame.getContentPane().add(txtPortServer);
		
		JLabel lblClient = new JLabel("CLIENT");
		lblClient.setBounds(12, 225, 56, 16);
		frame.getContentPane().add(lblClient);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(35, 254, 77, 16);
		frame.getContentPane().add(lblUsername);
		
		txtUsername = new JTextField();
		txtUsername.setColumns(10);
		txtUsername.setBounds(124, 251, 187, 22);
		frame.getContentPane().add(txtUsername);
		
		JButton btnConnect = new JButton("Connect");
		JButton btnLogin = new JButton("Login");
		btnLogin.setEnabled(false);
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String IP = txtIPAddrServer.getText();
				int port = Integer.parseInt(txtPortServer.getText());
				btnConnect_Click(IP, port);
				btnConnect.setEnabled(false);
				btnLogin.setEnabled(true);
			}
		});
		btnConnect.setBounds(78, 146, 97, 25);
		frame.getContentPane().add(btnConnect);
		
		
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (client!=null){
					client.port = Integer.parseInt(txtPortClient.getText());
					if (!Client.isAvailablePort(client.port)){
						int mc = JOptionPane.INFORMATION_MESSAGE;
						JOptionPane.showMessageDialog(null, "This port is not available!!","Notification", mc);
						return;
					}
						
					client.username = txtUsername.getText();
					try {
						client.sendMessage(new Message("login",client.username,getLocalAddress().getHostAddress(),txtPortClient.getText()));
					} catch (SocketException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnLogin.setBounds(78, 329, 97, 25);
		frame.getContentPane().add(btnLogin);
		
		JLabel lblPort = new JLabel("Port");
		lblPort.setBounds(35, 297, 77, 16);
		frame.getContentPane().add(lblPort);
		
		txtPortClient = new JTextField();
		txtPortClient.setColumns(10);
		txtPortClient.setBounds(124, 294, 187, 22);
		frame.getContentPane().add(txtPortClient);
	}
	
	private void btnConnect_Click(String IPAddr, int portServer){
		client = new Client(this,IPAddr,portServer);
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
