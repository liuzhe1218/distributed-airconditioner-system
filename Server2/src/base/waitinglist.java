package base;

/*
 * ������ģ��ȴ�����,�����ȳ�
 * ���Գɹ�
 */
public class waitinglist {
	Node first;
	int size;
	public waitinglist(){
		first = null;
		size = 0;
	}
	public Node getFirst() {
		return first;
	}
	public void setFirst(Node first) {
		this.first = first;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public Node getObject(int index){
	    int i=0;
	    Node temp = getFirst();
	    for (;i<(size-index-1);i++){
	    	temp = temp.next;
	    }
	    return temp;
    }
	public Node getCurrentNode(int size){
		Node node = getFirst();
		for (int i=0;i<size-1;i++)
			node = node.next;
		return node;
	}
	//�����ͷ���Ӷ�β�ó�����
    public void push(client data){// ��RoomID�ŵ���β
		Node node = new Node(data);
		node.next = first; //�嵽��ͷ
		first = node;
		size++;
	}
    public Node pop(){ //����ͷRoomID �Ӷ���ȡ����������
    	Node output = getCurrentNode(getSize());
    	size--;
    	return output;
    }
    public void popAll(){ //��״̬Ϊoffʱ, ������еĵȴ�����, ֻ��on֮����������Ŷ�
    	int length = getSize();
    	int i =0 ;
    	for (;i<length;i++){
    		pop();
    	}
    }
}