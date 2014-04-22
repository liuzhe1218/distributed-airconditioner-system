package base;

import java.io.Serializable;

/*
 * 动态温度专递类，传对象时通过地址来传socket
 */
public class temper implements Serializable{
	private double temper;
	private boolean flag; // 区分中间的温度包和最终的温度包,
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
