package base;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import serverdto.record;
import serverdto.serverstatus;
import serverdto.usagetimes;
import base.constant;
import base.waitinglist;
import threads.executethread;
import threads.serverthread;
import threads.waitingthread;
import base.client;
import socket.Servers;
/*
 * ??ݿ??Ŀ??ؿ?Ʒ???
 */
public class serverdao {
	public static SessionFactory sessionFactory;
	static{
		try{
			Configuration config = new Configuration().configure();
			sessionFactory = config.buildSessionFactory();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
    public static waitinglist wl = new waitinglist(); //?????Ķ?ж??
	public waitingthread test; //???ִ???̶??
    public executethread[] threads = new executethread[3]; //ִ???̳? ?ֱ?Ӧ??ݿ??????
	//static client cli; //????ͻ??????״̬
    //public serverstatus server; //???????????̬???
    public volatile static serverstatus[] servers = new serverstatus[3];
    public serverthread sthread;
    public Servers socket; // ????socket???????
    
    public executethread[] getThreads() {
		return threads;
	}
	public serverstatus[] getServers(){
		return this.servers;
	}
	public Servers getSocket(){
		return this.socket;
	}
    /*
	 * ???ܿ??ص?n ??off, ?Ϊon, ?Ҫ??use status?Ϊfree, ???ffҪ???
	 * ϵͳ??δ???
	 */
	public String serverControl(String input)throws Exception{ // input= open or close
		boolean waitingflag = true;
		String output = "";
		int i;
		if (input.equals("on")){ //????????? 
			//????ȴ????߳?
			//?ȴ????߳?Ѿ????
			//init();//??ʼ??
			for (i=0;i<3;i++){
				servers[i].setCurrentstatus("on");
				servers[i].setUsestatus("free");
			}
			test = new waitingthread(wl);
			test.setWaitingflag(waitingflag); //?ȴ?????ֱsleep
			sthread = new serverthread(test);
			socket = new Servers(constant.PORT);
			test.start();
			sthread.start();
			//serverthread??ʼ,??????true
			System.out.println("the waiting thread is opening");
			System.out.println("the server will be opened");
		}
		else if (input.equals("off")){  //???ر??????
			//serverthread??false
			System.out.println("the server will be closed");
			for (i=0;i<3;i++){
				servers[i].setCurrentstatus("off");
				servers[i].setUsestatus("");
				servers[i].setRoomID("");
				servers[i].setTemperdirection("");
			}
			sthread.setSignal(false);
			sthread.wait();//
			test.destroy(); //??յȴ????
			test.setWaitingflag(false); //sleep?ͣ,??̽??
			//test.wait(); //??ȡ?????
		}			
		
		for (int j=0;j<3;j++)//?ϵ???ִ????
			threads[j].wait();
		output="success";			
		return output;
	}
	public void init(){
		int i;
		for(i=0 ; i<3 ;i++){
			servers[i] = new serverstatus();
			servers[i].setCurrentstatus("on");
			servers[i].setTemperdirection("cool");
			servers[i].setUsestatus("free");
			servers[i].setThreadID(i+1);
			servers[i].setRoomID("");
		}
	}
    /*
     * threadControl(): ?????̵Ŀ?Ʒ???,???ڿ??߳???????ǰ?Ĳ??
     * socket ??????ٽ??,cli??ocket???
     * ??򷵻???,˵????Ѷ???ռ?
     * ??Գɹ?
     * ???߳??ȴ??????
     */
	public int threadControl(client cli, int[] executeflag) throws Exception{
		int output=-1; //??ʼ״̬
		int flag; //?ΪroomID???Ƿ???ı??λ
		String status="";
		int threadID;//ѡ?????????̵߳?D??
			status = servers[0].getCurrentstatus();
			//System.out.println(status);
			if (status.equals("off"))//?????????ѹر?
				output= 0;
			else if(status.equals("on")){
					//???Ҫ????Ƿ??????Ķ?????ִ????,????????
					flag = isExist(cli.getRoomID());
					if (flag == -1){ //??ǰ?????????ִ????
						//output=1;
						threadID = searchfree(cli.getRoomID());
						//updatethread("busy", threadID, cli.getRoomID());
						//System.out.println("the free thread is: "+threadID);
						output = threadID;
						System.out.println("threadID: "+threadID);
						threads[threadID-1] = new executethread(cli,threadID);
						executeflag[threadID-1] = threads[threadID-1].getNoticeflag(); //????????
						System.out.println("flag is: "+threads[threadID-1].getNoticeflag());
						System.out.println("the thread"+threadID+" is working");
						threads[threadID-1].start();
						System.out.println("the thread"+threadID+" starts");
						updateusage(cli.getRoomID()); //?????÷??????ؼ??
				        //saverecord(threads[threadID-1]);
						//??ﻹҪ???ocket????Ķ????????ӿڴ?д
					}
					else{//ִ???֮ǰ???????????
						System.out.println("????");
						output=flag;
						//threads[flag-1].wait();
						saverecord(threads[flag-1],cli.getStarttime(),false);  //?record???¼
						updateusage(cli.getRoomID());  //???俪?ش????
						threads[flag-1].setFlag(false);
						threads[flag-1].setCli(cli);
						threads[flag-1].setNoticeflag(1);
						//executeflag[flag-1] = 1;
						System.out.println("the status of array is: "+executeflag[flag-1]);
						threads[flag-1].setFlag(true);
						//noticeflag????,??Ҫ??1
					}
				//}
			}			
		return output;
	}
	public void updatethread(String requirement, int ID, String roomID){
		servers[ID-1].setUsestatus(requirement);
		servers[ID-1].setRoomID(roomID);
	}
	/*
	 * ȡ????ǰ??¶ȷ??
	 * ??Գɹ?
	 */
	public String getDirection(){ //ȡ????ǰ??¶ȱ仯???,??????????Ŵ?
		String direction = servers[0].getTemperdirection();
    	return direction;
	}
	/*
	 * ?????ǰ??¶ȷ??:  hot-> cool or cool -> hot
	 * ??Գɹ?
	 */
	public void UpdateTemperDirection(){
		String currentTemper = getDirection(); //?????ǰ??¶ȷ??
		int i=0;
		for (;i<3;i++){
			if (currentTemper.equals("cool"))
				servers[i].setTemperdirection("hot");
			else
				servers[i].setTemperdirection("cool");
		}
	}
	/*
	 * ????????Ƿ????????
	 * ??Գɹ?
	 */
	public int isExist(String roomID){
		int output = -1; //????ھͷ???-1
		int i=0;
		for (;i<3;i++){
			if (servers[i].getRoomID().equals(roomID)){
				output = servers[i].getThreadID();
				break;
			}
		}
		return output;
	}
	/*
	 * ????ǰִ???????server??Ķ????record??? ?ô???ִ?????????Ǹ???
	 * ???k, ????ȷ???????,???threadID???ֱΪ0,???????ݿ?
	 * ??ȼ?㻹??????
	 */
	public void saverecord(executethread server, Timestamp starttime,boolean flag) throws Exception{
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		record test = new record();
		client cli = server.getCli();
		serverstatus status = server.getExecute();
		Timestamp time = new Timestamp(System.currentTimeMillis()); //?????ǰʱ??
		double endtemper ; // starttemper + time* flowpermin
		String direction = getDirection();
		double speed;
		double watt;
		int ID;
		if(cli.getFlowpower().equals("high")){
			speed = constant.HIGH_SPEED;
			watt = constant.HIGH_COST;
		}
		else if (cli.getFlowpower().equals("middle")){
			speed = constant.MIDDLE_SPEED;
			watt = constant.MIDDLE_COST;
		}
		else{ 
			speed = constant.LOW_SPEED;
			watt = constant.LOW_COST;
		}
		if (cli.getTemperdirection().equals("cool"))
			speed *= -1;
		System.out.println("the current speed is: "+speed);
		if (flag==true)
			endtemper = cli.getEndtemper();
		else
			endtemper = cli.getCurrenttemper()+speed*(time.getTime()-starttime.getTime())/60000; //??ǰ???
		try{
			tx = session.beginTransaction();
			System.out.println("the current threadID is: "+server.getThreadID());
			if ((ID=server.getThreadID())!=0){ //??ǰ??ȷ?????ϼ??????
				test.setEndtemper(endtemper);
				test.setFlowpower(cli.getFlowpower());
				test.setEndtime(time);
				test.setRoomID(cli.getRoomID());
				test.setStarttemper(cli.getCurrenttemper()); //??????????,????ȱ仯?˶??
				test.setStarttime(starttime);//?ӵ?ǰִ?ʱ?俪ʼ??
				test.setTemperdirection(cli.getTemperdirection());
				test.setThreadID(ID);
				test.setFlowvolume(watt*(time.getTime()-starttime.getTime())/60000);
				session.save(test);
				tx.commit();
			}
			else
				System.out.println("This operation is illegal, no record returns");
		}catch(Exception e){
    		if (tx!=null) {tx.rollback();
    		}
    		throw e;
    	}
    	finally{
    		session.close();
    	}
	}
	/*
	 * ???room?????,??????򴴽?????Ϊ1, ??????
	 * ??Գɹ?
	 */
	public boolean updateusage(String roomID) throws Exception{
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		Query query;
		String selectSql = "from usagetimes t where t.roomID=?";
		//String insertSql = "insert into usage values(?,?)";
		String updateSql = "update usagetimes t set t.times=? where t.roomID=?";
		int num;
		boolean flag = true;
		usagetimes test = new usagetimes();
		List list = new ArrayList();
		try{
			tx = session.beginTransaction();
			query = session.createQuery(selectSql);
			query.setString(0,roomID);
			list = query.list();
			System.out.println("the size of list is: "+list.size());
		    if (list.size()==0){//???????????
		    	System.out.println("sorry, room"+roomID+" doesn't have record now, I will create it");
		    	test.setRoomID(roomID);
		    	test.setTimes(1);
		    	session.save(test);
		    	tx.commit();
		    }
		    else{ //ִ????һ?Ŀ???
		    	System.out.println("update this room");
		    	test = (usagetimes)list.get(0);
		    	num = test.getTimes();
		    	query = session.createQuery(updateSql);
		    	query.setInteger(0, num+1);
		    	query.setString(1, roomID);
		    	query.executeUpdate();
		    	tx.commit();
		    }
		}catch(Exception e){
    		if (tx!=null) {tx.rollback();
    		flag = false;
    		}
    		throw e;
    	}
    	finally{
    		session.close();
    	}
    	return flag;
	}
	/*
	 * ????ĳһ??????????һ?????????
	 * ??Գɹ?
	 */
	public List loadrecord(String roomID, Timestamp starttime, Timestamp endtime) throws Exception{
		List list = new ArrayList();
		String selectSql = "from record t where t.roomID=? and t.endtime>? and t.endtime<?";
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			Query query = session.createQuery(selectSql);
			query.setString(0,roomID);
			query.setTimestamp(1, starttime);
			query.setTimestamp(2, endtime);
			list = query.list();
			if (list.size()==0){
				System.out.println("there are no records about room"+roomID);
			}
		}catch(Exception e){
    		if (tx!=null) {tx.rollback();
    		}
    		throw e;
    	}
    	finally{
    		session.close();
    	}
    	return list;
	}
	public synchronized int searchfree(String roomID){
		int ID = -1;
		int i = 0;
		Timestamp time = new Timestamp(System.currentTimeMillis());
		for (;i<3;i++){
			if (servers[i].getUsestatus().equals("free")){
				ID = servers[i].getThreadID();
				System.out.println("the first Thread is: "+ID);
				System.out.println("the current time is: "+time);
				break;
			}
		}
		if (ID > 0){
			servers[ID-1].setRoomID(roomID);
			servers[ID-1].setUsestatus("busy");
		}
		return ID;
	}
	/*public static void main(String []args){// ???ain??
		controlDao test = new controlDao();
		int ID = 1;
		//String starttime = "2013-04-14 00:00:00";
		//String endtime = "2013-04-14 23:59:59";
		//Timestamp start = Timestamp.valueOf(starttime);
		//Timestamp end = Timestamp.valueOf(endtime);
		//List list = new ArrayList();
		record test1;
		try {
			test.updatethread("free", 1, "");
			//test.updatethread("free", 2, "");
			//list = test.loadrecord("508", start, end);
			//test1 = (record)list.get(0);
			//System.out.println("the room is: "+test1.getRoomID());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Timestamp time = new Timestamp(System.currentTimeMillis());
		//client cli = new client();
		//cli.setCurrenttemper(20);
		//cli.setEndtemper(25);
		//cli.setFlowpower("low");
		//cli.setRoomID("508");
		//cli.setStarttime(time);
		//cli.setTemperdirection("cool");
		//executethread server1 = new executethread(cli,ID);
		//try {
			//test.saverecord(server1);
		//} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}
	}*/
}