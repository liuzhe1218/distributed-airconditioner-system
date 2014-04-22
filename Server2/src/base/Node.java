package base;

public class Node{ //½ÚµãÀà
	client  data;
	Node next;
	public client  getData() {
		return data;
	}
	public Node(client  data) {
		this.data = data;
	}
	public Node getNext() {
		return next;
	}
	public void setNext(Node next) {
		this.next = next;
	}	
}