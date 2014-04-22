package serverdao;

public class test extends Thread{
	private boolean flag;
	private client cli;
	public void setCli(client cli){
		this.cli = cli;
	}
	public client getCli(){ 
		return this.cli;
	}
	public void setFlag(boolean flag){
		this.flag = flag;
	}
	public boolean getFlag(){
		return this.flag;
	}
	public void run(){
			while(flag){
				cli.setNum(cli.getNum()+1);
				System.out.println("the current minus is: "+(cli.getThreshold()-cli.getNum()));
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (cli.getThreshold()==cli.getNum()){
					System.out.println("stops");
					break;
				}			
			}	
			if (flag == false){
				System.out.println("±»ÆÈ¹Ø±Õ");
			}			
	}
	public static void main(String[] args){
		client cli1 = new client();
		client cli2 = new client();
		cli1.setNum(2);
		cli1.setThreshold(10);
		cli2.setNum(1);
		cli2.setThreshold(10);
		test thread1 = new test();
		thread1.setCli(cli1);
		thread1.setFlag(true);
		thread1.start();
		try {
			thread1.sleep(3000);
			thread1.setFlag(false);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//test thread2 = new test();
		thread1.setCli(cli2);
		thread1.setFlag(true);
		//thread1.start();
		
	}
}
class client{
	private int num;
	private int threshold;
	public int getNum() {
		return num;
	}
	public int getThreshold() {
		return threshold;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
}
