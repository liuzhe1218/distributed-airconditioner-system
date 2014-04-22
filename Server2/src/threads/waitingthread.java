package threads;

import base.constant;
import base.waitinglist;
import base.client;
//import base.Node;
/*
 * �ȴ����е��߳�,ά���ȴ�����
 */
public class waitingthread extends Thread{
	private waitinglist wl; //���е�˽�ж���
	private boolean waitingflag; //on-off ������ź�
	private int waitingminuts;
	public boolean isWaitingflag() {
		return waitingflag;
	}
	public void setWaitingflag(boolean waitingflag) {
		this.waitingflag = waitingflag;
	}
	public waitingthread(waitinglist wl){ //���췽����ʼ�����ж���,��Ϊ�������õ��̶߳���
		waitingminuts = 0;//��ʼ���ȴ�ʱ����0
		this.wl = wl;
	}
	public  waitinglist getWl() { //�������еĵȴ�����,
		try{
			sleep(10);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		return wl;
	}
	public synchronized void add(client cli){// ���ͻ��˴����������ʱ, ����жϽ��Ϊ��ǰ����,����������
		wl.push(cli);
	}
	public boolean search(client cli){ //����ĳ�������Ƿ��ڵȴ�������
		boolean flag =false;
		return flag;
	}
	public synchronized client minus(){//��ִ���̳߳��ֿ���ʱ,��β���Ķ���Ž�ִ���߳�
		return wl.pop().getData();
	}
	public synchronized void destroy(){
		wl.popAll();
		//destroy() ֮���ⲿstop����
	}
	public void run(){ //�����ݿ��ж���ǰ��û��ִ�н���ʱִ��
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
