package socket;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import base.client;
import base.constant;
import base.temper;

/*
 * �������˵�socket����������new�̣߳�Ȼ��ִ���̣߳�����ˢ��Ƶ���������¶�����
 * �������˵�socket����
 */
public class Servers implements Serializable{
	private static final long serialVersionUID = -1906476533863356634L;
	
	private int port;
	private ServerSocket server = null;
	private ObjectInputStream is = null;
	private ObjectOutputStream os = null;
	private boolean flag = false;
	
	private final static Logger logger = Logger.getLogger(Servers.class.getName());
	
	public Servers(int port) throws Exception{
		this.port = port;
		server = new ServerSocket(port);
		flag = true;
		
		while (flag) {
			//System.out.print("waiting now");
			Socket socket = server.accept();
			//System.out.println(socket.getLocalPort());
			invoke(socket);//��ͬ���ķ���
		}
		System.out.println("Sorry, the server is not working now...");			
	}
	public synchronized void invoke(final Socket socket) throws Exception{
		new Thread(new Runnable() {
			public void run(){
				try{
					is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
					os = new ObjectOutputStream(socket.getOutputStream());
					Object obj = is.readObject();//ֻ�������л��Ķ���
					client user = (client)obj;
					//������뽫�������waitinglist�Ľӿ�
					System.out.println("user: " + user.getRoomID() + "/" + Double.toString(user.getCurrenttemper()));
					//�����߳�ִ�еķ�������
					for (int i=0;i<4;i++){//д������ִ���߳����жϵ�while
						temper temp = new temper();
						temp.setTemper(user.getCurrenttemper());
						temp.setFlag(false);
						os.writeObject(temp);
						os.flush();
						try {
							Thread.sleep(constant.FREQUENCY*1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					temper temp = new temper();
					temp.setTemper(user.getCurrenttemper());
					temp.setFlag(true);
					os.writeObject(temp);
					os.flush();
				}
				catch (IOException ex) {
					logger.log(Level.SEVERE, null, ex);
				} catch(ClassNotFoundException ex) {
					logger.log(Level.SEVERE, null, ex);
				} finally {
					try {
						is.close();
					} catch(Exception ex) {}
					try {
						os.close();
					} catch(Exception ex) {}
					try {
						socket.close();
					} catch(Exception ex) {}
				}
			}
		}).start();
	}
	public void stop(){
		flag = false;
		System.out.println("the stop command executes");
	}
	
	public static void main(String[] args){
		int port = 9234;
		try {
			Servers socket = new Servers(port);
			//Thread.sleep(5000);
			//socket.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setFlag(boolean flag){
		this.flag = flag;
	}
	public boolean getFlag(){
		return this.flag;
	}
	public int getPort() {
		return this.port;
	}
}