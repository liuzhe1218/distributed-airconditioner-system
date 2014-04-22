package serverdto;

/*
 * 所有来自client的请求处理记录
 */
import java.sql.Timestamp;

public class record {
	private int ID; //记录ID
	private String roomID;
	private int threadID;
	private String temperdirection; // hot cool
	private double starttemper; // 请求起始温度
	private double endtemper; // 请求结束温度
	private Timestamp starttime;
	private Timestamp endtime; 
	private double flowvolume; //一次请求的总风量
	private String flowpower; //风强 ;  high middle low
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getRoomID() {
		return roomID;
	}
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	public int getThreadID() {
		return threadID;
	}
	public void setThreadID(int threadID) {
		this.threadID = threadID;
	}
	public String getTemperdirection() {
		return temperdirection;
	}
	public void setTemperdirection(String temperdirection) {
		this.temperdirection = temperdirection;
	}
	public double getStarttemper() {
		return starttemper;
	}
	public void setStarttemper(double starttemper) {
		this.starttemper = starttemper;
	}
	public double getEndtemper() {
		return endtemper;
	}
	public void setEndtemper(double endtemper) {
		this.endtemper = endtemper;
	}
	public Timestamp getStarttime() {
		return starttime;
	}
	public void setStarttime(Timestamp starttime) {
		this.starttime = starttime;
	}
	public Timestamp getEndtime() {
		return endtime;
	}
	public void setEndtime(Timestamp endtime) {
		this.endtime = endtime;
	}
	public double getFlowvolume() {
		return flowvolume;
	}
	public void setFlowvolume(double flowvolume) {
		this.flowvolume = flowvolume;
	}
	public String getFlowpower() {
		return flowpower;
	}
	public void setFlowpower(String flowpower) {
		this.flowpower = flowpower;
	}
}
