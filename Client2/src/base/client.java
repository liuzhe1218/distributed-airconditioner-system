package base;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
/*
 * 用来接收来自客户端的数据对象
 */
public class client implements Serializable{
	private String roomID;
	private double currenttemper;
	private double endtemper;
	private String temperdirection;
	private String flowpower;
	private Timestamp starttime;
	public String getRoomID() {
		return roomID;
	}
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	public double getCurrenttemper() {
		return currenttemper;
	}
	public void setCurrenttemper(double currenttemper) {
		this.currenttemper = currenttemper;
	}
	public double getEndtemper() {
		return endtemper;
	}
	public void setEndtemper(double endtemper) {
		this.endtemper = endtemper;
	}
	public String getTemperdirection() {
		return temperdirection;
	}
	public void setTemperdirection(String temperdirection) {
		this.temperdirection = temperdirection;
	}
	public String getFlowpower() {
		return flowpower;
	}
	public void setFlowpower(String flowpower) {
		this.flowpower = flowpower;
	}
	public Timestamp getStarttime() {
		return starttime;
	}
	public void setStarttime(Timestamp starttime) {
		this.starttime = starttime;
	}
}
