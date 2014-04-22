package base;

/*
 * 用链表模拟等待队列,先入先出
 * 测试成功
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
	//插入对头，从队尾拿出对象
    public void push(client data){// 将RoomID放到队尾
		Node node = new Node(data);
		node.next = first; //插到对头
		first = node;
		size++;
	}
    public Node pop(){ //将对头RoomID 从队列取出，并插入
    	Node output = getCurrentNode(getSize());
    	size--;
    	return output;
    }
    public void popAll(){ //当状态为off时, 清空所有的等待队列, 只有on之后才能重新排队
    	int length = getSize();
    	int i =0 ;
    	for (;i<length;i++){
    		pop();
    	}
    }
}