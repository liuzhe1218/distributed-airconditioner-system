package base;

import java.io.Serializable;

/*
 * ��̬�¶�ר���࣬������ʱͨ����ַ����socket
 */
public class temper implements Serializable{
	private double temper;
	private boolean flag; // �����м���¶Ȱ������յ��¶Ȱ�,
	public double getTemper() {
		return temper;
	}
	public boolean getFlag() {
		return flag;
	}
	public void setTemper(double temper) {
		this.temper = temper;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
}
