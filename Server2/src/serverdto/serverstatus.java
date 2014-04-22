package serverdto;

/*
 * 服务器当前三个线程的对象
 */
public class serverstatus {
	private int threadID; // 1 2 3
	private String currentstatus; // ON OFF
	private String usestatus;// free busy
	private String temperdirection;// hot cool
	private String roomID;
	public int getThreadID() {
		return threadID;
	}
	public void setThreadID(int threadID) {
		this.threadID = threadID;
	}
	public String getCurrentstatus() {
		return currentstatus;
	}
	public void setCurrentstatus(String currentstatus) {
		this.currentstatus = currentstatus;
	}
	public String getUsestatus() {
		return usestatus;
	}
	public void setUsestatus(String usestatus) {
		this.usestatus = usestatus;
	}
	public String getTemperdirection() {
		return temperdirection;
	}
	public void setTemperdirection(String temperdirection) {
		this.temperdirection = temperdirection;
	}
	public String getRoomID() {
		return roomID;
	}
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	
}
