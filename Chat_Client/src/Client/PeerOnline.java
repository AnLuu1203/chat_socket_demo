package Client;

import Chat.ChatMessager;

public class PeerOnline {
	public String username = null;
	private String IP = null;
	private int port = 0;
	public ChatMessager frmChat = null;
	
	public PeerOnline(String name, String ip, int p){
		this.username = name;
		this.IP = ip;
		this.port = p;
	}
	
	public int getPort(){
		return this.port;
	}
	public String getIP(){
		return this.IP;
	}
}
