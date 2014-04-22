package threads;

import java.sql.Timestamp;

import serverdao.controlDao;
import base.client;
import base.waitinglist;
import base.serverdao;
/*
 * ���߳�,������������
 * ����socket�ĺ���
 */
public class serverthread extends Thread{
	//controlDao control = new controlDao();
	serverdao control = new serverdao();
	client cli = new client(); //socket���մ������
	int[] noticeflag = {0,0,0}; //�ȴ��ź���  δ��ʼ��0  ִ����1 ִ�н���2
	private boolean signal; //ִ���ź���,��Դ��control�еĿ�������
	private waitingthread test;
	executethread[] threads = control.getThreads(); //����ִ���߳�
	int num;
	//socket����ģ��������ӿ�
	
	public serverthread( waitingthread test){
		this.test = test;
	}
	public void setSignal(boolean signal) { //�������̵߳��ź�������
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
			// socketģ��Ľ���ʵ����,ȡ��cli����
			//�ȵ���wl�Ķ���,Ȼ�����socket�Ķ���
			setCli(test.minus());
			System.out.println("the setted client is: "+cli.getRoomID());
			if (cli!=null){
				try {
					if (noticeflag[0]==0||noticeflag[1]==0||noticeflag[2]==0){// ���̻߳�δ����ʼ��
						num = control.threadControl(cli, noticeflag);
						System.out.println("the client "+cli.getRoomID()+" is executing");
					    continue;
					}
					else if ((threads[0].getNoticeflag()==1)&&(threads[1].getNoticeflag()==1)&&(threads[2].getNoticeflag()==1)){//��ǰ�ȴ��̴߳��ڵȴ����������еĶ���ִ��
					   // num = control.threadControl(test.minus(),noticeflag); //������ǰ
						num = -1;
						//�����׶����ظ��ж�
						if (control.isExist(cli.getRoomID())!=-1){
							num = control.threadControl(cli, noticeflag);
							System.out.println("substitute");
						}
						else{
							//δ�����ظ����󣬵ȴ�
							while(threads[0].getNoticeflag()!=2||threads[1].getNoticeflag()!=2||threads[2].getNoticeflag()!=2){//˵������ѭ�����������
								//System.out.println("server is waiting");
							}
							num = control.threadControl(cli, noticeflag);
							System.out.println("the waiting client "+cli.getRoomID()+" is working now");
						}
						continue;
					    //test.add(cli); //���յķŽ�waiting list
					}
					else if ((threads[0].getNoticeflag()==2)||(threads[1].getNoticeflag()==2)||(threads[2].getNoticeflag()==2)){//ĳ�߳�ִ�н���֮��
						//judge(noticeflag); //����
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
		sthread.setSignal(true);//����
		sthread.start();
		thread.start();
	}
}