package serverdto;

/*
 * ��������client���������¼
 */
import java.sql.Timestamp;

public class record {
	private int ID; //��¼ID
	private String roomID;
	private int threadID;
	private String temperdirection; // hot cool
	private double starttemper; // ������ʼ�¶�
	private double endtemper; // ��������¶�
	private Timestamp starttime;
	private Timestamp endtime; 
	private double flowvolume; //һ��������ܷ���
	private String flowpower; //��ǿ ;  high middle low
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
