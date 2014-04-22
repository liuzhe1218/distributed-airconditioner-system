package threads;

import base.constant;
import base.waitinglist;
import base.client;
//import base.Node;
/*
 * 等待队列的线程,维护等待队列
 */
public class waitingthread extends Thread{
	private waitinglist wl; //类中的私有对象
	private boolean waitingflag; //on-off 传达的信号
	private int waitingminuts;
	public boolean isWaitingflag() {
		return waitingflag;
	}
	public void setWaitingflag(boolean waitingflag) {
		this.waitingflag = waitingflag;
	}
	public waitingthread(waitinglist wl){ //构造方法初始化队列对象,成为公共调用的线程队列
		waitingminuts = 0;//初始化等待时间清0
		this.wl = wl;
	}
	public  waitinglist getWl() { //调出所有的等待对象,
		try{
			sleep(10);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		return wl;
	}
	public synchronized void add(client cli){// 当客户端传来请求对象时, 如果判断结果为当前已满,则将其加入队列
		wl.push(cli);
	}
	public boolean search(client cli){ //查找某个对象是否在等待队列中
		boolean flag =false;
		return flag;
	}
	public synchronized client minus(){//当执行线程出现空闲时,将尾部的对象放进执行线程
		return wl.pop().getData();
	}
	public synchronized void destroy(){
		wl.popAll();
		//destroy() 之后外部stop即可
	}
	public void run(){ //在数据库判定当前已没有执行进程时执行
		while(waitingflag){
			try {
				sleep(constant.WAIT_FREQ*1000);
				waitingminuts++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
