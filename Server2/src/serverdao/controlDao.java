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
 * 数据库层的开关控制方法
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
    public static waitinglist wl = new waitinglist(); //类声明的队列对象
	public waitingthread test; //公用执行线程对象
    public executethread[] threads = new executethread[3]; //执行线程池, 分别对应数据库的三条记录
	//static client cli; //公用客户端请求对象状态
    public serverstatus server; //公用服务器线程状态对象
    public serverthread sthread;
    
    public executethread[] getThreads() {
		return threads;
	}
	
    /*
	 * 控制总开关的on 和 off, 若为on, 则要将use status设为free, 否则off要滞空
	 * 测试成功
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
			Query query1 = session.createQuery(serverSql); //预备HQL
			System.out.println("query1 executes");
			Query query2 = session.createQuery(useSql);
			System.out.println("query2 executes");
			if (input.equals("on")){ //请求打开主服务器 
				//启动等待队列线程
				//等待队列线程已经启动
				test = new waitingthread(wl);
				test.setWaitingflag(waitingflag); //等待线程一直sleep
				sthread = new serverthread(test);
				test.start();
				sthread.start();
				//serverthread开始,信号量传true
				System.out.println("the waiting thread is opening");
				System.out.println("the server will be opened");
				query1.setString(0, "on");
				query1.setString(1, "free");
				for (i=0;i<3;i++){//update线程状态为free, 服务器状态改为on
					query1.setInteger(2,i+1);
					//query2.setInteger(1,i+1);
					query1.executeUpdate();
					//query2.executeUpdate();
				}
			}
			else if (input.equals("off")){  //请求关闭主服务器
				//serverthread传false
				System.out.println("the server will be closed");
				query1.setString(0, "off");
				query1.setString(1,"");
				query2.setString(0,"");  //当前房间号置空
				for (i=0;i<3;i++){//update 服务器状态为off, 线程状态改为空
					query1.setInteger(2,i+1);
					query2.setInteger(1,i+1);
					query1.executeUpdate();
					query2.executeUpdate();
				}
			}
			sthread.setSignal(false);
			sthread.wait();//
			test.destroy(); //清空等待队列
			test.setWaitingflag(false); //sleep暂停,线程结束
			test.wait(); //不取出内存
			//要不要考虑对象空指针的问题呢??
			
			for (int j=0;j<3;j++)//断掉所有执行线程
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
     * threadControl(): 三个线程的控制方法,若存在空闲线程则选择最靠前的插入,
     * socket 方法的临界对象,cli从socket传入
     * 否则返回数字,说明线程已都被占用
     * 测试成功
     * 加入线程,等待重新测试
     */
	public int threadControl(client cli, int[] executeflag) throws Exception{
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		int output=-1; //初始状态
		int flag; //作为roomID判断是否存在的标志位
		List<serverstatus> list = new ArrayList<serverstatus>();
		String status="";
		Query query;
		int threadID;//选择第一个空闲线程的ID号
		String serverExamineSql ="select currentstatus from serverstatus where threadID=1";
		String threadSelectSql = "from serverstatus where usestatus='free'";
		String threadUpdateSql = "update serverstatus t set t.usestatus=? where threadID=?";
		try{
			tx = session.beginTransaction();
			status = (String)session.createQuery(serverExamineSql).uniqueResult(); //预备HQL
			//System.out.println(status);
			if (status.equals("off"))//服务器总开关已关闭
				output= 0;
			else if(status.equals("on")){
				list = session.createQuery(threadSelectSql).list(); //这行里面不能有空数据，否则get方法就会报错
				System.out.println("the size of list is: "+list.size());
				//System.out.println("the list are returned");
				/*if (list.size()==0){ //全部为busy
					output=1;
					//这里执行插入等待队列尾部的函数
					//wlist = test.getWl(); //调出waitinglist,并且休眠线程10ms
					if (test.getWl().getSize() ==0)
						System.out.println(" there is no object in the waitinglist now");
					else{
						for (int i=0;i<test.getWl().getSize();i++)
							System.out.println("the "+i+"th roomID in the waitinglist is: "+test.getWl().getObject(i));
					}
					test.add(cli);//将对象放入,方法带有同步关键字
				}*/
				//else{  //当前还有空余线程, 插入第一个free的线程
					//但是要先判断是否存在雷同的对象,若有则执行更新,否则执行插入
					flag = isExist(cli.getRoomID());
					if (flag == -1){ //当前请求房间未被执行当中
						//output=1;
						threadID = list.get(0).getThreadID();//列表头的线程对象
						output = threadID;
						//updatethread("busy", threadID, cli.getRoomID()); //更新数据库状态
						System.out.println("threadID: "+threadID);
						updateusage(cli.getRoomID()); //创建该房间的开关记录
						threads[threadID-1] = new executethread(cli,threadID);
						executeflag[threadID-1] = threads[threadID-1].getNoticeflag(); //信号量对象赋值
						System.out.println("flag is: "+threads[threadID-1].getNoticeflag());
						threads[threadID-1].start();
						//saverecord(threads[threadID-1]);
						//这里还要插入socket获取的对象的其他数据,接口待写
					}
					else{//执行将之前的相同对象进行更新
						output=flag;
						//threads[flag-1].wait(); //断开线程
						saverecord(threads[flag-1]);  //向record插入记录
						updateusage(cli.getRoomID());  //房间开关次数加1
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
	 * 取出当前的温度方向
	 * 测试成功
	 */
	public String getDirection() throws Exception{ //取出当前的温度变化方向,返回执行线程以排错
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
	 * 更新当前的温度方向:  hot-> cool or cool -> hot
	 * 测试成功
	 */
	public String UpdateTemperDirection() throws Exception{
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		String updatetemperSql ="update serverstatus t set t.temperdirection=? where t.threadID=?";
		String output = "";
		String currentTemper = getDirection(); //获取当前的温度方向
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
	 * 查找某房间是否在执行线程中
	 * 测试成功
	 */
	public int isExist(String roomID) throws Exception{
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		String SearchRoomIDSql ="from serverstatus t where t.roomID=?";
		serverstatus test;
		int output = -1; //不存在就返回 -1
		try{
			tx = session.beginTransaction();
			Query query = session.createQuery(SearchRoomIDSql);
			query.setString(0,roomID);
			List list = query.list();
			if (list.size()==0){//该房间目前没有在执行
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
	 * 将当前执行完的一个server中的对象存到record里面, 怎么设定执行结束的时间是个问题
	 * 测试ok, 当温度方向不对时,对象的threadID会一直为0,不会插入数据库
	 * 温度计算还是存在问题
	 */
	public void saverecord(executethread server) throws Exception{
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		record test = new record();
		client cli = server.getCli();
		serverstatus status = server.getExecute();
		Timestamp time = new Timestamp(System.currentTimeMillis()); //获取当前时间
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
		endtemper = cli.getCurrenttemper()+speed*(time.getTime()-cli.getStarttime().getTime())/60; //当前温度
		try{
			tx = session.beginTransaction();
			if ((ID=server.getThreadID())!=0){ //当前温度方向符合季节条件
				test.setEndtemper(endtemper);
				test.setFlowpower(cli.getFlowpower());
				test.setEndtime(time);
				test.setRoomID(cli.getRoomID());
				test.setStarttemper(cli.getCurrenttemper()); //这里要计算拖延值,即温度变化了多少
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
	 * 更新room请求次数,若不存在则创建且次数为1, 否则次数加1
	 * 测试成功
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
		    if (list.size()==0){//不存在这个房间对象
		    	System.out.println("sorry, room"+roomID+" doesn't have record now, I will create it");
		    	test.setRoomID(roomID);
		    	test.setTimes(1);
		    	session.save(test);
		    	tx.commit();
		    }
		    else{ //执行对某一条目更行
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
	 * 调出某一个房间号的在某一天的相关请求记录
	 * 测试成功
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
	/*public static void main(String []args){// 测试main类
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