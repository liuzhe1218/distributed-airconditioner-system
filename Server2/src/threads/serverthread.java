package threads;

import java.sql.Timestamp;

import serverdao.controlDao;
import base.client;
import base.waitinglist;
import base.serverdao;
/*
 * 总线程,控制整个流程
 * 接收socket的函数
 */
public class serverthread extends Thread{
	//controlDao control = new controlDao();
	serverdao control = new serverdao();
	client cli = new client(); //socket接收传入对象
	int[] noticeflag = {0,0,0}; //等待信号量  未初始化0  执行中1 执行结束2
	private boolean signal; //执行信号量,来源于control中的开关命令
	private waitingthread test;
	executethread[] threads = control.getThreads(); //三条执行线程
	int num;
	//socket接收模块的启动接口
	
	public serverthread( waitingthread test){
		this.test = test;
	}
	public void setSignal(boolean signal) { //设置总线程的信号量函数
		this.signal = signal;
	}
	public synchronized void setCli(client cli){
		this.cli = cli;
	}
	public void judge(int[] input) throws Exception{
		for (int i=0;i<input.length;i++){
			if (input[i]==2)
				control.updatethread("free", i+1, "");
		}
	}
	public void run(){
		while(test.getWl().getSize()>0){
			// socket模块的接收实例化,取出cli对象
			//先调出wl的对象,然后才是socket的对象
			setCli(test.minus());
			System.out.println("the setted client is: "+cli.getRoomID());
			if (cli!=null){
				try {
					if (noticeflag[0]==0||noticeflag[1]==0||noticeflag[2]==0){// 有线程还未被初始化
						num = control.threadControl(cli, noticeflag);
						System.out.println("the client "+cli.getRoomID()+" is executing");
					    continue;
					}
					else if ((threads[0].getNoticeflag()==1)&&(threads[1].getNoticeflag()==1)&&(threads[2].getNoticeflag()==1)){//当前等待线程存在等待对象且所有的都在执行
					   // num = control.threadControl(test.minus(),noticeflag); //弹出当前
						num = -1;
						//加入首对象重复判断
						if (control.isExist(cli.getRoomID())!=-1){
							num = control.threadControl(cli, noticeflag);
							System.out.println("substitute");
						}
						else{
							//未发现重复对象，等待
							while(threads[0].getNoticeflag()!=2||threads[1].getNoticeflag()!=2||threads[2].getNoticeflag()!=2){//说明从死循环里面出来了
								//System.out.println("server is waiting");
							}
							num = control.threadControl(cli, noticeflag);
							System.out.println("the waiting client "+cli.getRoomID()+" is working now");
						}
						continue;
					    //test.add(cli); //接收的放进waiting list
					}
					else if ((threads[0].getNoticeflag()==2)||(threads[1].getNoticeflag()==2)||(threads[2].getNoticeflag()==2)){//某线程执行结束之后
						//judge(noticeflag); //更新
						//if (test.getWl().getSize()>0){
							num = control.threadControl(cli, noticeflag);
							System.out.println("the current size of queue is: "+ test.getWl().getSize());
							continue;
						//}
						//else{
							//num = control.threadControl(cli, noticeflag);
						//}
					}
					System.out.println("the thread is executing is: "+num);
					
					//control.updatethread("free", num, "");
					//control.saverecord(threads[num-1]);
					//cli = null;
					//threads[num-1].wait();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public static void main(String[] args){
		waitinglist wl = new waitinglist();
		waitingthread thread = new waitingthread(wl);
		serverthread sthread = new serverthread(thread);
		sthread.control.init();
		Timestamp time;
		String[] roomID = {"510","506","507","508","507"};
		client[] cli = new client[5];
		int i;
		for (i=0;i<roomID.length-1;i++){
			cli[i] = new client();
			cli[i].setRoomID(roomID[i]);
			cli[i].setCurrenttemper(25);
			cli[i].setEndtemper(24-i*0.5);
			cli[i].setFlowpower("high");
			time = new Timestamp(System.currentTimeMillis());
			cli[i].setStarttime(time);
			cli[i].setTemperdirection("cool");
			thread.add(cli[i]);
		}
		thread.setWaitingflag(true);
		sthread.setSignal(true);//开机
		sthread.start();
		thread.start();
	}
}