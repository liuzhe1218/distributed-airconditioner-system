package serverdto;

/*
 * 统计所有Room的开关次数
 */
public class usagetimes {
	private int id;
	private String roomID;
	private int times; // 不存在insert, 存在times加1
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
