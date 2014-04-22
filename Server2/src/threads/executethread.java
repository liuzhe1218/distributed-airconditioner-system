package threads;

import java.sql.Timestamp;

import serverdto.serverstatus;
import base.client;
import base.constant;
import serverdao.controlDao;
import base.serverdao;
import socket.Servers;
import base.temper;
/*
 * ִ���߳�,��಻�ܳ������, ��������ݿ��ж�
 * ����Ҫά��һ��client����+severstatus����,���ڽ������Խ�һЩ�����������д����ݿ���
 * ���Գɹ�
 */
public class executethread extends Thread{
	private client cli;
	private serverstatus execute = new serverstatus();
	private double currenttemper; //���¹�̲��Ϸ��صĽ��
	private int noticeflag ; //�߳�֪ͨ�ź���,��ĳ�߳�ִ��ok��,�����⹫��true,
	private int ID;
	private boolean flag; //��ݿ������
	private boolean over;
	private Servers server = null;
	private temper temperature;
	
	//controlDao test = new controlDao();
	serverdao test = new serverdao();
	public void setOver(boolean over){
		this.over = over;
	}
	public boolean getOver(){
		return this.over;
	}
	public boolean getFlag(){
		return this.flag;
	}
	public void setFlag(boolean flag){
		this.flag = flag;
	}
	public void setCli(client cli){
		this.cli = cli;
	}
	public int getNoticeflag(){
		return noticeflag;
	}
	public void setNoticeflag(int noticeflag){
		this.noticeflag = noticeflag;
	}
	public serverstatus getExecute() { //���ص�ǰִ�ж�����Ϣ
		try{
			sleep(100);
		} catch(InterruptedException e){
			e.printStackTrace();
		}
		return execute;
	}
	public client getCli(){ // ���ص�ǰ�Ķ���
		try {
			sleep(100); //ȡ���ʱ�߳�����100ms
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cli;
	}
	public double getCurrenttemper (){
		return currenttemper;
	}
	public executethread(client cli ,int ID){ //���췽��
		String direction;
		this.cli = cli;
		noticeflag = 1;
		this.ID = ID;
		flag = true;
		try {
			//���ж��Ƿ��ǵ�ǰӦ�õ��¶ȷ���
			direction = test.getDirection();
			if (!cli.getTemperdirection().equals(direction)){
				System.out.println("the requested temper direction is invalid");
				//socket����һЩ��ʾ�ַ�
			}
			/*else{ //�¶ȷ���ok, ������Ϊһ���߳�,����Ҫ�ж��Ƿ���ͬ,ֻҪִ�в��뼴��, executeֱ�Ӹ��Ǽ���
				execute.setTemperdirection(test.getDirection());
				execute.setRoomID(cli.getRoomID());
				execute.setThreadID(ID);
				execute.setCurrentstatus("on");
				execute.setUsestatus("busy");
			}*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int getThreadID(){
		return this.ID;
	}
	public void run(){
		while (flag == true){
			while (over == true){
				Timestamp starttime = new Timestamp(System.currentTimeMillis());//����ִ��ʱ���㿪ʼʱ��
				double strattemper = cli.getCurrenttemper();  //��������ᱻ��д
				double temp = strattemper;
				double endtemper = cli.getEndtemper();
				String power = cli.getFlowpower();
				double flowspeed;
				if (power.equals("high"))
					flowspeed = constant.HIGH_SPEED;
				else if (power.equals("middle"))
					flowspeed = constant.MIDDLE_SPEED;
				else
					flowspeed = constant.LOW_SPEED;
				if (cli.getTemperdirection().equals("cool"))
					flowspeed *= -1;
				while (temp != endtemper){
					if (flag == false){
						System.out.println("the thread is forced to be stopped");
						break;
					}
					try {
						sleep(constant.FREQUENCY*1000);
						temp += flowspeed/4;//constant.FREQUENCY*
						//将temp温度发送到servers
						System.out.println(temp);
						if (((temp< endtemper)&&cli.getTemperdirection().equals("cool"))||((temp> endtemper)&&cli.getTemperdirection().equals("hot"))){
							temp = endtemper;
							System.out.println("the current temper in thread"+this.ID +" is: "+temp);
							/*temperature = new temper();
							temperature.setTemper(temp);
							temperature.setFlag(true);
							test.getSocket().setTemp(temperature);*/
							break;
						}
						/*else{// the work is not finished yet...
							temperature.setTemper(temp);
							temperature.setFlag(false);
							test.getSocket().setTemp(temperature);
						}*/
						System.out.println("the current temper is: "+temp);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println(" the work for thread"+ID+" is finished, the thread will be stop automatically");
				try {
					test.saverecord(this,starttime,true);
					test.updatethread("free", ID, "");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//flag = false;
				noticeflag = 2;
				over = false;
			}
		}
	}
	/*public static void main(String []args){
		Timestamp starttime = new Timestamp(System.currentTimeMillis());
		client cli = new client();
		cli.setCurrenttemper(25);
		cli.setEndtemper(23);
		cli.setRoomID("508");
		cli.setFlowpower("high");
		cli.setStarttime(starttime);
		cli.setTemperdirection("cool");
		int ID = 1;
		executethread test = new executethread(cli,ID);
		test.start();
	}*/
}
