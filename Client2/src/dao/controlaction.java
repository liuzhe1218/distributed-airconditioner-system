package dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

import base.BaseAction;
import base.constant;
import base.temper;
import base.client;
import socket.ClientSocket;
/*
 * �ͻ��˵Ŀ�����
 * ��������: 1.����  2.�������ݿ�
 */
public class controlaction extends BaseAction{
	private static final long serialVersionUID = 1L;
	private client record;//��¼�ϴβ�������Ķ���
	private temper temp;
	//client �ӿ�
	private String roomID;
	private String temperdirection;
	private String flowpower;
	private double currenttemper;
	private double endtemper;
	private Timestamp starttime;
	ClientSocket clientSoc = null;
	
	public String control() throws Exception{
		String output="success";
		String control = this.getRequest().getParameter("control");
		if (control.equals("on")){ // off-on �����߳�
			clientSoc = new ClientSocket(constant.IPADDR,constant.PORT);
			clientSoc.start();
			//����socket-server��client���󣬼����ʹ���
			// client �����ź���һ����дget�������������ݽ��룬��get�����ź����仯������receiveData()
			PrintWriter writer = this.getResponse().getWriter();
			writer.print("on");
			writer.close();
		}
		else{ // on-off
			//�ر�socket-server��client����
			System.out.println("the socket for client is going to stop");
			clientSoc.stops();
			PrintWriter writer = this.getResponse().getWriter();
			writer.print("off");
			writer.close();
		}
		return output;
	}
	//��������淢�ͽӿ�
	public String getRecv(temper temp){
		String output = "success";
		PrintWriter writer;
		try {
			writer = this.getResponse().getWriter();
			writer.print(temp.getTemper());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}
	public void sendRequest(){ //socket ���ͽӿ�
		client cli = new client();
		starttime = new Timestamp(System.currentTimeMillis());
		cli.setCurrenttemper(getCurrenttemper());
		cli.setEndtemper(getEndtemper());
		cli.setFlowpower(getFlowpower());
		cli.setRoomID(getRoomID());
		cli.setTemperdirection(getTemperdirection());
		cli.setStarttime(starttime);
		clientSoc.setCli(cli);
		record=cli;//����һ����¼
		while (getTemp() == null){
			
		}
		while (getTemp().getFlag()==false){
			getRecv(temp);
			try {
				Thread.sleep(constant.FREQUENCY*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// time-delay??
		getRecv(getTemp());// last one
	}
	// get-set methods
	public temper getTemp() {
		return temp;
	}
	public void setTemp(temper temp) {
		this.temp = temp;
	}
	public String getRoomID() {
		return roomID;
	}
	public String getTemperdirection() {
		return temperdirection;
	}
	public String getFlowpower() {
		return flowpower;
	}
	public double getCurrenttemper() {
		return currenttemper;
	}
	public double getEndtemper() {
		return endtemper;
	}
	public Timestamp getStarttime() {
		return starttime;
	}
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	public void setTemperdirection(String temperdirection) {
		this.temperdirection = temperdirection;
	}
	public void setFlowpower(String flowpower) {
		this.flowpower = flowpower;
	}
	public void setCurrenttemper(double currenttemper) {
		this.currenttemper = currenttemper;
	}
	public void setEndtemper(double endtemper) {
		this.endtemper = endtemper;
	}
	public void setStarttime(Timestamp starttime) {
		this.starttime = starttime;
	}
}