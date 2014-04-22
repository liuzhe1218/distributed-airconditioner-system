package serverdao;

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
/*
 * ���ݿ��Ŀ��ؿ��Ʒ���
 */
public class controlDao {
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
    public static waitinglist wl = new waitinglist(); //�������Ķ��ж���
	public waitingthread test; //����ִ���̶߳���
    public executethread[] threads = new executethread[3]; //ִ���̳߳�, �ֱ��Ӧ���ݿ��������¼
	//static client cli; //���ÿͻ����������״̬
    public serverstatus server; //���÷������߳�״̬����
    public serverthread sthread;
    
    public executethread[] getThreads() {
		return threads;
	}
	
    /*
	 * �����ܿ��ص�on �� off, ��Ϊon, ��Ҫ��use status��Ϊfree, ����offҪ�Ϳ�
	 * ���Գɹ�
	 */
	public String serverControl(String input)throws Exception{ // input= open or close
		boolean waitingflag = true;
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		String output = "";
		 
		int i;
		String serverSql = "update serverstatus t set t.currentstatus=?,t.usestatus=? where threadID=?";
		String useSql = "update serverstatus t set t.roomID=? where threadID=?";
		try{
			tx = session.beginTransaction();
			Query query1 = session.createQuery(serverSql); //Ԥ��HQL
			System.out.println("query1 executes");
			Query query2 = session.createQuery(useSql);
			System.out.println("query2 executes");
			if (input.equals("on")){ //������������� 
				//�����ȴ������߳�
				//�ȴ������߳��Ѿ�����
				test = new waitingthread(wl);
				test.setWaitingflag(waitingflag); //�ȴ��߳�һֱsleep
				sthread = new serverthread(test);
				test.start();
				sthread.start();
				//serverthread��ʼ,�ź�����true
				System.out.println("the waiting thread is opening");
				System.out.println("the server will be opened");
				query1.setString(0, "on");
				query1.setString(1, "free");
				for (i=0;i<3;i++){//update�߳�״̬Ϊfree, ������״̬��Ϊon
					query1.setInteger(2,i+1);
					//query2.setInteger(1,i+1);
					query1.executeUpdate();
					//query2.executeUpdate();
				}
			}
			else if (input.equals("off")){  //����ر���������
				//serverthread��false
				System.out.println("the server will be closed");
				query1.setString(0, "off");
				query1.setString(1,"");
				query2.setString(0,"");  //��ǰ������ÿ�
				for (i=0;i<3;i++){//update ������״̬Ϊoff, �߳�״̬��Ϊ��
					query1.setInteger(2,i+1);
					query2.setInteger(1,i+1);
					query1.executeUpdate();
					query2.executeUpdate();
				}
			}
			sthread.setSignal(false);
			sthread.wait();//
			test.destroy(); //��յȴ�����
			test.setWaitingflag(false); //sleep��ͣ,�߳̽���
			test.wait(); //��ȡ���ڴ�
			//Ҫ��Ҫ���Ƕ����ָ���������??
			
			for (int j=0;j<3;j++)//�ϵ�����ִ���߳�
				threads[j].wait();
			output="success";
			tx.commit();		
		}catch(Exception e){
    		if (tx!=null) {tx.rollback();
    		output="failure";
    		}
    		throw e;
    	}
    	finally{
    		session.close();
    	}
		return output;
	}
    /*
     * threadControl(): �����̵߳Ŀ��Ʒ���,�����ڿ����߳���ѡ���ǰ�Ĳ���,
     * socket �������ٽ����,cli��socket����
     * ���򷵻�����,˵���߳��Ѷ���ռ��
     * ���Գɹ�
     * �����߳�,�ȴ����²���
     */
	public int threadControl(client cli, int[] executeflag) throws Exception{
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		int output=-1; //��ʼ״̬
		int flag; //��ΪroomID�ж��Ƿ���ڵı�־λ
		List<serverstatus> list = new ArrayList<serverstatus>();
		String status="";
		Query query;
		int threadID;//ѡ���һ�������̵߳�ID��
		String serverExamineSql ="select currentstatus from serverstatus where threadID=1";
		String threadSelectSql = "from serverstatus where usestatus='free'";
		String threadUpdateSql = "update serverstatus t set t.usestatus=? where threadID=?";
		try{
			tx = session.beginTransaction();
			status = (String)session.createQuery(serverExamineSql).uniqueResult(); //Ԥ��HQL
			//System.out.println(status);
			if (status.equals("off"))//�������ܿ����ѹر�
				output= 0;
			else if(status.equals("on")){
				list = session.createQuery(threadSelectSql).list(); //�������治���п����ݣ�����get�����ͻᱨ��
				System.out.println("the size of list is: "+list.size());
				//System.out.println("the list are returned");
				/*if (list.size()==0){ //ȫ��Ϊbusy
					output=1;
					//����ִ�в���ȴ�����β���ĺ���
					//wlist = test.getWl(); //����waitinglist,���������߳�10ms
					if (test.getWl().getSize() ==0)
						System.out.println(" there is no object in the waitinglist now");
					else{
						for (int i=0;i<test.getWl().getSize();i++)
							System.out.println("the "+i+"th roomID in the waitinglist is: "+test.getWl().getObject(i));
					}
					test.add(cli);//���������,��������ͬ���ؼ���
				}*/
				//else{  //��ǰ���п����߳�, �����һ��free���߳�
					//����Ҫ���ж��Ƿ������ͬ�Ķ���,������ִ�и���,����ִ�в���
					flag = isExist(cli.getRoomID());
					if (flag == -1){ //��ǰ���󷿼�δ��ִ�е���
						//output=1;
						threadID = list.get(0).getThreadID();//�б�ͷ���̶߳���
						output = threadID;
						//updatethread("busy", threadID, cli.getRoomID()); //�������ݿ�״̬
						System.out.println("threadID: "+threadID);
						updateusage(cli.getRoomID()); //�����÷���Ŀ��ؼ�¼
						threads[threadID-1] = new executethread(cli,threadID);
						executeflag[threadID-1] = threads[threadID-1].getNoticeflag(); //�ź�������ֵ
						System.out.println("flag is: "+threads[threadID-1].getNoticeflag());
						threads[threadID-1].start();
						//saverecord(threads[threadID-1]);
						//���ﻹҪ����socket��ȡ�Ķ������������,�ӿڴ�д
					}
					else{//ִ�н�֮ǰ����ͬ������и���
						output=flag;
						//threads[flag-1].wait(); //�Ͽ��߳�
						saverecord(threads[flag-1]);  //��record�����¼
						updateusage(cli.getRoomID());  //���俪�ش�����1
						threads[flag-1] = new executethread(cli,flag);
						threads[flag-1].start();
					}
				//}
			}			
		}catch(Exception e){
    		if (tx!=null) tx.rollback();
    		throw e;
    	}
    	finally{
    		session.close();
    	}
		return output;
	}
	public void updatethread(String requirement, int ID, String roomID)throws Exception{
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		Query query;
		String updatesql = "update serverstatus t set t.usestatus=?,t.roomID=? where threadID=?";
		try{
			tx = session.beginTransaction();
			query = session.createQuery(updatesql);
			query.setString(0, requirement);
			query.setString(1, roomID);
			query.setInteger(2, ID);
			query.executeUpdate();
			tx.commit();
			System.out.println("the update for "+requirement+" is over");
		}catch(Exception e){
    		if (tx!=null) tx.rollback();
    		throw e;
    	}
    	finally{
    		session.close();
    	}
	}
	/*
	 * ȡ����ǰ���¶ȷ���
	 * ���Գɹ�
	 */
	public String getDirection() throws Exception{ //ȡ����ǰ���¶ȱ仯����,����ִ���߳����Ŵ�
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		String direction="";
		String selectDirectionSql ="select temperdirection from serverstatus t where t.threadID =1";
		try{
			tx = session.beginTransaction();
			direction = (String)session.createQuery(selectDirectionSql).uniqueResult();
		}catch(Exception e){
    		if (tx!=null) tx.rollback();
    		throw e;
    	}
    	finally{
    		session.close();
    	}
    	return direction;
	}
	/*
	 * ���µ�ǰ���¶ȷ���:  hot-> cool or cool -> hot
	 * ���Գɹ�
	 */
	public String UpdateTemperDirection() throws Exception{
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		String updatetemperSql ="update serverstatus t set t.temperdirection=? where t.threadID=?";
		String output = "";
		String currentTemper = getDirection(); //��ȡ��ǰ���¶ȷ���
		int i=0;
		try{
			tx = session.beginTransaction();
			Query query = session.createQuery(updatetemperSql);
			if (currentTemper.equals("cool"))
				query.setString(0, "hot");
			else if(currentTemper.equals("hot"))
				query.setString(0, "cool");
			for(;i<3;i++){
				query.setInteger(1,i+1);
				query.executeUpdate();
			}
			tx.commit();
			output = "success";
		}catch(Exception e){
    		if (tx!=null) {tx.rollback();
    		output = "failure";
    		}
    		throw e;
    	}
    	finally{
    		session.close();
    	}
		return output;
	}
	/*
	 * ����ĳ�����Ƿ���ִ���߳���
	 * ���Գɹ�
	 */
	public int isExist(String roomID) throws Exception{
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		String SearchRoomIDSql ="from serverstatus t where t.roomID=?";
		serverstatus test;
		int output = -1; //�����ھͷ��� -1
		try{
			tx = session.beginTransaction();
			Query query = session.createQuery(SearchRoomIDSql);
			query.setString(0,roomID);
			List list = query.list();
			if (list.size()==0){//�÷���Ŀǰû����ִ��
				System.out.println(" the requested roomID is not in executethread now");
			}
			else { 
				test = (serverstatus)list.get(0);
				output = test.getThreadID();
			}
		}catch(Exception e){
    		if (tx!=null) {tx.rollback();
    		}
    		throw e;
    	}
    	finally{
    		session.close();
    	}
		return output;
	}
	/*
	 * ����ǰִ�����һ��server�еĶ���浽record����, ��ô�趨ִ�н�����ʱ���Ǹ�����
	 * ����ok, ���¶ȷ��򲻶�ʱ,�����threadID��һֱΪ0,����������ݿ�
	 * �¶ȼ��㻹�Ǵ�������
	 */
	public void saverecord(executethread server) throws Exception{
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		record test = new record();
		client cli = server.getCli();
		serverstatus status = server.getExecute();
		Timestamp time = new Timestamp(System.currentTimeMillis()); //��ȡ��ǰʱ��
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
		endtemper = cli.getCurrenttemper()+speed*(time.getTime()-cli.getStarttime().getTime())/60; //��ǰ�¶�
		try{
			tx = session.beginTransaction();
			if ((ID=server.getThreadID())!=0){ //��ǰ�¶ȷ�����ϼ�������
				test.setEndtemper(endtemper);
				test.setFlowpower(cli.getFlowpower());
				test.setEndtime(time);
				test.setRoomID(cli.getRoomID());
				test.setStarttemper(cli.getCurrenttemper()); //����Ҫ��������ֵ,���¶ȱ仯�˶���
				test.setStarttime(cli.getStarttime());
				test.setTemperdirection(cli.getTemperdirection());
				test.setThreadID(status.getThreadID());
				test.setFlowvolume(watt*(time.getTime()-cli.getStarttime().getTime())/60);
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
	 * ����room�������,���������򴴽��Ҵ���Ϊ1, ���������1
	 * ���Գɹ�
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
		    if (list.size()==0){//����������������
		    	System.out.println("sorry, room"+roomID+" doesn't have record now, I will create it");
		    	test.setRoomID(roomID);
		    	test.setTimes(1);
		    	session.save(test);
		    	tx.commit();
		    }
		    else{ //ִ�ж�ĳһ��Ŀ����
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
	 * ����ĳһ������ŵ���ĳһ�����������¼
	 * ���Գɹ�
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
	/*public static void main(String []args){// ����main��
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