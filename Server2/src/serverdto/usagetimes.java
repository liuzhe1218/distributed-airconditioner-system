package serverdto;

/*
 * ͳ������Room�Ŀ��ش���
 */
public class usagetimes {
	private int id;
	private String roomID;
	private int times; // ������insert, ����times��1
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRoomID() {
		return roomID;
	}
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
}
