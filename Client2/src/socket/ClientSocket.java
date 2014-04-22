package socket;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import base.client;
import base.constant;
import base.temper;
import dao.controlaction;

/*
 * 客户端端的socket：发送请求，循环接收临时温度
 */
public class ClientSocket extends Thread implements Serializable{
	private static final long serialVersionUID = -1906476533863356634L;
	
	private String IpAddr;
	//private int bind_port;
	private int server_port;
	private boolean flag;
	private Socket ClientSoc = null;
	private ObjectOutputStream os = null;
	private ObjectInputStream is = null;
	private client cli = null;
	private temper temp = null;
	Object obj;
	controlaction action = new controlaction();
	
	private final static Logger logger = Logger.getLogger(ClientSocket.class.getName());
	
	public ClientSocket(String IpAddr, int server_port){
		// initialization
		this.IpAddr = IpAddr;
		//this.bind_port = bind_port;
		this.server_port = server_port;
		//ClientSoc = new Socket();
		//ClientSoc.setReuseAddress(true);
		flag = true;
		//ClientSoc.bind(new InetSocketAddress("127.0.0.1",bind_port));
		//ClientSoc.connect(new InetSocketAddress(IpAddr,server_port));
	}
	public void stops(){
		flag = false;
		System.out.println("stop commmand executes");
	}
	public void run(){
		try {
			ClientSoc = new Socket(IpAddr, server_port);
		while (flag){// keep running with flag
				// socket starts
				while (cli == null){
					
				}
				os = new ObjectOutputStream(ClientSoc.getOutputStream());
				is = new ObjectInputStream(new BufferedInputStream(ClientSoc.getInputStream()));
				os.writeObject(cli);
				os.flush();
				cli = null;
				//按照刷新频率进行循环，直到接收到了最后一个包
				while ((temp == null)||temp.getFlag()==false){
					obj = is.readObject();
					if (obj != null) {
						temp = (temper)obj;
						System.out.println("user: " + temp.getTemper() + "/" + temp.getFlag());
						//将临时温度传到action中,做界面的温度交互
						action.setTemp(temp);
					}
					try {
						sleep(constant.FREQUENCY*1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}// while
				System.out.println("transmission finished");
				temp = null;
			} 
		      }catch (UnknownHostException e1){
				e1.printStackTrace();
			}
		      catch(IOException ex) {
				logger.log(Level.SEVERE, null, ex);
			} catch (ClassNotFoundException e){
				logger.log(Level.SEVERE, null, e);
			}
			finally {
				try {
					is.close();
				} catch(Exception ex) {}
				try {
					os.close();
				} catch(Exception ex) {}
				try {
					ClientSoc.close();
				} catch(Exception ex) {}
			}	
		System.out.println("the client is stopping");
		try {
			ClientSoc.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	public static void main(String[] args){
		String IpAddr = "127.0.0.1";
		int server_port = 9234;
		//int bind_port = 6661;
		client cli = new client();
		cli.setRoomID("508");
		cli.setCurrenttemper(20);
		try {
			ClientSocket socket = new ClientSocket(IpAddr,server_port);
			socket.start();
			sleep(3000);
			socket.setCli(cli);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setTemp(temper temp){
		this.temp = temp;
	}
	public temper getTemp(){
		return this.temp;
	}
	public void setCli(client cli){
		this.cli = cli;
	}
	public client getCli(){
		return this.cli;
	}
	public String getIpAddr() {
		return IpAddr;
	}
}